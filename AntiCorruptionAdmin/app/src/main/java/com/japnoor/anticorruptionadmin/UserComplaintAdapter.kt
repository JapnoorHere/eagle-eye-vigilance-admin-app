package com.japnoor.anticorruptionadmin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.japnoor.anticorruptionadmin.databinding.ItemComplaintBinding

class UserComplaintAdapter(var context: AdminHomeScreen, var complaintsList: ArrayList<Complaints>,var userComplaintClick: UserComplaintClick)  : RecyclerView.Adapter<UserComplaintAdapter.ViewHolder>() {


    class ViewHolder(var binding: ItemComplaintBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemComplaintBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.compAgainst.setText(complaintsList[position].complaintAgainst)
        holder.binding.tvDistrict.setText(complaintsList[position].complaintDistrict)
        holder.binding.compdetails.setText(complaintsList[position].complaintDetails)
        holder.binding.date.setText(complaintsList[position].complaintDate)
        holder.binding.time.setText(complaintsList[position].complaintTime)
        holder.binding.complaintNumber.setText(complaintsList[position].complaintNumber)
        when (complaintsList[position].status) {
            "1" -> {
                holder.binding.rightline.setBackgroundResource(R.drawable.acceptedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.acceptedback)
//                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_file_download_done_24)
            }
            "2" -> {
                holder.binding.rightline.setBackgroundResource(R.drawable.resolvedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.resolvedback)
                //                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_cancel_24)
            }
            "3" -> {
                holder.binding.rightline.setBackgroundResource(R.drawable.rejectedback)
                holder.binding.leftline.setBackgroundResource(R.drawable.rejectedback)
//                holder.binding.inpriority.setImageResource(R.drawable.ic_baseline_done_all_24)
            }
            else->{
//                holder.binding.inpriority.setImageResource(0)
            }

        }


        holder.itemView.setOnClickListener{
            userComplaintClick.onClick(complaintsList[position])
        }
    }

    override fun getItemCount(): Int {
        return complaintsList.size
    }

//    fun getuserId(position: Int): Complaints {
//        return complaintsList[position]
//    }

    fun FilteredList(filteredList: ArrayList<Complaints>) {
        complaintsList=filteredList
        notifyDataSetChanged()
    }

}
