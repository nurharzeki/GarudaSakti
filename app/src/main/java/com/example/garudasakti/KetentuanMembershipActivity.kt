package com.example.garudasakti

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.garudasakti.R.id.navBarMembership
import com.example.garudasakti.adapters.KetentuanMembershipAdapter
import com.example.garudasakti.adapters.PesananSayaAdapter
import com.example.garudasakti.models.KetentuanMembershipResponse
import com.example.garudasakti.models.PesananSaya
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KetentuanMembershipActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var ketentuanMembershipAdapter: KetentuanMembershipAdapter
    private lateinit var ketentuanMembershipList: ArrayList<KetentuanMembershipResponse>
    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ketentuan_membership)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(navBarMembership)
        bottomNavigationView.selectedItemId = R.id.menuMembership
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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
        recyclerView = findViewById(R.id.rvKetentuanMembership)
        recyclerView.layoutManager = LinearLayoutManager(this)
        ketentuanMembershipList = ArrayList()
        ketentuanMembershipAdapter = KetentuanMembershipAdapter(ketentuanMembershipList)
        recyclerView.adapter = ketentuanMembershipAdapter
        fetchKetentuanMembership()
    }
    private fun fetchKetentuanMembership() {
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(MainInterface::class.java)
        val call = apiService.getKetentuanMembership("Bearer $token")
        call.enqueue(object : Callback<List<KetentuanMembershipResponse>> {
            override fun onResponse(
                call: Call<List<KetentuanMembershipResponse>>,
                response: Response<List<KetentuanMembershipResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    ketentuanMembershipList.clear()
                    ketentuanMembershipList.addAll(response.body()!!)
                    ketentuanMembershipAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@KetentuanMembershipActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<KetentuanMembershipResponse>>, t: Throwable) {
                Toast.makeText(this@KetentuanMembershipActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



}