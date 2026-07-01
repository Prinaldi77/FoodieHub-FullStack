package com.codelamps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codelamps.R
import com.codelamps.network.MealItem

/**
 * Adapter untuk menampilkan daftar inspirasi kuliner dari API Publik TheMealDB.
 * Menggunakan Glide untuk memuat gambar dari URL (network image loading).
 * Data diambil secara real-time dari: https://www.themealdb.com/api/json/v1/1/
 */
class MealInspirationAdapter(
    private var meals: List<MealItem>,
    private val onItemClick: (MealItem) -> Unit
) : RecyclerView.Adapter<MealInspirationAdapter.MealViewHolder>() {

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMealImage: ImageView = view.findViewById(R.id.iv_meal_image)
        val tvMealName: TextView = view.findViewById(R.id.tv_meal_name)
        val tvMealCategory: TextView = view.findViewById(R.id.tv_meal_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meal_inspiration_item, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]

        // Tampilkan nama makanan
        holder.tvMealName.text = meal.name

        // Tampilkan kategori dengan emoji
        val categoryEmoji = when (meal.category?.lowercase()) {
            "beef"     -> "🥩"
            "chicken"  -> "🍗"
            "seafood"  -> "🦐"
            "vegetarian", "vegan" -> "🥗"
            "pasta"    -> "🍝"
            "dessert"  -> "🍮"
            "breakfast"-> "🍳"
            "lamb"     -> "🍖"
            else       -> "🍽️"
        }
        holder.tvMealCategory.text = "$categoryEmoji ${meal.category ?: "Kuliner"}"

        // Load gambar dari URL menggunakan Glide (library sudah ada di project)
        Glide.with(holder.itemView.context)
            .load(meal.imageUrl)
            .placeholder(R.drawable.food)
            .error(R.drawable.food)
            .centerCrop()
            .into(holder.ivMealImage)

        // Click listener
        holder.itemView.setOnClickListener {
            onItemClick(meal)
        }
    }

    override fun getItemCount(): Int = meals.size

    /** Update data dari API response baru */
    fun updateData(newMeals: List<MealItem>) {
        meals = newMeals
        notifyDataSetChanged()
    }
}
