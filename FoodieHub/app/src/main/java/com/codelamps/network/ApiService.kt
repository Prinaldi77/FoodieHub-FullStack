package com.codelamps.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/orders/checkout")
    fun checkout(@Body request: CheckoutRequest): Call<CheckoutResponse>

    @GET("api/orders/{id}")
    fun getOrderById(@Path("id") orderId: String): Call<OrderDetailResponse>

    @GET("api/orders/history")
    fun getOrderHistory(): Call<OrderHistoryResponse>

    @GET("api/cart")
    fun getCart(): Call<CartResponse>

    @PUT("api/cart/update")
    fun updateCartItem(@Body request: UpdateCartRequest): Call<CartActionResponse>

    @DELETE("api/cart/{id}")
    fun deleteCartItem(@Path("id") cartItemId: String): Call<CartActionResponse>

    @POST("api/cart/add")
    fun addToCart(@Body request: AddToCartRequest): Call<CartActionResponse>

    @GET("api/foods")
    fun getAllFoods(): Call<FoodsResponse>

    @GET("api/foods/search")
    fun searchFoods(@retrofit2.http.Query("q") query: String): Call<FoodsResponse>
}
