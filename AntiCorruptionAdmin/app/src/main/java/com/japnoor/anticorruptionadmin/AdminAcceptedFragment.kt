package com.japnoor.anticorruption.admin

import android.annotation.SuppressLint
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
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruptionadmin.*
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.EditComplaintDialogBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminAcceptedComplaintsBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminTotalComplaintsBinding
import com.japnoor.anticorruptionadmin.databinding.StatusDescriptionDialogBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminAcceptedFragment : Fragment(),ComplaintClickedInterface {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var adminHomeScreen: AdminHomeScreen
    lateinit var binding: FragmentAdminAcceptedComplaintsBinding
    var complaintsList: ArrayList<Complaints> = ArrayList()
    var usersList: ArrayList<Users> = ArrayList()
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var compref: DatabaseReference
    lateinit var adminAcceptedAdapter: AdminAcceptedAdapter
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storegeref: StorageReference
    var encryptionKey: String? =null
    var secretKeySpec: SecretKeySpec? =null

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
        binding = FragmentAdminAcceptedComplaintsBinding.inflate(layoutInflater, container, false)
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

                    if (complaint != null && complaint.status=="1") {
                        complaintsList.add(complaint)
                    }
                    complaintsList.reverse()
                    adminAcceptedAdapter =
                        AdminAcceptedAdapter(adminHomeScreen, complaintsList, this)
                    binding.recyclerView.layoutManager = GridLayoutManager(adminHomeScreen,2)
                    binding.recyclerView.adapter = adminAcceptedAdapter
                    binding.shimmer.visibility=View.GONE
                    binding.shimmer.stopShimmer()
                    binding.recyclerView.visibility=View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = ArrayList<Complaints>()
                            for (item in complaintsList){
                                if(decrypt(item.complaintAgainst).toLowerCase().contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintNumber).toLowerCase().contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintDate).toLowerCase().contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintTime).toLowerCase().contains(s.toString().toLowerCase())
                                    || item.status.toLowerCase().contains(s.toString().toLowerCase())
                                    || decrypt(item.userName).toLowerCase().contains(s.toString().toLowerCase())
                                    || decrypt(item.userEmail).toLowerCase().contains(s.toString().toLowerCase())
                                    || decrypt(item.complaintDistrict).toLowerCase().contains(s.toString().toLowerCase())
                                )
                                    filteredList.add(item)
                            }
                            adminAcceptedAdapter.FilteredList(filteredList)
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
                dialogBind.actionLayout.visibility=View.GONE
                dialogBind.stamp.visibility=View.VISIBLE
                dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
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
                dialogBind.emailbtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf( decrypt(complaints.userEmail) ));
                    intent.type = "message/rfc822"
                    startActivity(Intent.createChooser(intent, "Select email"))
                }

                dialogBind.oldemailbtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf( decrypt(complaints.userOldEmail) ));
                    intent.type = "message/rfc822"
                    startActivity(Intent.createChooser(intent, "Select email"))
                }
                if (complaints.audioUrl.isNullOrEmpty())
                    dialogBind.audio.visibility = View.GONE

                if (complaints.imageUrl.isNullOrEmpty())
                    dialogBind.image.visibility = View.GONE

                if (complaints.videoUrl.isNullOrEmpty())
                    dialogBind.video.visibility = View.GONE

                dialogBind.fabAccepted.visibility=View.GONE
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
                            compref.child(complaints.complaintId).child("statusDescription")
                                .setValue(encrypt(descriptionDialogBin.detail.text.toString()))
                                .addOnCompleteListener {
                                }
                            compref.child(complaints.complaintId).child("status").setValue("2")
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
                                complaints.complaintId,
                                complaints.userName,
                                "2",
                                complaints.complaintNumber, "c"
                            )
                            FirebaseDatabase.getInstance().reference.child("Notifications")
                                .child(notificationid).setValue(notification)

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
                    descriptionDialogBin.detail.setHint("Details about Rejecting Complaint")
                    descriptionDialogBin.cancel.setOnClickListener {
                        descriptionDialog.dismiss()
                    }
                    descriptionDialogBin.btnDone.setOnClickListener {
                        if (descriptionDialogBin.detail.text.toString().trim().length == 0) {
                            descriptionDialogBin.detail.error = "Cannot be Empty"
                        } else {
                            descriptionDialog.dismiss()
                            compref.child(complaints.complaintId).child("statusDescription")
                                .setValue(encrypt(descriptionDialogBin.detail.text.toString()))
                                .addOnCompleteListener {
                                }
                            compref.child(complaints.complaintId).child("status").setValue("3")
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
                    var intent=Intent(adminHomeScreen, AudioActivity::class.java)
                    intent.putExtra("audio",fileUri.toString())
                    adminHomeScreen.startActivity(intent)
                }

                dialogBind.image.setOnClickListener {
                    var url=decrypt(complaints.imageUrl)
                    val fileUri: Uri = url.toUri()
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(fileUri, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                    startActivity(intent)
                }

                dialogBind.video.setOnClickListener {
                    var url=decrypt(complaints.videoUrl)
                    val fileUri: Uri = url.toUri()
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