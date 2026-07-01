package com.codelamps.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.codelamps.R
import com.codelamps.network.AddToCartRequest
import com.codelamps.network.CartActionResponse
import com.codelamps.network.FoodData
import com.codelamps.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FoodDetailDialog {

    /**
     * Menampilkan BottomSheet Dialog yang berisi detail menu makanan secara real-time dari database.
     */
    fun show(context: Context, food: FoodData) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_food_detail, null)
        dialog.setContentView(view)

        val ivDetailImage = view.findViewById<ImageView>(R.id.iv_detail_image)
        val tvDetailName = view.findViewById<TextView>(R.id.tv_detail_name)
        val tvDetailPrice = view.findViewById<TextView>(R.id.tv_detail_price)
        val tvDetailDescription = view.findViewById<TextView>(R.id.tv_detail_description)
        val btnAddToCart = view.findViewById<Button>(R.id.btn_detail_add_to_cart)

        // Set Data
        tvDetailName.text = food.name
        val cleanPrice = food.price.substringBefore(".")
        tvDetailPrice.text = "Rp $cleanPrice"
        
        // Deskripsi dinamis diambil langsung dari database
        tvDetailDescription.text = if (!food.description.isNullOrEmpty()) {
            food.description
        } else {
            "Tidak ada deskripsi untuk menu ini."
        }

        // Load gambar
        if (!food.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(food.imageUrl)
                .placeholder(R.drawable.menu)
                .error(R.drawable.menu)
                .into(ivDetailImage)
        } else {
            ivDetailImage.setImageResource(R.drawable.menu)
        }

        // Tombol tambah ke keranjang
        btnAddToCart.setOnClickListener {
            btnAddToCart.isEnabled = false
            val apiService = RetrofitClient.getClient(context)
            apiService.addToCart(AddToCartRequest(food.id, 1))
                .enqueue(object : Callback<CartActionResponse> {
                    override fun onResponse(
                        call: Call<CartActionResponse>,
                        response: Response<CartActionResponse>
                    ) {
                        btnAddToCart.isEnabled = true
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(
                                context,
                                "${food.name} berhasil ditambahkan ke keranjang!",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(context, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<CartActionResponse>, t: Throwable) {
                        btnAddToCart.isEnabled = true
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        dialog.show()
    }
}
