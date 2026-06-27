package com.codelamps.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codelamps.R
import com.codelamps.network.OrderSummaryData

class OrderHistoryAdapter(
    private var orders: List<OrderSummaryData>,
    private val onCheckStatusClickListener: (OrderSummaryData) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tv_order_id)
        val tvOrderDate: TextView = view.findViewById(R.id.tv_order_date)
        val tvOrderAddress: TextView = view.findViewById(R.id.tv_order_address)
        val tvOrderPrice: TextView = view.findViewById(R.id.tv_order_price)
        val tvOrderStatus: TextView = view.findViewById(R.id.tv_order_status)
        val btnCekStatus: Button = view.findViewById(R.id.btn_cek_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "Order ID: #" + order.id.take(8)
        holder.tvOrderDate.text = "Tanggal: " + order.createdAt.take(10)
        holder.tvOrderAddress.text = "Alamat: " + order.deliveryAddress
        
        // Format price to clear decimals if present (e.g. 45000.00 -> 45000)
        val formattedPrice = order.totalPrice.substringBefore(".")
        holder.tvOrderPrice.text = "Total: Rp " + formattedPrice
        
        holder.tvOrderStatus.text = order.status.uppercase()
        
        // Set background color of status badges
        when (order.status.lowercase()) {
            "pending" -> {
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#FF9800")) // Orange
                holder.tvOrderStatus.setTextColor(Color.WHITE)
            }
            "processing" -> {
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                holder.tvOrderStatus.setTextColor(Color.WHITE)
            }
            "cancelled" -> {
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#F44336")) // Red
                holder.tvOrderStatus.setTextColor(Color.WHITE)
            }
            else -> {
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#9E9E9E")) // Grey
                holder.tvOrderStatus.setTextColor(Color.WHITE)
            }
        }

        holder.btnCekStatus.setOnClickListener {
            onCheckStatusClickListener(order)
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<OrderSummaryData>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
