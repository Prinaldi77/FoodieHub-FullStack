package com.codelamps.network

import com.google.gson.annotations.SerializedName

// Login Models
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: LoginData?
)

data class LoginData(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserData
)

data class UserData(
    @SerializedName("id") val id: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)

// Register Models
data class RegisterRequest(
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("phone") val phone: String
)

data class RegisterResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: RegisterData?
)

data class RegisterData(
    @SerializedName("id") val id: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("created_at") val createdAt: String
)

// Checkout Models
data class CheckoutRequest(
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("payment_method") val paymentMethod: String = "Midtrans"
)

data class CheckoutResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: CheckoutData?
)

data class CheckoutData(
    @SerializedName("id") val id: String,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("status") val status: String,
    @SerializedName("snap_token") val snapToken: String?,
    @SerializedName("snap_url") val snapUrl: String?
)

// Order History & Detail Models
data class OrderHistoryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<OrderSummaryData>?
)

data class OrderSummaryData(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("total_price") val totalPrice: String,
    @SerializedName("status") val status: String,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("snap_token") val snapToken: String?,
    @SerializedName("snap_url") val snapUrl: String?,
    @SerializedName("created_at") val createdAt: String
)

data class OrderDetailResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: OrderDetailData?
)

data class OrderDetailData(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("total_price") val totalPrice: String,
    @SerializedName("status") val status: String,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("snap_token") val snapToken: String?,
    @SerializedName("snap_url") val snapUrl: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("items") val items: List<OrderItemData>
)

data class OrderItemData(
    @SerializedName("id") val id: String,
    @SerializedName("food_id") val foodId: String,
    @SerializedName("qty") val qty: Int,
    @SerializedName("price") val price: String,
    @SerializedName("subtotal") val subtotal: String,
    @SerializedName("name") val name: String?,
    @SerializedName("image_url") val imageUrl: String?
)

// Add Cart & Food Models
data class CartResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: CartDetailsData?
)

data class CartDetailsData(
    @SerializedName("cart_id") val cartId: String,
    @SerializedName("items") val items: List<CartItemData>?,
    @SerializedName("total") val total: Double
)

data class CartItemData(
    @SerializedName("id") val id: String,
    @SerializedName("cart_id") val cartId: String,
    @SerializedName("food_id") val foodId: String,
    @SerializedName("qty") val qty: Int,
    @SerializedName("subtotal") val subtotal: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: String,
    @SerializedName("image_url") val imageUrl: String?
)

data class UpdateCartRequest(
    @SerializedName("cart_item_id") val cartItemId: String,
    @SerializedName("qty") val qty: Int
)

data class AddToCartRequest(
    @SerializedName("food_id") val foodId: String,
    @SerializedName("qty") val qty: Int = 1
)

data class CartActionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class FoodsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<FoodData>?
)

data class FoodData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String?
)

// ============================================================
// API PUBLIK: OpenStreetMap Nominatim Reverse Geocoding
// Digunakan untuk konversi koordinat GPS -> nama alamat lengkap
// Endpoint: https://nominatim.openstreetmap.org/reverse
// ============================================================
data class GeocodeResponse(
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("address") val address: GeocodeAddress?
)

data class GeocodeAddress(
    @SerializedName("road") val road: String?,
    @SerializedName("suburb") val suburb: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("postcode") val postcode: String?,
    @SerializedName("country") val country: String?
)
