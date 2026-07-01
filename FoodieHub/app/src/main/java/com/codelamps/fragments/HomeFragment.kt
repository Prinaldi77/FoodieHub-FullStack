package com.codelamps.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codelamps.adapter.MealInspirationAdapter
import com.codelamps.adapter.PopularAdapter
import com.codelamps.databinding.FragmentHomeBinding
import com.codelamps.network.FoodsResponse
import com.codelamps.network.MealItem
import com.codelamps.network.MealResponse
import com.codelamps.network.RetrofitClient
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var popularAdapter: PopularAdapter? = null
    private var mealInspirationAdapter: MealInspirationAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Tombol "Lihat Semua Menu" → buka BottomSheet daftar menu
        binding.viewMenu.setOnClickListener {
            val bottomSheetDialogs = MenuBottomSheetFragment()
            bottomSheetDialogs.show(parentFragmentManager, "MenuBottomSheet")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImageSlider()
        setupPopularRecyclerView()
        setupInspirationRecyclerView()

        // Muat data dari backend (makanan terpopuler) & API publik (inspirasi)
        loadPopularFoods()
        loadMealInspiration()
    }

    // ─── Banner Promo (Image Slider) ──────────────────────────────────────────
    private fun setupImageSlider() {
        val imageList = ArrayList<SlideModel>().apply {
            add(SlideModel(com.codelamps.R.drawable.banner1, ScaleTypes.FIT))
            add(SlideModel(com.codelamps.R.drawable.banner1, ScaleTypes.FIT))
            add(SlideModel(com.codelamps.R.drawable.banner1, ScaleTypes.FIT))
        }
        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {}
            override fun onItemSelected(position: Int) {
                Toast.makeText(requireContext(), "Promo ${position + 1}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ─── RecyclerView Makanan Terpopuler (dari backend sendiri) ──────────────
    private fun setupPopularRecyclerView() {
        popularAdapter = PopularAdapter(emptyList())
        binding.PopularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter = popularAdapter
        binding.PopularRecyclerView.isNestedScrollingEnabled = false
    }

    private fun loadPopularFoods() {
        val apiService = RetrofitClient.getClient(requireContext())
        apiService.getAllFoods().enqueue(object : Callback<FoodsResponse> {
            override fun onResponse(call: Call<FoodsResponse>, response: Response<FoodsResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val foods = response.body()?.data ?: emptyList()
                    popularAdapter?.updateData(foods.take(5))
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat makanan terpopuler", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodsResponse>, t: Throwable) {
                // Tidak tampilkan error ke user — backend mungkin belum aktif
            }
        })
    }

    // ─── RecyclerView Inspirasi Kuliner (dari API Publik TheMealDB) ──────────
    private fun setupInspirationRecyclerView() {
        mealInspirationAdapter = MealInspirationAdapter(emptyList()) { meal ->
            // Klik pada kartu inspirasi → tampilkan detail singkat
            Toast.makeText(
                requireContext(),
                "🍽️ ${meal.name}\nKategori: ${meal.category ?: "-"}\nAsal: ${meal.area ?: "Internasional"}",
                Toast.LENGTH_LONG
            ).show()
        }

        // Layout Manager HORIZONTAL untuk kartu inspirasi
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvMealInspiration.layoutManager = layoutManager
        binding.rvMealInspiration.adapter = mealInspirationAdapter
    }

    /**
     * Mengambil data inspirasi kuliner khas Indonesia/Melayu dari API Publik TheMealDB secara real-time.
     * Menggunakan query pencarian nama menu spesifik: Nasi Goreng, Rendang, Satay, Laksa.
     * API ini sepenuhnya gratis dan tidak memerlukan API key.
     */
    private fun loadMealInspiration() {
        binding.pbInspiration.visibility = View.VISIBLE

        val indonesianQueries = listOf("Nasi Goreng", "Rendang", "Satay", "Laksa")
        val allMeals = mutableListOf<MealItem>()
        var loadedCount = 0

        indonesianQueries.forEach { query ->
            RetrofitClient.getMealDbClient()
                .searchMeals(query)
                .enqueue(object : Callback<MealResponse> {
                    override fun onResponse(
                        call: Call<MealResponse>,
                        response: Response<MealResponse>
                    ) {
                        loadedCount++
                        if (response.isSuccessful) {
                            val meals = response.body()?.meals
                            if (!meals.isNullOrEmpty()) {
                                // Ambil menu teratas hasil pencarian
                                val firstMeal = meals[0]
                                
                                // Map kategori visual secara manual agar rapi
                                val matchedCategory = when {
                                    firstMeal.name.contains("Rendang", ignoreCase = true) -> "Beef"
                                    firstMeal.name.contains("Goreng", ignoreCase = true) -> "Rice"
                                    firstMeal.name.contains("Satay", ignoreCase = true) -> "Chicken"
                                    else -> "Soup"
                                }
                                
                                allMeals.add(firstMeal.copy(category = matchedCategory))
                            }
                        }

                        // Semua query pencarian selesai dimuat
                        if (loadedCount == indonesianQueries.size) {
                            binding.pbInspiration.visibility = View.GONE
                            if (allMeals.isNotEmpty()) {
                                mealInspirationAdapter?.updateData(allMeals)
                            } else {
                                binding.tvPoweredBy.text = "TheMealDB (offline)"
                            }
                        }
                    }

                    override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                        loadedCount++
                        if (loadedCount == indonesianQueries.size) {
                            binding.pbInspiration.visibility = View.GONE
                            binding.tvPoweredBy.text = "TheMealDB (tidak tersedia)"
                        }
                    }
                })
        }
    }
}
