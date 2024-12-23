package com.example.garudasakti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.ApiModels.Authentication.LoginResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var daftarButton: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        usernameEditText = findViewById(R.id.editUsernameLogin)
        passwordEditText = findViewById(R.id.editPasswordLogin)
        loginButton = findViewById(R.id.buttonLoginLogin)
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
        daftarButton = findViewById(R.id.buttonDaftarLogin)
        daftarButton.setOnClickListener {
            val intent = Intent(this, PendaftaranActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnLupaPassword = findViewById<TextView>(R.id.textLupaPasswordLogin)
        btnLupaPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, LupaPasswordActivity::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun loginUser(username: String, password: String) {
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(MainInterface::class.java)
        apiService.login(username, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    if (loginResponse?.user != null && loginResponse.token != null) {
                        val userName = loginResponse.user.name ?: "Unknown"
                        val is_member = loginResponse.user.is_member
                        val saldo = loginResponse.user.saldo
                        val poin = loginResponse.user.poin
                        val token = loginResponse.token
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("auth_token", token)
                        editor.putString("customer_name", userName)
                        editor.putInt("is_member", is_member)
                        if (saldo != null) {
                            editor.putInt("saldo", saldo)
                        }
                        if (poin != null) {
                            editor.putInt("poin", poin)
                        }
                        editor.apply()
                        Toast.makeText(
                            this@LoginActivity,
                            "Login berhasil! Welcome $userName",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Username atau password salah", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Gagal terhubung ke server.", Toast.LENGTH_LONG).show()
                Log.e("LoginError", "Error: ${t.message}")
            }
        })
    }
}