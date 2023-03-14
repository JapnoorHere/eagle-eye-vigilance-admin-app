package com.japnoor.anticorruptionadmin

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.ItemUserBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter

class UserListAdapter(var context : AdminHomeScreen,var userList: ArrayList<Users>, var clickInterface: UsersClick) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.name.setText(userList[position].name)
        holder.binding.email.setText(userList[position].email)
        holder.binding.date.setText(userList[position].userDate)
        holder.itemView.setOnClickListener{
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                clickInterface.onUsersClick(userList[position])
            }
            else{
                Toast.makeText(context,"Check your internet connection please", Toast.LENGTH_LONG).show()

            }
        }
        when (userList[position].profileValue) {
            "1" -> holder.binding.Profile.setImageResource(R.drawable.man1)
            "2" -> holder.binding.Profile.setImageResource(R.drawable.man2)
            "3" -> holder.binding.Profile.setImageResource(R.drawable.man3)
            "4" -> holder.binding.Profile.setImageResource(R.drawable.man4)
            "5" -> holder.binding.Profile.setImageResource(R.drawable.girl1)
            "6" -> holder.binding.Profile.setImageResource(R.drawable.girl2)
            "7" -> holder.binding.Profile.setImageResource(R.drawable.girl3)
            "8" -> holder.binding.Profile.setImageResource(R.drawable.girl4)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun FilteredList(filteredList: ArrayList<Users>) {
        userList=filteredList
        notifyDataSetChanged()

    }

}