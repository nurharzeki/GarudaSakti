package com.example.garudasakti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.R.id.navBarMembership
import com.google.android.material.bottomnavigation.BottomNavigationView

class MembershipActivity : AppCompatActivity() {
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

    }
}