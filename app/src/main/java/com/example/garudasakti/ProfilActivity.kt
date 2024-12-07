package com.example.garudasakti

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import com.example.garudasakti.models.ProfilResponse
import com.example.garudasakti.models.UpdatePasswordResponse
import com.example.garudasakti.models.UpdateProfilResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
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
        apiInterface.getProfil("Bearer $token").enqueue(object : Callback<ProfilResponse> {
            override fun onResponse(call: Call<ProfilResponse>, response: Response<ProfilResponse>) {
                if (response.isSuccessful) {
                    val profil = response.body()
                    profil?.let {
                        val textUsernameProfil = findViewById<EditText>(R.id.textUsernameProfil)
                        textUsernameProfil.setText(it.username)
                        val textNamaProfil = findViewById<EditText>(R.id.textNamaProfil)
                        textNamaProfil.setText(it.name)
                        val textEmailProfil = findViewById<EditText>(R.id.textEmailProfil)
                        textEmailProfil.setText(it.email)
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("customer_name", it.name)
                        editor.putString("customer_username", it.username)
                        editor.putString("customer_email", it.email)
                        editor.apply()
                    }
                } else {
                }
            }
            override fun onFailure(call: Call<ProfilResponse>, t: Throwable) {
                Toast.makeText(this@ProfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("error", "Error : ${t.message}")
            }
        })
        val buttonUpdateProfil = findViewById<Button>(R.id.buttonEditProfil)
        buttonUpdateProfil.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Update Profil")
            val dialogView = layoutInflater.inflate(R.layout.custom_layout_update_profil, null)
            dialogView.findViewById<EditText>(R.id.inputUsernameUpdateProfilCustom).text = findViewById<EditText>(R.id.textUsernameProfil).text
            dialogView.findViewById<EditText>(R.id.inputNamaUpdateProfilCustom).text = findViewById<EditText>(R.id.textNamaProfil).text
            dialogView.findViewById<EditText>(R.id.inputEmailUpdateProfilCustom).text = findViewById<EditText>(R.id.textEmailProfil).text
            builder.setView(dialogView)
            val inputUsername = dialogView.findViewById<EditText>(R.id.inputUsernameUpdateProfilCustom)
            val inputNama = dialogView.findViewById<EditText>(R.id.inputNamaUpdateProfilCustom)
            val inputEmail = dialogView.findViewById<EditText>(R.id.inputEmailUpdateProfilCustom)
            builder.setPositiveButton("Simpan") { dialog, _ ->
                val username = inputUsername.text.toString()
                val name = inputNama.text.toString()
                val email = inputEmail.text.toString()
                if (username.isNotEmpty() && name.isNotEmpty() && email.isNotEmpty()) {
                    Log.d("nama", name)
                    Log.d("username", username)
                    Log.d("email", email)
                    val profilData = mapOf(
                        "name" to name,
                        "username" to username,
                        "email" to email
                    )
                    apiInterface.updateProfil("Bearer $token", profilData).enqueue(object :
                        Callback<UpdateProfilResponse> {
                        override fun onResponse(
                            call: Call<UpdateProfilResponse>,
                            response: Response<UpdateProfilResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.let {
                                    if (it.errors == null) {
                                        Toast.makeText(this@ProfilActivity, "Berhasil memperbarui profil", Toast.LENGTH_SHORT).show()
                                        val textUsernameProfil = findViewById<EditText>(R.id.textUsernameProfil)
                                        textUsernameProfil.setText(username)
                                        val textNamaProfil = findViewById<EditText>(R.id.textNamaProfil)
                                        textNamaProfil.setText(name)
                                        val textEmailProfil = findViewById<EditText>(R.id.textEmailProfil)
                                        textEmailProfil.setText(email)
                                        val sharedPreferences = this@ProfilActivity.getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putString("customer_name", name)
                                        editor.apply()
                                    } else {
                                        it.errors.forEach { (field, errors) ->
                                            Toast.makeText(this@ProfilActivity, errors.joinToString(), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(this@ProfilActivity, "Gagal memperbarui profil : ${response.code()}", Toast.LENGTH_SHORT).show()
                                Log.d("UpdateProfilResponse", response.toString())
                                Log.d("UpdateProfilBody", response.body().toString())
                            }
                        }
                        override fun onFailure(call: Call<UpdateProfilResponse>, t: Throwable) {
                            Toast.makeText(this@ProfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Harap isi semua input diatas", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.cancel()
            }
            builder.create().show()
        }
        val buttonUpdatePassword = findViewById<Button>(R.id.buttonGantiPassword)
        buttonUpdatePassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Ganti Password")
            val dialogView = layoutInflater.inflate(R.layout.custom_layout_update_password, null)
            builder.setView(dialogView)
            val inputCurrentPassword = dialogView.findViewById<EditText>(R.id.inputCurrentPasswordCustom)
            val inputNewPassword = dialogView.findViewById<EditText>(R.id.inputNewPasswordCustom)
            val inputConfirmPassword = dialogView.findViewById<EditText>(R.id.inputConfirmPasswordCustom)
            builder.setPositiveButton("Simpan") { dialog, _ ->
                val current_password = inputCurrentPassword.text.toString()
                val new_password = inputNewPassword.text.toString()
                val confirm_password = inputConfirmPassword.text.toString()
                if (current_password.isNotEmpty() && new_password.isNotEmpty() && confirm_password.isNotEmpty()) {
                    if (new_password != confirm_password){
                        Toast.makeText(this, "Ketik ulang password baru harus sama", Toast.LENGTH_SHORT).show()
                    } else {
                        val passwordData = mapOf(
                            "current_password" to current_password,
                            "new_password" to new_password,
                            "confirm_password" to confirm_password
                        )
                        apiInterface.updatePassword("Bearer $token", passwordData).enqueue(object :
                            Callback<UpdatePasswordResponse> {
                            override fun onResponse(
                                call: Call<UpdatePasswordResponse>,
                                response: Response<UpdatePasswordResponse>
                            ) {
                                if (response.isSuccessful) {
                                    response.body()?.let {
                                        Toast.makeText(this@ProfilActivity, it.message, Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this@ProfilActivity, "Gagal memperbarui password", Toast.LENGTH_SHORT).show()
                                    Log.d("Error", response.toString())
                                    Log.d("Error", response.body().toString())
                                }
                            }
                            override fun onFailure(call: Call<UpdatePasswordResponse>, t: Throwable) {
                                Toast.makeText(this@ProfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {
                    Toast.makeText(this, "Harap isi semua input diatas", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.cancel()
            }
            builder.create().show()
        }
        val buttonLogout = findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Konfirmasi Logout")
                setMessage("Apakah Anda yakin ingin logout?")
                setPositiveButton("Logout") { dialog, _ ->
                    logout()
                    dialog.dismiss()
                }
                setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                create()
                show()
            }
        }
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

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarProfile)
        when (this) {
            is ProfilActivity -> bottomNavigationView.selectedItemId = R.id.menuProfil
        }
    }


    private fun logout() {
        apiInterface.logout("Bearer $token").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfilActivity, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("auth_token")
                    editor.remove("customer_name")
                    editor.remove("is_member")
                    editor.apply()
                    val intent = Intent(this@ProfilActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("auth_token")
                    editor.apply()
                    val intent = Intent(this@ProfilActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("auth_token")
                editor.apply()
                val intent = Intent(this@ProfilActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })
    }
}