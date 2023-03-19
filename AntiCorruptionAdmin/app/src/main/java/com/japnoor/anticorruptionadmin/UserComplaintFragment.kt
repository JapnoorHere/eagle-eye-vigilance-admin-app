package com.japnoor.anticorruptionadmin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.japnoor.anticorruptionadmin.databinding.EditComplaintDialogBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentUserComplaintBinding
import com.japnoor.anticorruptionadmin.databinding.StatusDescriptionDialogBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserComplaintFragment : Fragment(), UserComplaintClick {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var compref: DatabaseReference
    lateinit var adminHomeScreen: AdminHomeScreen
    var uid: String = ""
    lateinit var usercomplaintAdapter: UserComplaintAdapter
    var status: String = ""

    var encryptionKey: String? =null
    var secretKeySpec: SecretKeySpec? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        adminHomeScreen = activity as AdminHomeScreen
        arguments.let {
            uid = it?.getString("uid").toString()
        }
        var complaintArrayList = ArrayList<Complaints>()
        compref = FirebaseDatabase.getInstance().reference.child("Complaints")
        var binding = FragmentUserComplaintBinding.inflate(layoutInflater, container, false)


        binding.shimmer.startShimmer()
        binding.search.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.search.right - binding.search.compoundDrawables[2].bounds.width())) {
                    binding.search.text.clear()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
        compref.addValueEventListener(object : ValueEventListener, UserComplaintClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintArrayList.clear()
                for (each in snapshot.children) {
                    var complaintDetail = each.getValue(Complaints::class.java)
                    if (complaintDetail != null && complaintDetail.userId.equals(uid)) {
                        complaintArrayList.add(complaintDetail)
                    }
                    complaintArrayList.reverse()
                    usercomplaintAdapter =
                        UserComplaintAdapter(adminHomeScreen, complaintArrayList, this)
                    binding.recyclerView.layoutManager = LinearLayoutManager(adminHomeScreen)
                    binding.recyclerView.adapter = usercomplaintAdapter
                    binding.shimmer.visibility = View.GONE
                    binding.shimmer.stopShimmer()
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = ArrayList<Complaints>()
                            for (item in complaintArrayList) {
                                if (decrypt(item.complaintAgainst).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintNumber).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintDate).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintTime).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.status).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.userName).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.userEmail).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.userOldEmail).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintDistrict).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                )
                                    filteredList.add(item)
                            }
                            usercomplaintAdapter.FilteredList(filteredList)
                        }

                    })
                }
                binding.shimmer.visibility = View.GONE
                binding.shimmer.stopShimmer()
                binding.recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onClick(complaints: Complaints) {
                var dialog = Dialog(requireContext())
                var dialogBind = EditComplaintDialogBinding.inflate(layoutInflater)
                dialog.setContentView(dialogBind.root)
                dialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                dialogBind.name.setText(decrypt(complaints.userName))
                dialogBind.email.setText(decrypt(complaints.userEmail))
                dialogBind.date.setText(decrypt(complaints.complaintDate))
                dialogBind.tvDept.setText(decrypt(complaints.complaintDept))
                dialogBind.tvLoc.setText(decrypt(complaints.complaintLoc))
                dialogBind.tvCategory.setText(decrypt(complaints.complaintCategory))
                dialogBind.tvAgainst.setText(decrypt(complaints.complaintAgainst))
                dialogBind.tvDetails.setText(decrypt(complaints.complaintDetails))
                dialogBind.tvDistrict.setText(decrypt(complaints.complaintDistrict))
                dialogBind.oldemail.setText(decrypt(complaints.userOldEmail))
                if(complaints.status.equals("2")) {
                    dialogBind.actionstaken.setText(decrypt(complaints.statusDescription))
                }
                else if(complaints.status.equals("3")) {
                    dialogBind.actionLayout.setBackgroundDrawable(resources.getDrawable(R.drawable.rejectedcompbgtextstroke))
                    dialogBind.actionstaken.setText(decrypt(complaints.statusDescription))
                    dialogBind.actionstaken.setTextColor(Color.RED)
                    dialogBind.actionstakenHeading.setTextColor(Color.RED)
                }
                else{
                    dialogBind.actionLayout.visibility=View.GONE
                }

                dialogBind.emailbtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(
                        android.content.Intent.EXTRA_EMAIL,
                        arrayOf(decrypt(complaints.userEmail))
                    );
                    intent.type = "message/rfc822"
                    startActivity(Intent.createChooser(intent, "Select email"))
                }

                dialogBind.oldemailbtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(
                        android.content.Intent.EXTRA_EMAIL,
                        arrayOf(decrypt(complaints.userOldEmail))
                    );
                    intent.type = "message/rfc822"
                    startActivity(Intent.createChooser(intent, "Select email"))
                }

                if (decrypt(complaints.audioUrl).isNullOrEmpty())
                    dialogBind.audio.visibility = View.GONE

                if (decrypt(complaints.videoUrl).isNullOrEmpty())
                    dialogBind.video.visibility = View.GONE
                if(decrypt(complaints.imageUrl).isNullOrEmpty())
                    dialogBind.image.visibility=View.GONE



                if (complaints.status.equals("1")) {
                    dialogBind.fabAccepted.visibility = View.GONE
                    dialogBind.fabRejected.visibility = View.VISIBLE
                    dialogBind.stamp.visibility = View.VISIBLE
                    dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
                } else if (complaints.status.equals("2")) {
                    dialogBind.fabAccepted.visibility = View.GONE
                    dialogBind.fabRejected.visibility = View.GONE
                    dialogBind.fabResolved.visibility = View.GONE
                    dialogBind.stamp.visibility = View.VISIBLE
                    dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
                } else if (complaints.status.equals("3")) {
                    dialogBind.fabAccepted.visibility = View.GONE
                    dialogBind.fabRejected.visibility = View.GONE
                    dialogBind.fabResolved.visibility = View.GONE
                    dialogBind.stamp.visibility = View.VISIBLE
                    dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
                } else {
                    dialogBind.fabAccepted.visibility = View.VISIBLE
                    dialogBind.fabRejected.visibility = View.VISIBLE
                    dialogBind.fabResolved.visibility = View.VISIBLE
                    dialogBind.stamp.visibility = View.GONE
                }

                dialogBind.fabAccepted.setOnClickListener {
                    if (complaints.audioUrl.isNullOrEmpty()) {
                        var sendEmail = SendEmail()
                        sendEmail.sendComplaint(
                            decrypt(complaints.userEmail),
                            decrypt(complaints.userName),
                            decrypt(complaints.complaintNumber),
                            decrypt(complaints.complaintAgainst),
                            decrypt(complaints.complaintDetails),
                            decrypt(complaints.complaintDept),
                            decrypt(complaints.complaintCategory),
                            decrypt(complaints.complaintLoc),
                            decrypt(complaints.complaintDistrict),"No",decrypt(complaints.videoUrl))
                    }
                    else if(complaints.videoUrl.isNullOrEmpty()){
                        var sendEmail = SendEmail()
                        sendEmail.sendComplaint(
                            decrypt(complaints.userEmail),
                            decrypt(complaints.userName),
                            decrypt(complaints.complaintNumber),
                            decrypt(complaints.complaintAgainst),
                            decrypt(complaints.complaintDetails),
                            decrypt(complaints.complaintDept),
                            decrypt(complaints.complaintCategory),
                            decrypt(complaints.complaintLoc),
                            decrypt(complaints.complaintDistrict),decrypt(complaints.audioUrl),"No")
                    }
                    compref.child(complaints.complaintId).child("status").setValue("1")
                    var notificationid =
                        FirebaseDatabase.getInstance().reference.child("Notifications")
                            .push().key.toString()
                    val format = SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
                    val notificationTime = format.format(Date())

                    var notification = Notification(
                        notificationid,
                        complaints.complaintAgainst,
                        encrypt(notificationTime),
                        complaints.userId,
                        complaints.complaintId, complaints.userName,
                        "1",
                        complaints.complaintNumber, "c"
                    )
                    FirebaseDatabase.getInstance().reference.child("Notifications")
                        .child(notificationid).setValue(notification)
                    // Update the status of the complaint in the database
//                    val complaintRef =
//                        FirebaseDatabase.getInstance().getReference(complaints.complaintId)
//                    complaintRef.child("status").setValue(status)

//                        // Send a notification to the user's app via FCM
//                        val fcm = FirebaseMessaging.getInstance()
//                        var notification=Notification1("Complaint Status Update","Your complaint has been accepted")
//                    var data=Data1(complaints.complaintId,notification)
//                        val payload = FCM.MessagePayload(data)
//                        val target = FCM.MessageTarget(token = userToken) // userToken is the FCM token for the user's app
//                        fcm.send(target, payload)
                    dialog.dismiss()
                }
                dialogBind.fabResolved.setOnClickListener {
                    var descriptionDialog = Dialog(adminHomeScreen)
                    var descriptionDialogBin =
                        StatusDescriptionDialogBinding.inflate(layoutInflater)
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
                            compref.child(complaints.complaintId).child("status").setValue("2")
                            compref.child(complaints.complaintId).child("statusDescription")
                                .setValue(encrypt(descriptionDialogBin.detail.text.toString()))
                                .addOnCompleteListener {
                                    var notificationid =
                                        FirebaseDatabase.getInstance().reference.child("Notifications")
                                            .push().key.toString()
                                    val format =
                                        SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
                                    val notificationTime = format.format(Date())

                                    var notification = Notification(
                                        notificationid,
                                        complaints.complaintAgainst,
                                        encrypt(notificationTime),
                                        complaints.userId,
                                        complaints.complaintId,
                                        complaints.userName,
                                        "2",
                                        complaints.complaintNumber, "c"
                                    )
                                    FirebaseDatabase.getInstance().reference.child("Notifications")
                                        .child(notificationid).setValue(notification)
                                    dialog.dismiss()
                                }
                            compref.child(complaints.complaintId).child("status").setValue("2")
                            dialog.dismiss()
                        }
                    }
                }
                dialogBind.fabRejected.setOnClickListener {
                    var descriptionDialog = Dialog(adminHomeScreen)
                    var descriptionDialogBin =
                        StatusDescriptionDialogBinding.inflate(layoutInflater)
                    descriptionDialog.setContentView(descriptionDialogBin.root)
                    descriptionDialog.show()
                    descriptionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    descriptionDialog.setCancelable(false)
                    descriptionDialogBin.cancel.setOnClickListener {
                        descriptionDialog.dismiss()
                    }
                    descriptionDialogBin.detail.setHint("Details about Rejecting Complaint")

                    descriptionDialogBin.btnDone.setOnClickListener {
                        if (descriptionDialogBin.detail.text.toString().trim().length == 0) {
                            descriptionDialogBin.detail.error = "Cannot be Empty"
                        } else {
                            descriptionDialog.dismiss()
                            compref.child(complaints.complaintId).child("status").setValue("3")
                            compref.child(complaints.complaintId).child("statusDescription")
                                .setValue(encrypt(descriptionDialogBin.detail.text.toString()))
                            var notificationid =
                                FirebaseDatabase.getInstance().reference.child("Notifications")
                                    .push().key.toString()
                            val format =
                                SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
                            val notificationTime = format.format(Date())

                            var notification = Notification(
                                notificationid,
                                complaints.complaintAgainst,
                                encrypt(notificationTime),
                                complaints.userId,
                                complaints.complaintId,
                                complaints.userName,
                                "3",
                                complaints.complaintNumber, "c"
                            )
                            FirebaseDatabase.getInstance().reference.child("Notifications")
                                .child(notificationid).setValue(notification)
                            dialog.dismiss()
                        }
                    }
                }

                dialogBind.audio.setOnClickListener {
                    var url=decrypt(complaints.audioUrl)
                    val fileUri: Uri = url.toUri()
                    var intent = Intent(adminHomeScreen, AudioActivity::class.java)
                    intent.putExtra("audio", fileUri.toString())
                    adminHomeScreen.startActivity(intent)
                }

                dialogBind.video.setOnClickListener {
                    var url =decrypt(complaints.videoUrl)
                    val fileUri: Uri = url.toUri()
                    var intent = Intent(adminHomeScreen, VideoActivity::class.java)
                    intent.putExtra("video", fileUri.toString())
                    adminHomeScreen.startActivity(intent)
                }

                dialogBind.image.setOnClickListener {
                    var url=decrypt(complaints.imageUrl)
                    val fileUri: Uri = url.toUri()
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(fileUri, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    adminHomeScreen.startActivity(intent)
                }


                dialog.show()
            }


        })

        return binding.root
    }

    override fun onClick(complaints: Complaints) {
        TODO("Not yet implemented")
    }


}