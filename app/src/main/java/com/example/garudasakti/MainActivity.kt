package com.example.garudasakti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var lapanganAdapter: LapanganAdapter
    private var lapanganList = ArrayList<LapanganHome>()
    private lateinit var apiInterface: MainInterface
    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }
    private val customer_name: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("customer_name", "") ?: ""
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
//        createToken()
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        apiInterface = retrofit.create(MainInterface::class.java)
        if(token == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val textCustomerName: TextView = findViewById(R.id.textNamaCustomerHome)
        textCustomerName.text = customer_name
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        recyclerView = findViewById(R.id.rv_lapangan)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lapanganAdapter = LapanganAdapter(arrayListOf())
        recyclerView.adapter = lapanganAdapter

        fetchLapanganList()

        lapanganAdapter.setOnClickListener(object : LapanganAdapter.clickListener {
            override fun onItemClick(position: Int) {
                val clickedLapangan = lapanganList[position]
                val intent = Intent(this@MainActivity, DetailLapanganActivity::class.java)
                intent.putExtra("lapangan_id", clickedLapangan.id)
                intent.putExtra("lapangan_name", clickedLapangan.lapangan_name)
                intent.putExtra("jenis_name", clickedLapangan.jenis_name)
                intent.putExtra("alas_name", clickedLapangan.alas_name)
                intent.putExtra("harga_umum", clickedLapangan.harga_umum)
                intent.putExtra("harga_member", clickedLapangan.harga_member)
                intent.putExtra("harga_poin", clickedLapangan.harga_poin)
                startActivity(intent)
            }

            override fun onDetailClick(position: Int) {
                val lapanganPesan = lapanganList[position]
                val intent = Intent(this@MainActivity, DetailLapanganActivity::class.java)
                intent.putExtra("lapangan_id", lapanganPesan.id)
                intent.putExtra("lapangan_name", lapanganPesan.lapangan_name)
                intent.putExtra("jenis_name", lapanganPesan.jenis_name)
                intent.putExtra("alas_name", lapanganPesan.alas_name)
                intent.putExtra("harga_umum", lapanganPesan.harga_umum)
                intent.putExtra("harga_member", lapanganPesan.harga_member)
                intent.putExtra("harga_poin", lapanganPesan.harga_poin)
                startActivity(intent)
            }

            override fun onPesanClick(position: Int) {
                val lapanganPesan = lapanganList[position]
                val intent = Intent(this@MainActivity, PemesananActivity::class.java)
                intent.putExtra("lapangan_id", lapanganPesan.id)
                intent.putExtra("lapangan_name", lapanganPesan.lapangan_name)
                intent.putExtra("jenis_name", lapanganPesan.jenis_name)
                intent.putExtra("alas_name", lapanganPesan.alas_name)
                intent.putExtra("harga_umum", lapanganPesan.harga_umum)
                intent.putExtra("harga_member", lapanganPesan.harga_member)
                intent.putExtra("harga_poin", lapanganPesan.harga_poin)
                startActivity(intent)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarHome)
        when (this) {
            is MainActivity -> bottomNavigationView.selectedItemId = R.id.menuHome
        }
    }


    private fun fetchLapanganList() {
        val call = apiInterface.getLapanganList("Bearer $token")
        call.enqueue(object : Callback<List<LapanganHome>> {
            override fun onResponse(
                call: Call<List<LapanganHome>>,
                response: Response<List<LapanganHome>>
            ) {
                if (response.isSuccessful) {
                    val lapanganData = response.body() ?: emptyList()
                    lapanganList = ArrayList(lapanganData)
                    lapanganAdapter.setListLapangan(lapanganList)
                } else {
                    Toast.makeText(this@MainActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LapanganHome>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Koneksi error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun createToken(){
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if(!task.isSuccessful){
//                return@OnCompleteListener
//            }
//            val token = task.result
//            Log.d("FCM__TOKEN", token)
//        })
//    }

}