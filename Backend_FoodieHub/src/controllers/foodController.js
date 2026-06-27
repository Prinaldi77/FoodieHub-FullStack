const db = require('../config/database');

const getAllFoods = async (req, res) => {
    try {
        const result = await db.query('SELECT * FROM foods ORDER BY created_at DESC');
        res.json({
            success: true,
            message: 'Foods retrieved successfully',
            data: result.rows
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

const getFoodById = async (req, res) => {
    try {
        const { id } = req.params;
        const result = await db.query('SELECT * FROM foods WHERE id = $1', [id]);
        
        if (result.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Food not found'
            });
        }

        res.json({
            success: true,
            message: 'Food details retrieved',
            data: result.rows[0]
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

const searchFoods = async (req, res) => {
    try {
        const { q } = req.query;
        if (!q) {
            return res.status(400).json({
                success: false,
                message: 'Search query "q" is required'
            });
        }

        const result = await db.query(
            'SELECT * FROM foods WHERE name ILIKE $1 OR description ILIKE $2 ORDER BY created_at DESC',
            [`%${q}%`, `%${q}%`]
        );

        res.json({
            success: true,
            message: 'Search results retrieved',
            data: result.rows
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

const getFoodsByCategory = async (req, res) => {
    try {
        const { category } = req.params;
        const result = await db.query(
            'SELECT * FROM foods WHERE category ILIKE $1 ORDER BY created_at DESC',
            [category]
        );

        res.json({
            success: true,
            message: `Foods in category ${category} retrieved`,
            data: result.rows
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

module.exports = {
    getAllFoods,
    getFoodById,
    searchFoods,
    getFoodsByCategory
};
