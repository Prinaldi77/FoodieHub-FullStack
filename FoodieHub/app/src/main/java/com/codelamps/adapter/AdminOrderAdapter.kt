package com.codelamps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codelamps.R
import com.codelamps.network.OrderSummaryData

class AdminOrderAdapter(
    private var orders: List<OrderSummaryData>,
    private val onUpdateStatusClick: (OrderSummaryData) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tv_admin_order_id)
        val tvOrderUser: TextView = view.findViewById(R.id.tv_admin_order_user)
        val tvOrderAddress: TextView = view.findViewById(R.id.tv_admin_order_address)
        val tvOrderTotal: TextView = view.findViewById(R.id.tv_admin_order_total)
        val tvOrderStatus: TextView = view.findViewById(R.id.tv_admin_order_status)
        val btnUpdateStatus: Button = view.findViewById(R.id.btn_update_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        // Tampilkan order ID pendek
        val shortId = if (order.id.length > 8) order.id.substring(0, 8) else order.id
        holder.tvOrderId.text = "Order #$shortId"

        // Nama & Email (karena kita JOIN user di backend, properti ini ada)
        holder.tvOrderUser.text = "Pelanggan: ${order.fullname ?: "Customer"}"
        
        holder.tvOrderAddress.text = "Alamat: ${order.deliveryAddress}"
        
        val cleanPrice = order.totalPrice.substringBefore(".")
        holder.tvOrderTotal.text = "Total: Rp $cleanPrice"

        // Format visual status
        holder.tvOrderStatus.text = order.status.uppercase()
        when (order.status.lowercase()) {
            "pending" -> {
                holder.tvOrderStatus.setTextColor(holder.itemView.context.getColor(R.color.maroon))
            }
            "processing" -> {
                holder.tvOrderStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_orange_dark))
            }
            "shipped" -> {
                holder.tvOrderStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_blue_dark))
            }
            "delivered" -> {
                holder.tvOrderStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
            }
            else -> {
                holder.tvOrderStatus.setTextColor(holder.itemView.context.getColor(android.R.color.darker_gray))
            }
        }

        holder.btnUpdateStatus.setOnClickListener {
            onUpdateStatusClick(order)
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<OrderSummaryData>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
