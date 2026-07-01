package com.codelamps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.codelamps.network.FoodsResponse
import com.codelamps.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditFoodActivity : AppCompatActivity() {

    private lateinit var etFoodName: EditText
    private lateinit var etFoodPrice: EditText
    private lateinit var etFoodStock: EditText
    private lateinit var etFoodCategory: EditText
    private lateinit var etFoodImage: EditText
    private lateinit var etFoodDesc: EditText

    private var foodId: String? = null // Null = Mode Tambah Menu Baru

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_food)

        etFoodName = findViewById(R.id.et_food_name)
        etFoodPrice = findViewById(R.id.et_food_price)
        etFoodStock = findViewById(R.id.et_food_stock)
        etFoodCategory = findViewById(R.id.et_food_category)
        etFoodImage = findViewById(R.id.et_food_image)
        etFoodDesc = findViewById(R.id.et_food_desc)

        val btnBack = findViewById<ImageView>(R.id.btn_back_edit)
        val btnSave = findViewById<Button>(R.id.btn_save_food)
        val tvTitle = findViewById<TextView>(R.id.tv_title_edit)

        btnBack.setOnClickListener {
            finish()
        }

        // Cek mode Edit atau Tambah Baru
        foodId = intent.getStringExtra("EXTRA_FOOD_ID")
        if (foodId != null) {
            // Mode Edit
            tvTitle.text = "Ubah Detail Menu"
            etFoodName.setText(intent.getStringExtra("EXTRA_FOOD_NAME"))
            etFoodPrice.setText(intent.getStringExtra("EXTRA_FOOD_PRICE")?.substringBefore("."))
            etFoodStock.setText(intent.getIntExtra("EXTRA_FOOD_STOCK", 50).toString())
            etFoodCategory.setText(intent.getStringExtra("EXTRA_FOOD_CAT"))
            etFoodImage.setText(intent.getStringExtra("EXTRA_FOOD_IMAGE"))
            etFoodDesc.setText(intent.getStringExtra("EXTRA_FOOD_DESC"))
            btnSave.text = "Simpan Perubahan"
        } else {
            // Mode Tambah
            tvTitle.text = "Tambah Menu Baru"
            btnSave.text = "Tambahkan Menu"
        }

        btnSave.setOnClickListener {
            saveFoodData()
        }
    }

    private fun saveFoodData() {
        val name = etFoodName.text.toString().trim()
        val priceStr = etFoodPrice.text.toString().trim()
        val stockStr = etFoodStock.text.toString().trim()
        val category = etFoodCategory.text.toString().trim()
        val image = etFoodImage.text.toString().trim()
        val desc = etFoodDesc.text.toString().trim()

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Nama dan Harga wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0
        val stock = stockStr.toIntOrNull() ?: 50

        // Buat body request
        val body = HashMap<String, Any>().apply {
            put("name", name)
            put("price", price)
            put("stock", stock)
            put("category", if (category.isEmpty()) "Makanan" else category)
            put("image_url", image)
            put("description", desc)
            put("rating", 4.8) // Default rating
        }

        val apiService = RetrofitClient.getClient(this)
        val call = if (foodId != null) {
            apiService.updateFood(foodId!!, body)
        } else {
            apiService.createFood(body)
        }

        call.enqueue(object : Callback<FoodsResponse> {
            override fun onResponse(call: Call<FoodsResponse>, response: Response<FoodsResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@EditFoodActivity, "Data menu sukses disimpan!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditFoodActivity, "Gagal menyimpan data menu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodsResponse>, t: Throwable) {
                Toast.makeText(this@EditFoodActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
