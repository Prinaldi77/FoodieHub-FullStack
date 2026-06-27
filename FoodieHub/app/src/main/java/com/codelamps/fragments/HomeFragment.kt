package com.codelamps.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codelamps.R
import com.codelamps.adapter.PopularAdapter
import com.codelamps.databinding.FragmentHomeBinding
import com.codelamps.network.FoodsResponse
import com.codelamps.network.RetrofitClient
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var adapter: PopularAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewMenu.setOnClickListener {
            val bottomSheetDialogs = MenuBottomSheetFragment()
            bottomSheetDialogs.show(parentFragmentManager, "Test")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                Toast.makeText(requireContext(), "Too Long clicked", Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(position: Int) {
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })

        // Setup Popular RecyclerView with empty list initially
        adapter = PopularAdapter(emptyList())
        binding.PopularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter = adapter

        loadPopularFoods()
    }

    private fun loadPopularFoods() {
        val apiService = RetrofitClient.getClient(requireContext())
        apiService.getAllFoods().enqueue(object : Callback<FoodsResponse> {
            override fun onResponse(call: Call<FoodsResponse>, response: Response<FoodsResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val foods = response.body()?.data ?: emptyList()
                    // Display up to 5 popular items on home screen
                    val popularFoods = foods.take(5)
                    adapter?.updateData(popularFoods)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat makanan terpopuler", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Kesalahan jaringan: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
