package com.japnoor.anticorruptionadmin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.japnoor.anticorruptionadmin.databinding.EditComplaintDialogBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentUserComplaintBinding
import com.japnoor.anticorruptionadmin.databinding.StatusDescriptionDialogBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                                if (item.complaintAgainst.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || item.complaintNumber.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || item.complaintDate.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || item.complaintTime.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || item.status.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || item.userName.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || item.userEmail.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || item.userOldEmail.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || item.complaintDistrict.toLowerCase()
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
                dialogBind.name.setText(complaints.userName)
                dialogBind.email.setText(complaints.userEmail)
                dialogBind.date.setText(complaints.complaintDate)
                dialogBind.tvDept.setText(complaints.complaintDept)
                dialogBind.tvLoc.setText(complaints.complaintLoc)
                dialogBind.tvCategory.setText(complaints.complaintCategory)
                dialogBind.tvAgainst.setText(complaints.complaintAgainst)
                dialogBind.tvDetails.setText(complaints.complaintDetails)
                dialogBind.tvDistrict.setText(complaints.complaintDistrict)
                dialogBind.oldemail.setText(complaints.userOldEmail)



                dialogBind.emailbtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(
                        android.content.Intent.EXTRA_EMAIL,
                        arrayOf(complaints.userEmail)
                    );
                    intent.type = "message/rfc822"
                    startActivity(Intent.createChooser(intent, "Select email"))
                }

                dialogBind.oldemailbtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(
                        android.content.Intent.EXTRA_EMAIL,
                        arrayOf(complaints.userOldEmail)
                    );
                    intent.type = "message/rfc822"
                    startActivity(Intent.createChooser(intent, "Select email"))
                }

                if (complaints.audioUrl.isNullOrEmpty())
                    dialogBind.audio.visibility = View.GONE

                if (complaints.videoUrl.isNullOrEmpty())
                    dialogBind.video.visibility = View.GONE



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
                            complaints.userEmail,
                            complaints.userName,
                            complaints.complaintNumber,
                            complaints.complaintAgainst,
                            complaints.complaintDetails,
                            complaints.complaintDept,
                            complaints.complaintCategory,
                            complaints.complaintLoc,
                            complaints.complaintDistrict,"No",complaints.videoUrl)
                    }
                    else if(complaints.videoUrl.isNullOrEmpty()){
                        var sendEmail = SendEmail()
                        sendEmail.sendComplaint(
                            complaints.userEmail,
                            complaints.userName,
                            complaints.complaintNumber,
                            complaints.complaintAgainst,
                            complaints.complaintDetails,
                            complaints.complaintDept,
                            complaints.complaintCategory,
                            complaints.complaintLoc,
                            complaints.complaintDistrict,complaints.audioUrl,"No")
                    }
                    compref.child(complaints.complaintId).child("status").setValue("1")

                    // Update the status of the complaint in the database
                    val complaintRef =
                        FirebaseDatabase.getInstance().getReference(complaints.complaintId)
                    complaintRef.child("status").setValue(status)

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
                            compref.child(complaints.complaintId).child("status").setValue("2")
                            compref.child(complaints.complaintId).child("statusDescription")
                                .setValue(descriptionDialogBin.detail.text.toString())
                                .addOnCompleteListener {
                                    descriptionDialog.dismiss()
                                }
                            compref.child(complaints.complaintId).child("status").setValue("2")
                            dialog.dismiss()
                        }
                    }
                }
                dialogBind.fabRejected.setOnClickListener {
                    compref.child(complaints.complaintId).child("status").setValue("3")

                    dialog.dismiss()
                }

                dialogBind.audio.setOnClickListener {
                    val fileUri: Uri = complaints.audioUrl.toUri()
                    var intent = Intent(adminHomeScreen, AudioActivity::class.java)
                    intent.putExtra("audio", fileUri.toString())
                    adminHomeScreen.startActivity(intent)

                }

                dialogBind.video.setOnClickListener {
                    val fileUri: Uri = complaints.videoUrl.toUri()

                    var intent = Intent(adminHomeScreen, VideoActivity::class.java)
                    intent.putExtra("video", fileUri.toString())
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