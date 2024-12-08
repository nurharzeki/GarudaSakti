package com.example.garudasakti

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.models.DaftarMemberResponse
import com.example.garudasakti.models.VerifyEmailPasswordResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifikasiEmailLupaPasswordActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verifikasi_email_lupa_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val email = intent.getStringExtra("email")
        val btnVerifikasi = findViewById<Button>(R.id.buttonVerifikasiKodeLupaPassword)
        btnVerifikasi.setOnClickListener {
            var verification_code = 0
            val string_code = findViewById<EditText>(R.id.textInputKodeVerifikasiEmailLupaPassword).text.toString()
            if(string_code.isEmpty()){
                Toast.makeText(this, "Harap masukkan kode verifikasi", Toast.LENGTH_SHORT).show()
            } else {
                verification_code = string_code.toInt()
                val retrofit = RetrofitConfig().getRetrofitClientInstance()
                val apiService = retrofit.create(MainInterface::class.java)
                if (email != null) {
                    apiService.verifyEmailPassword(email, verification_code).enqueue(object : Callback<VerifyEmailPasswordResponse> {
                        override fun onResponse(call: Call<VerifyEmailPasswordResponse>, response: Response<VerifyEmailPasswordResponse>) {
                            if(response.isSuccessful){
                                val customer = response.body()?.customer
                                val name = customer?.name
                                val username = customer?.username
                                val intent = Intent(this@VerifikasiEmailLupaPasswordActivity, ResetPasswordActivity::class.java)
                                intent.putExtra("name", name)
                                intent.putExtra("email", email)
                                intent.putExtra("username", username)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@VerifikasiEmailLupaPasswordActivity, "Kode verifikasi salah atau telah kedaluwarsa", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<VerifyEmailPasswordResponse>, t: Throwable) {
                            Toast.makeText(this@VerifikasiEmailLupaPasswordActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
                        }

                    })
                }
            }
        }
    }
}