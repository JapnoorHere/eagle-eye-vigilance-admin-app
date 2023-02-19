package com.japnoor.anticorruption.admin

import Data1
import Notification1
import android.app.Dialog
import android.app.Notification
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruptionadmin.*
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.EditComplaintDialogBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminTotalComplaintsBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminTotalComplaints : Fragment(),ComplaintClickedInterface {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var adminHomeScreen: AdminHomeScreen
    lateinit var binding: FragmentAdminTotalComplaintsBinding
    var complaintsList: ArrayList<Complaints> = ArrayList()
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var compref: DatabaseReference
    lateinit var adminTotalComplaintsAdapter: AdminTotalComplaintsAdapter
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storegeref: StorageReference

    var status : String=""


    lateinit var activityResulLauncher: ActivityResultLauncher<Intent>
    lateinit var activityResulLauncher2: ActivityResultLauncher<Intent>
    var audioUrl: String = ""
    var audioUri: Uri? = null

    var videoUrl: String = ""
    var videoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminHomeScreen = activity as AdminHomeScreen
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminTotalComplaintsBinding.inflate(layoutInflater, container, false)
        firebaseStorage = FirebaseStorage.getInstance()
        storegeref = firebaseStorage.reference
        firebaseDatabase = FirebaseDatabase.getInstance()
        compref = firebaseDatabase.reference.child("Complaints")
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
        compref.addValueEventListener(object : ValueEventListener, ComplaintClickedInterface {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintsList.clear()
                for (eachComplaint in snapshot.children) {
                    val complaint = eachComplaint.getValue(Complaints::class.java)

                    if (complaint != null && !(complaint.status.equals("1")) && !(complaint.status.equals("2"))
                            && !(complaint.status.equals("3"))) {
                            complaintsList.add(complaint)
                    }
                    complaintsList.reverse()
                    adminTotalComplaintsAdapter =
                        AdminTotalComplaintsAdapter(adminHomeScreen, complaintsList, this)
                    binding.recyclerView.layoutManager = GridLayoutManager(adminHomeScreen,2)
                    binding.recyclerView.adapter = adminTotalComplaintsAdapter
                    binding.shimmer.visibility=View.GONE
                    binding.shimmer.stopShimmer()
                    binding.recyclerView.visibility=View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = ArrayList<Complaints>()
                            for (item in complaintsList){
                                if(item.complaintAgainst.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.complaintNumber.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.complaintDate.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.complaintTime.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.status.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.userName.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.userEmail.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.complaintDistrict.toLowerCase().contains(s.toString().toLowerCase())
                                )
                                    filteredList.add(item)
                            }
                            adminTotalComplaintsAdapter.FilteredList(filteredList)
                        }

                    })
                }
                binding.shimmer.visibility=View.GONE
                binding.shimmer.stopShimmer()
                binding.recyclerView.visibility=View.VISIBLE

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onComplaintsClicked(complaints: Complaints) {
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
                dialogBind.tvSummary.setText(complaints.complaintSummary)
                dialogBind.tvAgainst.setText(complaints.complaintAgainst)
                dialogBind.tvDetails.setText(complaints.complaintDetails)
                dialogBind.tvDistrict.setText(complaints.complaintDistrict)
                dialogBind.oldemail.setText(complaints.userOldEmail)



                dialogBind.emailbtn.setOnClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf( complaints.userEmail ));
                        intent.type = "message/rfc822"
                        startActivity(Intent.createChooser(intent, "Select email"))
                }

                dialogBind.oldemailbtn.setOnClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf( complaints.userOldEmail ));
                        intent.type = "message/rfc822"
                        startActivity(Intent.createChooser(intent, "Select email"))
                }

                if (complaints.audioUrl.isNullOrEmpty())
                    dialogBind.audio.visibility = View.GONE

                if (complaints.videoUrl.isNullOrEmpty())
                    dialogBind.video.visibility = View.GONE



                if(complaints.status.equals("1")){
            dialogBind.fabAccepted.visibility=View.GONE
            dialogBind.fabRejected.visibility=View.VISIBLE
            dialogBind.stamp.visibility=View.VISIBLE
            dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
        }
        else if(complaints.status.equals("2")){
            dialogBind.fabAccepted.visibility=View.GONE
            dialogBind.fabRejected.visibility=View.GONE
            dialogBind.fabResolved.visibility=View.GONE
            dialogBind.stamp.visibility=View.VISIBLE
            dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
        }
        else if(complaints.status.equals("3")){
            dialogBind.fabAccepted.visibility=View.GONE
            dialogBind.fabRejected.visibility=View.GONE
            dialogBind.fabResolved.visibility=View.GONE
            dialogBind.stamp.visibility=View.VISIBLE
            dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
        }
        else {
            dialogBind.fabAccepted.visibility=View.VISIBLE
            dialogBind.fabRejected.visibility=View.VISIBLE
            dialogBind.fabResolved.visibility=View.VISIBLE
            dialogBind.stamp.visibility=View.GONE
        }

                dialogBind.fabAccepted.setOnClickListener {
                    compref.child(complaints.complaintId).child("status").setValue("1")
                        // Update the status of the complaint in the database
//                        val complaintRef = firebaseDatabase.getReference(complaints.complaintId)
//                        complaintRef.child("status").setValue(status)

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
                    compref.child(complaints.complaintId).child("status").setValue("2")
                    dialog.dismiss()
                }
                dialogBind.fabRejected.setOnClickListener {
                    compref.child(complaints.complaintId).child("status").setValue("3")
                    dialog.dismiss()
                }

                dialogBind.audio.setOnClickListener {
                    val fileUri: Uri = complaints.audioUrl.toUri()
                    var intent=Intent(adminHomeScreen, AudioActivity::class.java)
                    intent.putExtra("audio",fileUri.toString())
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


    override fun onComplaintsClicked(complaints: Complaints) {
    }
}