package com.example.garudasakti

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.garudasakti.adapters.LapanganAdapter
import com.example.garudasakti.adapters.PesananSayaAdapter
import com.example.garudasakti.models.LapanganHome
import com.example.garudasakti.models.PesananSaya
import com.example.garudasakti.retro.MainInterface
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.garudasakti.retro.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PesananSayaActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var pesananSayaAdapter: PesananSayaAdapter
    private lateinit var pesananSayaList: ArrayList<PesananSaya>
    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pesanan_saya)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //navBar untuk setiap halaman
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarPesananSaya)
        bottomNavigationView.selectedItemId = R.id.menuPesananSaya
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuPesananSaya -> {
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

        // Dummy data untuk pesanan saya
//        pesananSayaList = arrayListOf(
//            PesananSaya("Lapangan 1", "Tim Badminton Zeki", "10-10-2024", "09:00"),
//            PesananSaya("Lapangan 2", "Tim Badminton Harriko", "11-10-2024", "10:00"),
//            // Tambahkan pesanan lainnya sesuai kebutuhan
//        )

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.rv_pesananSaya)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter dan set adapter ke RecyclerView
        pesananSayaList = ArrayList()
        pesananSayaAdapter = PesananSayaAdapter(pesananSayaList)
        recyclerView.adapter = pesananSayaAdapter

        fetchPesananSaya()

        val btnPesananMendatang = findViewById<Button>(R.id.buttonPesananMendatangPesananSaya)
        btnPesananMendatang.setOnClickListener {
            val textHeader = findViewById<TextView>(R.id.textHeaderRecyclerViewPesananSaya)
            textHeader.text = "Pesanan Mendatang"
            fetchPesananSaya()
        }

        val btnRiwayatPesanan = findViewById<Button>(R.id.buttonRiwayatPesananPesananSaya)
        btnRiwayatPesanan.setOnClickListener {
            val textHeader = findViewById<TextView>(R.id.textHeaderRecyclerViewPesananSaya)
            textHeader.text = "Riwayat Pesanan"
            fetchRiwayatPesananSaya()
        }

        // Set listener untuk item klik
        pesananSayaAdapter.setOnClickListener(object : PesananSayaAdapter.clickListener {
            override fun onItemClick(position: Int) {
                // Tindakan ketika item diklik, misalnya menampilkan detail lapangan
                val clickedPesananSaya = pesananSayaList[position]
                // Lakukan sesuatu dengan clickedLapangan
                // Misalnya, buka Activity detail lapangan atau tampilkan Toast
            }
        })

    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarPesananSaya)
        when (this) {
            is PesananSayaActivity -> bottomNavigationView.selectedItemId = R.id.menuPesananSaya
        }
    }



    private fun fetchPesananSaya() {
        val token = token
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(MainInterface::class.java)

        val call = apiService.getPesananSaya("Bearer $token")
        call.enqueue(object : Callback<List<PesananSaya>> {
            override fun onResponse(call: Call<List<PesananSaya>>, response: Response<List<PesananSaya>>) {
                if (response.isSuccessful) {
                    // Berhasil mendapatkan respons dari API
                    val pesananList = response.body()
                    if (pesananList != null) {
                        pesananSayaList.clear()
                        pesananSayaList.addAll(pesananList)
                        pesananSayaAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@PesananSayaActivity, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PesananSaya>>, t: Throwable) {
                Log.e("PesananSayaActivity", "Error: ${t.message}")
                Toast.makeText(this@PesananSayaActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchRiwayatPesananSaya() {
        val token = token
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(MainInterface::class.java)

        val call = apiService.getRiwayatPesananSaya("Bearer $token")
        call.enqueue(object : Callback<List<PesananSaya>> {
            override fun onResponse(call: Call<List<PesananSaya>>, response: Response<List<PesananSaya>>) {
                if (response.isSuccessful) {
                    // Berhasil mendapatkan respons dari API
                    val pesananList = response.body()
                    if (pesananList != null) {
                        pesananSayaList.clear()
                        pesananSayaList.addAll(pesananList)
                        pesananSayaAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@PesananSayaActivity, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PesananSaya>>, t: Throwable) {
                Log.e("PesananSayaActivity", "Error: ${t.message}")
                Toast.makeText(this@PesananSayaActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }



}