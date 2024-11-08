package com.example.garudasakti.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.garudasakti.R
import com.example.garudasakti.models.LapanganHome

class LapanganAdapter (private var data: ArrayList<LapanganHome>): RecyclerView.Adapter<LapanganAdapter.LapanganHolder>()  {

    private lateinit var LapanganListener: clickListener

    fun setListLapangan (data: ArrayList<LapanganHome>){
        this.data = data
        notifyDataSetChanged()
    }

    interface clickListener {
        fun onItemClick(position: Int)
        fun onDetailClick(position: Int)
        fun onPesanClick(position: Int)
    }

    fun setOnClickListener(listener: clickListener) {
        LapanganListener = listener
    }

    class LapanganHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        //tambahkan inisialisasi view disini
        private val nama: TextView =itemView.findViewById(R.id.textNamaLapangan)
        private val jenis: TextView = itemView.findViewById(R.id.textJenisLapangan)
        private val harga: TextView = itemView.findViewById(R.id.textHargaUmumLapanganHome)
        private val hargaMember: TextView = itemView.findViewById(R.id.textHargaMemberLapanganHome)
        private val hargaPoin: TextView = itemView.findViewById(R.id.textHargaPoinLapanganHome)

        val btnDetail: Button = itemView.findViewById(R.id.buttonDetailLapangan)
        val btnPesan: Button = itemView.findViewById(R.id.buttonPesanLapangan)

        fun bind(data: LapanganHome){
            if (data.jenis_name == "Hybrid"){
                jenis.text = "Badminton/Tenis"
            } else {
                jenis.text = data.jenis_name
            }
            nama.text = data.lapangan_name

            harga.text = data.harga_umum.toString()
            hargaMember.text = data.harga_member.toString()
            hargaPoin.text = data.harga_poin.toString()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LapanganAdapter.LapanganHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lapangan_home, parent, false)
        return LapanganHolder(view)
    }


    override fun onBindViewHolder(holder: LapanganAdapter.LapanganHolder, position: Int) {
        val lapangan = data[position]
        holder.bind(lapangan)

        // Menambahkan click listener untuk item lapangan
        holder.itemView.setOnClickListener {
            LapanganListener.onItemClick(position)
        }

        // Menambahkan click listener untuk tombol detail lapangan
        holder.btnDetail.setOnClickListener {
            LapanganListener.onDetailClick(position)
        }

        // Menambahkan click listener untuk tombol detail lapangan
        holder.btnPesan.setOnClickListener {
            LapanganListener.onPesanClick(position)
        }

    }


    override fun getItemCount(): Int {
        return data.size
    }
}