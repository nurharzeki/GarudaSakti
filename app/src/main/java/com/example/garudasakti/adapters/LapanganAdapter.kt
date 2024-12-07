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
        private val nama: TextView = itemView.findViewById(R.id.textNamaLapangan)
        private val jenis: TextView = itemView.findViewById(R.id.textJenisLapangan)
        private val harga: TextView = itemView.findViewById(R.id.textHargaUmumLapanganHome)
        private val hargaMember: TextView = itemView.findViewById(R.id.textHargaMemberLapanganHome)
        private val hargaPoin: TextView = itemView.findViewById(R.id.textHargaPoinLapanganHome)
        private val textStatus: TextView = itemView.findViewById(R.id.textStatusInactiveItemLapanganHome)

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

            // Display the inactive message and disable interactions
            if (data.status == "inactive") {
                textStatus.visibility = View.VISIBLE
                btnPesan.isEnabled = false
                btnDetail.isEnabled = false
                itemView.isEnabled = false
                itemView.alpha = 0.5f // Optional: Make the item look dimmed
                textStatus.alpha = 1.0f
            } else {
                textStatus.visibility = View.GONE
                btnPesan.isEnabled = true
                btnDetail.isEnabled = true
                itemView.isEnabled = true
                itemView.alpha = 1.0f // Reset appearance
            }

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

        if (lapangan.status == "inactive") {
            // Disable buttons and item click
            holder.itemView.isEnabled = false
            holder.btnPesan.isEnabled = false
            holder.btnDetail.isEnabled = false
            holder.itemView.alpha = 0.5f // Optional: Add transparency to indicate disabled
        } else {
            // Enable buttons and item click
            holder.itemView.isEnabled = true
            holder.btnPesan.isEnabled = true
            holder.btnDetail.isEnabled = true
            holder.itemView.alpha = 1.0f // Reset transparency
        }

        // Handle clicks only if the item is active
        holder.itemView.setOnClickListener {
            if (lapangan.status == "active") {
                LapanganListener.onItemClick(position)
            }
        }

        holder.btnDetail.setOnClickListener {
            if (lapangan.status == "active") {
                LapanganListener.onDetailClick(position)
            }
        }

        holder.btnPesan.setOnClickListener {
            if (lapangan.status == "active") {
                LapanganListener.onPesanClick(position)
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}