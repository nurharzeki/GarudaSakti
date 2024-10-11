package com.example.garudasakti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailLapanganActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_lapangan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil data yang dikirim melalui Intent
        val lapanganName = intent.getStringExtra("lapanganName")
        val lapanganJenis = intent.getStringExtra("lapanganJenis")
        val lapanganAlas = intent.getStringExtra("lapanganAlas")
        val lapanganHarga = intent.getIntExtra("lapanganHarga", 0)
        val lapanganHargaMember = intent.getIntExtra("lapanganHargaMember", 0)

        // Set data ke tampilan detail
        findViewById<TextView>(R.id.textHeaderNamaLapanganDetailLapangan).text = lapanganName
        findViewById<TextView>(R.id.textNamaLapanganDetailLapangan).text = lapanganName
        findViewById<TextView>(R.id.textJenisLapanganDetailLapangan).text = lapanganJenis
        findViewById<TextView>(R.id.textAlasLapanganDetailLapangan).text = lapanganAlas
        findViewById<TextView>(R.id.textHargaUmumDetailLapangan).text = lapanganHarga.toString()
        findViewById<TextView>(R.id.textHargaMemberDetailLapangan).text = lapanganHargaMember.toString()


        //navBar untuk setiap halaman
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarDetailLapangan)
        bottomNavigationView.selectedItemId = R.id.menuHome
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    true
                }
                R.id.menuPesananSaya -> {
                    val intent = Intent(this, PesananSayaActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuMembership -> {
                    val intent = Intent(this, MembershipActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuProfil -> {
                    val intent = Intent(this, ProfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }
}