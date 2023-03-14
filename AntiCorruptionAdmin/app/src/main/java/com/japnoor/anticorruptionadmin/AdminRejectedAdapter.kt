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
        holder.binding.tvAgainst.setText(complaintsList[position].complaintAgainst)
        holder.binding.upline.setBackgroundResource(R.drawable.rejectedback)
        holder.binding.downline.setBackgroundResource(R.drawable.rejectedback)
        holder.binding.tvDistrict.setText(complaintsList[position].complaintDistrict)
        holder.binding.tvSummary.setText(complaintsList[position].complaintDetails)
        holder.binding.compNumber.setText(complaintsList[position].complaintNumber)
        holder.binding.time.setText(complaintsList[position].complaintTime)
        holder.binding.Date.setText(complaintsList[position].complaintDate)
        holder.binding.userName.setText(complaintsList[position].userName)
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
                    if (complaintsList[position].audioUrl.isNullOrEmpty()) {
                        var sendEmail = SendEmail()
                        sendEmail.sendComplaint(
                            complaintsList[position].userEmail,
                            complaintsList[position].userName,
                            complaintsList[position].complaintNumber,
                            complaintsList[position].complaintAgainst,
                            complaintsList[position].complaintDetails,
                            complaintsList[position].complaintDept,
                            complaintsList[position].complaintCategory,
                            complaintsList[position].complaintLoc,
                            complaintsList[position].complaintDistrict,"No",complaintsList[position].videoUrl)
                    }
                    else if(complaintsList[position].videoUrl.isNullOrEmpty()){
                        var sendEmail = SendEmail()
                        sendEmail.sendComplaint(
                            complaintsList[position].userEmail,
                            complaintsList[position].userName,
                            complaintsList[position].complaintNumber,
                            complaintsList[position].complaintAgainst,
                            complaintsList[position].complaintDetails,
                            complaintsList[position].complaintDept,
                            complaintsList[position].complaintCategory,
                            complaintsList[position].complaintLoc,
                            complaintsList[position].complaintDistrict,complaintsList[position].audioUrl,"No")
                    }
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
                            FirebaseDatabase.getInstance().reference.child("Complaints")
                                .child(complaintsList[position].complaintId)
                                .child("statusDescription")
                                .setValue(descriptionDialogBin.detail.text.toString())
                                .addOnCompleteListener {
                                    descriptionDialog.dismiss()
                                }
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
                            intent.putExtra("name", complaintsList[position].userName)
                            intent.putExtra("profile", it.result.value.toString())
                            intent.putExtra("cnumber", complaintsList[position].complaintNumber)
                            intent.putExtra("type", "c")
                            intent.putExtra("status", complaintsList[position].status)
                            intent.putExtra("against", complaintsList[position].complaintAgainst)
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
                                        dialogBinding.name.setText(valueUser?.name)
                                        dialogBinding.email.setText(valueUser?.email)
                                        dialogBinding.birthdate.setText(valueUser?.birthdate)
                                        dialogBinding.date.setText(valueUser?.userDate)

                                        dialogBinding.cardEmail.setOnClickListener {
                                            val intent = Intent(Intent.ACTION_SEND)
                                            intent.putExtra(
                                                android.content.Intent.EXTRA_EMAIL,
                                                arrayOf(valueUser?.email)
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
}