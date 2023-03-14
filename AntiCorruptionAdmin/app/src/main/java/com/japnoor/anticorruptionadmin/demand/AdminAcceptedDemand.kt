package com.japnoor.anticorruption.admin.Demand

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruption.admin.AdminAcceptedAdapter
import com.japnoor.anticorruption.admin.AdminAcceptedDemandAdapter
import com.japnoor.anticorruption.admin.AdminTotalDemandAdapter
import com.japnoor.anticorruptionadmin.*
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.DemandDialogBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminAcceptedComplaintsBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminTotalComplaintsBinding
import com.japnoor.anticorruptionadmin.databinding.StatusDescriptionDialogBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminAcceptedDemand : Fragment(), DemandClick {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var demandList: ArrayList<DemandLetter> = ArrayList()
    lateinit var adminAcceptedDemandAdapter: AdminAcceptedDemandAdapter
    lateinit var adminHomeScreen: AdminHomeScreen
    lateinit var database: FirebaseDatabase
    lateinit var demRef: DatabaseReference
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference


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
        database = FirebaseDatabase.getInstance()
        demRef = database.reference.child("Demand Letter")
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("images")
        adminHomeScreen = activity as AdminHomeScreen
        var binding = FragmentAdminAcceptedComplaintsBinding.inflate(layoutInflater, container, false)
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
        binding.search.setHint("Search Demand Letters")
        demRef.addValueEventListener(object : ValueEventListener, DemandClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                demandList.clear()
                for (eachDemand in snapshot.children) {
                    var demand = eachDemand.getValue(DemandLetter::class.java)

                    if (demand != null && demand.status.equals("1")) {
                        demandList.add(demand)
                    }
                    demandList.reverse()
                    adminAcceptedDemandAdapter =
                        AdminAcceptedDemandAdapter(adminHomeScreen, demandList, this)
                    binding.recyclerView.layoutManager = GridLayoutManager(adminHomeScreen,2)
                    binding.recyclerView.adapter = adminAcceptedDemandAdapter
                    binding.shimmer.visibility=View.GONE
                    binding.shimmer.stopShimmer()
                    binding.recyclerView.visibility=View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = java.util.ArrayList<DemandLetter>()
                            for (item in demandList){
                                if(item.demandSubject.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.demandNumber.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.demandDate.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.demandTime.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.status.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.userName.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.userEmail.toLowerCase().contains(s.toString().toLowerCase())
                                    || item.demandDistrict.toLowerCase().contains(s.toString().toLowerCase())
                                )
                                    filteredList.add(item)
                            }
                            adminAcceptedDemandAdapter.FilteredList(filteredList)
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

            override fun onDemandClick(demandLetter: DemandLetter) {
                var dialog = Dialog(requireContext())
                var dialogBind = DemandDialogBinding.inflate(layoutInflater)
                dialog.setContentView(dialogBind.root)
                dialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                dialogBind.unionn.setText(demandLetter.unionName)
                dialogBind.actionLayout.visibility=View.GONE
                dialogBind.date.setText(demandLetter.demandDate)
                dialogBind.name.setText(demandLetter.userName)
                dialogBind.email.setText(demandLetter.userEmail)
                dialogBind.tvSummary.setText(demandLetter.demandSubject)
                dialogBind.tvDetails.setText(demandLetter.demandDetails)
                dialogBind.tvDistrict.setText(demandLetter.demandDistrict)
                dialogBind.image.setOnClickListener {
                }
                dialogBind.fabAccepted.visibility=View.GONE
                dialogBind.stamp.visibility=View.VISIBLE
                dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
                dialogBind.oldemail.setText(demandLetter.userOldEmail)


                dialogBind.emailbtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf( demandLetter.userEmail ))
                    intent.type = "message/rfc822"
                    startActivity(Intent.createChooser(intent, "Select email"))
                }

                dialogBind.oldemailbtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf( demandLetter.userOldEmail ))
                    intent.type = "message/rfc822"
                    startActivity(Intent.createChooser(intent, "Select email"))
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
                    descriptionDialogBin.detail.setHint("Details about Resolving Demand")
                    descriptionDialogBin.btnDone.setOnClickListener {
                        if (descriptionDialogBin.detail.text.toString().trim().length == 0) {
                            descriptionDialogBin.detail.error = "Cannot be Empty"
                        } else {
                            demRef.child(demandLetter.demandId).child("statusDescription")
                                .setValue(descriptionDialogBin.detail.text.toString())
                                .addOnCompleteListener {
                                    descriptionDialog.dismiss()
                                }
                            demRef.child(demandLetter.demandId).child("status").setValue("2")
                            var notificationid =
                                FirebaseDatabase.getInstance().reference.child("Notifications")
                                    .push().key.toString()
                            val format = SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
                            val notificationTime = format.format(Date())

                            var notification = Notification(
                                notificationid,
                                demandLetter.demandSubject,
                                notificationTime,
                                demandLetter.userId,
                                demandLetter.demandId,
                                demandLetter.userName,
                                "2",
                                demandLetter.demandNumber, "d"
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
                    descriptionDialogBin.cancel.setOnClickListener {
                        descriptionDialog.dismiss()
                    }
                    descriptionDialogBin.detail.setHint("Details about Rejecting Demand")
                    descriptionDialogBin.btnDone.setOnClickListener {
                        if (descriptionDialogBin.detail.text.toString().trim().length == 0) {
                            descriptionDialogBin.detail.error = "Cannot be Empty"
                        } else {
                            demRef.child(demandLetter.demandId).child("statusDescription")
                                .setValue(descriptionDialogBin.detail.text.toString())
                                .addOnCompleteListener {
                                    descriptionDialog.dismiss()
                                }
                            demRef.child(demandLetter.demandId).child("status").setValue("3")
                            var notificationid =
                                FirebaseDatabase.getInstance().reference.child("Notifications")
                                    .push().key.toString()
                            val format = SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
                            val notificationTime = format.format(Date())

                            var notification = Notification(
                                notificationid,
                                demandLetter.demandSubject,
                                notificationTime,
                                demandLetter.userId,
                                demandLetter.demandId,
                                demandLetter.userName,
                                "",
                                demandLetter.demandNumber, "d"
                            )
                            FirebaseDatabase.getInstance().reference.child("Notifications")
                                .child(notificationid).setValue(notification)
                            dialog.dismiss()
                        }
                    }
                }
                dialogBind.image.setOnClickListener {
                    val fileUri: Uri =demandLetter.imageUrl.toUri()

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(fileUri, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER

                    startActivity(intent)
                }
                dialog.show()
            }
        })

        return binding.root
    }


    override fun onDemandClick(demandLetter: DemandLetter) {


    }
}