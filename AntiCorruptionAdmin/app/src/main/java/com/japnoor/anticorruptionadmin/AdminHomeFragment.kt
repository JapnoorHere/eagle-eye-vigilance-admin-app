package com.japnoor.anticorruptionadmin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminHomeBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentAdminHomeBinding
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



        binding= FragmentAdminHomeBinding.inflate(layoutInflater,container,false)
        adminHomeScreen=activity as AdminHomeScreen
           setHasOptionsMenu(true)
        sharedPreferences = adminHomeScreen!!.getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        binding.cardTotal.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminTotalComplaints)
        }
        binding.cardAccepted.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminAcceptedFragment)
        }
        binding.cardResolved.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminResolvedfragment)
        }
        binding.cardRejected.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminRejectedFragment)

        }

        return binding.root
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