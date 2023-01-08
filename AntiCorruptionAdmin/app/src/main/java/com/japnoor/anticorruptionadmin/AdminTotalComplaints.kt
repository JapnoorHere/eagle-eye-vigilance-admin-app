package com.japnoor.anticorruption.admin

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruptionadmin.AdminHomeScreen
import com.japnoor.anticorruptionadmin.ComplaintClickedInterface
import com.japnoor.anticorruptionadmin.Complaints
import com.japnoor.anticorruptionadmin.Users
import com.japnoor.anticorruptionadmin.databinding.EditComplaintDialogBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminTotalComplaintsBinding
import java.util.ArrayList
import com.japnoor.anticorruptionadmin.R



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


        compref.addValueEventListener(object : ValueEventListener, ComplaintClickedInterface {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintsList.clear()
                for (eachComplaint in snapshot.children) {
                    val complaint = eachComplaint.getValue(Complaints::class.java)

                    if (complaint != null) {


                        complaintsList.add(complaint)
                    }
                    adminTotalComplaintsAdapter =
                        AdminTotalComplaintsAdapter(adminHomeScreen, complaintsList, this)
                    binding.recyclerView.layoutManager = LinearLayoutManager(adminHomeScreen)
                    binding.recyclerView.adapter = adminTotalComplaintsAdapter
                }

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
                dialogBind.phoneno.setText(complaints.userPhone)
                dialogBind.email.setText(complaints.userEmail)
                dialogBind.date.setText(complaints.complaintDate)
                dialogBind.tvSummary.setText(complaints.complaintSummary)
                dialogBind.tvAgainst.setText(complaints.complaintAgainst)
                dialogBind.tvDetails.setText(complaints.complaintDetails)
                dialogBind.tvDistrict.setText(complaints.complaintDistrict)

                if (complaints.audioUrl.isNullOrEmpty())
                    dialogBind.audio.visibility = View.GONE

                if (complaints.videoUrl.isNullOrEmpty())
                    dialogBind.video.visibility = View.GONE

        if(complaints.status.equals("1")){
            dialogBind.fabAccepted.visibility=View.GONE
            dialogBind.fabRejected.visibility=View.GONE
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
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(
                            fileUri, MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                MimeTypeMap.getFileExtensionFromUrl(audioUrl)
                            )
                        )
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                        startActivity(intent)
                    }
                dialogBind.video.setOnClickListener {
                    val fileUri: Uri = complaints.videoUrl.toUri()
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(
                        fileUri, MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                            MimeTypeMap.getFileExtensionFromUrl(videoUrl)
                        )
                    )
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                    startActivity(intent)
                }

                dialog.show()
            }
        })

        return binding.root
    }


    override fun onComplaintsClicked(complaints: Complaints) {
    }
}