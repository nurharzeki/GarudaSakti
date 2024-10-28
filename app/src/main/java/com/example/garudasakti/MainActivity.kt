package com.example.garudasakti

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.garudasakti.adapters.LapanganAdapter
import com.example.garudasakti.models.LapanganHome
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var lapanganAdapter: LapanganAdapter
    private lateinit var lapanganList: ArrayList<LapanganHome>
    private lateinit var apiInterface: MainInterface
    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        apiInterface = retrofit.create(MainInterface::class.java)


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
//        lapanganList = arrayListOf(
//            LapanganHome("Lapangan 1", "Badminton", "Karpet", 50000, 47000),
//            LapanganHome("Lapangan 2", "Hybrid", "Beton", 35000, 32000),
//            LapanganHome("Lapangan 3", "Badminton", "Karpet", 50000, 47000),
//            LapanganHome("Lapangan 4", "Hybrid", "Beton", 35000, 32000),
//            // Tambahkan lapangan lainnya sesuai kebutuhan
//        )

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.rv_lapangan)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter dan set adapter ke RecyclerView
        lapanganAdapter = LapanganAdapter(arrayListOf())
        recyclerView.adapter = lapanganAdapter

        fetchLapanganList()

        // Set listener untuk item klik
        lapanganAdapter.setOnClickListener(object : LapanganAdapter.clickListener {
            override fun onItemClick(position: Int) {
                // Tindakan ketika item diklik, misalnya menampilkan detail lapangan
                val clickedLapangan = lapanganList[position]
                // Lakukan sesuatu dengan clickedLapangan
                // Intent ke halaman detail lapangan
                val intent = Intent(this@MainActivity, DetailLapanganActivity::class.java)
                intent.putExtra("lapangan_name", clickedLapangan.lapangan_name)
                intent.putExtra("jenis_name", clickedLapangan.jenis_name)
                intent.putExtra("alas_name", clickedLapangan.alas_name)
                intent.putExtra("harga_umum", clickedLapangan.harga_umum)
                intent.putExtra("harga_member", clickedLapangan.harga_member)
                startActivity(intent)

            }

            override fun onDetailClick(position: Int) {
                val lapanganPesan = lapanganList[position]

                // Pindah ke activity pemesanan lapangan dengan membawa data lapangan yang dipilih
                val intent = Intent(this@MainActivity, DetailLapanganActivity::class.java)
                intent.putExtra("lapangan_name", lapanganPesan.lapangan_name)
                intent.putExtra("jenis_name", lapanganPesan.jenis_name)
                intent.putExtra("alas_name", lapanganPesan.alas_name)
                intent.putExtra("harga_umum", lapanganPesan.harga_umum)
                intent.putExtra("harga_member", lapanganPesan.harga_member)
                startActivity(intent)
            }

            override fun onPesanClick(position: Int) {
                val lapanganPesan = lapanganList[position]
                // Pindah ke activity pemesanan lapangan dengan membawa data lapangan yang dipilih
                val intent = Intent(this@MainActivity, PemesananActivity::class.java)
                intent.putExtra("lapangan_name", lapanganPesan.lapangan_name)
                intent.putExtra("jenis_name", lapanganPesan.jenis_name)
                intent.putExtra("alas_name", lapanganPesan.alas_name)
                intent.putExtra("harga_umum", lapanganPesan.harga_umum)
                intent.putExtra("harga_member", lapanganPesan.harga_member)
                startActivity(intent)
            }
        })




    }


    private fun fetchLapanganList() {
        // Panggil API dan tangani respons
        val call = apiInterface.getLapanganList("Bearer $token")
        call.enqueue(object : Callback<List<LapanganHome>> {
            override fun onResponse(
                call: Call<List<LapanganHome>>,
                response: Response<List<LapanganHome>>
            ) {
                if (response.isSuccessful) {
                    // Dapatkan data dan perbarui RecyclerView
                    val lapanganData = response.body() ?: emptyList()
                    lapanganAdapter.setListLapangan(ArrayList(lapanganData))
                } else {
                    // Tangani respons tidak berhasil
                    Toast.makeText(this@MainActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LapanganHome>>, t: Throwable) {
                // Tangani kegagalan koneksi
                Toast.makeText(this@MainActivity, "Koneksi error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}