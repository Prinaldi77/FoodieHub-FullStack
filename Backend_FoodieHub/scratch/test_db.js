const db = require('../src/config/database');

async function testConnection() {
    try {
        console.log("Testing connection...");
        const res = await db.query('SELECT NOW()');
        console.log("Connection successful. Server time:", res.rows[0].now);

        // Check tables
        const tables = await db.query(`
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'public'
        `);
        console.log("Tables in public schema:", tables.rows.map(r => r.table_name));

        // Check columns in orders
        const orderCols = await db.query(`
            SELECT column_name, data_type 
            FROM information_schema.columns 
            WHERE table_name = 'orders'
        `);
        console.log("Columns in 'orders' table:", orderCols.rows.map(r => `${r.column_name} (${r.data_type})`));

        // Check foods count
        const foods = await db.query('SELECT COUNT(*) FROM foods');
        console.log("Number of foods in DB:", foods.rows[0].count);

        // Check users count
        const users = await db.query('SELECT COUNT(*) FROM users');
        console.log("Number of users in DB:", users.rows[0].count);

        // Check orders count
        const orders = await db.query('SELECT COUNT(*) FROM orders');
        console.log("Number of orders in DB:", orders.rows[0].count);

    } catch (err) {
        console.error("Database connection/query failed:", err);
    } finally {
        process.exit();
    }
}

testConnection();
