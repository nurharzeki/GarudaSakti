package com.example.garudasakti

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.garudasakti.midtrans.MidtransConfig
import com.example.garudasakti.models.DaftarMemberResponse
import com.example.garudasakti.models.PemesananLangsungResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
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
import kotlin.properties.Delegates

class IsiSaldoPendaftaranActivity : AppCompatActivity() {
    private val token: String by lazy {
        getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }
    private lateinit var paymentLauncher: ActivityResultLauncher<Intent>
    private lateinit var pendingSnapToken: String
    private var nominalIsiSaldo by Delegates.notNull<Int>()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_isi_saldo_pendaftaran)
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
        val apiService = retrofit.create(MainInterface::class.java)
        val btnIsiSaldoDaftar = findViewById<Button>(R.id.buttonIsiSaldoPendaftaranMember)
        btnIsiSaldoDaftar.setOnClickListener {
            nominalIsiSaldo = findViewById<EditText>(R.id.inputSaldoPendaftaranMembership).text.toString().toIntOrNull() ?: 0
            if (nominalIsiSaldo < 50000) {
                Toast.makeText(this, "Harap isi saldo minimal 50000", Toast.LENGTH_SHORT).show()
            } else {
                AlertDialog.Builder(this@IsiSaldoPendaftaranActivity).apply {
                    setTitle("Konfirmasi Pengisian Saldo")
                    setMessage(
                        "Anda akan mengisi saldo sebesar $nominalIsiSaldo untuk melakukan pendaftaran.\n\n" +
                        "Lanjutkan?"
                    )
                    setPositiveButton("Lanjut Bayar") { dialog, _ ->
                        apiService.daftarMember("Bearer $token", nominalIsiSaldo).enqueue(object :
                            Callback<DaftarMemberResponse> {
                            override fun onResponse(call: Call<DaftarMemberResponse>, response: Response<DaftarMemberResponse>) {
                                if (response.isSuccessful) {
                                    val snapToken = response.body()?.snap_token
                                    if (snapToken != null) {
                                        pendingSnapToken = snapToken
                                        tampilkanPembayaranMidtrans(snapToken)
                                    } else {
                                        Log.e("isi response", response.body().toString())
                                        Toast.makeText(this@IsiSaldoPendaftaranActivity, "Gagal mendapatkan token pembayaran", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this@IsiSaldoPendaftaranActivity, "Terjadi kesalahan dalam pemesanan", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<DaftarMemberResponse>, t: Throwable) {
                                Toast.makeText(this@IsiSaldoPendaftaranActivity, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
                            }
                        })
                        dialog.dismiss()
                    }
                    setNegativeButton("Batalkan"){ dialog, _ ->
                        Toast.makeText(this@IsiSaldoPendaftaranActivity, "Pendaftaran dibatalkan", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }
        }
    }
    fun tampilkanPembayaranMidtrans(snapToken: String) {
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            this@IsiSaldoPendaftaranActivity,
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
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("is_member", 1)
                        editor.apply()
                        AlertDialog.Builder(this@IsiSaldoPendaftaranActivity).apply {
                            setTitle("Selamat!")
                            setMessage(
                                "Pendaftaran membership Anda berhasil!\n\n" +
                                "Anda mendapatkan 5 poin."
                            )
                            setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(this@IsiSaldoPendaftaranActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    STATUS_PENDING -> {
                        AlertDialog.Builder(this@IsiSaldoPendaftaranActivity).apply {
                            setTitle("Pembayaran Tertunda")
                            setMessage(
                                "Pembayaran Anda belum selesai, lanjutkan sekarang?"
                            )
                            setPositiveButton("Lanjut Bayar") { dialog, _ ->
                                tampilkanPembayaranMidtrans(pendingSnapToken)
                                dialog.dismiss()
                            }
                            setNegativeButton("Batalkan Pendaftaran"){ dialog, _ ->
                                Toast.makeText(this@IsiSaldoPendaftaranActivity, "Pendaftaran dibatalkan", Toast.LENGTH_SHORT).show()
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
                        AlertDialog.Builder(this@IsiSaldoPendaftaranActivity).apply {
                            setTitle("Pembayaran Tertunda")
                            setMessage(
                                "Pembayaran Anda belum selesai, lanjutkan sekarang?"
                            )
                            setPositiveButton("Lanjut Bayar") { dialog, _ ->
                                tampilkanPembayaranMidtrans(pendingSnapToken)
                                dialog.dismiss()
                            }
                            setNegativeButton("Batalkan Pendaftaran"){ dialog, _ ->
                                Toast.makeText(this@IsiSaldoPendaftaranActivity, "Pendaftaran dibatalkan", Toast.LENGTH_SHORT).show()
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