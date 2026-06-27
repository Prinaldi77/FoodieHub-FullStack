package com.codelamps.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codelamps.LoginActivity
import com.codelamps.PaymentWebViewActivity
import com.codelamps.R
import com.codelamps.adapter.OrderHistoryAdapter
import com.codelamps.network.OrderDetailResponse
import com.codelamps.network.OrderHistoryResponse
import com.codelamps.network.OrderSummaryData
import com.codelamps.network.RetrofitClient
import com.codelamps.network.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var historyProgress: ProgressBar
    private lateinit var tvEmptyHistory: TextView
    private lateinit var sessionManager: SessionManager
    private var adapter: OrderHistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        rvOrderHistory = view.findViewById(R.id.rv_order_history)
        historyProgress = view.findViewById(R.id.history_progress)
        tvEmptyHistory = view.findViewById(R.id.tv_empty_history)

        sessionManager = SessionManager(requireContext())

        rvOrderHistory.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onResume() {
        super.onResume()
        loadOrderHistory()
    }

    private fun loadOrderHistory() {
        if (!sessionManager.isLoggedIn()) {
            tvEmptyHistory.text = "Silakan login untuk melihat riwayat pesanan"
            tvEmptyHistory.visibility = View.VISIBLE
            rvOrderHistory.visibility = View.GONE
            return
        }

        historyProgress.visibility = View.VISIBLE
        tvEmptyHistory.visibility = View.GONE
        rvOrderHistory.visibility = View.GONE

        val apiService = RetrofitClient.getClient(requireContext())
        apiService.getOrderHistory().enqueue(object : Callback<OrderHistoryResponse> {
            override fun onResponse(call: Call<OrderHistoryResponse>, response: Response<OrderHistoryResponse>) {
                historyProgress.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val orders = response.body()?.data ?: emptyList()
                    if (orders.isEmpty()) {
                        tvEmptyHistory.text = "Belum ada riwayat pesanan"
                        tvEmptyHistory.visibility = View.VISIBLE
                        rvOrderHistory.visibility = View.GONE
                    } else {
                        tvEmptyHistory.visibility = View.GONE
                        rvOrderHistory.visibility = View.VISIBLE
                        
                        setupAdapter(orders)
                    }
                } else {
                    tvEmptyHistory.text = "Gagal mengambil riwayat pesanan"
                    tvEmptyHistory.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<OrderHistoryResponse>, t: Throwable) {
                historyProgress.visibility = View.GONE
                tvEmptyHistory.text = "Kesalahan jaringan: " + t.message
                tvEmptyHistory.visibility = View.VISIBLE
            }
        })
    }

    private fun setupAdapter(orders: List<OrderSummaryData>) {
        if (adapter == null) {
            adapter = OrderHistoryAdapter(orders) { order ->
                checkOrderStatus(order)
            }
            rvOrderHistory.adapter = adapter
        } else {
            adapter?.updateData(orders)
        }
    }

    private fun checkOrderStatus(order: OrderSummaryData) {
        historyProgress.visibility = View.VISIBLE
        val apiService = RetrofitClient.getClient(requireContext())
        apiService.getOrderById(order.id).enqueue(object : Callback<OrderDetailResponse> {
            override fun onResponse(call: Call<OrderDetailResponse>, response: Response<OrderDetailResponse>) {
                historyProgress.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val orderDetails = response.body()?.data
                    if (orderDetails != null) {
                        val currentStatus = orderDetails.status
                        
                        if (currentStatus.lowercase() != order.status.lowercase()) {
                            Toast.makeText(requireContext(), "Status pesanan diperbarui dari " + order.status + " ke " + currentStatus + "!", Toast.LENGTH_LONG).show()
                            loadOrderHistory() // Reload to refresh list visual
                        } else {
                            if (currentStatus.lowercase() == "pending" && !orderDetails.snapUrl.isNullOrEmpty()) {
                                // Show option to repay
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Pesanan Belum Dibayar")
                                    .setMessage("Status pesanan masih pending. Apakah Anda ingin melanjutkan pembayaran sekarang?")
                                    .setPositiveButton("Bayar") { _, _ ->
                                        val intent = Intent(requireActivity(), PaymentWebViewActivity::class.java)
                                        intent.putExtra(PaymentWebViewActivity.EXTRA_SNAP_URL, orderDetails.snapUrl)
                                        startActivity(intent)
                                    }
                                    .setNegativeButton("Tutup", null)
                                    .show()
                            } else {
                                Toast.makeText(requireContext(), "Status pesanan masih: " + currentStatus.uppercase(), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal memeriksa status: " + response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                historyProgress.visibility = View.GONE
                Toast.makeText(requireContext(), "Kesalahan jaringan status: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
