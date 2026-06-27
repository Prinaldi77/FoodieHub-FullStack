const db = require('../config/database');
const snap = require('../config/midtrans');
const crypto = require('crypto');

const checkout = async (req, res) => {
    // Start transaction
    const client = await db.pool.connect();
    let orderId = null;
    let totalPrice = 0;

    try {
        const { delivery_address, payment_method } = req.body;

        if (!delivery_address || !payment_method) {
            return res.status(400).json({
                success: false,
                message: 'delivery_address and payment_method are required'
            });
        }

        await client.query('BEGIN');

        // 1. Get user's cart and items
        const cartResult = await client.query('SELECT * FROM carts WHERE user_id = $1', [req.user.id]);
        if (cartResult.rows.length === 0) {
            throw new Error('Cart is empty');
        }
        const cartId = cartResult.rows[0].id;

        const cartItemsResult = await client.query(`
            SELECT ci.food_id, ci.qty, ci.subtotal, f.price 
            FROM cart_items ci
            JOIN foods f ON ci.food_id = f.id
            WHERE ci.cart_id = $1
        `, [cartId]);

        if (cartItemsResult.rows.length === 0) {
            throw new Error('Cart is empty');
        }

        // 2. Calculate total_price
        totalPrice = cartItemsResult.rows.reduce((sum, item) => sum + parseFloat(item.subtotal), 0);

        // 3. Save to orders
        const orderResult = await client.query(
            `INSERT INTO orders (user_id, total_price, status, delivery_address, payment_method) 
             VALUES ($1, $2, 'pending', $3, $4) RETURNING *`,
            [req.user.id, totalPrice, delivery_address, payment_method]
        );
        orderId = orderResult.rows[0].id;

        // 4. Save to order_items
        for (const item of cartItemsResult.rows) {
            await client.query(
                `INSERT INTO order_items (order_id, food_id, qty, price, subtotal) 
                 VALUES ($1, $2, $3, $4, $5)`,
                [orderId, item.food_id, item.qty, item.price, item.subtotal]
            );
        }

        // 5. Empty cart after checkout
        await client.query('DELETE FROM cart_items WHERE cart_id = $1', [cartId]);

        await client.query('COMMIT');
    } catch (error) {
        await client.query('ROLLBACK');
        return res.status(500).json({
            success: false,
            message: error.message
        });
    } finally {
        client.release();
    }

    // Call Midtrans Snap API outside database transaction to prevent connection pool locking
    try {
        const userResult = await db.query('SELECT fullname, email, phone FROM users WHERE id = $1', [req.user.id]);
        const user = userResult.rows[0];

        const parameter = {
            transaction_details: {
                order_id: orderId,
                gross_amount: Math.round(totalPrice)
            },
            customer_details: {
                first_name: user.fullname,
                email: user.email,
                phone: user.phone || ''
            },
            callbacks: {
                finish: "https://foodiehub.com/finish",
                unfinish: "https://foodiehub.com/unfinish",
                error: "https://foodiehub.com/error"
            }
        };

        const transaction = await snap.createTransaction(parameter);

        // Save snap token and redirect url to database
        await db.query(
            'UPDATE orders SET snap_token = $1, snap_url = $2 WHERE id = $3',
            [transaction.token, transaction.redirect_url, orderId]
        );

        res.json({
            success: true,
            message: 'Checkout successful',
            data: {
                id: orderId,
                total_price: totalPrice,
                status: 'pending',
                snap_token: transaction.token,
                snap_url: transaction.redirect_url
            }
        });
    } catch (midtransError) {
        console.error('Midtrans Snap transaction creation failed:', midtransError);
        // Fallback: order is already created in db even if Midtrans generated error
        res.json({
            success: true,
            message: 'Checkout successful, but Midtrans payment gateway is temporarily offline.',
            data: {
                id: orderId,
                total_price: totalPrice,
                status: 'pending',
                snap_token: null,
                snap_url: null
            }
        });
    }
};

const getOrderHistory = async (req, res) => {
    try {
        const result = await db.query(
            'SELECT * FROM orders WHERE user_id = $1 ORDER BY created_at DESC',
            [req.user.id]
        );

        res.json({
            success: true,
            message: 'Order history retrieved',
            data: result.rows
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

const getOrderById = async (req, res) => {
    try {
        const { id } = req.params;

        // Validate if order belongs to user
        const orderResult = await db.query(
            'SELECT * FROM orders WHERE id = $1 AND user_id = $2',
            [id, req.user.id]
        );

        if (orderResult.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Order not found'
            });
        }

        const order = orderResult.rows[0];

        // If order status is pending, sync with Midtrans Snap API directly (in case webhook notifications didn't reach localhost)
        if (order.status === 'pending') {
            try {
                const midtransStatus = await snap.transaction.status(id);
                let newStatus = 'pending';
                
                const txStatus = midtransStatus.transaction_status;
                const fraudStatus = midtransStatus.fraud_status;

                if (txStatus === 'capture') {
                    if (fraudStatus === 'challenge') {
                        newStatus = 'pending';
                    } else if (fraudStatus === 'accept') {
                        newStatus = 'processing';
                    }
                } else if (txStatus === 'settlement') {
                    newStatus = 'processing';
                } else if (txStatus === 'cancel' || txStatus === 'deny' || txStatus === 'expire') {
                    newStatus = 'cancelled';
                }

                if (newStatus !== order.status) {
                    await db.query(
                        'UPDATE orders SET status = $1 WHERE id = $2',
                        [newStatus, id]
                    );
                    order.status = newStatus;
                }
            } catch (err) {
                console.error(`Failed to fetch status from Midtrans for order ${id}:`, err.message);
            }
        }

        const itemsResult = await db.query(`
            SELECT oi.id, oi.food_id, oi.qty, oi.price, oi.subtotal, f.name, f.image_url
            FROM order_items oi
            LEFT JOIN foods f ON oi.food_id = f.id
            WHERE oi.order_id = $1
        `, [id]);

        const orderDetails = {
            ...order,
            items: itemsResult.rows
        };

        res.json({
            success: true,
            message: 'Order details retrieved',
            data: orderDetails
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

const paymentNotification = async (req, res) => {
    try {
        const notification = req.body;

        const orderId = notification.order_id;
        const statusCode = notification.status_code;
        const grossAmount = notification.gross_amount;
        const signatureKeyReceived = notification.signature_key;

        const serverKey = process.env.MIDTRANS_SERVER_KEY;
        const stringToHash = orderId + statusCode + grossAmount + serverKey;
        const signatureKeyCalculated = crypto
            .createHash('sha512')
            .update(stringToHash)
            .digest('hex');

        if (signatureKeyReceived !== signatureKeyCalculated) {
            return res.status(403).json({
                success: false,
                message: 'Invalid signature key'
            });
        }

        const transactionStatus = notification.transaction_status;
        const fraudStatus = notification.fraud_status;

        let orderStatus = 'pending';

        if (transactionStatus === 'capture') {
            if (fraudStatus === 'challenge') {
                orderStatus = 'pending';
            } else if (fraudStatus === 'accept') {
                orderStatus = 'processing';
            }
        } else if (transactionStatus === 'settlement') {
            orderStatus = 'processing';
        } else if (transactionStatus === 'cancel' || transactionStatus === 'deny' || transactionStatus === 'expire') {
            orderStatus = 'cancelled';
        } else if (transactionStatus === 'pending') {
            orderStatus = 'pending';
        }

        await db.query(
            'UPDATE orders SET status = $1 WHERE id = $2',
            [orderStatus, orderId]
        );

        res.json({
            success: true,
            message: 'Notification processed successfully'
        });
    } catch (error) {
        console.error('Midtrans Webhook error:', error);
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

module.exports = {
    checkout,
    getOrderHistory,
    getOrderById,
    paymentNotification
};
