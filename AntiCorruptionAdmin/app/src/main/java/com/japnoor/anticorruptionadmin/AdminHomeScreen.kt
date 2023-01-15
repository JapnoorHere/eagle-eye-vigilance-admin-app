package com.japnoor.anticorruptionadmin

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.japnoor.anticorruptionadmin.databinding.ActivityAdminHomeScreenBinding

class AdminHomeScreen : AppCompatActivity() {

    lateinit var binding: ActivityAdminHomeScreenBinding
    lateinit var navController: NavController
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

            sharedPreferences=getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)
            editor=sharedPreferences.edit()

        navController = findNavController(R.id.navControllerAdmin)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.BottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bnComplaint -> {
                    navController.navigate(R.id.adminHomeFragment)
                    this.setTitle("Complaints")

                }
                R.id.bnDemandLetter -> {
                    navController.navigate(R.id.adminDemandFragment)
                    this.setTitle("Demand Letters")


                }
                R.id.bnProfile -> {
                    navController.navigate(R.id.usersFragment)
                    this.setTitle("Users")

                }
                else -> {}
            }
            return@setOnItemSelectedListener true


        }

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menu?.add("Logout")
//        menu?.add("Blocked Users")
//
//        return super.onCreateOptionsMenu(menu)
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        if (item.title?.equals("Logout") == true) {
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle("Logout")
//            builder.setMessage("Are you sure you want to logout?")
//            builder.setPositiveButton("Yes") { dialog, which ->
//                editor.remove("value")
//                editor.commit()
//                var intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//                finish()
//                Toast.makeText(this,"Logout Successful", Toast.LENGTH_LONG).show()
//            }
//            builder.setNegativeButton("No") { dialog, which ->
//                dialog.dismiss()
//            }
//            builder.show()
//
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

}



