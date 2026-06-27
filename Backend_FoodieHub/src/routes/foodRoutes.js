const express = require('express');
const router = express.Router();
const foodController = require('../controllers/foodController');

router.get('/', foodController.getAllFoods);
router.get('/search', foodController.searchFoods);
router.get('/category/:category', foodController.getFoodsByCategory);
router.get('/:id', foodController.getFoodById);

module.exports = router;
