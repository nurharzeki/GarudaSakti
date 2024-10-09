package com.example.garudasakti

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.garudasakti.adapters.LapanganAdapter
import com.example.garudasakti.models.Lapangan

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var lapanganAdapter: LapanganAdapter
    private lateinit var lapanganList: ArrayList<Lapangan>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Dummy data untuk lapangan
        lapanganList = arrayListOf(
            Lapangan("Lapangan 1", "Badminton", "Karpet", 50000, 47000),
            Lapangan("Lapangan 2", "Hybrid", "Beton", 35000, 32000),
            Lapangan("Lapangan 3", "Badminton", "Karpet", 50000, 47000),
            Lapangan("Lapangan 4", "Hybrid", "Beton", 35000, 32000),
            // Tambahkan lapangan lainnya sesuai kebutuhan
        )

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.rv_lapangan)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter dan set adapter ke RecyclerView
        lapanganAdapter = LapanganAdapter(lapanganList)
        recyclerView.adapter = lapanganAdapter

        // Set listener untuk item klik
        lapanganAdapter.setOnClickListener(object : LapanganAdapter.clickListener {
            override fun onItemClick(position: Int) {
                // Tindakan ketika item diklik, misalnya menampilkan detail lapangan
                val clickedLapangan = lapanganList[position]
                // Lakukan sesuatu dengan clickedLapangan
                // Misalnya, buka Activity detail lapangan atau tampilkan Toast
            }
        })

    }
}