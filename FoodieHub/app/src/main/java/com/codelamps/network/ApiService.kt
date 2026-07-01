package com.codelamps.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ── AUTH ──────────────────────────────────────────────────────────
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    // ── CART ──────────────────────────────────────────────────────────
    @GET("api/cart")
    fun getCart(): Call<CartResponse>

    @POST("api/cart/add")
    fun addToCart(@Body request: AddToCartRequest): Call<CartActionResponse>

    @PUT("api/cart/update")
    fun updateCartItem(@Body request: UpdateCartRequest): Call<CartActionResponse>

    @DELETE("api/cart/{id}")
    fun deleteCartItem(@Path("id") cartItemId: String): Call<CartActionResponse>

    // ── ORDERS ────────────────────────────────────────────────────────
    @POST("api/orders/checkout")
    fun checkout(@Body request: CheckoutRequest): Call<CheckoutResponse>

    @GET("api/orders/history")
    fun getOrderHistory(): Call<OrderHistoryResponse>

    @GET("api/orders/{id}")
    fun getOrderById(@Path("id") orderId: String): Call<OrderDetailResponse>

    // ── FOODS ─────────────────────────────────────────────────────────
    @GET("api/foods")
    fun getAllFoods(): Call<FoodsResponse>

    @GET("api/foods/search")
    fun searchFoods(@Query("q") query: String): Call<FoodsResponse>

    // ─── ADMIN: KELOLA MENU MAKANAN ──────────────────────────────────────
    @POST("api/foods")
    fun createFood(@Body body: HashMap<String, Any>): Call<FoodsResponse>

    @PUT("api/foods/{id}")
    fun updateFood(@Path("id") id: String, @Body body: HashMap<String, Any>): Call<FoodsResponse>

    @DELETE("api/foods/{id}")
    fun deleteFood(@Path("id") id: String): Call<FoodsResponse>

    // ─── ADMIN: KELOLA PESANAN CUSTOMER ──────────────────────────────────
    @GET("api/orders/all")
    fun getAllOrders(): Call<OrderHistoryResponse>

    @PUT("api/orders/{id}/status")
    fun updateOrderStatus(@Path("id") id: String, @Body body: HashMap<String, String>): Call<OrderDetailResponse>
}

// ============================================================
// INTERFACE TERPISAH untuk API PUBLIK OpenStreetMap Nominatim
// Base URL: https://nominatim.openstreetmap.org/
// Tujuan  : Reverse geocoding (GPS koordinat → alamat teks)
// Bebas digunakan (Public API, gratis, tidak perlu API key)
// ============================================================
interface NominatimApiService {

    @Headers(
        "User-Agent: BaksoYDTQA-AndroidApp",
        "Accept-Language: id"
    )
    @GET("reverse")
    fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json"
    ): Call<GeocodeResponse>
}

// ============================================================
// INTERFACE TERPISAH untuk API PUBLIK TheMealDB
// Base URL: https://www.themealdb.com/api/json/v1/1/
// Tujuan  : Menampilkan inspirasi kuliner di halaman beranda
// Bebas digunakan (Public API, gratis, tanpa API key)
// ============================================================
interface TheMealDbApiService {

    /** Ambil semua kategori makanan (Beef, Chicken, Seafood, dst) */
    @GET("categories.php")
    fun getCategories(): Call<MealCategoryResponse>

    /** Cari makanan berdasarkan nama (mis: "chicken", "beef") */
    @GET("search.php")
    fun searchMeals(@Query("s") query: String): Call<MealResponse>

    /** Filter makanan berdasarkan kategori */
    @GET("filter.php")
    fun getMealsByCategory(@Query("c") category: String): Call<MealResponse>

    /** Ambil 1 makanan acak untuk banner inspirasi */
    @GET("random.php")
    fun getRandomMeal(): Call<MealResponse>
}
