package com.example.garudasakti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailLapanganActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var btnPesan: Button

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

        val lapangan_id = intent.getIntExtra("lapangan_id", 0)
        val lapanganNama = intent.getStringExtra("lapangan_name")
        val lapanganJenis = intent.getStringExtra("jenis_name")
        val lapanganAlas = intent.getStringExtra("alas_name")
        val lapanganHarga = intent.getIntExtra("harga_umum", 0)
        val lapanganHargaMember = intent.getIntExtra("harga_member", 0)
        val lapanganHargaPoin = intent.getIntExtra("harga_poin", 0)

        findViewById<TextView>(R.id.textHeaderNamaLapanganDetailLapangan).text = lapanganNama
        findViewById<TextView>(R.id.textNamaLapanganDetailLapangan).text = lapanganNama
        findViewById<TextView>(R.id.textJenisLapanganDetailLapangan).text = lapanganJenis
        findViewById<TextView>(R.id.textAlasLapanganDetailLapangan).text = lapanganAlas
        findViewById<TextView>(R.id.textHargaUmumDetailLapangan).text = "Rp$lapanganHarga"
        findViewById<TextView>(R.id.textHargaMemberDetailLapangan).text = "Rp$lapanganHargaMember"
        findViewById<TextView>(R.id.textHargaPoinDetailLapangan).text = lapanganHargaPoin.toString()

        btnPesan = findViewById(R.id.buttonPesanLapanganDetailLapangan)

        btnPesan.setOnClickListener{
            val intent = Intent(this, PemesananActivity::class.java)
            intent.putExtra("lapangan_id", lapangan_id)
            intent.putExtra("lapangan_name", lapanganNama)
            intent.putExtra("jenis_name", lapanganJenis)
            intent.putExtra("alas_name", lapanganAlas)
            intent.putExtra("harga_umum", lapanganHarga)
            intent.putExtra("harga_member", lapanganHargaMember)
            intent.putExtra("harga_poin", lapanganHargaPoin)
            startActivity(intent)
        }

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

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarDetailLapangan)
        when (this) {
            is DetailLapanganActivity -> bottomNavigationView.selectedItemId = R.id.menuHome
        }
    }


}