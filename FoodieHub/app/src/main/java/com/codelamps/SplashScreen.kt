package com.codelamps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper // 1. Wajib tambah import ini

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // 2. PERBAIKAN: Tambahkan Looper.getMainLooper() agar tidak deprecated
        Handler(Looper.getMainLooper()).postDelayed({

            // 3. UBAH TUJUAN: Ganti StartActivity menjadi LoginActivity
            // Agar setelah loading logo, langsung masuk menu Login
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
            finish() // Agar kalau diback tidak balik ke Splash Screen

        }, 3000) // Waktu tunggu 3 detik

    }
}