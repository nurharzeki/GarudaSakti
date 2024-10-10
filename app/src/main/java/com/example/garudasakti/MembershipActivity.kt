package com.example.garudasakti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
        // Ambil referensi ke elemen-elemen di layout
        val layoutMember = findViewById<ConstraintLayout>(R.id.layoutMember)
        val layoutNonMember = findViewById<ConstraintLayout>(R.id.layoutNonMember)
        val textNamaMember = findViewById<TextView>(R.id.textNamaMember)
        val textUsernameMember = findViewById<TextView>(R.id.textUsernameMember)
        val textEmailMember = findViewById<TextView>(R.id.textEmailMember)
        val textSaldoMember = findViewById<TextView>(R.id.textSaldoMember)
        val textPoinMember = findViewById<TextView>(R.id.textPoinMember)
        val buttonIsiSaldoMember = findViewById<Button>(R.id.buttonIsiSaldoMember)
        val buttonDaftarMember = findViewById<Button>(R.id.buttonDaftarMember)
        val buttonKetentuanMembershipMember = findViewById<Button>(R.id.buttonKetentuanMembershipMember)
        val buttonKetentuanMembershipNonMember = findViewById<Button>(R.id.buttonKetentuanMembershipNonMember)

        // Data dummy untuk simulasi
        val isMember = false // Ubah ini ke false jika ingin menguji kondisi non-member
        val namaMember = "Harriko Nur Harzeki"
        val usernameMember = "nurharzeki"
        val emailMember = "nurharzeki@gmail.com"
        val saldoMember = 150000 // Dummy saldo member
        val poinMember = 150 // Dummy poin member

        // Logika berdasarkan status member (data dummy)
        if (isMember) {
            // Jika pengguna adalah member
            layoutMember.visibility = LinearLayout.VISIBLE
            layoutNonMember.visibility = LinearLayout.GONE

            // Tampilkan saldo dan poin dummy
            textNamaMember.text = namaMember
            textUsernameMember.text = usernameMember
            textEmailMember.text = emailMember
            textSaldoMember.text = saldoMember.toString()
            textPoinMember.text = poinMember.toString()

            buttonKetentuanMembershipMember.setOnClickListener {
                // logika saat member mengklik button ketentuan membership

            }

            buttonIsiSaldoMember.setOnClickListener {
                // logika saat member mengklik button isi saldo

            }

        } else {
            // Jika pengguna bukan member
            layoutMember.visibility = LinearLayout.GONE
            layoutNonMember.visibility = LinearLayout.VISIBLE

            // Set aksi untuk tombol daftar
            buttonDaftarMember.setOnClickListener {
                // Logika untuk mendaftar sebagai member

            }
            buttonKetentuanMembershipNonMember.setOnClickListener {
                // Logika saat pelanggan non member mengklik button ketentuan membership

            }
        }

    }
}