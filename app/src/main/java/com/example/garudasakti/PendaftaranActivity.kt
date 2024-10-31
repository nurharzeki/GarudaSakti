package com.example.garudasakti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.models.RegisterRequest
import com.example.garudasakti.models.RegisterResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PendaftaranActivity : AppCompatActivity() {

    val retrofit = RetrofitConfig().getRetrofitClientInstance()
    val apiService = retrofit.create(MainInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {





        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pendaftaran)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val buttonDaftar = findViewById<Button>(R.id.buttonDaftarPendaftaran)
        buttonDaftar.setOnClickListener {
            registerUser()
        }





    }


    private fun registerUser() {
        val username = findViewById<TextInputEditText>(R.id.editUsernamePendaftaran).text.toString()
        val name = findViewById<TextInputEditText>(R.id.editNamaLengkapPendaftaran).text.toString()
        val email = findViewById<TextInputEditText>(R.id.editEmailPendaftaran).text.toString()
        val password = findViewById<TextInputEditText>(R.id.editPasswordPendaftaran).text.toString()
        val password_confirmation = findViewById<TextInputEditText>(R.id.editRetypePasswordPendaftaran).text.toString()

        val registerRequest = RegisterRequest(username, name, email, password, password_confirmation)
        apiService.registerUser(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val successResponse = response.body()
                    Toast.makeText(this@PendaftaranActivity, successResponse?.message ?: "Pendaftaran berhasil", Toast.LENGTH_LONG).show()
                    val token = response.body()?.token
                    val userName = response.body()?.created_account?.name
                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("auth_token", token)
                    editor.putString("customer_name", userName)
                    editor.apply()
                    val intent = Intent(this@PendaftaranActivity, PendaftaranBerhasilActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    handleErrorResponse(errorResponse)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@PendaftaranActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun handleErrorResponse(errorBody: String?) {
        // Parsing error untuk menangani "username/email sudah diambil"
        errorBody?.let {
            try {
                val jsonObject = JSONObject(it)
                val errors = jsonObject.getJSONObject("errors")
                if (errors.has("name")) {
                    val nameError = errors.getJSONArray("name").getString(0)
                    Toast.makeText(this, nameError, Toast.LENGTH_LONG).show()
                } else if (errors.has("email")) {
                    val emailError = errors.getJSONArray("email").getString(0)
                    Toast.makeText(this, emailError, Toast.LENGTH_LONG).show()
                } else if (errors.has("username")) {
                    val usernameError = errors.getJSONArray("username").getString(0)
                    Toast.makeText(this, usernameError, Toast.LENGTH_LONG).show()
                } else if (errors.has("password")) {
                    val passwordError = errors.getJSONArray("password").getString(0)
                    Toast.makeText(this, passwordError, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error : $e", Toast.LENGTH_SHORT).show()
            }
        }
    }




}