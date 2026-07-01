const db = require('../src/config/database');

const foodsAndDrinks = [
    // ─── BAKSO & MIE (Menu Awal) ──────────────────────────────────────────
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c01",
        name: "Bakso Urat Spesial",
        description: "Bakso urat sapi asli ukuran besar disajikan dengan kuah kaldu sapi gurih hangat, mie kuning, bihun, dan sayuran segar.",
        price: 25000.00,
        category: "Makanan",
        rating: 4.8,
        stock: 50,
        image_url: "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c02",
        name: "Bakso Mercon Pedas",
        description: "Bakso ukuran jumbo isi cincang cabe rawit pedas meledak di mulut, dilengkapi bakso kecil dan kuah pedas gurih.",
        price: 26000.00,
        category: "Makanan",
        rating: 4.7,
        stock: 30,
        image_url: "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c03",
        name: "Bakso Telur Puyuh",
        description: "Bakso halus berisi telur puyuh gurih, disajikan lengkap dengan kuah sup kaldu bening yang wangi.",
        price: 24000.00,
        category: "Makanan",
        rating: 4.6,
        stock: 45,
        image_url: "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c04",
        name: "Mie Ayam Bakso",
        description: "Mie kenyal buatan sendiri ditaburi daging ayam kecap bumbu rempah pilihan dan disajikan dengan 2 butir bakso sapi halus.",
        price: 22000.00,
        category: "Makanan",
        rating: 4.5,
        stock: 40,
        image_url: "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c05",
        name: "Mie Ayam Ceker",
        description: "Mie ayam lezat dengan topping ceker ayam empuk rasa manis gurih khas Jawa.",
        price: 18000.00,
        category: "Makanan",
        rating: 4.4,
        stock: 25,
        image_url: "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c06",
        name: "Bakso Beranak Jumbo",
        description: "Bakso super jumbo yang di dalamnya berisi bakso-bakso kecil, tetelan, dan telur puyuh. Sangat mengenyangkan.",
        price: 35000.00,
        category: "Makanan",
        rating: 4.7,
        stock: 35,
        image_url: "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c07",
        name: "Tahu Bakso Goreng",
        description: "Tahu goreng dengan isian adonan bakso sapi gurih renyah, isi 5 pcs lengkap dengan cabai rawit hijau.",
        price: 15000.00,
        category: "Makanan",
        rating: 4.6,
        stock: 70,
        image_url: "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=500"
    },

    // ─── KULINER INDONESIA (Menu Baru Tambahan) ───────────────────────────
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c20",
        name: "Nasi Goreng Spesial",
        description: "Nasi goreng bumbu Jawa harum gurih wangi pandan disajikan dengan telur mata sapi, kerupuk, acar segar, dan taburan ayam suwir.",
        price: 20000.00,
        category: "Makanan",
        rating: 4.9,
        stock: 60,
        image_url: "https://images.unsplash.com/photo-1601050690597-df056fb4ce78?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c21",
        name: "Rendang Daging Sapi",
        description: "Daging sapi pilihan empuk dimasak perlahan dalam santan kental dengan ramuan rempah khas Minang yang kaya rasa.",
        price: 32000.00,
        category: "Makanan",
        rating: 4.9,
        stock: 30,
        image_url: "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c22",
        name: "Sate Ayam Madura",
        description: "Sate daging ayam bakar bumbu kacang manis gurih khas Madura, isi 10 tusuk lengkap dengan lontong empuk dan irisan bawang merah.",
        price: 25000.00,
        category: "Makanan",
        rating: 4.8,
        stock: 40,
        image_url: "https://images.unsplash.com/photo-1529042410759-befb1204b468?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c23",
        name: "Soto Ayam Lamongan",
        description: "Soto ayam kuah kuning hangat dengan bumbu koya gurih melimpah, suwiran daging ayam, soun, kol, dan telur rebus setengah matang.",
        price: 20000.00,
        category: "Makanan",
        rating: 4.7,
        stock: 35,
        image_url: "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c24",
        name: "Gado-Gado Betawi",
        description: "Aneka sayur rebus segar (tauge, kacang panjang, kol), tahu, tempe, kentang, telur rebus, disiram dengan bumbu kacang kental legit khas Betawi.",
        price: 18000.00,
        category: "Makanan",
        rating: 4.6,
        stock: 45,
        image_url: "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c25",
        name: "Sop Buntut Sapi",
        description: "Sop kaldu sapi bening kaya bumbu rempah dengan buntut sapi empuk, kentang, wortel, daun bawang, lengkap dengan emping renyah.",
        price: 45000.00,
        category: "Makanan",
        rating: 4.9,
        stock: 20,
        image_url: "https://images.unsplash.com/photo-1547592180-85f173990554?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c26",
        name: "Ayam Goreng Penyet",
        description: "Ayam goreng bumbu kuning empuk yang digeprek bersama sambal bawang pedas meledak dan lalapan segar timun serta kemangi.",
        price: 22000.00,
        category: "Makanan",
        rating: 4.7,
        stock: 40,
        image_url: "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=500"
    },

    // ─── ANEKA MINUMAN ────────────────────────────────────────────────────
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c08",
        name: "Es Teh Manis",
        description: "Teh melati manis wangi disajikan segar dengan es es batu melimpah.",
        price: 5000.00,
        category: "Minuman",
        rating: 4.9,
        stock: 100,
        image_url: "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c09",
        name: "Es Jeruk Peras",
        description: "Jeruk peras murni segar, manis alami asam menyegarkan tenggorokan.",
        price: 8000.00,
        category: "Minuman",
        rating: 4.7,
        stock: 80,
        image_url: "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c10",
        name: "Jus Alpukat Mentega",
        description: "Jus alpukat mentega segar kental manis, ditambah lumuran susu kental manis cokelat di dinding gelas.",
        price: 12000.00,
        category: "Minuman",
        rating: 4.6,
        stock: 40,
        image_url: "https://images.unsplash.com/photo-1603046891744-1f76eb10aec1?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c11",
        name: "Es Campur Spesial",
        description: "Es campur berisi potongan buah alpukat, nangka, kelapa muda, jelly, kolang kaling, dengan siraman sirup merah dan susu kental manis.",
        price: 15000.00,
        category: "Minuman",
        rating: 4.8,
        stock: 60,
        image_url: "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500"
    },
    {
        id: "b1a2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c27",
        name: "Es Kelapa Muda Gula Jawa",
        description: "Air kelapa muda segar berpadu dengan daging kelapa muda lembut, serutan es batu, dan manis khas gula Jawa cair.",
        price: 10000.00,
        category: "Minuman",
        rating: 4.8,
        stock: 50,
        image_url: "https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=500"
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
                INSERT INTO foods (id, name, description, price, category, rating, stock, image_url)
                VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
            `, [item.id, item.name, item.description, item.price, item.category, item.rating, item.stock, item.image_url]);
        }
        console.log("Successfully seeded " + foodsAndDrinks.length + " items!");
    } catch (err) {
        console.error("Seeding failed:", err);
    } finally {
        process.exit();
    }
}

seedFoods();
