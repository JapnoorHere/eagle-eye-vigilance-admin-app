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
import com.japnoor.anticorruptionadmin.databinding.ItemDemandBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter

class AdminAcceptedDemandAdapter (var context: AdminHomeScreen,var demandletter: ArrayList<DemandLetter>, var demandClick: DemandClick)  : RecyclerView.Adapter<AdminAcceptedDemandAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemDemandBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDemandBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.upline.setBackgroundResource(R.drawable.acceptedback)
        holder.binding.downline.setBackgroundResource(R.drawable.acceptedback)
        holder.binding.tvAgainst.setText(demandletter[position].demandSubject)
        holder.binding.tvSummary.setText(demandletter[position].demandDetails)
        holder.binding.time.setText(demandletter[position].demandTime)
        holder.binding.compNumber.setText(demandletter[position].demandNumber)
        holder.binding.Date.setText(demandletter[position].demandDate)
        holder.binding.userName.setText(demandletter[position].userName)

        holder.itemView.setOnClickListener{
            demandClick.onDemandClick(demandletter[position])
        }
    }

    override fun getItemCount(): Int {
        return demandletter.size
    }

    fun FilteredList(filteredList: ArrayList<DemandLetter>) {
        demandletter=filteredList
        notifyDataSetChanged()

    }
}