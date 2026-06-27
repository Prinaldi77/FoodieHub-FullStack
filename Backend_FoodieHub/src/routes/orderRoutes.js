const express = require('express');
const router = express.Router();
const orderController = require('../controllers/orderController');
const authMiddleware = require('../middleware/authMiddleware');

// Public route for Midtrans webhooks (must be before authMiddleware)
router.post('/notification', orderController.paymentNotification);

router.use(authMiddleware);

router.post('/checkout', orderController.checkout);
router.get('/history', orderController.getOrderHistory);
router.get('/:id', orderController.getOrderById);

module.exports = router;
