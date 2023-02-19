package com.japnoor.anticorruption.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context
import com.japnoor.anticorruptionadmin.AdminHomeScreen
import com.japnoor.anticorruptionadmin.DemandClick
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.UserDemandClick
import com.japnoor.anticorruptionadmin.databinding.ItemComlaintBinding
import com.japnoor.anticorruptionadmin.databinding.ItemDemandBinding
import com.japnoor.anticorruptionadmin.databinding.ItemDemandtBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter

class UserDemandAdapter(
    var context: AdminHomeScreen,
    var demandletter: ArrayList<DemandLetter>, var demandClick: UserDemandClick
)  : RecyclerView.Adapter<UserDemandAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemDemandtBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDemandtBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(demandletter[position].status){
            "1"->{
                holder.binding.rightline.setBackgroundResource(R.drawable.acceptedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.acceptedback)
            }
            "2"->{
                holder.binding.rightline.setBackgroundResource(R.drawable.resolvedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.resolvedback)
            }
            "3"->{
                holder.binding.rightline.setBackgroundResource(R.drawable.rejectedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.rejectedback)
            }
            else->{
                holder.binding.rightline.setBackgroundResource(R.drawable.purpleback)
                holder.binding.leftline.setBackgroundResource(R.drawable.purpleback)
            }

        }

        holder.binding.demSubject.setText(demandletter[position].demandSubject)
        holder.binding.demandDetails.setText(demandletter[position].demandDetails)
        holder.binding.time.setText(demandletter[position].demandTime)
        holder.binding.demNumber.setText(demandletter[position].demandNumber)
        holder.binding.date.setText(demandletter[position].demandDate)
        holder.itemView.setOnClickListener{
            demandClick.onClick(demandletter[position])
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