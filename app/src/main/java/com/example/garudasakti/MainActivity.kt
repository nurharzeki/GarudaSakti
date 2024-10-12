package com.example.garudasakti

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.garudasakti.adapters.LapanganAdapter
import com.example.garudasakti.models.LapanganHome
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var lapanganAdapter: LapanganAdapter
    private lateinit var lapanganList: ArrayList<LapanganHome>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //navBar untuk setiap halaman
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarHome)
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

        // Dummy data untuk lapangan
        lapanganList = arrayListOf(
            LapanganHome("Lapangan 1", "Badminton", "Karpet", 50000, 47000),
            LapanganHome("Lapangan 2", "Hybrid", "Beton", 35000, 32000),
            LapanganHome("Lapangan 3", "Badminton", "Karpet", 50000, 47000),
            LapanganHome("Lapangan 4", "Hybrid", "Beton", 35000, 32000),
            // Tambahkan lapangan lainnya sesuai kebutuhan
        )

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.rv_lapangan)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter dan set adapter ke RecyclerView
        lapanganAdapter = LapanganAdapter(lapanganList)
        recyclerView.adapter = lapanganAdapter

        // Set listener untuk item klik
        lapanganAdapter.setOnClickListener(object : LapanganAdapter.clickListener {
            override fun onItemClick(position: Int) {
                // Tindakan ketika item diklik, misalnya menampilkan detail lapangan
                val clickedLapangan = lapanganList[position]
                // Lakukan sesuatu dengan clickedLapangan
                // Intent ke halaman detail lapangan
                val intent = Intent(this@MainActivity, DetailLapanganActivity::class.java)
                intent.putExtra("lapanganNama", clickedLapangan.nama)
                intent.putExtra("lapanganJenis", clickedLapangan.jenis)
                intent.putExtra("lapanganAlas", clickedLapangan.alas)
                intent.putExtra("lapanganHarga", clickedLapangan.harga)
                intent.putExtra("lapanganHargaMember", clickedLapangan.hargaMember)
                startActivity(intent)

            }

            override fun onDetailClick(position: Int) {
                val lapanganPesan = lapanganList[position]

                // Pindah ke activity pemesanan lapangan dengan membawa data lapangan yang dipilih
                val intent = Intent(this@MainActivity, DetailLapanganActivity::class.java)
                intent.putExtra("lapanganNama", lapanganPesan.nama)
                intent.putExtra("lapanganJenis", lapanganPesan.jenis)
                intent.putExtra("lapanganAlas", lapanganPesan.alas)
                intent.putExtra("lapanganHarga", lapanganPesan.harga)
                intent.putExtra("lapanganHargaMember", lapanganPesan.hargaMember)
                startActivity(intent)
            }

            override fun onPesanClick(position: Int) {
                val lapanganPesan = lapanganList[position]
                // Pindah ke activity pemesanan lapangan dengan membawa data lapangan yang dipilih
                val intent = Intent(this@MainActivity, PemesananActivity::class.java)
                intent.putExtra("lapanganNama", lapanganPesan.nama)
                intent.putExtra("lapanganJenis", lapanganPesan.jenis)
                intent.putExtra("lapanganAlas", lapanganPesan.alas)
                intent.putExtra("lapanganHarga", lapanganPesan.harga)
                intent.putExtra("lapanganHargaMember", lapanganPesan.hargaMember)
                startActivity(intent)
            }
        })






    }
}