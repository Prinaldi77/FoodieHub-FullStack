package com.codelamps

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codelamps.adapter.AdminOrderAdapter
import com.codelamps.network.OrderDetailResponse
import com.codelamps.network.OrderHistoryResponse
import com.codelamps.network.OrderSummaryData
import com.codelamps.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderActivity : AppCompatActivity() {

    private lateinit var rvAdminOrders: RecyclerView
    private lateinit var pbAdminOrder: ProgressBar
    private var adapter: AdminOrderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_order)

        rvAdminOrders = findViewById(R.id.rv_admin_orders)
        pbAdminOrder = findViewById(R.id.pb_admin_order)
        val btnBack = findViewById<ImageView>(R.id.btn_back_order)

        rvAdminOrders.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        loadAllOrders()
    }

    private fun loadAllOrders() {
        pbAdminOrder.visibility = View.VISIBLE
        val apiService = RetrofitClient.getClient(this)
        apiService.getAllOrders().enqueue(object : Callback<OrderHistoryResponse> {
            override fun onResponse(call: Call<OrderHistoryResponse>, response: Response<OrderHistoryResponse>) {
                pbAdminOrder.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val orders = response.body()?.data ?: emptyList()
                    setupAdapter(orders)
                } else {
                    Toast.makeText(this@AdminOrderActivity, "Gagal mengambil daftar pesanan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderHistoryResponse>, t: Throwable) {
                pbAdminOrder.visibility = View.GONE
                Toast.makeText(this@AdminOrderActivity, "Kesalahan jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAdapter(orders: List<OrderSummaryData>) {
        adapter = AdminOrderAdapter(orders) { order ->
            // Tampilkan pilihan status baru
            val statusOptions = arrayOf("pending", "processing", "shipped", "delivered", "cancelled")
            AlertDialog.Builder(this)
                .setTitle("Ubah Status Pesanan")
                .setItems(statusOptions) { _, which ->
                    val selectedStatus = statusOptions[which]
                    updateOrderStatus(order.id, selectedStatus)
                }
                .show()
        }
        rvAdminOrders.adapter = adapter
    }

    private fun updateOrderStatus(id: String, newStatus: String) {
        pbAdminOrder.visibility = View.VISIBLE
        
        val body = HashMap<String, String>().apply {
            put("status", newStatus)
        }

        val apiService = RetrofitClient.getClient(this)
        apiService.updateOrderStatus(id, body).enqueue(object : Callback<OrderDetailResponse> {
            override fun onResponse(call: Call<OrderDetailResponse>, response: Response<OrderDetailResponse>) {
                pbAdminOrder.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@AdminOrderActivity, "Status pesanan diperbarui!", Toast.LENGTH_SHORT).show()
                    loadAllOrders()
                } else {
                    Toast.makeText(this@AdminOrderActivity, "Gagal memperbarui status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                pbAdminOrder.visibility = View.GONE
                Toast.makeText(this@AdminOrderActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
