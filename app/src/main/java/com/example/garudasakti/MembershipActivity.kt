package com.example.garudasakti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.R.id.navBarMembership
import com.example.garudasakti.models.MemberResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MembershipActivity : AppCompatActivity() {
    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }
    private val customer_name: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("customer_name", "") ?: ""
    }
    private lateinit var apiInterface: MainInterface
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_membership)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        apiInterface = retrofit.create(MainInterface::class.java)


        // NAVBAR
        //navBar untuk setiap halaman
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


        //LAYOUT MEMBER DAN NON MEMBER

        // Member
        val layoutMember = findViewById<ConstraintLayout>(R.id.layoutMember)

        // Non Member
        val layoutNonMember = findViewById<ConstraintLayout>(R.id.layoutNonMember)

        fetchMemberData(layoutMember, layoutNonMember)

    }


    private fun fetchMemberData(layoutMember: ConstraintLayout, layoutNonMember: ConstraintLayout) {
        apiInterface.getMemberData("Bearer $token").enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful && response.body() != null ) {
                    if (response.body()?.membership_status == 1){
                        val member = response.body()!!.data
                        if (member != null) {
                            findViewById<TextView>(R.id.textNamaMember).text = member.customer_name
                            findViewById<TextView>(R.id.textSaldoMember).text = member.saldo.toString()
                            findViewById<TextView>(R.id.textPoinMember).text = member.poin.toString()
                        }
                        layoutMember.visibility = View.VISIBLE
                        layoutNonMember.visibility = View.GONE
                    } else {
                        layoutMember.visibility = View.GONE
                        layoutNonMember.visibility = View.VISIBLE
                    }

                } else {
                    Toast.makeText(this@MembershipActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(this@MembershipActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }


}