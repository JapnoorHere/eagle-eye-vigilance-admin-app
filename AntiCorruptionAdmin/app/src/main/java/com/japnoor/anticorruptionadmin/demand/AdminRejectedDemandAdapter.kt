package com.japnoor.anticorruption.admin

import android.app.Dialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
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
import com.japnoor.anticorruptionadmin.databinding.ItemDemandBinding
import com.japnoor.anticorruptionadmin.databinding.ShowUserDeatailsBinding
import com.japnoor.anticorruptionadmin.databinding.StatusDescriptionDialogBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

class AdminRejectedDemandAdapter (var context: AdminHomeScreen,var demandletter: ArrayList<DemandLetter>, var demandClick: DemandClick)  : RecyclerView.Adapter<AdminRejectedDemandAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemDemandBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDemandBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.upline.setBackgroundResource(R.drawable.rejectedback)
        holder.binding.downline.setBackgroundResource(R.drawable.rejectedback)
        holder.binding.tvAgainst.setText(decrypt(demandletter[position].demandSubject))
        holder.binding.tvSummary.setText(decrypt(demandletter[position].demandDetails))
        holder.binding.time.setText(decrypt(demandletter[position].demandTime))
        holder.binding.compNumber.setText(decrypt(demandletter[position].demandNumber))
        holder.binding.Date.setText(decrypt(demandletter[position].demandDate))
        holder.binding.userName.setText(decrypt(demandletter[position].userName))

        holder.itemView.setOnClickListener{
            demandClick.onDemandClick(demandletter[position])
        }
        holder.binding.more.setOnClickListener{
            var bottomSheet = BottomSheetDialog(context)
            bottomSheet.setContentView(R.layout.rejected_complaint_menu_hold)
            var chat=bottomSheet.findViewById<CardView>(R.id.chat)
            var block=bottomSheet.findViewById<CardView>(R.id.blockUser)
            var blocktext=bottomSheet.findViewById<TextView>(R.id.blocktext)
            var blockvalue = ""
            var accepted=bottomSheet.findViewById<CardView>(R.id.accept)
            var resolve=bottomSheet.findViewById<CardView>(R.id.resolve)

            accepted?.setOnClickListener {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    var sendEmail = SendEmail()
                    sendEmail.sendDemand(
                        decrypt(demandletter[position].userName),
                        decrypt(demandletter[position].userEmail),
                        decrypt(demandletter[position].demandNumber),
                        decrypt(demandletter[position].demandDistrict),
                        decrypt(demandletter[position].demandSubject),
                        decrypt(demandletter[position].demandDetails),
                        decrypt(demandletter[position].unionName),
                        decrypt(demandletter[position].imageUrl)
                    )
                    var notificationid =
                        FirebaseDatabase.getInstance().reference.child("Notifications")
                            .push().key.toString()
                    val format = SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
                    val notificationTime = format.format(Date())
                    var notification = Notification(
                        notificationid,
                        demandletter[position].demandSubject,
                        encrypt(notificationTime),
                        demandletter[position].userId,
                        demandletter[position].demandId,
                        demandletter[position].userName,
                        "1",
                        demandletter[position].demandNumber, "d"
                    )
                    FirebaseDatabase.getInstance().reference.child("Notifications")
                        .child(notificationid).setValue(notification)
                    bottomSheet.dismiss()
                    FirebaseDatabase.getInstance().reference.child("Demand Letter")
                        .child(demandletter[position].demandId).child("status").setValue("1")
                }
                else{
                    Toast.makeText(context,"Check your internet connection please", Toast.LENGTH_LONG).show()
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
                    descriptionDialogBin.detail.setHint("Details about Resolving Demand")
                    descriptionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    descriptionDialog.setCancelable(false)
                    descriptionDialogBin.cancel.setOnClickListener {
                        descriptionDialog.dismiss()
                    }
                    descriptionDialogBin.btnDone.setOnClickListener {
                        if (descriptionDialogBin.detail.text.toString().trim().length == 0) {
                            descriptionDialogBin.detail.error = "Cannot be Empty"
                        } else {
                            FirebaseDatabase.getInstance().reference.child("Demand Letter").child(demandletter[position].demandId).child("statusDescription")
                                .setValue(encrypt(descriptionDialogBin.detail.text.toString()))
                                .addOnCompleteListener {
                                    descriptionDialog.dismiss()
                                }
                            bottomSheet.dismiss()
                            FirebaseDatabase.getInstance().reference.child("Demand Letter")
                                .child(demandletter[position].demandId).child("status")
                                .setValue("2")
                            var notificationid =
                                FirebaseDatabase.getInstance().reference.child("Notifications")
                                    .push().key.toString()
                            val format = SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
                            val notificationTime = format.format(Date())
                            var notification = Notification(
                                notificationid,
                                demandletter[position].demandSubject,
                                encrypt(notificationTime),
                                demandletter[position].userId,
                                demandletter[position].demandId,
                                demandletter[position].userName,
                                "2",
                                demandletter[position].demandNumber, "d"
                            )
                            FirebaseDatabase.getInstance().reference.child("Notifications")
                                .child(notificationid).setValue(notification)
                        }
                    }
                }
                else{
                    Toast.makeText(context,"Check your internet connection please", Toast.LENGTH_LONG).show()

                }
            }

            FirebaseDatabase.getInstance().reference.child("Users").child(demandletter[position].userId).child("userStatus")
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        blockvalue = task.result?.value as String
                        println("lkhfsjkf" + blockvalue)
                        if(blockvalue.equals("0")){
                            blocktext?.setText("   Block this User")
                        }
                        else{
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
                FirebaseDatabase.getInstance().reference.child("Users").child(demandletter[position].userId).child("userStatus")
                    .get().addOnCompleteListener { task->
                        if (task.isSuccessful) {
                            blockvalue = task.result?.value as String
                            println("lkhfsjkf" + blockvalue)
                            if(blockvalue.equals("0")){
                                val currentTime = System.currentTimeMillis()
                                FirebaseDatabase.getInstance().reference.child("Users").child(demandletter[position].userId).child("userStatus")
                                    .setValue(currentTime.toString())
                                Toast.makeText(context, "User has been blocked", Toast.LENGTH_SHORT).show()
                                bottomSheet.dismiss()
                            }
                            else{
                                FirebaseDatabase.getInstance().reference.child("Users").child(demandletter[position].userId).child("userStatus")
                                    .setValue("0")
                                Toast.makeText(context, "User has been unblocked", Toast.LENGTH_SHORT).show()
                                bottomSheet.dismiss()
                            }
                        } else {
                            // Handle error
                        }
                    }
                }
                else{
                    Toast.makeText(context,"Check your internet connection please", Toast.LENGTH_LONG).show()

                }

            }

            chat?.setOnClickListener {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(demandletter[position].userId).child("profileValue").get()
                        .addOnCompleteListener {
                            var intent = Intent(context, ComplaintChatActivity::class.java)
                            intent.putExtra("uid", demandletter[position].userId)
                            intent.putExtra("cid", demandletter[position].demandId)
                            intent.putExtra("name", decrypt(demandletter[position].userName))
                            intent.putExtra("profile", it.result.value.toString())
                            intent.putExtra("cnumber", decrypt(demandletter[position].demandNumber))
                            intent.putExtra("type", "d")
                            intent.putExtra("status", demandletter[position].status)
                            intent.putExtra("against", decrypt(demandletter[position].demandSubject))
                            context.startActivity(intent)
                        }
                }
                else{
                    Toast.makeText(context,"Check your internet connection please", Toast.LENGTH_LONG).show()

                }
            }

            var userProfile=bottomSheet.findViewById<CardView>(R.id.userProfile)
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
                        .addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (eachUser in snapshot.children) {
                                    var valueUser = eachUser.getValue(Users::class.java)
                                    if (valueUser?.userId.equals(demandletter[position].userId)) {
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
                                            .addValueEventListener(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    var complaintCount = 0
                                                    for (eachComplaint in snapshot.children) {
                                                        var comp =
                                                            eachComplaint.getValue(Complaints::class.java)
                                                        if (comp != null && demandletter[position].userId.equals(
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
                                            .addValueEventListener(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    var demandCount = 0
                                                    for (eachdem in snapshot.children) {
                                                        var dem =
                                                            eachdem.getValue(DemandLetter::class.java)
                                                        if (dem != null && demandletter[position].userId.equals(
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

                }
                else{
                    Toast.makeText(context,"Check your internet connection please", Toast.LENGTH_LONG).show()

                }
            }
            bottomSheet.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return demandletter.size
    }

    fun FilteredList(filteredList: ArrayList<DemandLetter>) {
        demandletter=filteredList
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