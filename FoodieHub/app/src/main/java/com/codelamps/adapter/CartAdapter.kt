package com.codelamps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codelamps.R
import com.codelamps.databinding.CartItemBinding
import com.codelamps.network.CartActionResponse
import com.codelamps.network.CartItemData
import com.codelamps.network.RetrofitClient
import com.codelamps.network.UpdateCartRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private var cartItems: MutableList<CartItemData>,
    private val onItemDeletedListener: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    fun updateData(newCartItems: List<CartItemData>) {
        cartItems.clear()
        cartItems.addAll(newCartItems)
        notifyDataSetChanged()
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(position: Int) {
            val item = cartItems[position]
            binding.apply {
                cartTitle.text = item.name
                
                val cleanPrice = item.price.substringBefore(".")
                priceItem.text = "Rp " + cleanPrice
                
                countCart.text = item.qty.toString()

                // Load image using Glide
                if (!item.imageUrl.isNullOrEmpty()) {
                    Glide.with(itemView.context)
                        .load(item.imageUrl)
                        .placeholder(R.drawable.menu)
                        .error(R.drawable.menu)
                        .into(imageCart)
                } else {
                    imageCart.setImageResource(R.drawable.menu)
                }

                buttonCartMinus.setOnClickListener {
                    decreaseQuantity(position, item)
                }

                buttonCartPlus.setOnClickListener {
                    increaseQuantity(position, item)
                }

                deleteCart.setOnClickListener {
                    deleteItem(position, item)
                }
            }
        }

        private fun decreaseQuantity(position: Int, item: CartItemData) {
            if (item.qty > 1) {
                updateQuantityOnBackend(item.id, item.qty - 1)
            }
        }

        private fun increaseQuantity(position: Int, item: CartItemData) {
            if (item.qty < 10) {
                updateQuantityOnBackend(item.id, item.qty + 1)
            }
        }

        private fun updateQuantityOnBackend(cartItemId: String, newQty: Int) {
            val context = itemView.context
            val apiService = RetrofitClient.getClient(context)
            apiService.updateCartItem(UpdateCartRequest(cartItemId, newQty)).enqueue(object : Callback<CartActionResponse> {
                override fun onResponse(call: Call<CartActionResponse>, response: Response<CartActionResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        onItemDeletedListener() // Trigger parent to reload cart items
                    } else {
                        Toast.makeText(context, "Gagal mengubah jumlah barang", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CartActionResponse>, t: Throwable) {
                    Toast.makeText(context, "Kesalahan jaringan: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

        private fun deleteItem(position: Int, item: CartItemData) {
            val context = itemView.context
            val apiService = RetrofitClient.getClient(context)
            apiService.deleteCartItem(item.id).enqueue(object : Callback<CartActionResponse> {
                override fun onResponse(call: Call<CartActionResponse>, response: Response<CartActionResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(context, item.name + " dihapus dari keranjang", Toast.LENGTH_SHORT).show()
                        onItemDeletedListener() // Trigger parent to reload
                    } else {
                        Toast.makeText(context, "Gagal menghapus barang", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CartActionResponse>, t: Throwable) {
                    Toast.makeText(context, "Kesalahan jaringan: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
