package com.codelamps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codelamps.R
import com.codelamps.network.FoodData

class ManageMenuAdapter(
    private var foods: List<FoodData>,
    private val onEditClick: (FoodData) -> Unit,
    private val onDeleteClick: (FoodData) -> Unit
) : RecyclerView.Adapter<ManageMenuAdapter.ManageViewHolder>() {

    class ManageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivManageImage: ImageView = view.findViewById(R.id.iv_manage_image)
        val tvManageName: TextView = view.findViewById(R.id.tv_manage_name)
        val tvManagePrice: TextView = view.findViewById(R.id.tv_manage_price)
        val tvManageStock: TextView = view.findViewById(R.id.tv_manage_stock)
        val btnEditMenu: ImageView = view.findViewById(R.id.btn_edit_menu)
        val btnDeleteMenu: ImageView = view.findViewById(R.id.btn_delete_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manage_menu_item, parent, false)
        return ManageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        val food = foods[position]

        holder.tvManageName.text = food.name
        val cleanPrice = food.price.substringBefore(".")
        holder.tvManagePrice.text = "Rp $cleanPrice"
        holder.tvManageStock.text = "Stok: ${food.stock}"

        if (!food.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(food.imageUrl)
                .placeholder(R.drawable.menu)
                .error(R.drawable.menu)
                .centerCrop()
                .into(holder.ivManageImage)
        } else {
            holder.ivManageImage.setImageResource(R.drawable.menu)
        }

        // Action edit
        holder.btnEditMenu.setOnClickListener {
            onEditClick(food)
        }

        // Action delete
        holder.btnDeleteMenu.setOnClickListener {
            onDeleteClick(food)
        }
    }

    override fun getItemCount(): Int = foods.size

    fun updateData(newFoods: List<FoodData>) {
        foods = newFoods
        notifyDataSetChanged()
    }
}
