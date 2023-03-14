package com.japnoor.anticorruptionadmin

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.japnoor.anticorruptionadmin.databinding.FragmentNotificationBinding
import com.japnoor.anticorruptionadmin.databinding.PasscodeDialogBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NotificationFragment : Fragment(), NotificationClick {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adminHomeScreen: AdminHomeScreen
    lateinit var sharedPreferencesDetails: SharedPreferences
    lateinit var editorDetails: SharedPreferences.Editor
    lateinit var notificationAdapter: NotificationAdapter
    lateinit var notificationList: ArrayList<NotificationChat>

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
        setHasOptionsMenu(true)

        var binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        adminHomeScreen = activity as AdminHomeScreen
        sharedPreferencesDetails=adminHomeScreen.getSharedPreferences("Details",
            AppCompatActivity.MODE_PRIVATE)
        editorDetails=sharedPreferencesDetails.edit()

        notificationList = ArrayList()
        notificationAdapter = NotificationAdapter(adminHomeScreen, notificationList, this)

        FirebaseDatabase.getInstance().reference.child("NotificationChat")
            .addValueEventListener(object : ValueEventListener,
                NotificationClick {
                override fun onDataChange(snapshot: DataSnapshot) {
                    notificationList.clear()
                    for (each in snapshot.children) {
                        var notify = each.getValue(NotificationChat::class.java)
                        if (notify != null) {
                            notificationList.add(notify)
                        }
                        notificationList.reverse()
                        notificationAdapter =
                            NotificationAdapter(adminHomeScreen, notificationList, this)
                        binding.recyclerView.layoutManager = LinearLayoutManager(adminHomeScreen)
                        binding.recyclerView.adapter = notificationAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
                override fun onClick(notification: Notification) {}
            })
        return binding.root
    }

    override fun onClick(notification: Notification) {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add("Change Password")
        menu.add("Change Passcode")
        menu.add("Logout")


        return super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.title?.equals("Logout") == true) {
            val builder = AlertDialog.Builder(adminHomeScreen)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, which ->
                FirebaseAuth.getInstance().signOut()
                var intent = Intent(adminHomeScreen, LoginActivity::class.java)
                intent.putExtra("adminEmail", adminHomeScreen.adminEmail)
                intent.putExtra("adminPass", adminHomeScreen.adminPass)
                intent.putExtra("adminPasscode", adminHomeScreen.adminPasscode)
                startActivity(intent)
                adminHomeScreen.finish()
                Toast.makeText(adminHomeScreen, "Logout Successful", Toast.LENGTH_LONG).show()
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()

        }
        else if(item.title?.equals("Change Password")==true){
            var intent= Intent(adminHomeScreen,ForgotPassword::class.java)
            adminHomeScreen.startActivity(intent)
            adminHomeScreen.finish()
        }

         else if (item.title?.equals("Change Passcode") == true) {
            var dialog = Dialog(adminHomeScreen)
            var dialogBinding = PasscodeDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogBinding.etPasswordLayout1.visibility = View.GONE
            dialogBinding.etPasswordLayout2.visibility = View.GONE
            dialogBinding.btnSignup.visibility = View.GONE
            dialogBinding.passw.visibility = View.VISIBLE
            dialogBinding.btn.visibility = View.VISIBLE
            dialogBinding.tv.setText("Enter your Account's Password")
            dialogBinding.btn.setOnClickListener {
                if (dialogBinding.etpass.text.toString().isNullOrEmpty()) {
                    dialogBinding.etpass.error = "Enter Password"
                    dialogBinding.etpass.requestFocus()
                } else if (dialogBinding.etpass.text.toString().equals(adminHomeScreen.adminPass)) {
                    dialogBinding.tv.setText("Enter the 5 digit passcode which will help \n to keep your app safe")
                    dialogBinding.etPasswordLayout1.visibility = View.VISIBLE
                    dialogBinding.etPasswordLayout2.visibility = View.VISIBLE
                    dialogBinding.btnSignup.visibility = View.VISIBLE
                    dialogBinding.passw.visibility = View.GONE
                    dialogBinding.btn.visibility = View.GONE
                } else {
                    Toast.makeText(adminHomeScreen, "Wrong Password", Toast.LENGTH_LONG).show()
                }
            }

            dialogBinding.btnSignup.setOnClickListener {
                val connectivityManager =
                    adminHomeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (dialogBinding.etPassword.text.toString().isNullOrEmpty()) {
                    dialogBinding.etPassword.error = "Enter Passcode"
                    dialogBinding.etPassword.requestFocus()
                } else if (dialogBinding.etPassword.text.toString().length < 5) {
                    dialogBinding.etPassword.error = "Passcode must be of at least 5 characters"
                    dialogBinding.etPassword.requestFocus()
                } else if (dialogBinding.etPassword.text.toString().length > 5) {
                    dialogBinding.etPassword.error = "Passcode must be of 5 characters only "
                    dialogBinding.etPassword.requestFocus()
                } else if (dialogBinding.etREPassword.text.toString().isNullOrEmpty()) {
                    dialogBinding.etREPassword.error = "Enter Passcode again"
                    dialogBinding.etREPassword.requestFocus()
                } else if ((!dialogBinding.etPassword.text.toString()
                        .equals(dialogBinding.etREPassword.text.toString()))
                ) {
                    dialogBinding.etREPassword.error = "Passcode must be same"
                    dialogBinding.etREPassword.requestFocus()
                } else {
                    if (isConnected) {
                        dialogBinding.btnSignup.visibility = View.GONE
                        dialogBinding.progressbar.visibility = View.VISIBLE
                        FirebaseDatabase.getInstance().reference.child("Admin")
                            .child("adminPasscode")
                            .setValue(dialogBinding.etPassword.text.toString())
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    editorDetails.putString(
                                        "adminPasscode",
                                        dialogBinding.etPassword.text.toString()
                                    )
                                    editorDetails.apply()
                                    editorDetails.commit()
                                    dialogBinding.btnSignup.visibility = View.VISIBLE
                                    dialogBinding.progressbar.visibility = View.GONE
                                    dialog.dismiss()
                                }
                                else {
                                    dialogBinding.btnSignup.visibility = View.VISIBLE
                                    dialogBinding.progressbar.visibility = View.GONE
                                    Toast.makeText(adminHomeScreen, it.exception.toString(), Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                    else {
                        Toast.makeText(
                            adminHomeScreen, "Check you Internet connection please",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

}