package com.example.garudasakti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.w3c.dom.Text
import kotlin.math.tan

class PesananBerhasilActivity : AppCompatActivity() {

    private lateinit var btnOK: Button

    private val is_member: Int by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("is_member", 0)
    }

    private val saldo: Int by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("saldo", 0)
    }

    private val poin: Int by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("poin", 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pesanan_berhasil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val namaTim = intent.getStringExtra("nama_tim")
        val lapanganNama = intent.getStringExtra("lapangan_name")
        val tanggal = intent.getStringExtra("tanggal")
        val jam = intent.getStringExtra("jam")

        val tvNamaTim = findViewById<TextView>(R.id.textNamaTimPesananBerhasil)
        val tvLapangan = findViewById<TextView>(R.id.textNamaLapanganPesananBerhasil)
        val tvTanggal = findViewById<TextView>(R.id.textTanggalPesananBerhasil)
        val tvJam = findViewById<TextView>(R.id.textJamPesananBerhasil)

        tvNamaTim.text = namaTim
        tvLapangan.text = lapanganNama
        tvTanggal.text = tanggal
        tvJam.text = jam

        btnOK = findViewById(R.id.buttonOKPesananBerhasil)

        btnOK.setOnClickListener {
            val intent = Intent(this, PesananSayaActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarPesananBerhasil)
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