package com.example.garudasakti.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.garudasakti.R
import com.example.garudasakti.models.Lapangan

class LapanganAdapter (private var data: ArrayList<Lapangan>): RecyclerView.Adapter<LapanganAdapter.LapanganHolder>()  {

    private lateinit var LapanganListener: clickListener

    fun setListLapangan (data: ArrayList<Lapangan>){
        this.data = data
        notifyDataSetChanged()
    }

    interface clickListener {
        fun onItemClick(position: Int)
    }

    fun setOnClickListener(listener: clickListener) {
        LapanganListener = listener
    }

    class LapanganHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        //tambahkan inisialisasi view disini
        private val nama: TextView =itemView.findViewById(R.id.textNamaLapangan)
        private val jenis: TextView = itemView.findViewById(R.id.textJenisLapangan)
        private val harga: TextView = itemView.findViewById(R.id.textHargaLapangan)

        fun bind(data: Lapangan){
            nama.text = data.nama
            jenis.text = data.jenis
            harga.text = data.harga.toString()
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
    }


    override fun getItemCount(): Int {
        return data.size
    }
}