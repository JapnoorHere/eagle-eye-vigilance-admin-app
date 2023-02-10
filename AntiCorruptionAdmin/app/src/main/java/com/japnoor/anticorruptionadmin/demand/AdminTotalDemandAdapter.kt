package com.japnoor.anticorruption.admin

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

class AdminTotalDemandAdapter (var context: AdminHomeScreen,var demandletter: ArrayList<DemandLetter>, var demandClick: DemandClick)  : RecyclerView.Adapter<AdminTotalDemandAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemDemandBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDemandBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(demandletter[position].status){
            "1"->{
                holder.binding.upline.setBackgroundResource(R.drawable.acceptedback)
                holder.binding.downline.setBackgroundResource(R.drawable.acceptedback)
            }
            "2"->{
                holder.binding.upline.setBackgroundResource(R.drawable.resolvedback)
                holder.binding.downline.setBackgroundResource(R.drawable.resolvedback)
            }
            "3"->{
                holder.binding.upline.setBackgroundResource(R.drawable.rejectedback)
                holder.binding.downline.setBackgroundResource(R.drawable.rejectedback)
            }
            else->{
                holder.binding.upline.setBackgroundResource(R.drawable.purpleback)
                holder.binding.downline.setBackgroundResource(R.drawable.purpleback)
            }

        }

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