package com.japnoor.anticorruption.admin

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener
import com.japnoor.anticorruptionadmin.AdminHomeScreen
import com.japnoor.anticorruptionadmin.ComplaintClickedInterface
import com.japnoor.anticorruptionadmin.Complaints
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.ItemComlaintBinding

class AdminResolvedAdapter(
    var context: AdminHomeScreen, var complaintsList: ArrayList<Complaints>,
    var complaintClickedInterface: ComplaintClickedInterface
) : RecyclerView.Adapter<AdminResolvedAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemComlaintBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemComlaintBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.CardView.setCardBackgroundColor(Color.parseColor("#3BAAFF"))
        holder.binding.tvAgainst.setText(complaintsList[position].complaintAgainst)
        holder.binding.tvSummary.setText(complaintsList[position].complaintSummary)
        holder.binding.Date.setText(complaintsList[position].complaintDate)
        holder.binding.userName.setText(complaintsList[position].userName)
        holder.binding.userName.visibility = View.VISIBLE
        holder.itemView.setOnClickListener {
            complaintClickedInterface.onComplaintsClicked(complaintsList[position])
        }
    }

    override fun getItemCount(): Int {
        return complaintsList.size
    }
}