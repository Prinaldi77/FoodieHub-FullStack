package com.codelamps.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codelamps.R
import com.codelamps.adapter.FoodSearchAdapter
import com.codelamps.network.FoodsResponse
import com.codelamps.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var searchProgress: ProgressBar
    private lateinit var tvSearchPlaceholder: TextView
    private var adapter: FoodSearchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchView = view.findViewById(R.id.search_view)
        rvSearchResults = view.findViewById(R.id.rv_search_results)
        searchProgress = view.findViewById(R.id.search_progress)
        tvSearchPlaceholder = view.findViewById(R.id.tv_search_placeholder)

        rvSearchResults.layoutManager = LinearLayoutManager(requireContext())

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    adapter?.updateData(emptyList())
                    tvSearchPlaceholder.text = "Ketik untuk mulai mencari makanan..."
                    tvSearchPlaceholder.visibility = View.VISIBLE
                    rvSearchResults.visibility = View.GONE
                } else {
                    performSearch(newText)
                }
                return true
            }
        })

        return view
    }

    private fun performSearch(query: String) {
        searchProgress.visibility = View.VISIBLE
        tvSearchPlaceholder.visibility = View.GONE
        
        val apiService = RetrofitClient.getClient(requireContext())
        apiService.searchFoods(query).enqueue(object : Callback<FoodsResponse> {
            override fun onResponse(call: Call<FoodsResponse>, response: Response<FoodsResponse>) {
                searchProgress.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val foods = response.body()?.data ?: emptyList()
                    if (foods.isEmpty()) {
                        tvSearchPlaceholder.text = "Makanan tidak ditemukan"
                        tvSearchPlaceholder.visibility = View.VISIBLE
                        rvSearchResults.visibility = View.GONE
                    } else {
                        tvSearchPlaceholder.visibility = View.GONE
                        rvSearchResults.visibility = View.VISIBLE
                        setupAdapter(foods)
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal mencari makanan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodsResponse>, t: Throwable) {
                searchProgress.visibility = View.GONE
                Toast.makeText(requireContext(), "Kesalahan jaringan: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAdapter(foods: List<com.codelamps.network.FoodData>) {
        if (adapter == null) {
            adapter = FoodSearchAdapter(foods)
            rvSearchResults.adapter = adapter
        } else {
            adapter?.updateData(foods)
        }
    }
}
