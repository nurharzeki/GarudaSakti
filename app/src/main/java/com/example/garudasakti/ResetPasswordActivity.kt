package com.example.garudasakti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.models.DaftarMemberResponse
import com.example.garudasakti.models.ResetPasswordResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val name = intent.getStringExtra("name")
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val btnSimpan = findViewById<Button>(R.id.buttonSimpanResetPassword)
        btnSimpan.setOnClickListener {
            val newPassword = findViewById<EditText>(R.id.textInputNewPasswordResetPassword).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.textInputConfirmPasswordResetPassword).text.toString()
            if(newPassword.isEmpty() || confirmPassword.isEmpty()){
                Toast.makeText(this@ResetPasswordActivity, "Harap isi kedua field diatas", Toast.LENGTH_SHORT).show()
            } else {
                if(newPassword != confirmPassword){
                    Toast.makeText(this@ResetPasswordActivity, "Ketik ulang password harus sama", Toast.LENGTH_SHORT).show()
                } else {
                    val retrofit = RetrofitConfig().getRetrofitClientInstance()
                    val apiService = retrofit.create(MainInterface::class.java)
                    if (email != null) {
                        apiService.resetPassword(email, newPassword, confirmPassword).enqueue(object : Callback<ResetPasswordResponse> {
                            override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                                if(response.isSuccessful){
                                    val customer = response.body()?.customer
                                    val token = response.body()?.token
                                    val name = customer?.name
                                    val username = customer?.username
                                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("auth_token", token)
                                    editor.putString("customer_name", name)
                                    editor.apply()
                                    AlertDialog.Builder(this@ResetPasswordActivity).apply {
                                        setTitle("Pemberitahuan")
                                        setMessage(
                                            "Password berhasil direset."
                                        )
                                        setPositiveButton("Ke Halaman Utama") { dialog, _ ->
                                            val intent = Intent(this@ResetPasswordActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                            dialog.dismiss()
                                        }
                                        create()
                                        show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {

                            }

                        })
                    }
                }
            }
        }
    }
}