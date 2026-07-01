package com.codelamps

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codelamps.adapter.ManageMenuAdapter
import com.codelamps.network.FoodsResponse
import com.codelamps.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageMenuActivity : AppCompatActivity() {

    private lateinit var rvManageMenu: RecyclerView
    private lateinit var pbManageMenu: ProgressBar
    private var adapter: ManageMenuAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_menu)

        rvManageMenu = findViewById(R.id.rv_manage_menu)
        pbManageMenu = findViewById(R.id.pb_manage_menu)
        val btnBack = findViewById<ImageView>(R.id.btn_back_manage)
        val fabAddFood = findViewById<FloatingActionButton>(R.id.fab_add_food)

        rvManageMenu.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        // Tambah makanan baru
        fabAddFood.setOnClickListener {
            val intent = Intent(this, EditFoodActivity::class.java)
            startActivity(intent)
        }

        loadManageMenu()
    }

    override fun onResume() {
        super.onResume()
        loadManageMenu()
    }

    private fun loadManageMenu() {
        pbManageMenu.visibility = View.VISIBLE
        val apiService = RetrofitClient.getClient(this)
        apiService.getAllFoods().enqueue(object : Callback<FoodsResponse> {
            override fun onResponse(call: Call<FoodsResponse>, response: Response<FoodsResponse>) {
                pbManageMenu.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val foods = response.body()?.data ?: emptyList()
                    setupAdapter(foods)
                } else {
                    Toast.makeText(this@ManageMenuActivity, "Gagal mengambil data menu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodsResponse>, t: Throwable) {
                pbManageMenu.visibility = View.GONE
                Toast.makeText(this@ManageMenuActivity, "Kesalahan jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAdapter(foods: List<com.codelamps.network.FoodData>) {
        adapter = ManageMenuAdapter(
            foods,
            onEditClick = { food ->
                // Pindah ke EditFoodActivity dengan data menu
                val intent = Intent(this, EditFoodActivity::class.java).apply {
                    putExtra("EXTRA_FOOD_ID", food.id)
                    putExtra("EXTRA_FOOD_NAME", food.name)
                    putExtra("EXTRA_FOOD_PRICE", food.price)
                    putExtra("EXTRA_FOOD_DESC", food.description)
                    putExtra("EXTRA_FOOD_IMAGE", food.imageUrl)
                    putExtra("EXTRA_FOOD_STOCK", food.stock ?: 0)
                    putExtra("EXTRA_FOOD_CAT", food.category)
                }
                startActivity(intent)
            },
            onDeleteClick = { food ->
                // Tampilkan konfirmasi hapus
                AlertDialog.Builder(this)
                    .setTitle("Hapus Menu")
                    .setMessage("Apakah Anda yakin ingin menghapus '${food.name}'?")
                    .setPositiveButton("Hapus") { _, _ ->
                        deleteFoodItem(food.id)
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )
        rvManageMenu.adapter = adapter
    }

    private fun deleteFoodItem(id: String) {
        pbManageMenu.visibility = View.VISIBLE
        val apiService = RetrofitClient.getClient(this)
        apiService.deleteFood(id).enqueue(object : Callback<FoodsResponse> {
            override fun onResponse(call: Call<FoodsResponse>, response: Response<FoodsResponse>) {
                pbManageMenu.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@ManageMenuActivity, "Menu berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    loadManageMenu()
                } else {
                    Toast.makeText(this@ManageMenuActivity, "Gagal menghapus menu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodsResponse>, t: Throwable) {
                pbManageMenu.visibility = View.GONE
                Toast.makeText(this@ManageMenuActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
