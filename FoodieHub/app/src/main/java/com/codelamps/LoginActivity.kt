package com.codelamps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.codelamps.databinding.ActivityLoginBinding
import com.codelamps.network.LoginRequest
import com.codelamps.network.LoginResponse
import com.codelamps.network.RetrofitClient
import com.codelamps.network.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    // Inisialisasi ViewBinding
    private val binding : ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            val userEmail = sessionManager.getUserEmail() ?: ""
            if (userEmail.contains("admin", ignoreCase = true)) {
                startActivity(Intent(this, AdminActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
            return
        }

        // 1. Aksi Tombol "Don't Have Account" -> Pindah ke Register
        // Pastikan ID di XML activity_login.xml adalah: @+id/dont_have_account
        binding.dontHaveAccount.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // 2. Aksi Tombol "Login" -> Pindah ke Dashboard
        // Pastikan ID di XML activity_login.xml adalah: @+id/login
        binding.login.setOnClickListener{
            val email = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.login.isEnabled = false

            val apiService = RetrofitClient.getClient(this)
            apiService.login(LoginRequest(email, password)).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    binding.login.isEnabled = true
                    if (response.isSuccessful && response.body()?.success == true) {
                        val loginData = response.body()?.data
                        if (loginData != null) {
                            sessionManager.saveAuthToken(loginData.token)
                            sessionManager.saveUser(
                                loginData.user.id,
                                loginData.user.fullname,
                                loginData.user.email
                            )

                            // Tampilkan pesan berhasil
                            Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                            // Arahkan ke Admin atau Customer berdasarkan email
                            val intent = if (loginData.user.email.contains("admin", ignoreCase = true)) {
                                Intent(this@LoginActivity, AdminActivity::class.java)
                            } else {
                                Intent(this@LoginActivity, MainActivity::class.java)
                            }
                            startActivity(intent)

                            // PENTING: Tutup halaman login agar tidak menumpuk
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login data is empty", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errMsg = response.body()?.message ?: "Login gagal. Periksa kembali email & password Anda."
                        Toast.makeText(this@LoginActivity, errMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    binding.login.isEnabled = true
                    Toast.makeText(this@LoginActivity, "Kesalahan jaringan: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}