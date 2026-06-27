# FoodieHub Backend REST API

Backend API production-ready untuk aplikasi Android FoodieHub. Dibangun menggunakan Node.js, Express.js, dan PostgreSQL (via Supabase).

## Tech Stack
- Node.js & Express.js
- PostgreSQL (Supabase) dengan driver `pg`
- JWT Authentication & bcryptjs
- multer, cors, dotenv

## Langkah Instalasi

1. **Clone repository atau ekstrak file ke folder lokal Anda.**
2. **Buka terminal dan arahkan ke folder proyek ini:**
   ```bash
   cd Backend_FoodieHub
   ```
3. **Install semua dependensi:**
   ```bash
   npm install
   ```
4. **Setup Database di Supabase:**
   - Buat project baru di Supabase.
   - Buka SQL Editor di dashboard Supabase.
   - Copy semua isi dari file `database.sql` dan jalankan query tersebut untuk membuat tabel-tabel yang diperlukan.
5. **Setup Environment Variables:**
   - Copy file `.env.example` dan ubah namanya menjadi `.env`.
   - Buka `.env` dan isi sesuai dengan konfigurasi Anda:
     ```env
     PORT=5000
     DATABASE_URL=postgresql://postgres:[PASSWORD]@db.[PROJECT_REF].supabase.co:5432/postgres
     JWT_SECRET=your_super_secret_jwt_key_here
     ```
6. **Jalankan server (Development Mode):**
   ```bash
   npm run dev
   ```
7. **Jalankan server (Production Mode):**
   ```bash
   npm start
   ```

## Contoh Request dan Response API

### 1. Register
**Request:**
`POST /api/auth/register`
```json
{
  "fullname": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "08123456789"
}
```
**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "uuid-here",
    "fullname": "John Doe",
    "email": "john@example.com",
    "phone": "08123456789",
    "avatar_url": null,
    "created_at": "timestamp"
  }
}
```

### 2. Login
**Request:**
`POST /api/auth/login`
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "jwt-token-string",
    "user": { ... }
  }
}
```

### 3. Get All Foods
**Request:**
`GET /api/foods`

**Response:**
```json
{
  "success": true,
  "message": "Foods retrieved successfully",
  "data": [
    {
      "id": "uuid",
      "name": "Nasi Goreng",
      "price": "20000",
      "category": "Main Course",
      "stock": 50,
      "rating": "4.5"
    }
  ]
}
```

### 4. Checkout Order (Membutuhkan Bearer Token)
**Request:**
`POST /api/orders/checkout`
```json
{
  "delivery_address": "Jl. Mawar No. 12",
  "payment_method": "Cash on Delivery"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Checkout successful",
  "data": {
    "id": "order-uuid",
    "total_price": "50000",
    "status": "pending"
  }
}
```

## Struktur Direktori
Lihat `implementation_plan.md` untuk gambaran struktur folder lengkap. File upload disimpan di `src/uploads/`.
