package com.codelamps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.codelamps.network.SessionManager

class AdminActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        sessionManager = SessionManager(this)

        val btnManageMenu = findViewById<CardView>(R.id.btn_manage_menu)
        val btnManageOrders = findViewById<CardView>(R.id.btn_manage_orders)
        val btnLogout = findViewById<Button>(R.id.btn_admin_logout)

        // 1. Pindah ke Halaman Kelola Menu
        btnManageMenu.setOnClickListener {
            val intent = Intent(this, ManageMenuActivity::class.java)
            startActivity(intent)
        }

        // 2. Pindah ke Halaman Kelola Pesanan
        btnManageOrders.setOnClickListener {
            val intent = Intent(this, AdminOrderActivity::class.java)
            startActivity(intent)
        }

        // 3. Logout
        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(this, "Berhasil keluar dari akun Admin", Toast.LENGTH_SHORT).show()
            
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
