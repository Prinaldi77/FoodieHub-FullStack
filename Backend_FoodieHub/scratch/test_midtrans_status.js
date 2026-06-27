const snap = require('../src/config/midtrans');

async function checkStatus() {
    const orderId = '9265f865-2e70-4cf5-aed8-e549aed3760e';
    try {
        console.log(`Querying status for order: ${orderId}...`);
        const status = await snap.transaction.status(orderId);
        console.log("Midtrans Response Status:");
        console.log(JSON.stringify(status, null, 2));
    } catch (err) {
        console.error("Failed to query Midtrans:", err.message);
    } finally {
        process.exit();
    }
}

checkStatus();
