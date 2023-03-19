package com.japnoor.anticorruption.admin

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.japnoor.anticorruptionadmin.*
import com.japnoor.anticorruptionadmin.databinding.AnnouncementItemBinding
import com.japnoor.anticorruptionadmin.databinding.ItemComlaintBinding
import com.japnoor.anticorruptionadmin.databinding.ShowUserDeatailsBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AnnouncementAdapter(
    var context: AdminHomeScreen, var announcementsList: ArrayList<Announcements>,var announcementsClick: AnnouncementsClick
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {
    class ViewHolder(var binding: AnnouncementItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AnnouncementItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.detail.text=decrypt(announcementsList[position].detail)
        holder.binding.subject.text=decrypt(announcementsList[position].subject)
        holder.binding.time.text=decrypt(announcementsList[position].time)
        holder.itemView.setOnClickListener {
            announcementsClick.onUsersClick(announcementsList[position])
        }
        holder.binding.more.setOnClickListener{
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                var bottomSheet = BottomSheetDialog(context)
                bottomSheet.setContentView(R.layout.announce_menu_hold)
                var delete = bottomSheet.findViewById<CardView>(R.id.delete)
                bottomSheet.show()
                delete?.setOnClickListener {
                    FirebaseDatabase.getInstance().reference.child("Announcements")
                        .child(announcementsList[position].announcementsId).removeValue()
                        .addOnCompleteListener {
                            bottomSheet.dismiss()
                        }
                }
            }
            else{
                Toast.makeText(context,"Check your internet connection please", Toast.LENGTH_LONG).show()

            }
            true
        }

    }

    override fun getItemCount(): Int {
        return announcementsList.size
    }

    private fun decrypt(input: String): String {
        var forgot = ForogotPasscode()
        var encryptionKey=forgot.key()
        var secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")

        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }
    private fun encrypt(input: String): String {
        var forgot = ForogotPasscode()
        var encryptionKey=forgot.key()
        var secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }
}