package com.japnoor.anticorruptionadmin.demand

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.japnoor.anticorruption.admin.UserDemandAdapter
import com.japnoor.anticorruptionadmin.*
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.DemandDialogBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentUserDemandBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserDemandFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var userDemandAdapter: UserDemandAdapter
    lateinit var adminHomeScreen : AdminHomeScreen
    lateinit var demref: DatabaseReference
    var uid : String=""

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
        arguments.let {
            uid=it?.getString("uid").toString()
        }
        adminHomeScreen=activity as AdminHomeScreen
        demref=FirebaseDatabase.getInstance().reference.child("Demand Letter")

        var binding=FragmentUserDemandBinding.inflate(layoutInflater,container,false)
        var demArrayList=ArrayList<DemandLetter>()

        binding.search.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.search.right - binding.search.compoundDrawables[2].bounds.width())) {
                    binding.search.text.clear()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
        demref.addValueEventListener(object : ValueEventListener, UserDemandClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                demArrayList.clear()
                for (each in snapshot.children) {
                    var complaintDetail = each.getValue(DemandLetter::class.java)
                    if (complaintDetail != null && complaintDetail.userId.equals(uid)) {
                        demArrayList.add(complaintDetail)
                    }
                    demArrayList.reverse()
                    userDemandAdapter =
                        UserDemandAdapter(adminHomeScreen, demArrayList, this)
                    binding.recyclerView.layoutManager = LinearLayoutManager(adminHomeScreen)
                    binding.recyclerView.adapter = userDemandAdapter
                    binding.shimmer.visibility = View.GONE
                    binding.shimmer.stopShimmer()
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = java.util.ArrayList<DemandLetter>()
                            for (item in demArrayList){
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
                            userDemandAdapter.FilteredList(filteredList)
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

            override fun onClick(demandLetter: DemandLetter) {
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
                if (demandLetter.status == "1") {
                    dialogBind.fabAccepted.visibility = View.GONE
                    dialogBind.fabRejected.visibility = View.VISIBLE
                    dialogBind.stamp.visibility = View.VISIBLE
                    dialogBind.stamp.setImageResource(R.drawable.accpeted_stamp)
                } else if (demandLetter.status == "2") {
                    dialogBind.fabAccepted.visibility = View.GONE
                    dialogBind.fabRejected.visibility = View.GONE
                    dialogBind.fabResolved.visibility = View.GONE
                    dialogBind.stamp.visibility = View.VISIBLE
                    dialogBind.stamp.setImageResource(R.drawable.resolved_stamp)
                } else if (demandLetter.status == "3") {
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
                    dialogBind.stamp.setImageResource(R.drawable.rejected_stamp)
                }

                dialogBind.fabAccepted.setOnClickListener {
                    demref.child(demandLetter.demandId).child("status").setValue("1")
                    dialog.dismiss()

                }
                dialogBind.fabResolved.setOnClickListener {
                    demref.child(demandLetter.demandId).child("status").setValue("2")
                    dialog.dismiss()

                }
                dialogBind.fabRejected.setOnClickListener {
                    demref.child(demandLetter.demandId).child("status").setValue("3")
                    dialog.dismiss()
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

}