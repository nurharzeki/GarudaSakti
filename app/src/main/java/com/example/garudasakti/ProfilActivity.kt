package com.example.garudasakti

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import com.example.garudasakti.models.ProfilResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilActivity : AppCompatActivity() {
    private lateinit var apiInterface: MainInterface

    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        apiInterface = retrofit.create(MainInterface::class.java)



        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchProfilData()

        val buttonUpdateProfil = findViewById<Button>(R.id.buttonEditProfil)
        buttonUpdateProfil.setOnClickListener {
            val fragment = UpdateProfilFragment()
            val fragmentContainer = findViewById<FragmentContainerView>(R.id.fragment_container_profil)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_profil, fragment)
                .addToBackStack(null)
                .commit()
            fragmentContainer.visibility = View.VISIBLE

        }

        val buttonUpdatePassword = findViewById<Button>(R.id.buttonGantiPassword)
        buttonUpdatePassword.setOnClickListener {
            val fragment = UpdatePasswordFragment()
            val fragmentContainer = findViewById<FragmentContainerView>(R.id.fragment_container_profil)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_profil, fragment)
                .addToBackStack(null)
                .commit()
            fragmentContainer.visibility = View.VISIBLE
        }


        val buttonLogout = findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            logout()
        }



        //navBar untuk setiap halaman
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarProfile)
        bottomNavigationView.selectedItemId = R.id.menuProfil
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
                    true
                }
                else -> false
            }
        }

    }





    private fun fetchProfilData() {
        apiInterface.getProfil("Bearer $token").enqueue(object : Callback<ProfilResponse> {
            override fun onResponse(call: Call<ProfilResponse>, response: Response<ProfilResponse>) {
                if (response.isSuccessful) {
                    val profil = response.body()
                    profil?.let {
                        // Menampilkan data ke dalam view binding
                        val textUsernameProfil = findViewById<EditText>(R.id.textUsernameProfil)
                        textUsernameProfil.setText(it.username)
                        val textNamaProfil = findViewById<EditText>(R.id.textNamaProfil)
                        textNamaProfil.setText(it.name)
                        val textEmailProfil = findViewById<EditText>(R.id.textEmailProfil)
                        textEmailProfil.setText(it.email)
                        // Anda bisa menampilkan saldo dan poin, jika ada tampilan untuk itu

                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("customer_name", it.name)
                        editor.putString("customer_username", it.username)
                        editor.putString("customer_email", it.email)
                        editor.apply()

                    }
//                    Toast.makeText(this@ProfilActivity, "berhasil mendapatkan data profil", Toast.LENGTH_SHORT).show()

                } else {
//                    Toast.makeText(this@ProfilActivity, "Gagal mendapatkan data profil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProfilResponse>, t: Throwable) {
                Toast.makeText(this@ProfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("error", "Error : ${t.message}")
            }
        })
    }


    private fun logout() {
        // Hapus token dari SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("auth_token")  // Sesuaikan dengan key token yang Anda simpan
        editor.apply()

        // Navigasi ke LoginActivity dan clear activity stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()  // Menutup ProfilActivity
    }




}