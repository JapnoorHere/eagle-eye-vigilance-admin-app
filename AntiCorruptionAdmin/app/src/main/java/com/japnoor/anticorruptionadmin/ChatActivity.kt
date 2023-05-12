package com.japnoor.anticorruptionadmin

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.japnoor.anticorruptionadmin.databinding.ActivityChatBinding
import com.japnoor.anticorruptionadmin.databinding.ShowUserDeatailsBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter
import java.text.SimpleDateFormat
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var chatAdapter: ChatAdapter
    lateinit var chatList: ArrayList<Chat>
    var senderUid: String = ""
    var recieverUid: String = ""
    var recieverName: String = ""
    var recieverRoom: String = ""
    var senderRoom: String = ""
    var profileValue: String = ""
    var encryptionKey: String? =null
    var secretKeySpec: SecretKeySpec? =null
    private fun encrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun decrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        recieverUid = intent.getStringExtra("uid").toString()
        recieverName = intent.getStringExtra("name").toString()
        profileValue = intent.getStringExtra("profile").toString()

        chatList = ArrayList()
        chatAdapter = ChatAdapter(this@ChatActivity, chatList)

        binding.horizScroll.visibility = View.GONE

        recieverRoom = recieverUid + senderUid
        senderRoom = senderUid + recieverUid

        binding.titleName.text = recieverName

        println("Admin " + senderUid)
        println("User " + recieverUid)


        when (profileValue) {
            "1" -> binding.Profile.setImageResource(R.drawable.man1)
            "2" -> binding.Profile.setImageResource(R.drawable.man2)
            "3" -> binding.Profile.setImageResource(R.drawable.man3)
            "4" -> binding.Profile.setImageResource(R.drawable.man4)
            "5" -> binding.Profile.setImageResource(R.drawable.girl1)
            "6" -> binding.Profile.setImageResource(R.drawable.girl2)
            "7" -> binding.Profile.setImageResource(R.drawable.girl3)
            "8" -> binding.Profile.setImageResource(R.drawable.girl4)
        }

        binding.cardProfile.setOnClickListener {
            var dialog = Dialog(this)
            var dialogB = ShowUserDeatailsBinding.inflate(layoutInflater)
            dialog.setContentView(dialogB.root)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.show()
            dialogB.unblocktime.visibility=View.GONE
            dialogB.cardComplaint.visibility=View.GONE
            dialogB.cardDemand.visibility=View.GONE
            dialogB.cardEmail.setOnClickListener {
                FirebaseDatabase.getInstance().reference.child("Users").child(recieverUid)
                    .child("email").get().addOnCompleteListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.putExtra(
                            android.content.Intent.EXTRA_EMAIL,
                            arrayOf(decrypt(it.result.value.toString()))
                        )
                        intent.type = "message/rfc822"
                        startActivity(Intent.createChooser(intent, "Select email"))
                    }
            }
            FirebaseDatabase.getInstance().reference.child("Users").child(recieverUid)
                .child("profileValue").get().addOnCompleteListener {
                when (it.result.value.toString()) {
                    "1" -> dialogB.Profile.setImageResource(R.drawable.man1)
                    "2" -> dialogB.Profile.setImageResource(R.drawable.man2)
                    "3" -> dialogB.Profile.setImageResource(R.drawable.man3)
                    "4" -> dialogB.Profile.setImageResource(R.drawable.man4)
                    "5" -> dialogB.Profile.setImageResource(R.drawable.girl1)
                    "6" -> dialogB.Profile.setImageResource(R.drawable.girl2)
                    "7" -> dialogB.Profile.setImageResource(R.drawable.girl3)
                    "8" -> dialogB.Profile.setImageResource(R.drawable.girl4)
                }
            }
            FirebaseDatabase.getInstance().reference.child("Users").child(recieverUid)
                .child("name").get().addOnCompleteListener {
                    dialogB.name.setText(decrypt(it.result.value.toString()))
                }

            FirebaseDatabase.getInstance().reference.child("Users").child(recieverUid)
                .child("email").get().addOnCompleteListener {
                    dialogB.email.setText(decrypt(it.result.value.toString()))
                }


            FirebaseDatabase.getInstance().reference.child("Users").child(recieverUid)
                .child("birthdate").get().addOnCompleteListener {
                    dialogB.birthdate.setText(decrypt(it.result.value.toString()))
                }

            FirebaseDatabase.getInstance().reference.child("Users").child(recieverUid)
                .child("userDate").get().addOnCompleteListener {
                    dialogB.date.setText(decrypt(it.result.value.toString()))
                }
            var complaintCount = 0
            var demandCount = 0

            FirebaseDatabase.getInstance().reference.child("Complaints").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (eachComplaint in snapshot.children) {
                        var comp = eachComplaint.getValue(Complaints::class.java)
                        if (comp != null && recieverRoom.equals(comp.userId)) {
                            complaintCount += 1
                            println("Count0     -> " + complaintCount.toString())
                            dialogB.complaints.setText(complaintCount.toString())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            FirebaseDatabase.getInstance().reference.child("Demand Letter").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (eachdem in snapshot.children) {
                        var dem = eachdem.getValue(DemandLetter::class.java)
                        if (dem != null && recieverUid.equals(dem.userId)) {
                            demandCount += 1
                            dialogB.demand.setText(demandCount.toString())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

           FirebaseDatabase.getInstance().reference.child("Users").child(recieverUid).child("userStatus").get().addOnCompleteListener { status->
               if(status.result.value.toString().equals("0")){
                   dialogB.block.setText("Block this User")
               }
               else{
                   val sevenDaysInMillisecond: Long = 604800000
                   var timecheck: Long = 0
                   val currentTime = System.currentTimeMillis()
                   FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object : ValueEventListener {
                       override fun onDataChange(snapshot: DataSnapshot) {
                           for (each in snapshot.children) {
                               var userdetail = each.getValue(Users::class.java)
                               if (userdetail != null && userdetail.userId.equals(recieverUid) && userdetail.userStatus != "0") {
                                   timecheck = userdetail.userStatus.toLong() + sevenDaysInMillisecond
                                   println("Time->" + timecheck)
                                   if (currentTime >= timecheck) {
                                       FirebaseDatabase.getInstance().reference.child("Users").child(recieverUid).child("userStatus").setValue("0")

                                   }
                               }
                           }
                           var timeleft = timecheck - currentTime
                           val days = timeleft / (1000 * 60 * 60 * 24)
                           val hours = (timeleft / (1000 * 60 * 60)) % 24
                           dialogB.unblocktime.visibility=View.VISIBLE
                           dialogB.unblocktime.text =
                               "Unblocks in : " + days.toString() + "d " + hours.toString() + "h"
                       }

                       override fun onCancelled(error: DatabaseError) {

                       }

                   })

                   dialogB.block.setText("Unlock this User")
               }


               dialogB.block.setOnClickListener {
                   if (status.result.value.toString().equals("0")) {
                       val connectivityManager =
                           getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                       val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                       val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                       if (isConnected) {
                           var bottomSheet = BottomSheetDialog(this)
                           bottomSheet.setContentView(R.layout.dialog_delete_users)
                           bottomSheet.show()
                           var tvYes = bottomSheet.findViewById<TextView>(R.id.tvYes)
                           var tvNo = bottomSheet.findViewById<TextView>(R.id.tvNo)

                           tvNo?.setOnClickListener {
                               bottomSheet.dismiss()
                           }
                           tvYes?.setBackgroundResource(R.drawable.yes_btn_red)
                           tvYes?.setOnClickListener {
                               val currentTime = System.currentTimeMillis()
                               FirebaseDatabase.getInstance().reference.child("Users")
                                   .child(recieverUid).child("userStatus")
                                   .setValue(currentTime.toString())
                               bottomSheet.dismiss()
                               dialog.dismiss()
                           }
                       } else {
                           Toast.makeText(
                               this,
                               "Check your internet connection please",
                               Toast.LENGTH_LONG
                           ).show()
                       }
                   }
                   else{
                       val connectivityManager =
                           getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                       val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                       val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                       if (isConnected) {
                           var bottomSheet = BottomSheetDialog(this)
                           bottomSheet.setContentView(R.layout.dialog_delete_users)
                           bottomSheet.show()
                           var tvmsg = bottomSheet.findViewById<TextView>(R.id.textmsg)
                           var tvYes = bottomSheet.findViewById<TextView>(R.id.tvYes)
                           var tvNo = bottomSheet.findViewById<TextView>(R.id.tvNo)
                           tvYes?.setBackgroundResource(R.drawable.yes_btn_red)
                           tvmsg?.setText("Are you sure you want \n to unblock this User ?")
                           tvNo?.setOnClickListener {
                               bottomSheet.dismiss()
                           }
                           tvYes?.setOnClickListener {
                               FirebaseDatabase.getInstance().reference.child("Users").child(recieverUid).child("userStatus").setValue("0")
                               bottomSheet.dismiss()
                               dialog.dismiss()
                           }
                       }
                       else{
                           Toast.makeText(this,"Check your internet connection please", Toast.LENGTH_LONG).show()

                       }
                   }
               }

           }


        }


        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (isConnected) {
            FirebaseDatabase.getInstance().reference.child("Chats").child(senderRoom)
                .child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatList.clear()
                        for (each in snapshot.children) {
                            var msg = each.getValue(Chat::class.java)
                            if (msg != null)
                                chatList.add(msg)
                        }
                        chatAdapter.notifyDataSetChanged()
                        chatAdapter = ChatAdapter(this@ChatActivity, chatList)
                        binding.recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
                        binding.recyclerView.adapter = chatAdapter
                        binding.recyclerView.scrollToPosition(chatList.size - 1)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } else {
            Toast.makeText(this, "Check your internet connection please", Toast.LENGTH_LONG).show()

        }


        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    // Text is empty, hide the button
                    binding.sendButton.visibility = View.GONE
                } else if (binding.etMessage.text.toString().trim().length == 0) {
                    binding.sendButton.visibility = View.GONE
                } else {
                    // Text is not empty, show the button
                    binding.sendButton.visibility = View.VISIBLE
                }
            }
        })

        binding.sendButton.setOnClickListener {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected) {
                    val currentTimeMillis = System.currentTimeMillis()
                val formatter = SimpleDateFormat("dd/MM/yyyy-HH:mm")
                val dateTime = Date(currentTimeMillis)
                val time = formatter.format(dateTime)

                var chats = Chat(
                    encrypt(binding.etMessage.text.toString().trim()),
                    encrypt(time.toString()),
                    FirebaseAuth.getInstance().currentUser?.uid.toString()
                )
                FirebaseDatabase.getInstance().reference.child("Chats").child(senderRoom)
                    .child("messages")
                    .push().setValue(chats).addOnCompleteListener {
                        FirebaseDatabase.getInstance().reference.child("Chats").child(recieverRoom)
                            .child("messages")
                            .push().setValue(chats)
                    }
                binding.etMessage.text.clear()

            } else {
                Toast.makeText(this, "Check your internet connection please", Toast.LENGTH_LONG)
                    .show()

            }
        }


    }
}