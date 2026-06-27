package com.codelamps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.codelamps.network.RegisterRequest
import com.codelamps.network.RegisterResponse
import com.codelamps.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val inUsername = findViewById<EditText>(R.id.inUsername)
        val inEmail = findViewById<EditText>(R.id.inEmail)
        val inPassword = findViewById<EditText>(R.id.inPassword)
        val btnDaftar = findViewById<Button>(R.id.signup)


        // 2. Aksi saat tombol SignUp diklik
        btnDaftar.setOnClickListener {
            val fullname = inUsername.text.toString().trim()
            val email = inEmail.text.toString().trim()
            val password = inPassword.text.toString().trim()
            // Backend memerlukan no telepon, kita buat default jika user input email
            val phone = "08123456789"

            if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnDaftar.isEnabled = false

            val apiService = RetrofitClient.getClient(this)
            apiService.register(RegisterRequest(fullname, email, password, phone)).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    btnDaftar.isEnabled = true
                    if (response.isSuccessful && response.body()?.success == true) {
                        // Tampilkan pesan sukses
                        Toast.makeText(this@SignUpActivity, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_SHORT).show()

                        // Pindah ke halaman LoginActivity
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        startActivity(intent)

                        // Tutup halaman register
                        finish()
                    } else {
                        val errMsg = response.body()?.message ?: "Registrasi gagal. Email mungkin sudah terdaftar."
                        Toast.makeText(this@SignUpActivity, errMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    btnDaftar.isEnabled = true
                    Toast.makeText(this@SignUpActivity, "Kesalahan jaringan: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}