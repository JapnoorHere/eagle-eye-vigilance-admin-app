package com.japnoor.anticorruptionadmin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.japnoor.anticorruptionadmin.databinding.ItemUserBinding

class UserListAdapter(var context : AdminHomeScreen,var userList: ArrayList<Users>, var clickInterface: UsersClick) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.name.setText(userList[position].name)
        holder.binding.phoneno.setText(userList[position].phone)
        holder.itemView.setOnClickListener{
            clickInterface.onUsersClick(userList[position])
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}