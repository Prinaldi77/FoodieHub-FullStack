package com.codelamps.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ──────────────────────────────────────────────────────────────────────
    // BASE URL BACKEND
    // Untuk EMULATOR Android  → gunakan: "http://10.0.2.2:5000/"
    // Untuk HP FISIK (WiFi)   → gunakan: IP komputer di jaringan yang sama
    // IP komputer saat ini    : 10.12.14.100 (cek via: ipconfig di CMD)
    // ⚠️  HP fisik & komputer HARUS terhubung ke WiFi yang SAMA
    // ──────────────────────────────────────────────────────────────────────
    private const val BASE_URL = "http://10.12.14.100:5000/"

    // URL API Publik OpenStreetMap Nominatim (reverse geocoding GPS)
    private const val NOMINATIM_URL = "https://nominatim.openstreetmap.org/"

    // URL API Publik TheMealDB (inspirasi kuliner di halaman Home)
    private const val MEALDB_URL = "https://www.themealdb.com/api/json/v1/1/"

    private var retrofit: Retrofit? = null
    private var nominatimRetrofit: Retrofit? = null
    private var mealDbRetrofit: Retrofit? = null

    /**
     * Retrofit client untuk backend pribadi (dengan token auth)
     */
    fun getClient(context: Context): ApiService {
        if (retrofit == null) {
            val sessionManager = SessionManager(context)

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                    sessionManager.fetchAuthToken()?.let { token ->
                        requestBuilder.addHeader("Authorization", "Bearer $token")
                    }
                    chain.proceed(requestBuilder.build())
                }
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }

    /**
     * Retrofit client untuk API Publik Nominatim (tanpa auth token)
     * Digunakan untuk reverse geocoding: koordinat GPS → nama alamat
     */
    fun getNominatimClient(): NominatimApiService {
        if (nominatimRetrofit == null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            nominatimRetrofit = Retrofit.Builder()
                .baseUrl(NOMINATIM_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return nominatimRetrofit!!.create(NominatimApiService::class.java)
    }

    /**
     * Retrofit client untuk API Publik TheMealDB (tanpa auth token)
     * Digunakan untuk menampilkan inspirasi kuliner di HomeFragment
     * Endpoint: https://www.themealdb.com/api/json/v1/1/
     */
    fun getMealDbClient(): TheMealDbApiService {
        if (mealDbRetrofit == null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            mealDbRetrofit = Retrofit.Builder()
                .baseUrl(MEALDB_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return mealDbRetrofit!!.create(TheMealDbApiService::class.java)
    }
}
