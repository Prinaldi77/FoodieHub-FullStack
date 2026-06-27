const http = require('http');

const loginData = JSON.stringify({
    email: "aldi@gmail.com", // Assume a user with this email exists
    password: "password123"   // Replace with correct credentials if different
});

// Helper for making POST requests
function makePost(path, data, token = null) {
    return new Promise((resolve, reject) => {
        const headers = {
            'Content-Type': 'application/json',
            'Content-Length': Buffer.byteLength(data)
        };
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const req = http.request({
            hostname: 'localhost',
            port: 5000,
            path: path,
            method: 'POST',
            headers: headers
        }, (res) => {
            let body = '';
            res.setEncoding('utf8');
            res.on('data', (chunk) => body += chunk);
            res.on('end', () => resolve({ statusCode: res.statusCode, body: JSON.parse(body) }));
        });

        req.on('error', (e) => reject(e));
        req.write(data);
        req.end();
    });
}

// Helper for making GET requests
function makeGet(path, token = null) {
    return new Promise((resolve, reject) => {
        const headers = {};
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const req = http.request({
            hostname: 'localhost',
            port: 5000,
            path: path,
            method: 'GET',
            headers: headers
        }, (res) => {
            let body = '';
            res.setEncoding('utf8');
            res.on('data', (chunk) => body += chunk);
            res.on('end', () => resolve({ statusCode: res.statusCode, body: JSON.parse(body) }));
        });

        req.on('error', (e) => reject(e));
        req.end();
    });
}

async function runTest() {
    try {
        console.log("Simulating registration/login check...");
        
        // Let's create a test user first to ensure it exists
        const registerData = JSON.stringify({
            fullname: "Aldi Test User",
            email: "test_user_" + Date.now() + "@test.com",
            password: "password123",
            phone: "08123456789"
        });

        console.log("Registering new test user...");
        const regRes = await makePost('/api/auth/register', registerData);
        console.log("Register Response Status:", regRes.statusCode, regRes.body);

        if (!regRes.body.success) {
            console.error("Registration failed.");
            return;
        }

        // Login as new test user
        const newLoginData = JSON.stringify({
            email: regRes.body.data.email,
            password: "password123"
        });

        console.log("Logging in as the new test user...");
        const loginRes = await makePost('/api/auth/login', newLoginData);
        console.log("Login Response Status:", loginRes.statusCode, loginRes.body);

        if (!loginRes.body.success) {
            console.error("Login failed.");
            return;
        }

        const token = loginRes.body.data.token;
        console.log("User JWT Token retrieved successfully.");

        // Query Order History
        console.log("Fetching order history with token...");
        const historyRes = await makeGet('/api/orders', token);
        console.log("Order History Status:", historyRes.statusCode, historyRes.body);

        // Try to perform a mock checkout
        console.log("Trying to seed cart first...");
        // 1. Get foods list
        const foodsRes = await makeGet('/api/foods', token);
        console.log("Foods Status:", foodsRes.statusCode);
        
        if (foodsRes.body.success && foodsRes.body.data.length > 0) {
            const foodId = foodsRes.body.data[0].id;
            console.log("Adding food item to cart:", foodId);
            const addToCartRes = await makePost('/api/cart', JSON.stringify({ food_id: foodId, qty: 1 }), token);
            console.log("AddToCart Status:", addToCartRes.statusCode, addToCartRes.body);

            // 2. Checkout
            console.log("Performing checkout...");
            const checkoutRes = await makePost('/api/orders/checkout', JSON.stringify({
                delivery_address: "Jalan Sukarno Hatta No 12",
                payment_method: "Midtrans"
            }), token);
            console.log("Checkout Status:", checkoutRes.statusCode, checkoutRes.body);
        } else {
            console.log("No foods found to test checkout.");
        }

    } catch (err) {
        console.error("API test failed:", err);
    }
}

runTest();
