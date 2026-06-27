package com.codelamps.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codelamps.LoginActivity
import com.codelamps.PaymentWebViewActivity
import com.codelamps.R
import com.codelamps.adapter.CartAdapter
import com.codelamps.databinding.FragmentCartBinding
import com.codelamps.network.AddToCartRequest
import com.codelamps.network.CartActionResponse
import com.codelamps.network.CartItemData
import com.codelamps.network.CartResponse
import com.codelamps.network.CheckoutRequest
import com.codelamps.network.CheckoutResponse
import com.codelamps.network.FoodsResponse
import com.codelamps.network.RetrofitClient
import com.codelamps.network.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var sessionManager: SessionManager
    private var adapter: CartAdapter? = null
    private var cartItemsList = ArrayList<CartItemData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        // Initial empty adapter setup
        adapter = CartAdapter(cartItemsList) {
            loadCartItems() // Reload when items are updated/deleted
        }
        binding.CartRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.CartRecycleView.adapter = adapter

        binding.prosesCart.setOnClickListener {
            handleCheckoutFlow()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.isLoggedIn()) {
            loadCartItems()
        } else {
            cartItemsList.clear()
            adapter?.notifyDataSetChanged()
        }
    }

    private fun loadCartItems() {
        val apiService = RetrofitClient.getClient(requireContext())
        apiService.getCart().enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    val items = response.body()?.data?.items ?: emptyList()
                    cartItemsList.clear()
                    cartItemsList.addAll(items)
                    adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat keranjang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Kesalahan jaringan keranjang: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleCheckoutFlow() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            return
        }

        if (cartItemsList.isEmpty()) {
            // Cart is empty, let's proactively seed a food item to make it testable
            binding.prosesCart.isEnabled = false
            seedCartAndProceed()
        } else {
            showAddressDialog()
        }
    }

    private fun seedCartAndProceed() {
        val apiService = RetrofitClient.getClient(requireContext())
        // Get all foods from database to add one
        apiService.getAllFoods().enqueue(object : Callback<FoodsResponse> {
            override fun onResponse(call: Call<FoodsResponse>, response: Response<FoodsResponse>) {
                if (response.isSuccessful) {
                    val foods = response.body()?.data
                    if (!foods.isNullOrEmpty()) {
                        val foodId = foods[0].id
                        // Add first food to cart
                        apiService.addToCart(AddToCartRequest(foodId, 2)).enqueue(object : Callback<CartActionResponse> {
                            override fun onResponse(call: Call<CartActionResponse>, response: Response<CartActionResponse>) {
                                binding.prosesCart.isEnabled = true
                                if (response.isSuccessful && response.body()?.success == true) {
                                    Toast.makeText(requireContext(), "Menambahkan item demo ke keranjang", Toast.LENGTH_SHORT).show()
                                    loadCartItems() // Reload cart list
                                    showAddressDialog()
                                } else {
                                    Toast.makeText(requireContext(), "Gagal menambahkan item demo ke keranjang", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<CartActionResponse>, t: Throwable) {
                                binding.prosesCart.isEnabled = true
                                Toast.makeText(requireContext(), "Gagal menambahkan item demo: " + t.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        binding.prosesCart.isEnabled = true
                        Toast.makeText(requireContext(), "DB backend tidak memiliki data makanan. Silakan tambahkan makanan ke DB terlebih dahulu.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    binding.prosesCart.isEnabled = true
                    Toast.makeText(requireContext(), "Gagal mengambil data makanan: " + response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodsResponse>, t: Throwable) {
                binding.prosesCart.isEnabled = true
                Toast.makeText(requireContext(), "Kesalahan jaringan makanan: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddressDialog() {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alamat Pengiriman")

        val input = EditText(context)
        input.hint = "Masukkan alamat pengiriman lengkap..."
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        builder.setView(input)

        builder.setPositiveButton("Checkout") { dialog, _ ->
            val address = input.text.toString().trim()
            if (address.isEmpty()) {
                Toast.makeText(context, "Alamat tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            } else {
                performCheckout(address)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun performCheckout(address: String) {
        val apiService = RetrofitClient.getClient(requireContext())
        apiService.checkout(CheckoutRequest(address, "Midtrans")).enqueue(object : Callback<CheckoutResponse> {
            override fun onResponse(call: Call<CheckoutResponse>, response: Response<CheckoutResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val checkoutData = response.body()?.data
                    val snapUrl = checkoutData?.snapUrl
                    if (!snapUrl.isNullOrEmpty()) {
                        val intent = Intent(requireActivity(), PaymentWebViewActivity::class.java)
                        intent.putExtra(PaymentWebViewActivity.EXTRA_SNAP_URL, snapUrl)
                        startActivity(intent)
                        
                        // Navigate to HistoryFragment
                        findNavController().navigate(R.id.historyFragment)
                    } else {
                        Toast.makeText(requireContext(), "Pembayaran Midtrans offline: " + (response.body()?.message ?: ""), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Checkout gagal: " + (response.body()?.message ?: "Error"), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Kesalahan checkout: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
