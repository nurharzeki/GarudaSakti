package com.example.garudasakti

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.R.id.navBarMembership
import com.example.garudasakti.midtrans.MidtransConfig
import com.example.garudasakti.models.DaftarMemberResponse
import com.example.garudasakti.models.IsiSaldoResponse
import com.example.garudasakti.models.MemberResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_FAILED
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_INVALID
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_PENDING
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_SUCCESS
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import com.midtrans.sdk.uikit.internal.util.UiKitConstants.STATUS_CANCELED
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MembershipActivity : AppCompatActivity() {
    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }
    private val customer_name: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("customer_name", "") ?: ""
    }
    private lateinit var apiInterface: MainInterface
    private lateinit var paymentLauncher: ActivityResultLauncher<Intent>
    private lateinit var pendingSnapToken: String
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
        val midtrans = MidtransConfig()
        midtrans.getMidtrans(applicationContext)
        paymentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == RESULT_OK) {
                result.data?.let {
                    val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                }
            }
        }
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        apiInterface = retrofit.create(MainInterface::class.java)
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
        val buttonKetentuanMembershipMember = findViewById<Button>(R.id.buttonKetentuanMembershipMember)
        val buttonKetentuanMembershipNonMember = findViewById<Button>(R.id.buttonKetentuanMembershipNonMember)
        buttonKetentuanMembershipNonMember.setOnClickListener {
            val intent = Intent(this, KetentuanMembershipActivity::class.java)
            startActivity(intent)
        }
        buttonKetentuanMembershipMember.setOnClickListener {
            val intent = Intent(this, KetentuanMembershipActivity::class.java)
            startActivity(intent)
        }
        val layoutMember = findViewById<ConstraintLayout>(R.id.layoutMember)
        val layoutNonMember = findViewById<ConstraintLayout>(R.id.layoutNonMember)
        fetchMemberData(layoutMember, layoutNonMember)
        val btnDaftarMembership = findViewById<Button>(R.id.buttonDaftarMember)
        btnDaftarMembership.setOnClickListener {
            val checkbox = CheckBox(this).apply {
                text = "Saya telah membaca dan memahami ketentuan membership."
            }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Konfirmasi Pendaftaran")
                .setMessage("Anda akan mendaftar menjadi member Garuda Sakti Sport Hall.\n\nLanjutkan?\n")
                .setView(checkbox)
                .setPositiveButton("Lanjutkan") { dialog, _ ->
                    val intent = Intent(this, IsiSaldoPendaftaranActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.isEnabled = false
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                positiveButton.isEnabled = isChecked
            }
        }
        val btnIsiSaldoMember = findViewById<Button>(R.id.buttonIsiSaldoMember)
        btnIsiSaldoMember.setOnClickListener {
            val inputNominal = EditText(this).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
            }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Pilih nominal isi saldo (Minimal : 50000)")
                .setView(inputNominal)
                .setPositiveButton("Lanjutkan") { dialog, _ ->
                    val nominal = inputNominal.text.toString().toInt()
                    if(nominal < 50000){
                        Toast.makeText(this, "Harap isi nominal minimal 50000", Toast.LENGTH_SHORT).show()
                    } else {
                        AlertDialog.Builder(this@MembershipActivity).apply {
                            setTitle("Konfirmasi Pengisian Saldo")
                            setMessage(
                                "Anda akan mengisi saldo sebesar Rp$nominal.\n\n" +
                                "Lanjutkan?"
                            )
                            setPositiveButton("Lanjut bayar") { dialog, _ ->
                                apiInterface.isiSaldo("Bearer $token", nominal).enqueue(object :
                                    Callback<IsiSaldoResponse> {
                                    override fun onResponse(call: Call<IsiSaldoResponse>, response: Response<IsiSaldoResponse>) {
                                        if (response.isSuccessful) {
                                            val snapToken = response.body()?.snap_token
                                            if (snapToken != null) {
                                                pendingSnapToken = snapToken
                                                tampilkanPembayaranMidtrans(snapToken)
                                            } else {
                                                Log.e("isi response", response.body().toString())
                                                Toast.makeText(this@MembershipActivity, "Gagal mendapatkan token pembayaran", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(this@MembershipActivity, "Terjadi kesalahan dalam pemesanan", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    override fun onFailure(call: Call<IsiSaldoResponse>, t: Throwable) {
                                        Toast.makeText(this@MembershipActivity, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
                                    }
                                })
                                dialog.dismiss()
                            }
                            setNegativeButton("Batal"){ dialog, _ ->
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }
    private fun fetchMemberData(layoutMember: ConstraintLayout, layoutNonMember: ConstraintLayout) {
        apiInterface.getMemberData("Bearer $token").enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful && response.body() != null ) {
                    if (response.body()?.membership_status == 1){
                        val member = response.body()!!.data
                        if (member != null) {
                            findViewById<TextView>(R.id.textNamaMember).text = member.customer_name
                            findViewById<TextView>(R.id.textSaldoMember).text = member.saldo.toString()
                            findViewById<TextView>(R.id.textPoinMember).text = member.poin.toString()
                        }
                        layoutMember.visibility = View.VISIBLE
                        layoutNonMember.visibility = View.GONE
                    } else {
                        layoutMember.visibility = View.GONE
                        layoutNonMember.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@MembershipActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(this@MembershipActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun tampilkanPembayaranMidtrans(snapToken: String) {
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            this@MembershipActivity,
            paymentLauncher,
            snapToken
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val transactionResult = data?.getParcelableExtra<TransactionResult>(
                UiKitConstants.KEY_TRANSACTION_RESULT
            )
            if (transactionResult != null) {
                when (transactionResult.status) {
                    STATUS_SUCCESS -> {
                        AlertDialog.Builder(this@MembershipActivity).apply {
                            setTitle("Selamat!")
                            setMessage(
                                "Pengisian Saldo Berhasil"
                            )
                            setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(this@MembershipActivity, MembershipActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    STATUS_PENDING -> {
                        AlertDialog.Builder(this@MembershipActivity).apply {
                            setTitle("Pembayaran Tertunda")
                            setMessage(
                                "Pembayaran Anda belum selesai, lanjutkan sekarang?"
                            )
                            setPositiveButton("Lanjut Bayar") { dialog, _ ->
                                tampilkanPembayaranMidtrans(pendingSnapToken)
                                dialog.dismiss()
                            }
                            setNegativeButton("Batalkan Pendaftaran"){ dialog, _ ->
                                Toast.makeText(this@MembershipActivity, "Pendaftaran dibatalkan", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }
                    }
                    STATUS_FAILED -> {
                        Toast.makeText(this, "Transaksi Gagal.", Toast.LENGTH_SHORT).show()
                    }
                    STATUS_CANCELED -> {
                        AlertDialog.Builder(this@MembershipActivity).apply {
                            setTitle("Pembayaran Tertunda")
                            setMessage(
                                "Pembayaran Anda belum selesai, lanjutkan sekarang?"
                            )
                            setPositiveButton("Lanjut Bayar") { dialog, _ ->
                                tampilkanPembayaranMidtrans(pendingSnapToken)
                                dialog.dismiss()
                            }
                            setNegativeButton("Batalkan Pendaftaran"){ dialog, _ ->
                                Toast.makeText(this@MembershipActivity, "Pendaftaran dibatalkan", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }
                    }
                    STATUS_INVALID -> {
                        Toast.makeText(this, "Transaction Invalid.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "Transaction Invalid.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}