package com.japnoor.anticorruption.admin.Demand

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.japnoor.anticorruption.admin.AdminAcceptedAdapter
import com.japnoor.anticorruption.admin.AdminAcceptedDemandAdapter
import com.japnoor.anticorruption.admin.AdminRejectedDemandAdapter
import com.japnoor.anticorruption.admin.AdminTotalDemandAdapter
import com.japnoor.anticorruptionadmin.*
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.DemandDialogBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminRejectedComplaintsBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminTotalComplaintsBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminRejectedDemand : Fragment(), DemandClick {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var demandList: ArrayList<DemandLetter> = ArrayList()
    lateinit var adminRejectedDemandAdapter: AdminRejectedDemandAdapter
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
        var binding = FragmentAdminRejectedComplaintsBinding.inflate(layoutInflater, container, false)
        binding.shimmer.startShimmer()
        binding.title.setText("Rejected Letters")
        demRef.addValueEventListener(object : ValueEventListener, DemandClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                demandList.clear()
                for (eachDemand in snapshot.children) {
                    var demand = eachDemand.getValue(DemandLetter::class.java)

                    if (demand != null && demand.status.equals("3")) {
                        demandList.add(demand)
                    }
                    adminRejectedDemandAdapter =
                        AdminRejectedDemandAdapter(adminHomeScreen, demandList, this)
                    binding.recyclerView.layoutManager = GridLayoutManager(adminHomeScreen,2)
                    binding.recyclerView.adapter = adminRejectedDemandAdapter
                    binding.shimmer.visibility=View.GONE
                    binding.shimmer.stopShimmer()
                    binding.recyclerView.visibility=View.VISIBLE
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
                dialogBind.date.setText(demandLetter.demandDate)
                dialogBind.name.setText(demandLetter.userName)
                dialogBind.email.setText(demandLetter.userEmail)
                dialogBind.tvSummary.setText(demandLetter.demandSubject)
                dialogBind.tvDetails.setText(demandLetter.demandDetails)
                dialogBind.tvDistrict.setText(demandLetter.demandDistrict)
                dialogBind.image.setOnClickListener {
                }
                dialogBind.fabAccepted.visibility=View.GONE
                dialogBind.fabRejected.visibility=View.GONE
                dialogBind.fabResolved.visibility=View.GONE
                dialogBind.stamp.visibility=View.VISIBLE
                dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)

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