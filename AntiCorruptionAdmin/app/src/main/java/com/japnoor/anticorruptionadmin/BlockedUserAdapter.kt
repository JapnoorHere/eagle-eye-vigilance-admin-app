package com.japnoor.anticorruptionadmin

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.ItemUserBinding
import com.japnoor.anticorruptionadmin.databinding.ItemUserBlockedBinding


class BlockedUserAdapter(var context : AdminHomeScreen,var userList: ArrayList<Users>, var clickInterface: UsersClick) : RecyclerView.Adapter<BlockedUserAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemUserBlockedBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBlockedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.name.setText(userList[position].name)
        holder.binding.email.setText(userList[position].email)
        holder.binding.date.setText(userList[position].userDate)
        holder.itemView.setOnClickListener{
            var bottomSheet = BottomSheetDialog(context)
            bottomSheet.setContentView(R.layout.user_menu_hold)
            bottomSheet.show()
            var userProfile=bottomSheet.findViewById<CardView>(R.id.userProfile)
            var chat=bottomSheet.findViewById<CardView>(R.id.chat)

            chat?.setOnClickListener {
                bottomSheet.dismiss()
                var intent = Intent(context,ChatActivity::class.java)
                intent.putExtra("uid",userList[position].userId)
                intent.putExtra("name",userList[position].name)
                intent.putExtra("profile",userList[position].profileValue)
                context.startActivity(intent)
            }
             userProfile?.setOnClickListener {
                 bottomSheet.dismiss()
            clickInterface.onUsersClick(userList[position])
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