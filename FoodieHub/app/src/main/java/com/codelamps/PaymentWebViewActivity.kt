package com.codelamps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codelamps.databinding.ActivityPaymentWebViewBinding

class PaymentWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentWebViewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.paymentToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.paymentToolbar.setNavigationOnClickListener {
            showExitConfirmationDialog()
        }

        val snapUrl = intent.getStringExtra(EXTRA_SNAP_URL)
        if (snapUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Snap URL tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configure WebView
        binding.paymentWebview.settings.javaScriptEnabled = true
        binding.paymentWebview.settings.domStorageEnabled = true
        binding.paymentWebview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: ""
                
                // Intercept midtrans status redirects or custom finish page redirects
                if (url.contains("status_code=200") || url.contains("transaction_status=settlement") || url.contains("/finish")) {
                    Toast.makeText(this@PaymentWebViewActivity, "Pembayaran Berhasil!", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                    return true
                } else if (url.contains("status_code=201") || url.contains("transaction_status=pending") || url.contains("/unfinish")) {
                    Toast.makeText(this@PaymentWebViewActivity, "Menunggu Pembayaran...", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                    return true
                } else if (url.contains("status_code=202") || url.contains("/error")) {
                    Toast.makeText(this@PaymentWebViewActivity, "Pembayaran Gagal/Error.", Toast.LENGTH_LONG).show()
                    setResult(RESULT_CANCELED)
                    finish()
                    return true
                }
                
                return false
            }
        }

        binding.paymentWebview.loadUrl(snapUrl)
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda sudah selesai melakukan pembayaran?")
            .setPositiveButton("Sudah") { _, _ ->
                setResult(RESULT_OK)
                finish()
            }
            .setNegativeButton("Belum", null)
            .show()
    }

    override fun onBackPressed() {
        showExitConfirmationDialog()
    }

    companion object {
        const val EXTRA_SNAP_URL = "extra_snap_url"
    }
}
