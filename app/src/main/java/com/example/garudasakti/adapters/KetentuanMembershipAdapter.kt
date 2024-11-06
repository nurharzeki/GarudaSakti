package com.example.garudasakti.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.garudasakti.R
import com.example.garudasakti.models.KetentuanMembershipResponse

class KetentuanMembershipAdapter (private var data: ArrayList<KetentuanMembershipResponse>): RecyclerView.Adapter<KetentuanMembershipAdapter.KetentuanMembershipHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KetentuanMembershipHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ketentuan_membership, parent, false)
        return KetentuanMembershipHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: KetentuanMembershipHolder, position: Int) {
        holder.bind(data[position])
    }

    class KetentuanMembershipHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberList: TextView = itemView.findViewById(R.id.textNumberKetentuanMembership)
        private val contentList: TextView = itemView.findViewById(R.id.textContentKetentuanMembership)

        fun bind(data: KetentuanMembershipResponse) {
            numberList.text = data.number.toString()
            contentList.text = data.content
        }
    }

}