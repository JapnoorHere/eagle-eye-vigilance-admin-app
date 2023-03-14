package com.japnoor.anticorruption.admin

import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.japnoor.anticorruptionadmin.databinding.ItemDemandBinding
import com.japnoor.anticorruptionadmin.databinding.ShowUserDeatailsBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter

class AdminTotalDemandAdapter (var context: AdminHomeScreen,var demandletter: ArrayList<DemandLetter>, var demandClick: DemandClick)  : RecyclerView.Adapter<AdminTotalDemandAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemDemandBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDemandBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(demandletter[position].status){
            "1"->{
                holder.binding.upline.setBackgroundResource(R.drawable.acceptedback)
                holder.binding.downline.setBackgroundResource(R.drawable.acceptedback)
            }
            "2"->{
                holder.binding.upline.setBackgroundResource(R.drawable.resolvedback)
                holder.binding.downline.setBackgroundResource(R.drawable.resolvedback)
            }
            "3"->{
                holder.binding.upline.setBackgroundResource(R.drawable.rejectedback)
                holder.binding.downline.setBackgroundResource(R.drawable.rejectedback)
            }
            else->{
                holder.binding.upline.setBackgroundResource(R.drawable.purpleback)
                holder.binding.downline.setBackgroundResource(R.drawable.purpleback)
            }

        }

        holder.binding.tvAgainst.setText(demandletter[position].demandSubject)
        holder.binding.tvSummary.setText(demandletter[position].demandDetails)
        holder.binding.time.setText(demandletter[position].demandTime)
        holder.binding.compNumber.setText(demandletter[position].demandNumber)
        holder.binding.Date.setText(demandletter[position].demandDate)
        holder.binding.userName.setText(demandletter[position].userName)
        holder.itemView.setOnClickListener{
            demandClick.onDemandClick(demandletter[position])
        }
        holder.binding.more.setOnClickListener{
            var bottomSheet = BottomSheetDialog(context)
            bottomSheet.setContentView(R.layout.complaint_menu_hold)
            var chat=bottomSheet.findViewById<CardView>(R.id.chat)
            var block=bottomSheet.findViewById<CardView>(R.id.blockUser)
            var blocktext=bottomSheet.findViewById<TextView>(R.id.blocktext)
            var blockvalue = ""
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

            chat?.setOnClickListener {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(demandletter[position].userId).child("profileValue").get()
                        .addOnCompleteListener {
                            println("profile" + it.result.value.toString())
                            var intent = Intent(context, ComplaintChatActivity::class.java)
                            intent.putExtra("uid", demandletter[position].userId)
                            intent.putExtra("cid", demandletter[position].demandId)
                            intent.putExtra("name", demandletter[position].userName)
                            intent.putExtra("profile", it.result.value.toString())
                            intent.putExtra("cnumber", demandletter[position].demandNumber)
                            intent.putExtra("type", "d")
                            intent.putExtra("status", demandletter[position].status)
                            intent.putExtra("against", demandletter[position].demandSubject)
                            context.startActivity(intent)
                        }
                }
                else{
                    Toast.makeText(context,"Check your internet connection please", Toast.LENGTH_LONG).show()

                }
            }

            block?.setOnClickListener {
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
            var userProfile=bottomSheet.findViewById<CardView>(R.id.userProfile)
            userProfile?.setOnClickListener {
                bottomSheet.dismiss()
                var dialog= Dialog(context)
                var dialogBinding= ShowUserDeatailsBinding.inflate(context.layoutInflater)
                dialog.setContentView(dialogBinding.root)
                dialog.show()
                dialogBinding.block.visibility= View.GONE
                dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(eachUser in snapshot.children){
                            var valueUser=eachUser.getValue(Users::class.java)
                            if(valueUser?.userId.equals(demandletter[position].userId)){
                                dialogBinding.name.setText(valueUser?.name)
                                dialogBinding.email.setText(valueUser?.email)
                                dialogBinding.birthdate.setText(valueUser?.birthdate)
                                dialogBinding.date.setText(valueUser?.userDate)

                                dialogBinding.cardEmail.setOnClickListener {
                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf( valueUser?.email ));
                                    intent.type = "message/rfc822"
                                    context.startActivity(Intent.createChooser(intent, "Select email"))
                                }
                                dialogBinding.cardComplaint.setOnClickListener {
                                    dialog.dismiss()
                                    bottomSheet.dismiss()
                                    var bundle = Bundle()
                                    bundle.putString("uid", valueUser?.userId.toString())
                                    context.navController.navigate(R.id.userComplaintFragment, bundle)
                                }
                                dialogBinding.cardDemand.setOnClickListener {
                                    dialog.dismiss()
                                    bottomSheet.dismiss()
                                    var bundle = Bundle()
                                    bundle.putString("uid", valueUser?.userId.toString())
                                    context.navController.navigate(R.id.userDemandFragment, bundle)
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
                                FirebaseDatabase.getInstance().reference.child("Complaints").addValueEventListener(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var complaintCount=0
                                        for (eachComplaint in snapshot.children) {
                                            var comp = eachComplaint.getValue(Complaints::class.java)
                                            if (comp != null && demandletter[position].userId.equals(comp.userId)) {
                                                complaintCount += 1
                                                println("Count0     -> " + complaintCount.toString())
                                                dialogBinding.complaints.setText(complaintCount.toString())
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })

                                FirebaseDatabase.getInstance().reference.child("Demand Letter").addValueEventListener(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var demandCount=0
                                        for (eachdem in snapshot.children) {
                                            var dem = eachdem.getValue(DemandLetter::class.java)
                                            if (dem != null && demandletter[position].userId.equals(dem.userId)) {
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
}