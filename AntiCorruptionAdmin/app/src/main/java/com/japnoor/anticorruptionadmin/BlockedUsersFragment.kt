package com.japnoor.anticorruptionadmin

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruptionadmin.databinding.FragmentBlockedUsersBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentUsersBinding
import com.japnoor.anticorruptionadmin.databinding.ShowUserDeatailsBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BlockedUsersFragment : Fragment(),UsersClick {
    private var param1: String? = null
    private var param2: String? = null
    var arrayList: ArrayList<Users> = ArrayList()
    lateinit var adapter: BlockedUserAdapter
    lateinit var database: FirebaseDatabase
    lateinit var userref: DatabaseReference
    lateinit var compref: DatabaseReference
    lateinit var demref: DatabaseReference
    lateinit var adminHomeScreen: AdminHomeScreen
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

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

        var binding = FragmentBlockedUsersBinding.inflate(layoutInflater, container, false)
        database = FirebaseDatabase.getInstance()
        userref = database.reference.child("Users")
        compref = database.reference.child("Complaints")
        demref = database.reference.child("Demand Letter")
        setHasOptionsMenu(true)
        sharedPreferences = adminHomeScreen!!.getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
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
        userref.addValueEventListener(object : ValueEventListener, UsersClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                for (eachUser in snapshot.children) {
                    val user = eachUser.getValue(Users::class.java)
                    if (user != null && user.userStatus!="0") {
                        arrayList.add(user)
                    }
                    arrayList.reverse()
                    adapter = BlockedUserAdapter(adminHomeScreen, arrayList, this)
                    binding.recyclerView.layoutManager = LinearLayoutManager(adminHomeScreen)
                    binding.recyclerView.adapter = adapter
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = ArrayList<Users>()
                            for (item in arrayList) {
                                if (item.name.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || item.email.toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                )
                                    filteredList.add(item)
                            }
                            adapter.FilteredList(filteredList)
                        }

                    })
                }
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onUsersClick(users: Users) {

                var dialog = Dialog(requireContext())
                var dialogBinding = ShowUserDeatailsBinding.inflate(layoutInflater)
                dialog.setContentView(dialogBinding.root)
                dialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )

                var complaintCount = 0
                var demandCount = 0
                dialogBinding.cardComplaint.setOnClickListener {
                    dialog.dismiss()
                    var bundle=Bundle()
                    bundle.putString("uid",users.userId.toString())
                    adminHomeScreen.navController.navigate(R.id.userComplaintFragment,bundle)
                }
                dialogBinding.cardDemand.setOnClickListener {
                    dialog.dismiss()
                    var bundle=Bundle()
                    bundle.putString("uid",users.userId.toString())
                    adminHomeScreen.navController.navigate(R.id.userDemandFragment,bundle)
                }
                compref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (eachComplaint in snapshot.children) {
                            var comp = eachComplaint.getValue(Complaints::class.java)
                            if (comp != null && users.userId.equals(comp.userId)) {
                                complaintCount += 1
                                println("Count0 -> " + complaintCount.toString())
                                dialogBinding.complaints.setText(complaintCount.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                demref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (eachdem in snapshot.children) {
                            var dem = eachdem.getValue(DemandLetter::class.java)
                            if (dem != null && users.userId.equals(dem.userId) ) {
                                demandCount += 1
                                dialogBinding.demand.setText(demandCount.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
                when (users.profileValue) {
                    "1" -> dialogBinding.Profile.setImageResource(R.drawable.man1)
                    "2" -> dialogBinding.Profile.setImageResource(R.drawable.man2)
                    "3" -> dialogBinding.Profile.setImageResource(R.drawable.man3)
                    "4" -> dialogBinding.Profile.setImageResource(R.drawable.man4)
                    "5" -> dialogBinding.Profile.setImageResource(R.drawable.girl1)
                    "6" -> dialogBinding.Profile.setImageResource(R.drawable.girl2)
                    "7" -> dialogBinding.Profile.setImageResource(R.drawable.girl3)
                    "8" -> dialogBinding.Profile.setImageResource(R.drawable.girl4)
                }
                dialogBinding.name.setText(users.name)
                dialogBinding.email.setText(users.email)
                dialogBinding.birthdate.setText(users.birthdate)
                dialogBinding.date.setText(users.userDate)
                val currentTime = System.currentTimeMillis()
                //                    val sevenDaysInMillisecond = 7 * 24 * 60 * 60 * 1000
                val sevenDaysInMillisecond : Long = 604800000
                var timecheck:Long=0
                userref.addValueEventListener(object  : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(each in snapshot.children){
                            var userdetail=each.getValue(Users::class.java)
                            if(userdetail!=null && userdetail.userId.equals(users.userId) && userdetail.userStatus!="0"){
                                timecheck=userdetail.userStatus.toLong() + sevenDaysInMillisecond
                                println("Time->" + timecheck)

                            }
                        }

                        var timeleft=timecheck-currentTime
                        val days = timeleft / (1000 * 60 * 60 * 24)
                        val hours = (timeleft / (1000 * 60 * 60)) % 24
                        dialogBinding.unblocktime.text ="Unblocks in : " + days.toString() + "d " + hours.toString() +"h"
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
                println("Count-> " + complaintCount.toString())
                dialogBinding.block.setText("unblock this user")
                dialogBinding.block.setOnClickListener {
                    var bottomSheet = BottomSheetDialog(requireContext())
                    bottomSheet.setContentView(R.layout.dialog_delete_users)
                    bottomSheet.show()
                    var tvmsg=bottomSheet.findViewById<TextView>(R.id.textmsg)
                    var tvYes = bottomSheet.findViewById<TextView>(R.id.tvYes)
                    var tvNo = bottomSheet.findViewById<TextView>(R.id.tvNo)
                    tvYes?.setBackgroundResource(R.drawable.yes_btn_red)
                    tvmsg?.setText("Are you sure you want \n to unblock this User ?")
                    tvNo?.setOnClickListener {
                        bottomSheet.dismiss()
                    }
                    tvYes?.setOnClickListener {
                        userref.child(users.userId).child("userStatus").setValue("0")
                        adminHomeScreen.navController.navigate(R.id.blockedUsersFragment)
                        bottomSheet.dismiss()
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
        })

        return binding.root
    }

    override fun onUsersClick(users: Users) {
        TODO("Not yet implemented")
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu?.add("Logout")


        return super.onCreateOptionsMenu(menu,inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.title?.equals("Logout") == true) {
            val builder = AlertDialog.Builder(adminHomeScreen)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, which ->
                editor.remove("value")
                editor.apply()
                var intent = Intent(adminHomeScreen, LoginActivity::class.java)
                startActivity(intent)
                adminHomeScreen.finish()
                Toast.makeText(adminHomeScreen,"Logout Successful", Toast.LENGTH_LONG).show()
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()

        }

        return super.onOptionsItemSelected(item)
    }

}