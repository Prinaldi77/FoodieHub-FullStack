const db = require('../src/config/database');

const foodsAndDrinks = [
    {
        name: "Nasi Goreng Spesial",
        description: "Nasi goreng khas Indonesia dengan bumbu rempah pilihan, dilengkapi telur mata sapi, kerupuk, acar, dan potongan ayam goreng.",
        price: 25000.00,
        category: "Makanan",
        rating: 4.8,
        stock: 50,
        image_url: "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=500"
    },
    {
        name: "Sate Ayam Madura",
        description: "10 tusuk sate daging ayam pilihan yang dibakar dengan bumbu kecap manis khas Madura, disajikan dengan saus kacang gurih dan irisan bawang merah.",
        price: 30000.00,
        category: "Makanan",
        rating: 4.7,
        stock: 30,
        image_url: "https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=500"
    },
    {
        name: "Mie Goreng Jawa",
        description: "Mie kuning basah khas Jawa yang ditumis dengan telur, sayur kol, sawi, bakso sapi, ayam suwir, dan rasa manis gurih kecap manis.",
        price: 22000.00,
        category: "Makanan",
        rating: 4.5,
        stock: 40,
        image_url: "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500"
    },
    {
        name: "Bakso Sapi Solo",
        description: "Bakso daging sapi asli Solo disajikan dengan kuah kaldu sapi hangat nan gurih, dilengkapi mie kuning, bihun, sawi hijau, dan seledri.",
        price: 20000.00,
        category: "Makanan",
        rating: 4.6,
        stock: 45,
        image_url: "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=500"
    },
    {
        name: "Gado-Gado Betawi",
        description: "Sayuran rebus segar (tauge, kangkung, kacang panjang), tahu goreng, tempe, telur rebus, disiram dengan bumbu kacang khas Betawi dan kerupuk emping.",
        price: 18000.00,
        category: "Makanan",
        rating: 4.4,
        stock: 25,
        image_url: "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=500"
    },
    {
        name: "Ayam Goreng Kremes",
        description: "Ayam goreng bumbu ungkep gurih dengan taburan kremesan yang renyah di luar dan daging ayam yang lembut di dalam, lengkap dengan lalapan dan sambal korek.",
        price: 28000.00,
        category: "Makanan",
        rating: 4.7,
        stock: 35,
        image_url: "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=500"
    },
    {
        name: "Es Teh Manis",
        description: "Teh hitam melati seduh berkualitas disajikan dingin dengan es batu dan gula asli yang manis menyegarkan.",
        price: 5000.00,
        category: "Minuman",
        rating: 4.9,
        stock: 100,
        image_url: "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500"
    },
    {
        name: "Es Jeruk Peras",
        description: "Jeruk peras segar alami kaya vitamin C, dipadukan dengan es batu dan sirup gula murni untuk melepas dahaga.",
        price: 8000.00,
        category: "Minuman",
        rating: 4.7,
        stock: 80,
        image_url: "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=500"
    },
    {
        name: "Jus Alpukat",
        description: "Jus buah alpukat mentega segar yang kental, disajikan dingin dengan es serut dan siraman susu kental manis cokelat.",
        price: 12000.00,
        category: "Minuman",
        rating: 4.6,
        stock: 40,
        image_url: "https://images.unsplash.com/photo-1603046891744-1f76eb10aec1?w=500"
    },
    {
        name: "Kopi Susu Gula Aren",
        description: "Espresso robusta berkualitas dicampur dengan susu cair segar dan pemanis gula aren alami yang wangi dan gurih.",
        price: 15000.00,
        category: "Minuman",
        rating: 4.8,
        stock: 60,
        image_url: "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=500"
    },
    {
        name: "Pizza Margherita Spesial",
        description: "Pizza khas Italia dengan saus tomat premium, irisan keju mozzarella leleh, daun basil segar, dan siraman minyak zaitun hangat.",
        price: 75000.00,
        category: "Makanan",
        rating: 4.9,
        stock: 25,
        image_url: "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500"
    },
    {
        name: "Burger Sapi Panggang",
        description: "Double patty daging sapi panggang juicy dengan lelehan keju cheddar, selada segar, tomat, bawang bombay, dan saus burger spesial.",
        price: 35000.00,
        category: "Makanan",
        rating: 4.7,
        stock: 40,
        image_url: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500"
    },
    {
        name: "Kentang Goreng Renyah",
        description: "Kentang goreng potongan tebal gurih dengan bumbu garam bawang, renyah di luar dan lembut di dalam. Disajikan dengan saus sambal.",
        price: 15000.00,
        category: "Makanan",
        rating: 4.6,
        stock: 70,
        image_url: "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=500"
    },
    {
        name: "Fried Chicken Crispy",
        description: "Daging ayam goreng tepung bumbu rahasia yang super renyah dan kriuk di kulitnya namun tetap juicy di dalam. Isi 2 potong besar.",
        price: 26000.00,
        category: "Makanan",
        rating: 4.8,
        stock: 45,
        image_url: "https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?w=500"
    },
    {
        name: "Spageti Bolognese",
        description: "Pasta spageti lembut disiram dengan saus bolognese daging sapi cincang berlimpah, parutan keju parmesan, dan garnish peterseli segar.",
        price: 38000.00,
        category: "Makanan",
        rating: 4.7,
        stock: 30,
        image_url: "https://images.unsplash.com/photo-1516100882582-76c9a28b0611?w=500"
    },
    {
        name: "Milkshake Cokelat Oreo",
        description: "Susu segar diblender creamy dengan es krim cokelat belgian premium dan remahan biskuit Oreo manis gurih, ditambah whipped cream.",
        price: 18000.00,
        category: "Minuman",
        rating: 4.9,
        stock: 50,
        image_url: "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500"
    },
    {
        name: "Jus Mangga Segar",
        description: "Jus buah mangga arumanis segar manis matang pohon diblender es batu tanpa pemanis buatan, kaya vitamin A & C.",
        price: 14000.00,
        category: "Minuman",
        rating: 4.8,
        stock: 60,
        image_url: "https://images.unsplash.com/photo-1553177174-1e20ebc525c3?w=500"
    }
];

async function seedFoods() {
    try {
        console.log("Clearing existing food items...");
        await db.query('DELETE FROM cart_items');
        await db.query('DELETE FROM order_items');
        await db.query('DELETE FROM foods');
        console.log("Old food items cleared.");

        console.log("Seeding new food and drink items...");
        for (const item of foodsAndDrinks) {
            await db.query(`
                INSERT INTO foods (name, description, price, category, rating, stock, image_url)
                VALUES ($1, $2, $3, $4, $5, $6, $7)
            `, [item.name, item.description, item.price, item.category, item.rating, item.stock, item.image_url]);
        }
        console.log("Successfully seeded " + foodsAndDrinks.length + " items!");
    } catch (err) {
        console.error("Seeding failed:", err);
    } finally {
        process.exit();
    }
}

seedFoods();
