package com.japnoor.anticorruptionadmin

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.japnoor.anticorruptionadmin.AdminHomeScreen
import com.japnoor.anticorruptionadmin.Notification
import com.japnoor.anticorruptionadmin.NotificationClick
import com.japnoor.anticorruptionadmin.databinding.NotificationItemBinding
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class NotificationAdapter(
    var context: AdminHomeScreen,
    var notificationList: ArrayList<NotificationChat>,
    var notificationClick: NotificationClick
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    class ViewHolder(var binding: NotificationItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(
            NotificationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (notificationList[position].notificationType) {
            "c" -> {
                holder.binding.compnum.setText("Complaint Number : ")
                holder.binding.aga.setText("Against : ")
                holder.binding.compAgainst.setText(decrypt(notificationList[position].complaintAgainst))
                holder.binding.msg.setText(decrypt(notificationList[position].notificationMsg))
                holder.binding.time.setText(decrypt(notificationList[position].notificationTime))
                holder.binding.name.setText(decrypt(notificationList[position].userName))
                holder.binding.complaintNumber.setText(decrypt(notificationList[position].complaintNumber))
            }
            "d" -> {
                holder.binding.compnum.setText("Demand Number : ")
                holder.binding.aga.setText("Subject : ")
                holder.binding.compAgainst.setText(decrypt(notificationList[position].complaintAgainst))
                holder.binding.msg.setText(decrypt(notificationList[position].notificationMsg))
                holder.binding.time.setText(decrypt(notificationList[position].notificationTime))
                holder.binding.name.setText(decrypt(notificationList[position].userName))
                holder.binding.complaintNumber.setText(decrypt(notificationList[position].complaintNumber))
            }
            "b" -> {
                holder.binding.compnum.setText("User Blocked")
                holder.binding.complaintNumber.visibility=View.GONE
                holder.binding.aga.visibility=View.GONE
                holder.binding.compAgainst.visibility=View.GONE
                holder.binding.msg.setText(decrypt(notificationList[position].notificationMsg))
                holder.binding.time.setText(decrypt(notificationList[position].notificationTime))
                holder.binding.name.setText(decrypt(notificationList[position].userName))

            }

        }

        holder.itemView.setOnClickListener {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                when (notificationList[position].notificationType) {
                    "c" -> {
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(notificationList[position].userId).child("profileValue").get()
                            .addOnCompleteListener {
                                var intent = Intent(context, ComplaintChatActivity::class.java)
                                intent.putExtra("uid", notificationList[position].userId)
                                intent.putExtra("cid", notificationList[position].complaintId)
                                intent.putExtra("name", decrypt(notificationList[position].userName))
                                intent.putExtra("profile", it.result.value.toString())
                                intent.putExtra(
                                    "cnumber",
                                    decrypt(notificationList[position].complaintNumber)
                                )
                                intent.putExtra("type", "c")
                                intent.putExtra(
                                    "status",
                                    notificationList[position].complaintStatus
                                )
                                intent.putExtra(
                                    "against",
                                    decrypt(notificationList[position].complaintAgainst)
                                )
                                context.startActivity(intent)
                            }
                    }
                    "d" -> {
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(notificationList[position].userId).child("profileValue").get()
                            .addOnCompleteListener {
                                var intent = Intent(context, ComplaintChatActivity::class.java)
                                intent.putExtra("uid", notificationList[position].userId)
                                intent.putExtra("cid", notificationList[position].complaintId)
                                intent.putExtra("name", decrypt(notificationList[position].userName))
                                intent.putExtra("profile", it.result.value.toString())
                                intent.putExtra(
                                    "cnumber",
                                    decrypt(notificationList[position].complaintNumber)
                                )
                                intent.putExtra("type", "d")
                                intent.putExtra(
                                    "status",
                                    notificationList[position].complaintStatus
                                )
                                intent.putExtra(
                                    "against",
                                    decrypt(notificationList[position].complaintAgainst)
                                )
                                context.startActivity(intent)
                            }
                    }

                    "b" -> {
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(notificationList[position].userId).child("profileValue").get()
                            .addOnCompleteListener {
                                var intent = Intent(context, ChatActivity::class.java)
                                intent.putExtra("uid", notificationList[position].userId)
                                intent.putExtra("name", decrypt(notificationList[position].userName))
                                intent.putExtra("profile", it.result.value.toString())
                                context.startActivity(intent)
                            }
                    }

                }
            }
            else{
                Toast.makeText(context,"Check your internet connection please", Toast.LENGTH_LONG).show()

            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
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
}
