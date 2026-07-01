package com.codelamps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codelamps.R
import com.codelamps.databinding.PopulerItemBinding
import com.codelamps.network.AddToCartRequest
import com.codelamps.network.CartActionResponse
import com.codelamps.network.FoodData
import com.codelamps.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopularAdapter(private var foods: List<FoodData>) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    
    class PopularViewHolder(val binding: PopulerItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = PopulerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularViewHolder(binding)
    }

    override fun getItemCount(): Int = foods.size

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val food = foods[position]
        holder.binding.foodProduct.text = food.name
        
        val cleanPrice = food.price.substringBefore(".")
        holder.binding.priceItem.text = "Rp " + cleanPrice
        
        if (!food.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(food.imageUrl)
                .placeholder(R.drawable.menu)
                .error(R.drawable.menu)
                .into(holder.binding.imageItem)
        } else {
            holder.binding.imageItem.setImageResource(R.drawable.menu)
        }

        // Tampilkan Detail Makanan ketika kartu item diklik
        holder.itemView.setOnClickListener {
            com.codelamps.utils.FoodDetailDialog.show(holder.itemView.context, food)
        }

        holder.binding.addToCart.setOnClickListener {
            holder.binding.addToCart.isEnabled = false
            val apiService = RetrofitClient.getClient(holder.itemView.context)
            apiService.addToCart(AddToCartRequest(food.id, 1)).enqueue(object : Callback<CartActionResponse> {
                override fun onResponse(call: Call<CartActionResponse>, response: Response<CartActionResponse>) {
                    holder.binding.addToCart.isEnabled = true
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(holder.itemView.context, food.name + " berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CartActionResponse>, t: Throwable) {
                    holder.binding.addToCart.isEnabled = true
                    Toast.makeText(holder.itemView.context, "Kesalahan jaringan: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun updateData(newFoods: List<FoodData>) {
        foods = newFoods
        notifyDataSetChanged()
    }
}
