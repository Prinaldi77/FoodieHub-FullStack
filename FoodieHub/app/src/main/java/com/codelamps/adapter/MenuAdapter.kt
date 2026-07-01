package com.codelamps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codelamps.R
import com.codelamps.databinding.MenuItemBinding
import com.codelamps.network.AddToCartRequest
import com.codelamps.network.CartActionResponse
import com.codelamps.network.FoodData
import com.codelamps.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuAdapter(private var foods: List<FoodData>) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: FoodData) {
            binding.apply {
                menuFoodName.text = food.name
                
                val cleanPrice = food.price.substringBefore(".")
                menuPrice.text = "Rp " + cleanPrice

                if (!food.imageUrl.isNullOrEmpty()) {
                    Glide.with(itemView.context)
                        .load(food.imageUrl)
                        .placeholder(R.drawable.menu)
                        .error(R.drawable.menu)
                        .into(menuImage)
                } else {
                    menuImage.setImageResource(R.drawable.menu)
                }

                // Tampilkan Detail Makanan ketika kartu item diklik
                itemView.setOnClickListener {
                    com.codelamps.utils.FoodDetailDialog.show(itemView.context, food)
                }

                addToCart.setOnClickListener {
                    addToCart.isEnabled = false
                    val apiService = RetrofitClient.getClient(itemView.context)
                    apiService.addToCart(AddToCartRequest(food.id, 1)).enqueue(object : Callback<CartActionResponse> {
                        override fun onResponse(call: Call<CartActionResponse>, response: Response<CartActionResponse>) {
                            addToCart.isEnabled = true
                            if (response.isSuccessful && response.body()?.success == true) {
                                Toast.makeText(itemView.context, food.name + " berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(itemView.context, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<CartActionResponse>, t: Throwable) {
                            addToCart.isEnabled = true
                            Toast.makeText(itemView.context, "Kesalahan jaringan: " + t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int = foods.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    fun updateData(newFoods: List<FoodData>) {
        foods = newFoods
        notifyDataSetChanged()
    }
}
