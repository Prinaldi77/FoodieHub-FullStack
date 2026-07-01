package com.codelamps.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.codelamps.network.GeocodeResponse
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

    companion object {
        private const val LOCATION_PERMISSION_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        // Setup RecyclerView adapter
        adapter = CartAdapter(cartItemsList) {
            loadCartItems() // Reload ketika item diupdate/dihapus
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

    // ─── Muat item keranjang dari database backend ────────────────────────────
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
                Toast.makeText(requireContext(), "Kesalahan jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ─── Alur checkout: cek login & isi keranjang ─────────────────────────────
    private fun handleCheckoutFlow() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            return
        }

        if (cartItemsList.isEmpty()) {
            binding.prosesCart.isEnabled = false
            seedCartAndProceed()
        } else {
            showAddressDialog()
        }
    }

    // ─── Tambah item demo ke keranjang jika kosong ────────────────────────────
    private fun seedCartAndProceed() {
        val apiService = RetrofitClient.getClient(requireContext())
        apiService.getAllFoods().enqueue(object : Callback<FoodsResponse> {
            override fun onResponse(call: Call<FoodsResponse>, response: Response<FoodsResponse>) {
                if (response.isSuccessful) {
                    val foods = response.body()?.data
                    if (!foods.isNullOrEmpty()) {
                        apiService.addToCart(AddToCartRequest(foods[0].id, 1))
                            .enqueue(object : Callback<CartActionResponse> {
                                override fun onResponse(
                                    call: Call<CartActionResponse>,
                                    response: Response<CartActionResponse>
                                ) {
                                    binding.prosesCart.isEnabled = true
                                    if (response.isSuccessful && response.body()?.success == true) {
                                        Toast.makeText(requireContext(), "Item ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                                        loadCartItems()
                                        showAddressDialog()
                                    } else {
                                        Toast.makeText(requireContext(), "Gagal menambahkan item", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<CartActionResponse>, t: Throwable) {
                                    binding.prosesCart.isEnabled = true
                                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        binding.prosesCart.isEnabled = true
                        Toast.makeText(requireContext(), "Tidak ada data makanan di database", Toast.LENGTH_LONG).show()
                    }
                } else {
                    binding.prosesCart.isEnabled = true
                }
            }

            override fun onFailure(call: Call<FoodsResponse>, t: Throwable) {
                binding.prosesCart.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ─── Dialog alamat pengiriman dengan fitur GPS (Nominatim API Publik) ─────
    private fun showAddressDialog() {
        val context = requireContext()
        val dialogView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 20)
        }

        val inputAddress = EditText(context).apply {
            hint = "Masukkan alamat pengiriman lengkap..."
        }

        val progressBar = ProgressBar(context).apply {
            visibility = View.GONE
        }

        val tvGpsStatus = TextView(context).apply {
            text = "📍 Gunakan GPS untuk isi alamat otomatis"
            textSize = 12f
            setPadding(0, 8, 0, 0)
        }

        val btnGps = Button(context).apply {
            text = "📡 Deteksi Lokasi via GPS"
        }

        // Tombol GPS menggunakan OpenStreetMap Nominatim (API Publik)
        btnGps.setOnClickListener {
            requestGpsAndFillAddress(inputAddress, tvGpsStatus, progressBar)
        }

        dialogView.addView(inputAddress)
        dialogView.addView(tvGpsStatus)
        dialogView.addView(progressBar)
        dialogView.addView(btnGps)

        AlertDialog.Builder(context)
            .setTitle("📦 Alamat Pengiriman")
            .setView(dialogView)
            .setPositiveButton("Checkout") { dialog, _ ->
                val address = inputAddress.text.toString().trim()
                if (address.isEmpty()) {
                    Toast.makeText(context, "Alamat tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                } else {
                    performCheckout(address)
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }
            .show()
    }

    // ─── Minta izin GPS lalu panggil Nominatim API untuk reverse geocoding ────
    private fun requestGpsAndFillAddress(
        inputAddress: EditText,
        tvStatus: TextView,
        progressBar: ProgressBar
    ) {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
            Toast.makeText(requireContext(), "Izin GPS diperlukan", Toast.LENGTH_SHORT).show()
            return
        }

        val locationManager = requireContext().getSystemService(android.content.Context.LOCATION_SERVICE)
                as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(requireContext(), "Aktifkan GPS di pengaturan HP Anda", Toast.LENGTH_LONG).show()
            return
        }

        tvStatus.text = "🔄 Mendeteksi lokasi Anda..."
        progressBar.visibility = View.VISIBLE

        try {
            val provider = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER

            locationManager.requestSingleUpdate(
                provider,
                object : android.location.LocationListener {
                    override fun onLocationChanged(location: android.location.Location) {
                        val lat = location.latitude
                        val lon = location.longitude
                        tvStatus.text = "📍 Koordinat: $lat, $lon — Mengambil alamat..."
                        fetchAddressFromNominatim(lat, lon, inputAddress, tvStatus, progressBar)
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                },
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            progressBar.visibility = View.GONE
            tvStatus.text = "❌ Izin GPS ditolak"
        }
    }

    /**
     * Memanggil API Publik OpenStreetMap Nominatim untuk reverse geocoding.
     * Mengubah koordinat GPS (latitude, longitude) menjadi nama alamat lengkap.
     * API ini gratis, tidak memerlukan API key, dan terbuka untuk umum.
     * Endpoint: https://nominatim.openstreetmap.org/reverse?lat=...&lon=...&format=json
     */
    private fun fetchAddressFromNominatim(
        lat: Double,
        lon: Double,
        inputAddress: EditText,
        tvStatus: TextView,
        progressBar: ProgressBar
    ) {
        RetrofitClient.getNominatimClient()
            .reverseGeocode(lat, lon, "json")
            .enqueue(object : Callback<GeocodeResponse> {
                override fun onResponse(
                    call: Call<GeocodeResponse>,
                    response: Response<GeocodeResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val displayName = response.body()?.displayName ?: ""
                        if (displayName.isNotEmpty()) {
                            inputAddress.setText(displayName)
                            tvStatus.text = "✅ Alamat berhasil dideteksi via GPS!"
                        } else {
                            tvStatus.text = "⚠️ Alamat tidak ditemukan, isi manual"
                        }
                    } else {
                        tvStatus.text = "⚠️ Gagal mendapatkan alamat, isi manual"
                    }
                }

                override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    tvStatus.text = "❌ Error: ${t.message}"
                }
            })
    }

    // ─── Proses checkout ke backend ───────────────────────────────────────────
    private fun performCheckout(address: String) {
        val apiService = RetrofitClient.getClient(requireContext())
        apiService.checkout(CheckoutRequest(address, "Midtrans"))
            .enqueue(object : Callback<CheckoutResponse> {
                override fun onResponse(
                    call: Call<CheckoutResponse>,
                    response: Response<CheckoutResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val snapUrl = response.body()?.data?.snapUrl
                        if (!snapUrl.isNullOrEmpty()) {
                            val intent = Intent(requireActivity(), PaymentWebViewActivity::class.java)
                            intent.putExtra(PaymentWebViewActivity.EXTRA_SNAP_URL, snapUrl)
                            startActivity(intent)
                            findNavController().navigate(R.id.historyFragment)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Checkout berhasil! ${response.body()?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigate(R.id.historyFragment)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Checkout gagal: ${response.body()?.message ?: "Error"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
