package com.japnoor.anticorruptionadmin

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminDemandBinding
import com.japnoor.anticorruptionadmin.databinding.PasscodeDialogBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminDemandFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAdminDemandBinding
    lateinit var adminHomeScreen:AdminHomeScreen
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesDetails: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var editorDetails : SharedPreferences.Editor

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

        sharedPreferencesDetails=adminHomeScreen.getSharedPreferences("Details", AppCompatActivity.MODE_PRIVATE)
        editorDetails=sharedPreferencesDetails.edit()
        binding=FragmentAdminDemandBinding.inflate(layoutInflater,container,false)
        setHasOptionsMenu(true)
        sharedPreferences = adminHomeScreen!!.getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        binding.cardTotal.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminTotalDemand)

        }
        binding.cardAccepted.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminAcceptedDemand)

        }
        binding.cardResolved.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminresolvedDemand)

        }
        binding.cardRejected.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminRejectedDemand)

        }
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add("Change Password")
        menu.add("Change Passcode")
        menu.add("Logout")

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
                intent.putExtra("adminEmail", adminHomeScreen.adminEmail)
                intent.putExtra("adminPass", adminHomeScreen.adminPass)
                intent.putExtra("adminPasscode", adminHomeScreen.adminPasscode)
                startActivity(intent)
                adminHomeScreen.finish()
                Toast.makeText(adminHomeScreen,"Logout Successful", Toast.LENGTH_LONG).show()
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()

        }
        else if(item.title?.equals("Change Passcode")==true){
            var dialog= Dialog(adminHomeScreen)
            var dialogBinding= PasscodeDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialogBinding.etPasswordLayout1.visibility=View.GONE
            dialogBinding.etPasswordLayout2.visibility=View.GONE
            dialogBinding.btnSignup.visibility=View.GONE
            dialogBinding.passw.visibility = View.VISIBLE
            dialogBinding.btn.visibility = View.VISIBLE
            dialogBinding.tv.setText("Enter your Account's Password")
            dialogBinding.btn.setOnClickListener {
                if (dialogBinding.etpass.text.toString().isNullOrEmpty()) {
                    dialogBinding.etpass.error = "Enter Password"
                    dialogBinding.etpass.requestFocus()
                } else if(dialogBinding.etpass.text.toString().equals(adminHomeScreen.adminPass))
                {
                    dialogBinding.tv.setText("Enter the 5 digit passcode which will help \n to keep your app safe")
                    dialogBinding.etPasswordLayout1.visibility = View.VISIBLE
                    dialogBinding.etPasswordLayout2.visibility = View.VISIBLE
                    dialogBinding.btnSignup.visibility = View.VISIBLE
                    dialogBinding.passw.visibility = View.GONE
                    dialogBinding.btn.visibility = View.GONE
                }
                else{
                    Toast.makeText(adminHomeScreen,"Wrong Password", Toast.LENGTH_LONG).show()
                }
            }

            dialogBinding.btnSignup.setOnClickListener {
                val connectivityManager =  adminHomeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if(dialogBinding.etPassword.text.toString().isNullOrEmpty()){
                    dialogBinding.etPassword.error="Enter Passcode"
                    dialogBinding.etPassword.requestFocus()
                }
                else if(dialogBinding.etPassword.text.toString().length<5){
                    dialogBinding.etPassword.error="Passcode must be of at least 5 characters"
                    dialogBinding.etPassword.requestFocus()
                }
                else if(dialogBinding.etPassword.text.toString().length>5){
                    dialogBinding.etPassword.error="Passcode must be of 5 characters only "
                    dialogBinding.etPassword.requestFocus()
                }

                else if(dialogBinding.etREPassword.text.toString().isNullOrEmpty()){
                    dialogBinding.etREPassword.error="Enter Passcode again"
                    dialogBinding.etREPassword.requestFocus()
                }
                else if((!dialogBinding.etPassword.text.toString().equals(dialogBinding.etREPassword.text.toString()))){
                    dialogBinding.etREPassword.error="Passcode must be same"
                    dialogBinding.etREPassword.requestFocus()
                }


                else{
                    if(isConnected){
                        dialogBinding.btnSignup.visibility = View.GONE
                        dialogBinding.progressbar.visibility = View.VISIBLE
                        FirebaseDatabase.getInstance().reference.child("Admin").child("adminPasscode").setValue(dialogBinding.etPassword.text.toString()).addOnCompleteListener {
                            if(it.isSuccessful) {
                                editorDetails.putString("adminPasscode",dialogBinding.etPassword.text.toString())
                                editorDetails.apply()
                                editorDetails.commit()
                                dialogBinding.btnSignup.visibility = View.VISIBLE
                                dialogBinding.progressbar.visibility = View.GONE
                                dialog.dismiss()
                            }
                            else{
                                dialogBinding.btnSignup.visibility = View.VISIBLE
                                dialogBinding.progressbar.visibility = View.GONE
                                Toast.makeText(adminHomeScreen,it.exception.toString(),
                                    Toast.LENGTH_LONG).show()
                            }
                        }


                    }
                    else{
                        Toast.makeText(adminHomeScreen,"Check you Internet connection please",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
            dialog.show()

        }


        return super.onOptionsItemSelected(item)
    }
}