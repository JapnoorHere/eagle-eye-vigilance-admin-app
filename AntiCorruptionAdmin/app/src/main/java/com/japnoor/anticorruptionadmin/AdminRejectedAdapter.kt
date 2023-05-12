package com.japnoor.anticorruption.admin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.japnoor.anticorruptionadmin.databinding.ItemComlaintBinding
import com.japnoor.anticorruptionadmin.databinding.ShowUserDeatailsBinding
import com.japnoor.anticorruptionadmin.databinding.StatusDescriptionDialogBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

class AdminRejectedAdapter(
    var context: AdminHomeScreen, var complaintsList: ArrayList<Complaints>,
    var complaintClickedInterface: ComplaintClickedInterface
) : RecyclerView.Adapter<AdminRejectedAdapter.ViewHolder>() {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvAgainst.setText(decrypt(complaintsList[position].complaintAgainst))
        holder.binding.upline.setBackgroundResource(R.drawable.rejectedback)
        holder.binding.downline.setBackgroundResource(R.drawable.rejectedback)
        holder.binding.tvDistrict.setText(decrypt(complaintsList[position].complaintDistrict))
        holder.binding.tvSummary.setText(decrypt(complaintsList[position].complaintDetails))
        holder.binding.compNumber.setText(decrypt(complaintsList[position].complaintNumber))
        holder.binding.time.setText(decrypt(complaintsList[position].complaintTime))
        holder.binding.Date.setText(decrypt(complaintsList[position].complaintDate))
        holder.binding.userName.setText(decrypt(complaintsList[position].userName))
        holder.itemView.setOnClickListener {
            complaintClickedInterface.onComplaintsClicked(complaintsList[position])
        }
        holder.binding.more.setOnClickListener {
            var bottomSheet = BottomSheetDialog(context)
            bottomSheet.setContentView(R.layout.rejected_complaint_menu_hold)
            var chat = bottomSheet.findViewById<CardView>(R.id.chat)
            var block = bottomSheet.findViewById<CardView>(R.id.blockUser)
            var blocktext = bottomSheet.findViewById<TextView>(R.id.blocktext)
            var accepted = bottomSheet.findViewById<CardView>(R.id.accept)
            var resolve = bottomSheet.findViewById<CardView>(R.id.resolve)

            accepted?.setOnClickListener {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    if (complaintsList[position].audioUrl.isNullOrEmpty() && complaintsList[position].imageUrl.isNullOrEmpty()) {
                        var sendEmail = SendEmail()
                        sendEmail.sendComplaint(
                            decrypt(complaintsList[position].userEmail),
                            decrypt(complaintsList[position].userName),
                            decrypt(complaintsList[position].complaintNumber),
                            decrypt(complaintsList[position].complaintAgainst),
                            decrypt(complaintsList[position].complaintDetails),
                            decrypt(complaintsList[position].complaintDept),
                            decrypt(complaintsList[position].complaintCategory),
                            decrypt(complaintsList[position].complaintLoc),
                            decrypt(complaintsList[position].complaintDistrict),"No",decrypt(complaintsList[position].videoUrl),"No")
                    }
                    else if(complaintsList[position].videoUrl.isNullOrEmpty() && complaintsList[position].imageUrl.isNullOrEmpty() ){
                        var sendEmail = SendEmail()
                        sendEmail.sendComplaint(
                            decrypt(complaintsList[position].userEmail),
                            decrypt(complaintsList[position].userName),
                            decrypt(complaintsList[position].complaintNumber),
                            decrypt(complaintsList[position].complaintAgainst),
                            decrypt(complaintsList[position].complaintDetails),
                            decrypt(complaintsList[position].complaintDept),
                            decrypt(complaintsList[position].complaintCategory),
                            decrypt(complaintsList[position].complaintLoc),
                            decrypt(complaintsList[position].complaintDistrict),decrypt(complaintsList[position].audioUrl),"No","No")
                    }
                    else if(complaintsList[position].videoUrl.isNullOrEmpty() && complaintsList[position].audioUrl.isNullOrEmpty()){
                        var sendEmail = SendEmail()
                        sendEmail.sendComplaint(
                            decrypt(complaintsList[position].userEmail),
                            decrypt(complaintsList[position].userName),
                            decrypt(complaintsList[position].complaintNumber),
                            decrypt(complaintsList[position].complaintAgainst),
                            decrypt(complaintsList[position].complaintDetails),
                            decrypt(complaintsList[position].complaintDept),
                            decrypt(complaintsList[position].complaintCategory),
                            decrypt(complaintsList[position].complaintLoc),
                            decrypt(complaintsList[position].complaintDistrict),"No","No",decrypt(complaintsList[position].imageUrl))
                    }
                    var notificationid =
                        FirebaseDatabase.getInstance().reference.child("Notifications")
                            .push().key.toString()
                    val format = SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
                    val notificationTime = format.format(Date())

                    var notification = Notification(
                        notificationid,
                        complaintsList[position].complaintAgainst,
                        encrypt(notificationTime),
                        complaintsList[position].userId,
                        complaintsList[position].complaintId,
                        complaintsList[position].userName,
                        "1",
                        complaintsList[position].complaintNumber, "c"
                    )
                    FirebaseDatabase.getInstance().reference.child("Notifications")
                        .child(notificationid).setValue(notification)
                    bottomSheet.dismiss()
                    FirebaseDatabase.getInstance().reference.child("Complaints").child(
                        complaintsList[position].complaintId
                    ).child("status").setValue("1")
                } else {
                    Toast.makeText(
                        context,
                        "Check your internet connection please",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
            resolve?.setOnClickListener {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    var descriptionDialog = Dialog(context)
                    var descriptionDialogBin =
                        StatusDescriptionDialogBinding.inflate(context.layoutInflater)
                    descriptionDialog.setContentView(descriptionDialogBin.root)
                    descriptionDialog.show()
                    descriptionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    descriptionDialog.setCancelable(false)
                    descriptionDialogBin.cancel.setOnClickListener {
                        descriptionDialog.dismiss()
                    }
                    descriptionDialogBin.btnDone.setOnClickListener {
                        if (descriptionDialogBin.detail.text.toString().trim().length == 0) {
                            descriptionDialogBin.detail.error = "Cannot be Empty"
                        } else {
                            descriptionDialog.dismiss()
                            FirebaseDatabase.getInstance().reference.child("Complaints")
                                .child(complaintsList[position].complaintId)
                                .child("statusDescription")
                                .setValue(encrypt((descriptionDialogBin.detail.text.toString())))
                                .addOnCompleteListener {
                                }
                            var notificationid =
                                FirebaseDatabase.getInstance().reference.child("Notifications")
                                    .push().key.toString()
                            val format = SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
                            val notificationTime = format.format(Date())

                            var notification = Notification(
                                notificationid,
                                complaintsList[position].complaintAgainst,
                                encrypt(notificationTime),
                                complaintsList[position].userId,
                                complaintsList[position].complaintId,
                                complaintsList[position].userName,
                                "2",
                                complaintsList[position].complaintNumber, "c"
                            )
                            FirebaseDatabase.getInstance().reference.child("Notifications")
                                .child(notificationid).setValue(notification)
                            bottomSheet.dismiss()
                            FirebaseDatabase.getInstance().reference.child("Complaints")
                                .child(complaintsList[position].complaintId).child("status")
                                .setValue("2")
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Check your internet connection please",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            var blockvalue = ""
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(complaintsList[position].userId).child("userStatus")
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        blockvalue = task.result?.value as String
                        println("lkhfsjkf" + blockvalue)
                        if (blockvalue.equals("0")) {
                            blocktext?.setText("   Block this User")
                        } else {
                            blocktext?.setText("   Unblock this User")
                        }
                    } else {
                    }
                }
            block?.setOnClickListener {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(complaintsList[position].userId).child("userStatus")
                        .get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                blockvalue = task.result?.value as String
                                println("lkhfsjkf" + blockvalue)
                                if (blockvalue.equals("0")) {
                                    val currentTime = System.currentTimeMillis()
                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(complaintsList[position].userId).child("userStatus")
                                        .setValue(currentTime.toString())
                                    Toast.makeText(
                                        context,
                                        "User has been blocked",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    bottomSheet.dismiss()
                                } else {
                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(complaintsList[position].userId).child("userStatus")
                                        .setValue("0")
                                    Toast.makeText(
                                        context,
                                        "User has been unblocked",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    bottomSheet.dismiss()
                                }
                            } else {
                                // Handle error
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Check your internet connection please",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }



            chat?.setOnClickListener {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(complaintsList[position].userId).child("profileValue").get()
                        .addOnCompleteListener {
                            var intent = Intent(context, ComplaintChatActivity::class.java)
                            intent.putExtra("uid", complaintsList[position].userId)
                            intent.putExtra("cid", complaintsList[position].complaintId)
                            intent.putExtra("name", encrypt(complaintsList[position].userName))
                            intent.putExtra("profile", it.result.value.toString())
                            intent.putExtra("cnumber", encrypt(complaintsList[position].complaintNumber))
                            intent.putExtra("type", "c")
                            intent.putExtra("status", complaintsList[position].status)
                            intent.putExtra("against", encrypt(complaintsList[position].complaintAgainst))
                            context.startActivity(intent)
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Check your internet connection please",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            var userProfile = bottomSheet.findViewById<CardView>(R.id.userProfile)
            userProfile?.setOnClickListener {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    bottomSheet.dismiss()
                    var dialog = Dialog(context)
                    var dialogBinding = ShowUserDeatailsBinding.inflate(context.layoutInflater)
                    dialog.setContentView(dialogBinding.root)
                    dialog.show()
                    dialogBinding.block.visibility = View.GONE
                    dialog.window?.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT
                    )
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (eachUser in snapshot.children) {
                                    var valueUser = eachUser.getValue(Users::class.java)
                                    if (valueUser?.userId.equals(complaintsList[position].userId)) {
                                        dialogBinding.name.setText(decrypt(valueUser?.name.toString()))
                                        dialogBinding.email.setText(decrypt(valueUser?.email.toString()))
                                        dialogBinding.birthdate.setText(decrypt(valueUser?.birthdate.toString()))
                                        dialogBinding.date.setText(decrypt(valueUser?.userDate.toString()))

                                        dialogBinding.cardEmail.setOnClickListener {
                                            val intent = Intent(Intent.ACTION_SEND)
                                            intent.putExtra(
                                                android.content.Intent.EXTRA_EMAIL,
                                                arrayOf(decrypt(valueUser?.email.toString()))
                                            );
                                            intent.type = "message/rfc822"
                                            context.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Select email"
                                                )
                                            )
                                        }
                                        dialogBinding.cardComplaint.setOnClickListener {
                                            dialog.dismiss()
                                            bottomSheet.dismiss()
                                            var bundle = Bundle()
                                            bundle.putString("uid", valueUser?.userId.toString())
                                            context.navController.navigate(
                                                R.id.userComplaintFragment,
                                                bundle
                                            )
                                        }

                                        dialogBinding.cardDemand.setOnClickListener {
                                            dialog.dismiss()
                                            bottomSheet.dismiss()
                                            var bundle = Bundle()
                                            bundle.putString("uid", valueUser?.userId.toString())
                                            context.navController.navigate(
                                                R.id.userDemandFragment,
                                                bundle
                                            )
                                        }

                                        when (valueUser?.profileValue) {
                                            "1" -> dialogBinding.Profile.setImageResource(R.drawable.man1)
                                            "2" -> dialogBinding.Profile.setImageResource(R.drawable.man2)
                                            "3" -> dialogBinding.Profile.setImageResource(R.drawable.man3)
                                            "4" -> dialogBinding.Profile.setImageResource(R.drawable.man4)
                                            "5" -> dialogBinding.Profile.setImageResource(R.drawable.girl1)
                                            "6" -> dialogBinding.Profile.setImageResource(R.drawable.girl2)
                                            "7" -> dialogBinding.Profile.setImageResource(R.drawable.girl3)
                                            "8" -> dialogBinding.Profile.setImageResource(R.drawable.girl4)
                                        }
                                        FirebaseDatabase.getInstance().reference.child("Complaints")
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    var complaintCount = 0
                                                    for (eachComplaint in snapshot.children) {
                                                        var comp =
                                                            eachComplaint.getValue(Complaints::class.java)
                                                        if (comp != null && complaintsList[position].userId.equals(
                                                                comp.userId
                                                            )
                                                        ) {
                                                            complaintCount += 1
                                                            println("Count0     -> " + complaintCount.toString())
                                                            dialogBinding.complaints.setText(
                                                                complaintCount.toString()
                                                            )
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }
                                            })

                                        FirebaseDatabase.getInstance().reference.child("Demand Letter")
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    var demandCount = 0
                                                    for (eachdem in snapshot.children) {
                                                        var dem =
                                                            eachdem.getValue(DemandLetter::class.java)
                                                        if (dem != null && complaintsList[position].userId.equals(
                                                                dem.userId
                                                            )
                                                        ) {
                                                            demandCount += 1
                                                            dialogBinding.demand.setText(demandCount.toString())
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }
                                            })
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                } else {
                    Toast.makeText(
                        context,
                        "Check your internet connection please",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            bottomSheet.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return complaintsList.size
    }

    fun FilteredList(filteredList: ArrayList<Complaints>) {
        complaintsList = filteredList
        notifyDataSetChanged()
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