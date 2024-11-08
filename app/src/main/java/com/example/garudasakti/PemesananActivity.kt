package com.example.garudasakti

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.models.JamResponse
import com.example.garudasakti.models.TanggalResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import retrofit2.*

class PemesananActivity : AppCompatActivity() {

    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }

    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var btnPilihPembayaran: Button
    private lateinit var tanggalTersedia: List<String>
    private lateinit var jamTersedia: List<String>
    private lateinit var tanggaldipilih: String
    private lateinit var jamDipilih: String
    private lateinit var namaTim: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pemesanan)

        val lapangan_id = intent.getIntExtra("lapangan_id", 0)
        val lapanganNama = intent.getStringExtra("lapangan_name")
        val lapanganJenis = intent.getStringExtra("jenis_name")
        val lapanganAlas = intent.getStringExtra("alas_name")
        val lapanganHarga = intent.getIntExtra("harga_umum", 0)
        val lapanganHargaMember = intent.getIntExtra("harga_member", 0)
        val lapanganHargaPoin = intent.getIntExtra("harga_poin", 0)

        val token = token
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(MainInterface::class.java)

        namaTim = findViewById<TextInputEditText>(R.id.inputTextNamaTimPemesanan).text.toString()

        tanggalTersedia = listOf()
        jamTersedia = listOf()

        val call = apiService.getTanggal("Bearer $token", lapangan_id)
        call.enqueue(object : Callback<List<TanggalResponse>> {
            override fun onResponse(call: Call<List<TanggalResponse>>, response: Response<List<TanggalResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    tanggalTersedia = response.body()!!.map { it.tanggal }
                } else {
                    Log.e("API Error", "Response failed: ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<List<TanggalResponse>>, t: Throwable) {
                Log.e("API Error", "Failed to fetch tanggal: ${t.message}")
            }
        })

        val tvLapanganNamaHeader = findViewById<TextView>(R.id.textHeaderNamaLapanganPemesanan)
        val tvLapanganNama = findViewById<TextView>(R.id.textNamaLapanganPemesanan)
        val tvLapanganAlas = findViewById<TextView>(R.id.textAlasLapanganPemesanan)
        val tvLapanganJenis = findViewById<TextView>(R.id.textJenisLapanganPemesanan)
        val tvLapanganHarga = findViewById<TextView>(R.id.textHargaUmumPemesanan)
        val tvLapanganHargaMember = findViewById<TextView>(R.id.textHargaMemberPemesanan)
        val tvLapanganHargaPoin = findViewById<TextView>(R.id.textHargaPoinPemesanan)

        tvLapanganNamaHeader.text = lapanganNama
        tvLapanganNama.text = lapanganNama
        tvLapanganJenis.text = lapanganJenis
        tvLapanganAlas.text = lapanganAlas
        tvLapanganHarga.text = "Rp$lapanganHarga"
        tvLapanganHargaMember.text = "Rp$lapanganHargaMember"
        tvLapanganHargaPoin.text = lapanganHargaPoin.toString()

        tvSelectedDate = findViewById(R.id.textViewPilihTanggalPemesanan)
        tvSelectedTime = findViewById(R.id.textViewPilihJamPemesanan)
        btnPickDate = findViewById(R.id.buttonPilihTanggalPemesanan)
        btnPickTime = findViewById(R.id.buttonPilihJamPemesanan)

        tanggaldipilih = ""
        btnPickDate.setOnClickListener {
            val tanggalArray = tanggalTersedia.toTypedArray()
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Pilih Tanggal")
            builder.setItems(tanggalArray) { dialog, which ->
                val selectedDate = tanggalArray[which]
                tvSelectedDate.text = selectedDate
                tanggaldipilih = selectedDate

                val call = apiService.getJam("Bearer $token", lapangan_id, selectedDate)
                call.enqueue(object : Callback<List<JamResponse>> {
                    override fun onResponse(call: Call<List<JamResponse>>, response: Response<List<JamResponse>>) {
                        if (response.isSuccessful && response.body() != null) {
                            jamTersedia = response.body()!!.map { it.jam }
                        } else {
                            Log.e("API Error", "Response failed: ${response.errorBody()}")
                        }
                    }
                    override fun onFailure(call: Call<List<JamResponse>>, t: Throwable) {
                        Log.e("API Error", "Failed to fetch jam: ${t.message}")
                    }
                })

            }
            builder.show()
        }

        jamDipilih = ""
        btnPickTime.setOnClickListener {
            val selectedDate = tanggaldipilih
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Pilih tanggal terlebih dahulu!", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                if (jamTersedia.isNotEmpty()) {
                    val jamArray = jamTersedia.toTypedArray()
                    val builder = android.app.AlertDialog.Builder(this)
                    builder.setTitle("Pilih Jam")
                    builder.setItems(jamArray) { dialog, which ->
                        tvSelectedTime.text = jamArray[which]
                        jamDipilih = jamArray[which]
                    }
                    builder.show()
                } else {
                    Toast.makeText(this, "Tidak ada jam yang tersedia untuk tanggal ini!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnPilihPembayaran = findViewById(R.id.buttonPilihPembayaranPesanan)

        btnPilihPembayaran.setOnClickListener {
            val selectedDate = tanggaldipilih
            val selectedTime = jamDipilih
            namaTim = findViewById<TextInputEditText>(R.id.inputTextNamaTimPemesanan).text.toString()
            if(selectedDate.isEmpty() || selectedTime.isEmpty() || namaTim.isEmpty()){
                Toast.makeText(this, "Pilih nama tim, tanggal, dan jam terlebih dahulu!", Toast.LENGTH_SHORT).show()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Pilih Metode Pembayaran")
                builder.setItems(arrayOf("Bayar Langsung", "Bayar dengan Saldo", "Bayar dengan Poin")) { dialog, which ->
                    when (which) {
                        0 -> {
                            // Aksi untuk "Bayar Langsung"
                            Toast.makeText(this, "Ke halaman pembayaran (tunggu API Pembayaran)", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, PembayaranActivity::class.java)
                            intent.putExtra("lapangan_id", lapangan_id)
                            intent.putExtra("tanggal", tanggaldipilih)
                            intent.putExtra("jam", jamDipilih)
                            intent.putExtra("namaTim", namaTim)
                            intent.putExtra("lapangan_name", lapanganNama)
                            intent.putExtra("jenis_name", lapanganJenis)
                            intent.putExtra("alas_name", lapanganAlas)
                            intent.putExtra("harga_umum", lapanganHarga)
                            intent.putExtra("harga_member", lapanganHargaMember)
                            intent.putExtra("harga_poin", lapanganHargaPoin)
                            Log.d("Tanggal Dipilih", tanggaldipilih)
                            Log.d("Jam Dipilih", jamDipilih)
                            startActivity(intent)
                        }
                        1 -> {
                            // Aksi untuk "Bayar dengan Saldo"
                            Toast.makeText(this, "Bayar dengan Saldo dipilih", Toast.LENGTH_SHORT).show()
                            // Tambahkan logika pembayaran dengan saldo di sini
                        }
                        2 -> {
                            // Aksi untuk "Bayar dengan Poin"
                            Toast.makeText(this, "Bayar dengan Poin dipilih", Toast.LENGTH_SHORT).show()
                            // Tambahkan logika pembayaran dengan poin di sini
                        }
                    }
                }
                builder.show()
            }
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