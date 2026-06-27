package com.codelamps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codelamps.R
import com.codelamps.network.AddToCartRequest
import com.codelamps.network.CartActionResponse
import com.codelamps.network.FoodData
import com.codelamps.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodSearchAdapter(
    private var foods: List<FoodData>
) : RecyclerView.Adapter<FoodSearchAdapter.FoodViewHolder>() {

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val menuImage: ImageView = view.findViewById(R.id.menu_image)
        val menuFoodName: TextView = view.findViewById(R.id.menu_food_name)
        val menuPrice: TextView = view.findViewById(R.id.menu_price)
        val addToCart: TextView = view.findViewById(R.id.addToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        holder.menuFoodName.text = food.name
        
        // Clean price decimal (e.g. 5000.00 -> 5000)
        val cleanPrice = food.price.substringBefore(".")
        holder.menuPrice.text = "Rp " + cleanPrice
        
        if (!food.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(food.imageUrl)
                .placeholder(R.drawable.menu)
                .error(R.drawable.menu)
                .into(holder.menuImage)
        } else {
            holder.menuImage.setImageResource(R.drawable.menu)
        }

        holder.addToCart.setOnClickListener {
            holder.addToCart.isEnabled = false
            val apiService = RetrofitClient.getClient(holder.itemView.context)
            apiService.addToCart(AddToCartRequest(food.id, 1)).enqueue(object : Callback<CartActionResponse> {
                override fun onResponse(call: Call<CartActionResponse>, response: Response<CartActionResponse>) {
                    holder.addToCart.isEnabled = true
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(holder.itemView.context, food.name + " berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CartActionResponse>, t: Throwable) {
                    holder.addToCart.isEnabled = true
                    Toast.makeText(holder.itemView.context, "Kesalahan jaringan: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun getItemCount(): Int = foods.size

    fun updateData(newFoods: List<FoodData>) {
        foods = newFoods
        notifyDataSetChanged()
    }
}
