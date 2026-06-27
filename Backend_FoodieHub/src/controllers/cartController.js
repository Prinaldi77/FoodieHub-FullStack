const db = require('../config/database');

// Helper function to get or create cart for user
const getOrCreateCart = async (userId) => {
    let cartResult = await db.query('SELECT * FROM carts WHERE user_id = $1', [userId]);
    if (cartResult.rows.length === 0) {
        cartResult = await db.query(
            'INSERT INTO carts (user_id) VALUES ($1) RETURNING *',
            [userId]
        );
    }
    return cartResult.rows[0];
};

const getCart = async (req, res) => {
    try {
        const cart = await getOrCreateCart(req.user.id);

        const itemsResult = await db.query(`
            SELECT ci.id, ci.cart_id, ci.food_id, ci.qty, ci.subtotal, 
                   f.name, f.price, f.image_url 
            FROM cart_items ci
            JOIN foods f ON ci.food_id = f.id
            WHERE ci.cart_id = $1
            ORDER BY ci.id ASC
        `, [cart.id]);

        // Calculate total cart value
        const total = itemsResult.rows.reduce((sum, item) => sum + parseFloat(item.subtotal), 0);

        res.json({
            success: true,
            message: 'Cart retrieved successfully',
            data: {
                cart_id: cart.id,
                items: itemsResult.rows,
                total: total
            }
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

const addToCart = async (req, res) => {
    try {
        const { food_id, qty = 1 } = req.body;
        
        if (!food_id) {
            return res.status(400).json({ success: false, message: 'food_id is required' });
        }

        // Validate qty (must be integer and >= 1)
        if (!Number.isInteger(qty) || qty < 1) {
            return res.status(400).json({ success: false, message: 'qty must be a positive integer >= 1' });
        }

        // Get food price
        const foodResult = await db.query('SELECT price FROM foods WHERE id = $1', [food_id]);
        if (foodResult.rows.length === 0) {
            return res.status(404).json({ success: false, message: 'Food not found' });
        }
        const foodPrice = parseFloat(foodResult.rows[0].price);

        const cart = await getOrCreateCart(req.user.id);

        // Check if item already in cart
        const existingItem = await db.query(
            'SELECT * FROM cart_items WHERE cart_id = $1 AND food_id = $2',
            [cart.id, food_id]
        );

        if (existingItem.rows.length > 0) {
            // Update qty and subtotal
            const newQty = existingItem.rows[0].qty + qty;
            const newSubtotal = newQty * foodPrice;

            await db.query(
                'UPDATE cart_items SET qty = $1, subtotal = $2 WHERE id = $3',
                [newQty, newSubtotal, existingItem.rows[0].id]
            );
        } else {
            // Insert new item
            const subtotal = qty * foodPrice;
            await db.query(
                'INSERT INTO cart_items (cart_id, food_id, qty, subtotal) VALUES ($1, $2, $3, $4)',
                [cart.id, food_id, qty, subtotal]
            );
        }

        res.json({
            success: true,
            message: 'Item added to cart successfully'
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

const updateCartItem = async (req, res) => {
    try {
        const { cart_item_id, qty } = req.body;

        if (!cart_item_id || qty === undefined) {
            return res.status(400).json({ success: false, message: 'cart_item_id and qty are required' });
        }

        // Validate qty (must be integer and >= 0)
        if (!Number.isInteger(qty) || qty < 0) {
            return res.status(400).json({ success: false, message: 'qty must be a non-negative integer >= 0' });
        }

        if (qty <= 0) {
            // if qty 0, delete it (with ownership check)
            const deleteResult = await db.query(`
                DELETE FROM cart_items 
                WHERE id = $1 AND cart_id IN (SELECT id FROM carts WHERE user_id = $2)
                RETURNING id
            `, [cart_item_id, req.user.id]);

            if (deleteResult.rows.length === 0) {
                return res.status(404).json({ success: false, message: 'Cart item not found or unauthorized' });
            }
            return res.json({ success: true, message: 'Item removed from cart' });
        }

        // Get cart item and food price, verifying ownership
        const itemResult = await db.query(`
            SELECT ci.*, f.price 
            FROM cart_items ci
            JOIN foods f ON ci.food_id = f.id
            JOIN carts c ON ci.cart_id = c.id
            WHERE ci.id = $1 AND c.user_id = $2
        `, [cart_item_id, req.user.id]);

        if (itemResult.rows.length === 0) {
            return res.status(404).json({ success: false, message: 'Cart item not found or unauthorized' });
        }

        const foodPrice = parseFloat(itemResult.rows[0].price);
        const newSubtotal = qty * foodPrice;

        await db.query(
            'UPDATE cart_items SET qty = $1, subtotal = $2 WHERE id = $3',
            [qty, newSubtotal, cart_item_id]
        );

        res.json({
            success: true,
            message: 'Cart item updated successfully'
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

const deleteCartItem = async (req, res) => {
    try {
        const { id } = req.params;
        
        // Delete item with ownership check
        const result = await db.query(`
            DELETE FROM cart_items 
            WHERE id = $1 AND cart_id IN (SELECT id FROM carts WHERE user_id = $2)
            RETURNING id
        `, [id, req.user.id]);
        
        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, message: 'Cart item not found or unauthorized' });
        }

        res.json({
            success: true,
            message: 'Cart item removed successfully'
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

module.exports = {
    getCart,
    addToCart,
    updateCartItem,
    deleteCartItem
};
