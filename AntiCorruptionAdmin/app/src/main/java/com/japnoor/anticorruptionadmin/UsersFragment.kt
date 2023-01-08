package com.japnoor.anticorruptionadmin

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.japnoor.anticorruptionadmin.databinding.FragmentUsersBinding
import com.japnoor.anticorruptionadmin.databinding.ShowUserDeatailsBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UsersFragment : Fragment(),UsersClick{
    private var param1: String? = null
    private var param2: String? = null
    var arrayList: ArrayList<Users> = ArrayList()
    lateinit var adapter : UserListAdapter
    lateinit var database: FirebaseDatabase
    lateinit var userref : DatabaseReference
    lateinit var adminHomeScreen:AdminHomeScreen



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
        adminHomeScreen=activity as AdminHomeScreen

        var binding = FragmentUsersBinding.inflate(layoutInflater, container, false)
        database=FirebaseDatabase.getInstance()
        userref=database.reference.child("Users")

        userref.addValueEventListener(object : ValueEventListener, UsersClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                for (eachUser in snapshot.children) {
                    val user = eachUser.getValue(Users::class.java)
                    if (user != null) {
                        arrayList.add(user)
                    }
                    adapter = UserListAdapter(adminHomeScreen, arrayList, this)
                    binding.recyclerView.layoutManager = LinearLayoutManager(adminHomeScreen)
                    binding.recyclerView.adapter = adapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onUsersClick(users: Users) {
                var dialog=Dialog(requireContext())
                var dialogBinding= ShowUserDeatailsBinding.inflate(layoutInflater)
                dialog.setContentView(dialogBinding.root)
                dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
                dialogBinding.name.setText(users.name)
                dialogBinding.phoneno.setText(users.phone)
                dialogBinding.email.setText(users.email)
                dialogBinding.fabAdd1.setOnClickListener{
                    dialog.dismiss()
                }
                dialogBinding.fabAdd2.setOnClickListener{
                    var bottomSheet = BottomSheetDialog(requireContext())
                    bottomSheet.setContentView(R.layout.dialog_delete_users)
                    bottomSheet.show()
                    var tvYes = bottomSheet.findViewById<TextView>(R.id.tvYes)
                    var tvNo = bottomSheet.findViewById<TextView>(R.id.tvNo)

                    tvNo?.setOnClickListener{
                        bottomSheet.dismiss()
                    }
                    tvYes?.setOnClickListener{
                        userref.child(users.userId).removeValue()
                        bottomSheet.dismiss()
                        dialog.dismiss()
                    }
                }
                dialog.show()            }
        })

            return binding.root
    }



    override fun onUsersClick(users: Users) {
        TODO("Not yet implemented")
    }


}