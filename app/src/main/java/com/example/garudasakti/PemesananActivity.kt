package com.example.garudasakti

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.models.JamResponse
import com.example.garudasakti.models.MemberResponse
import com.example.garudasakti.models.PemesananLangsungRequest
import com.example.garudasakti.models.PemesananLangsungResponse
import com.example.garudasakti.models.PemesananPoinResponse
import com.example.garudasakti.models.PemesananSaldoResponse
import com.example.garudasakti.models.TanggalResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import retrofit2.*
import kotlin.properties.Delegates

class PemesananActivity : AppCompatActivity() {

    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }

    private val is_member: Int by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("is_member", 0)
    }

    private var saldo by Delegates.notNull<Int>()
    private var poin by Delegates.notNull<Int>()

    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var btnPilihPembayaran: Button
    private lateinit var tanggalTersedia: List<String>
    private lateinit var jamTersedia: List<String>
    private lateinit var tanggaldipilih: String
    private lateinit var jamDipilih: List<String>
    private lateinit var namaTim: String
    private lateinit var selectedJam: String

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

        // ambil informasi customer apakah member atau bukan, ambil jumlah saldo dan poinnya
        apiService.getMemberData("Bearer $token").enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful && response.body() != null ) {
                    if (response.body()?.membership_status == 1){
                        val member = response.body()!!.data
                        if (member != null) {
                            saldo = member.saldo
                            poin = member.poin
                        }
                    } else {
                        //bukan member
                        saldo = 0
                        poin = 0
                    }
                } else {
                    Toast.makeText(this@PemesananActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(this@PemesananActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })

        tanggalTersedia = listOf()
        jamTersedia = listOf()

        // ambil tanggal tersedia untuk lapangan yang dipilih
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

        namaTim = findViewById<TextInputEditText>(R.id.inputTextNamaTimPemesanan).text.toString()

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

        // pilih tanggal
        tanggaldipilih = ""
        btnPickDate.setOnClickListener {
            val tanggalArray = tanggalTersedia.toTypedArray()
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Pilih Tanggal")
            builder.setItems(tanggalArray) { dialog, which ->
                val selectedDate = tanggalArray[which]
                tvSelectedDate.text = selectedDate
                tanggaldipilih = selectedDate

                // ambil data jam tersedia untuk lapangan dan tanggal yang dipilih
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

        // pilih jam
        selectedJam = ""
        var jumlahJamDipilih = 0
        var selectedItemsStatus = BooleanArray(0)
        val selectedJams = mutableListOf<String>()
        btnPickTime.setOnClickListener {
            val selectedDate = tanggaldipilih
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Pilih tanggal terlebih dahulu!", Toast.LENGTH_SHORT).show()
            } else {
                if (jamTersedia.isNotEmpty()) {
                    jamDipilih = mutableListOf()
                    if (selectedItemsStatus.size != jamTersedia.size) {
                        selectedItemsStatus = BooleanArray(jamTersedia.size) { false }
                    }
                    val jamArray = jamTersedia.toTypedArray()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Pilih Jam")
                    builder.setMultiChoiceItems(jamArray, selectedItemsStatus) { _, which, isChecked ->
                        if (isChecked) {
                            selectedJams.add(jamArray[which])
                        } else {
                            selectedJams.remove(jamArray[which])
                        }
                        selectedItemsStatus[which] = isChecked
                    }
                    builder.setPositiveButton("OK") { dialog, _ ->
                        if (selectedJams.isNotEmpty()) {
                            jumlahJamDipilih = selectedJams.size
                            selectedJam = selectedJams.joinToString(",\n")
                            tvSelectedTime.text = selectedJams.joinToString(",\n")
                            // Menyimpan jam yang dipilih ke dalam list
                            jamDipilih = selectedJams.map { it.split(" - ")[0] }
                            dialog.dismiss()
                        } else {
                            tvSelectedTime.text = "belum dipilih"
                            Toast.makeText(this, "Pilih setidaknya satu jam!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    builder.show()
                } else {
                    Toast.makeText(this, "Tidak ada jam yang tersedia untuk tanggal ini!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnPilihPembayaran = findViewById(R.id.buttonPilihPembayaranPesanan)

        btnPilihPembayaran.setOnClickListener {
            var totalHargaPemesanan = 0
            var totalPoinPemesanan = 0
            val selectedDate = tanggaldipilih
            val selectedTime = jamDipilih
            namaTim = findViewById<TextInputEditText>(R.id.inputTextNamaTimPemesanan).text.toString()
            if(selectedDate.isEmpty() || selectedTime.isEmpty() || namaTim.isEmpty()){
                Toast.makeText(this, "Masukkan nama tim, tanggal, dan jam terlebih dahulu!", Toast.LENGTH_SHORT).show()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Pilih Metode Pembayaran")
                builder.setItems(arrayOf("Bayar Langsung", "Bayar dengan Saldo", "Penukaran Poin")) { dialog, which ->
                    when (which) {
                        // bayar langsung
                        0 -> {
                            // Aksi untuk "Bayar Langsung"
                            if (is_member == 0){
                                // jika bukan member (gunakan harga umum)
                                totalHargaPemesanan = lapanganHarga*jumlahJamDipilih
                                AlertDialog.Builder(this).apply {
                                    setTitle("Konfirmasi Pemesanan")
                                    setMessage(
                                        "Anda akan memesan lapangan sebagai berikut:\n\n" +
                                                "Nama Tim\t\t: $namaTim\n" +
                                                "Lapangan\t\t: $lapanganNama\n" +
                                                "Tanggal\t\t\t\t: $tanggaldipilih\n\n" +
                                                "Jam\t: \n$selectedJam\n\n" +
                                                "Total harga pesanan\t\t: Rp.$totalHargaPemesanan\n" +
                                                "\nYakin?\n"
                                    )
                                    setPositiveButton("Konfirmasi") { dialog, _ ->
                                        // Logika pemesanan/pembayaran
                                        Toast.makeText(this@PemesananActivity, "Bayar Langsung dipilih, tunggu Midtrans", Toast.LENGTH_SHORT).show()
                                        val pemesananRequest = PemesananLangsungRequest(
                                            nama_tim = namaTim,
                                            lapangan_id = lapangan_id,
                                            tanggal = tanggaldipilih,
                                            jam = jamDipilih
                                        )
//                                        apiService.pemesananLangsung("Bearer $token", pemesananRequest).enqueue(object : Callback<PemesananLangsungResponse> {
//                                            override fun onResponse(call: Call<PemesananLangsungResponse>, response: Response<PemesananLangsungResponse>) {
//                                                if (response.isSuccessful) {
//                                                    val snapToken = response.body()?.snap_token
//                                                    if (snapToken != null) {
//                                                        // Jika berhasil, arahkan ke halaman pembayaran Midtrans
//                                                        tampilkanPembayaranMidtrans(snapToken)
//                                                    } else {
//                                                        // Tangani jika snap_token tidak ditemukan
//                                                        Toast.makeText(this@PemesananActivity, "Gagal mendapatkan token pembayaran", Toast.LENGTH_SHORT).show()
//                                                    }
//                                                } else {
//                                                    // Tangani jika request gagal
//                                                    Toast.makeText(this@PemesananActivity, "Terjadi kesalahan dalam pemesanan", Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
//
//                                            override fun onFailure(call: Call<PemesananLangsungResponse>, t: Throwable) {
//                                                // Tangani jika Retrofit gagal melakukan request
//                                                Toast.makeText(this@PemesananActivity, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
//                                            }
//                                        })
                                        dialog.dismiss()
                                    }
                                    setNegativeButton("Batal") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    create()
                                    show()
                                }
                            } else {
                                // jika member (gunakan harga member)
                                totalHargaPemesanan = lapanganHargaMember * jumlahJamDipilih
                                AlertDialog.Builder(this).apply {
                                    setTitle("Konfirmasi Pemesanan")
                                    setMessage(
                                        "Anda akan memesan lapangan sebagai berikut:\n\n" +
                                                "Nama Tim\t\t: $namaTim\n" +
                                                "Lapangan\t\t: $lapanganNama\n" +
                                                "Tanggal\t\t\t\t: $tanggaldipilih\n\n" +
                                                "Jam\t: \n$selectedJam\n\n" +
                                                "Total harga pesanan\t\t: Rp.$totalHargaPemesanan\n" +
                                                "\nYakin?\n"
                                    )
                                    setPositiveButton("Konfirmasi") { dialog, _ ->
                                        Toast.makeText(this@PemesananActivity, "Bayar Langsung dipilih, tunggu Midtrans", Toast.LENGTH_SHORT).show()
                                        // Logika pemesanan/pembayaran
                                        val pemesananRequest = PemesananLangsungRequest(
                                            nama_tim = namaTim,
                                            lapangan_id = lapangan_id,
                                            tanggal = tanggaldipilih,
                                            jam = jamDipilih
                                        )
//                                        apiService.pemesananLangsung("Bearer $token", pemesananRequest).enqueue(object : Callback<PemesananLangsungResponse> {
//                                            override fun onResponse(call: Call<PemesananLangsungResponse>, response: Response<PemesananLangsungResponse>) {
//                                                if (response.isSuccessful) {
//                                                    val snapToken = response.body()?.snap_token
//                                                    if (snapToken != null) {
//                                                        // Jika berhasil, arahkan ke halaman pembayaran Midtrans
//                                                        tampilkanPembayaranMidtrans(snapToken)
//                                                    } else {
//                                                        // Tangani jika snap_token tidak ditemukan
//                                                        Toast.makeText(this@PemesananActivity, "Gagal mendapatkan token pembayaran", Toast.LENGTH_SHORT).show()
//                                                    }
//                                                } else {
//                                                    // Tangani jika request gagal
//                                                    Toast.makeText(this@PemesananActivity, "Terjadi kesalahan dalam pemesanan", Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
//
//                                            override fun onFailure(call: Call<PemesananLangsungResponse>, t: Throwable) {
//                                                // Tangani jika Retrofit gagal melakukan request
//                                                Toast.makeText(this@PemesananActivity, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
//                                            }
//                                        })
                                        dialog.dismiss()
                                    }
                                    setNegativeButton("Batal") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    create()
                                    show()
                                }
                            }
                        }

                        // bayar dengan saldo
                        1 -> {
                            // Aksi untuk "Bayar dengan Saldo"
                            if(is_member == 0){
                                // jika bukan member
                                AlertDialog.Builder(this).apply {
                                    setTitle("Pemberitahuan")
                                    setMessage(
                                        "Maaf, Anda belum terdaftar sebagai member Garuda Sakti Sport Hall.\n" +
                                                "Silahkan mendaftar membership terlebih dahulu atau pilih metode pembayaran lain."
                                    )
                                    setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    create()
                                    show()
                                }
                            } else {
                                // jika member
                                totalHargaPemesanan = jumlahJamDipilih * lapanganHargaMember
                                if (saldo < totalHargaPemesanan){
                                    //jika saldo tidak cukup
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Pemberitahuan")
                                        setMessage(
                                            "Maaf, saldo Anda tidak cukup.\n" +
                                            "Silahkan isi saldo terlebih dahulu atau pilih metode pembayaran lain.\n\n" +
                                            "Saldo Anda saat ini\t: $saldo\n" +
                                            "Total harga pesanan\t: $totalHargaPemesanan"
                                        )
                                        setPositiveButton("OK") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        create()
                                        show()
                                    }
                                } else {
                                    // jika saldo cukup = buat pesanan
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Konfirmasi Pemesanan")
                                        setMessage(
                                            "Anda akan memesan lapangan sebagai berikut:\n\n" +
                                                    "Nama Tim\t\t: $namaTim\n" +
                                                    "Lapangan\t\t: $lapanganNama\n" +
                                                    "Tanggal\t\t\t\t: $tanggaldipilih\n\n" +
                                                    "Jam\t: \n$selectedJam\n\n" +
                                                    "Saldo Anda saat ini\t\t: $saldo\n" +
                                                    "Total harga pesanan\t\t: $totalHargaPemesanan\n" +
                                                    "\nSaldo Anda akan dikurangi sebesar total harga pesanan.\n" +
                                                    "\nYakin?\n"
                                        )
                                        setPositiveButton("Konfirmasi") { dialog, _ ->
                                            val call = apiService.pemesananSaldo("Bearer $token", lapangan_id, tanggaldipilih, jamDipilih, namaTim)
                                            call.enqueue(object : Callback<PemesananSaldoResponse> {
                                                override fun onResponse(call: Call<PemesananSaldoResponse>, response: Response<PemesananSaldoResponse>) {
                                                    if (response.isSuccessful && response.body() != null) {
                                                        val pemesananData = response.body()!!.data
                                                        Toast.makeText(this@PemesananActivity, "Pemesanan Berhasil!", Toast.LENGTH_SHORT).show()

                                                        val sharedPreferences = this@PemesananActivity.getSharedPreferences("user_prefs", MODE_PRIVATE)
                                                        val editor = sharedPreferences.edit()
                                                        editor.putInt("poin", pemesananData.poinTerakhir)
                                                        editor.putInt("saldo", pemesananData.saldoTerakhir)
                                                        editor.apply()

                                                        val pointForAlertShow = pemesananData.poinTerakhir
                                                        val saldoForAlertShow = pemesananData.saldoTerakhir

                                                        AlertDialog.Builder(this@PemesananActivity).apply {
                                                            setTitle("Selamat!")
                                                            setMessage(
                                                                "Pesanan Anda berhasil dibuat.\n" +
                                                                "Anda mendapatkan $jumlahJamDipilih poin.\n\n" +
                                                                "Poin Anda saat ini\t: $pointForAlertShow\n\n" +
                                                                "Sisa saldo Anda\t\t\t: $saldoForAlertShow"
                                                            )
                                                            setPositiveButton("OK") { dialog, _ ->
                                                                dialog.dismiss()
                                                                val intent = Intent(this@PemesananActivity, PesananBerhasilActivity::class.java)
                                                                intent.putExtra("nama_tim", namaTim)
                                                                intent.putExtra("lapangan_name", lapanganNama)
                                                                intent.putExtra("tanggal", tanggaldipilih)
                                                                intent.putExtra("jam", selectedJam)
                                                                intent.putExtra("sisa_saldo", pemesananData.saldoTerakhir)
                                                                startActivity(intent)
                                                                finish()
                                                            }
                                                            create()
                                                            show()
                                                        }
                                                    } else {
                                                        Toast.makeText(this@PemesananActivity, "Gagal melakukan pemesanan: ${response.message()}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                override fun onFailure(call: Call<PemesananSaldoResponse>, t: Throwable) {
                                                    Toast.makeText(this@PemesananActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                            dialog.dismiss()
                                        }
                                        setNegativeButton("Batal") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        create()
                                        show()
                                    }
                                }
                            }
                        }

                        // penukaran poin
                        2 -> {
                            // Aksi untuk "Bayar dengan Poin"
                            if(is_member == 0){
                                // jika bukan member
                                AlertDialog.Builder(this).apply {
                                    setTitle("Pemberitahuan")
                                    setMessage(
                                        "Maaf, Anda belum terdaftar sebagai member Garuda Sakti Sport Hall.\n" +
                                        "Silahkan mendaftar membership terlebih dahulu atau pilih metode pembayaran lain."
                                    )
                                    setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    create()
                                    show()
                                }
                            } else {
                                // jika member
                                totalPoinPemesanan = lapanganHargaPoin * jumlahJamDipilih
                                if (poin < totalPoinPemesanan){
                                    // jika poin tidak cukup
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Pemberitahuan")
                                        setMessage(
                                            "Maaf, poin Anda tidak cukup.\n" +
                                                    "Silahkan pilih metode pembayaran lain.\n\n" +
                                                    "Poin Anda saat ini\t\t: $poin\n" +
                                                    "Total penukaran poin\t: $totalPoinPemesanan"
                                        )
                                        setPositiveButton("OK") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        create()
                                        show()
                                    }
                                } else {
                                    // jika poin cukup = buat pesanan
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Konfirmasi Pemesanan")
                                        setMessage(
                                            "Anda akan memesan lapangan sebagai berikut:\n\n" +
                                                    "Nama Tim\t\t: $namaTim\n" +
                                                    "Lapangan\t\t: $lapanganNama\n" +
                                                    "Tanggal\t\t\t\t: $tanggaldipilih\n\n" +
                                                    "Jam\t: \n$selectedJam\n\n" +
                                                    "Poin Anda saat ini\t\t: $poin\n" +
                                                    "Total penukaran poin\t\t: $totalPoinPemesanan\n" +
                                                    "\nPoin Anda akan dikurangi sebesar total penukaran poin.\n" +
                                                    "\nYakin?\n"
                                        )
                                        setPositiveButton("Konfirmasi") { dialog, _ ->

                                            val call = apiService.pemesananPoin("Bearer $token", lapangan_id, tanggaldipilih, jamDipilih, namaTim)
                                            call.enqueue(object : Callback<PemesananPoinResponse> {
                                                override fun onResponse(call: Call<PemesananPoinResponse>, response: Response<PemesananPoinResponse>) {
                                                    if (response.isSuccessful && response.body() != null) {
                                                        val pemesananData = response.body()!!.data
                                                        Toast.makeText(this@PemesananActivity, "Pemesanan Berhasil!", Toast.LENGTH_SHORT).show()

                                                        val sharedPreferences = this@PemesananActivity.getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
                                                        val editor = sharedPreferences.edit()
                                                        editor.putInt("poin", pemesananData.poinTerakhir)
                                                        editor.apply()

                                                        val pointForAlertShow = pemesananData.poinTerakhir

                                                        AlertDialog.Builder(this@PemesananActivity).apply {
                                                            setTitle("Selamat!")
                                                            setMessage(
                                                                "Pesanan Anda berhasil dibuat.\n" +
                                                                        "Sisa poin Anda\t\t: $pointForAlertShow\n\n"
                                                            )
                                                            setPositiveButton("OK") { dialog, _ ->
                                                                dialog.dismiss()
                                                                val intent = Intent(this@PemesananActivity, PesananBerhasilActivity::class.java)
                                                                intent.putExtra("nama_tim", namaTim)
                                                                intent.putExtra("lapangan_name", lapanganNama)
                                                                intent.putExtra("tanggal", tanggaldipilih)
                                                                intent.putExtra("jam", selectedJam)
                                                                startActivity(intent)
                                                                finish()
                                                            }
                                                            create()
                                                            show()
                                                        }
                                                    } else {
                                                        Toast.makeText(this@PemesananActivity, "Gagal melakukan pemesanan: ${response.message()}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                override fun onFailure(call: Call<PemesananPoinResponse>, t: Throwable) {
                                                    Toast.makeText(this@PemesananActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                            dialog.dismiss()
                                        }
                                        setNegativeButton("Batal") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        create()
                                        show()
                                    }
                                }
                            }
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

    fun tampilkanPembayaranMidtrans(snapToken: String) {
//        val intent = Intent(this, WebViewActivity::class.java)
//        intent.putExtra("snap_token", snapToken)
//        startActivity(intent)
    }

}