package com.example.garudasakti

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.w3c.dom.Text
import java.util.Calendar

class PemesananActivity : AppCompatActivity() {


    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var btnPembayaran: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pemesanan)



        // Terima data dari MainActivity
        val lapanganNama = intent.getStringExtra("lapangan_name")
        val lapanganJenis = intent.getStringExtra("jenis_name")
        val lapanganAlas = intent.getStringExtra("alas_name")
        val lapanganHarga = intent.getIntExtra("harga_umum", 0)
        val lapanganHargaMember = intent.getIntExtra("harga_member", 0)

        // Tampilkan data di UI
        val tvLapanganNamaHeader = findViewById<TextView>(R.id.textHeaderNamaLapanganPemesanan)
        val tvLapanganNama = findViewById<TextView>(R.id.textNamaLapanganPemesanan)
        val tvLapanganAlas = findViewById<TextView>(R.id.textAlasLapanganPemesanan)
        val tvLapanganJenis = findViewById<TextView>(R.id.textJenisLapanganPemesanan)
        val tvLapanganHarga = findViewById<TextView>(R.id.textHargaUmumPemesanan)
        val tvLapanganHargaMember = findViewById<TextView>(R.id.textHargaMemberPemesanan)

        tvLapanganNamaHeader.text = lapanganNama
        tvLapanganNama.text = lapanganNama
        tvLapanganJenis.text = lapanganJenis
        tvLapanganAlas.text = lapanganAlas
        tvLapanganHarga.text = "Rp$lapanganHarga"
        tvLapanganHargaMember.text = "Rp$lapanganHargaMember"



        // Inisialisasi View
        tvSelectedDate = findViewById(R.id.textViewPilihTanggalPemesanan)
        tvSelectedTime = findViewById(R.id.textViewPilihJamPemesanan)
        btnPickDate = findViewById(R.id.buttonPilihTanggalPemesanan)
        btnPickTime = findViewById(R.id.buttonPilihJamPemesanan)




        // Data dummy untuk tanggal yang tersedia
        val dummyTanggalTersedia = listOf(
            "2024-10-15",
            "2024-10-18",
            "2024-10-20"
        )

        // Data dummy untuk jam yang tersedia pada setiap tanggal
        val dummyJamTersedia = mapOf(
            "2024-10-15" to listOf("09:00", "10:00", "11:00"),
            "2024-10-18" to listOf("12:00", "13:00", "14:00"),
            "2024-10-20" to listOf("15:00", "16:00", "17:00")
        )


        // Event listener untuk tombol pilih tanggal
        btnPickDate.setOnClickListener {
            // Buat Array untuk menyimpan daftar tanggal yang tersedia
            val tanggalArray = dummyTanggalTersedia.toTypedArray()

            // Buat AlertDialog untuk menampilkan pilihan tanggal
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Pilih Tanggal")
            builder.setItems(tanggalArray) { dialog, which ->
                // Set tanggal yang dipilih ke TextView
                tvSelectedDate.text = tanggalArray[which]
            }
            builder.show()
        }



        // Event listener untuk tombol pilih jam
        btnPickTime.setOnClickListener {
            // Cek apakah pengguna sudah memilih tanggal
            val selectedDate = tvSelectedDate.text.toString()

            if (selectedDate.isEmpty()) {
                // Tampilkan pesan jika belum memilih tanggal
                android.widget.Toast.makeText(this, "Pilih tanggal terlebih dahulu!", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                // Ambil daftar jam yang tersedia untuk tanggal yang dipilih
                val jamTersedia = dummyJamTersedia[selectedDate] ?: listOf()

                if (jamTersedia.isNotEmpty()) {
                    // Buat Array untuk menyimpan daftar jam
                    val jamArray = jamTersedia.toTypedArray()

                    // Buat AlertDialog untuk menampilkan pilihan jam
                    val builder = android.app.AlertDialog.Builder(this)
                    builder.setTitle("Pilih Jam")
                    builder.setItems(jamArray) { dialog, which ->
                        // Set jam yang dipilih ke TextView
                        tvSelectedTime.text = jamArray[which]
                    }
                    builder.show()
                } else {
                    // Tampilkan pesan jika tidak ada jam yang tersedia untuk tanggal tersebut
                    android.widget.Toast.makeText(this, "Tidak ada jam yang tersedia untuk tanggal ini!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }


        btnPembayaran = findViewById(R.id.buttonPembayaranPesanan)

        btnPembayaran.setOnClickListener {
            //ini ke halaman pembayaran, sesuaikan dengan API 3rd party payment gateway yang digunakan nantinya
            android.widget.Toast.makeText(this, "Ke halaman pembayaran (tunggu API Pembayaran)", android.widget.Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PembayaranActivity::class.java)
            startActivity(intent)
        }








        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }





        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBarPemesanan)
        bottomNavigationView.selectedItemId = R.id.menuHome
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
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
                    val intent = Intent(this, ProfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }







    }
}