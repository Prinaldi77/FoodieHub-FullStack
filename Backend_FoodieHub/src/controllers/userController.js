const db = require('../config/database');

// Upload Avatar
const uploadAvatar = async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({
                success: false,
                message: 'No image uploaded'
            });
        }

        // Generate the URL for the uploaded file
        // e.g. /uploads/avatar-12345.jpg
        const avatarUrl = `/uploads/${req.file.filename}`;

        const updatedUser = await db.query(
            'UPDATE users SET avatar_url = $1 WHERE id = $2 RETURNING id, fullname, email, phone, avatar_url, created_at',
            [avatarUrl, req.user.id]
        );

        if (updatedUser.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        res.json({
            success: true,
            message: 'Avatar uploaded successfully',
            data: updatedUser.rows[0]
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};

module.exports = {
    uploadAvatar
};
