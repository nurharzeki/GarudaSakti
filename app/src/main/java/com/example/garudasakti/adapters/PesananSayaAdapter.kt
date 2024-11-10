package com.example.garudasakti.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.garudasakti.R
import com.example.garudasakti.models.PesananSaya

class PesananSayaAdapter (private var data: ArrayList<PesananSaya>): RecyclerView.Adapter<PesananSayaAdapter.PesananSayaHolder>()  {

    private lateinit var PesananSayaListener: clickListener

    fun setListPesananSaya (data: ArrayList<PesananSaya>){
        this.data = data
        notifyDataSetChanged()
    }

    interface clickListener {
        fun onItemClick(position: Int)
    }

    fun setOnClickListener(listener: clickListener) {
        PesananSayaListener = listener
    }

    class PesananSayaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headLapangan: TextView = itemView.findViewById(R.id.textNamaLapanganPesananSayaHeader)
        private val lapangan: TextView = itemView.findViewById(R.id.textNamaLapanganPesananSaya)
        private val customerName: TextView = itemView.findViewById(R.id.textNamaPemesanPesananSaya)
        private val namaTim: TextView = itemView.findViewById(R.id.textNamaTimPesananSaya)
        private val tanggal: TextView = itemView.findViewById(R.id.textTanggalPesananSaya)
        private val jam: TextView = itemView.findViewById(R.id.textJamPesananSaya)

        fun bind(data: PesananSaya) {
            headLapangan.text = data.lapangan
            lapangan.text = data.lapangan
            customerName.text = data.customerName
            namaTim.text = data.namaTim
            tanggal.text = data.tanggal
            jam.text = data.jam
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PesananSayaAdapter.PesananSayaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pesanan_saya, parent, false)
        return PesananSayaHolder(view)
    }


    override fun onBindViewHolder(holder: PesananSayaAdapter.PesananSayaHolder, position: Int) {
        val lapangan = data[position]
        holder.bind(lapangan)

        // Menambahkan click listener untuk item lapangan
        holder.itemView.setOnClickListener {
            PesananSayaListener.onItemClick(position)
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }
}