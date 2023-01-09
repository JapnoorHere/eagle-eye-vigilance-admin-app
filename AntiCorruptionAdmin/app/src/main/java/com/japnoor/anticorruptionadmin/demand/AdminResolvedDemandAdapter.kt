package com.japnoor.anticorruption.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.core.Context
import com.japnoor.anticorruptionadmin.AdminHomeScreen
import com.japnoor.anticorruptionadmin.DemandClick
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.ItemComlaintBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter

class AdminResolvedDemandAdapter (var context: AdminHomeScreen,var demandletter: ArrayList<DemandLetter>, var demandClick: DemandClick)  : RecyclerView.Adapter<AdminResolvedDemandAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemComlaintBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemComlaintBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.CardView.setCardBackgroundColor(Color.parseColor("#3BAAFF"))
        holder.binding.tvAgainst.setText(demandletter[position].demandSubject)
        holder.binding.tvSummary.setText(demandletter[position].demandDetails)
        holder.binding.Date.setText(demandletter[position].demandDate)
        holder.binding.userName.visibility=View.VISIBLE
        holder.binding.icon.setImageResource(R.drawable.imageitem)
        holder.binding.userName.setText(demandletter[position].userName)
        holder.itemView.setOnClickListener{
            demandClick.onDemandClick(demandletter[position])
        }
    }

    override fun getItemCount(): Int {
        return demandletter.size
    }
}