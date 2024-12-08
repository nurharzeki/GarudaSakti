package com.example.garudasakti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.models.RegisterResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifikasiPendaftaranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verifikasi_pendaftaran)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("name")
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        val btnVerifikasi = findViewById<Button>(R.id.buttonVerifikasiVerifikasiPendaftaran)

        btnVerifikasi.setOnClickListener {
            val string_code = findViewById<EditText>(R.id.editKodeVerifikasiVerifikasiPendaftaran).text
            if(string_code.isEmpty()){
                Toast.makeText(this, "Harap isi kode verifikasi", Toast.LENGTH_SHORT).show()
            } else {
                val verification_code = string_code.toString().toInt()
                if (email != null && name != null && username != null && password != null) {
                    verifikasi(email, name, username, password, verification_code)
                }
            }
        }

    }

    private fun verifikasi(email: String, name:String, username: String, password: String, verification_code:Int){
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(MainInterface::class.java)
        apiService.verifyEmail(email, name, username, password, verification_code).enqueue(object: Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if(response.isSuccessful){
                    val successResponse = response.body()
                    Toast.makeText(this@VerifikasiPendaftaranActivity, successResponse?.message ?: "Pendaftaran berhasil", Toast.LENGTH_LONG).show()
                    val token = response.body()?.token
                    val userName = response.body()?.created_account?.name
                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("auth_token", token)
                    editor.putString("customer_name", userName)
                    editor.putInt("is_member", 0)
                    editor.apply()
                    val intent = Intent(this@VerifikasiPendaftaranActivity, PendaftaranBerhasilActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@VerifikasiPendaftaranActivity, "Kode verifikasi salah atau telah kedaluwarsa", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@VerifikasiPendaftaranActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }

        })
    }

}