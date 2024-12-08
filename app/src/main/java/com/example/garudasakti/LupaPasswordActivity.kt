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
import com.example.garudasakti.models.VerifyResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LupaPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lupa_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnOK = findViewById<Button>(R.id.buttonOKLupaPassword)
        btnOK.setOnClickListener {
            val email = findViewById<EditText>(R.id.textInputEmailLupaPassword).text.toString()
            if(email.isEmpty()){
                Toast.makeText(this@LupaPasswordActivity, "Harap isi field email.", Toast.LENGTH_SHORT).show()
            } else {
                val retrofit = RetrofitConfig().getRetrofitClientInstance()
                val apiService = retrofit.create(MainInterface::class.java)
                apiService.lupaPassword(email).enqueue(object : Callback<VerifyResponse> {
                    override fun onResponse(call: Call<VerifyResponse>, response: Response<VerifyResponse>) {
                        if(response.isSuccessful){
                            val intent = Intent(this@LupaPasswordActivity, VerifikasiEmailLupaPasswordActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LupaPasswordActivity, "Akun tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
                        Toast.makeText(this@LupaPasswordActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
                    }

                })
            }

        }
    }
}