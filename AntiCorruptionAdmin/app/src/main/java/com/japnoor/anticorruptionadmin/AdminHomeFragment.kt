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
import android.util.Base64
import android.util.Base64.encodeToString
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminHomeBinding
import com.japnoor.anticorruptionadmin.databinding.PasscodeDialogBinding
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAdminHomeBinding
    lateinit var adminHomeScreen: AdminHomeScreen
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesDetails: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var editorDetails: SharedPreferences.Editor
    var encryptionKey: String? =null
    var secretKeySpec: SecretKeySpec? =null

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

        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        binding = FragmentAdminHomeBinding.inflate(layoutInflater, container, false)
        adminHomeScreen = activity as AdminHomeScreen

        sharedPreferencesDetails =
            adminHomeScreen.getSharedPreferences("Details", AppCompatActivity.MODE_PRIVATE)
        editorDetails = sharedPreferencesDetails.edit()

        setHasOptionsMenu(true)
        sharedPreferences = adminHomeScreen!!.getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()


        FirebaseDatabase.getInstance().reference.child("Complaints")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var cpen = 0
                    var cacc = 0
                    var cres = 0
                    var crej = 0
                    for (each in snapshot.children) {
                        var comp = each.getValue(Complaints::class.java)
                        if (comp?.status.equals("1")) {
                            cacc++
                        } else if (comp?.status.equals("2")) {
                            cres++
                        } else if (comp?.status.equals("3")) {
                            crej++
                        } else {
                            cpen++
                        }
                    }
                    binding.tvNo1.setText(cpen.toString())
                    binding.tvNo2.setText(cres.toString())
                    binding.tvNo3.setText(cacc.toString())
                    binding.tvNo4.setText(crej.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })



        binding.cardTotal.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminTotalComplaints)
        }
        binding.cardAccepted.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminAcceptedFragment)
        }
        binding.cardResolved.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminResolvedfragment)
        }
        binding.cardRejected.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminHomeFragment_to_adminRejectedFragment)
        }


        FirebaseDatabase.getInstance().reference.child("Complaints").limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (each in snapshot.children) {
                    var comp = each.getValue(Complaints::class.java)
                    if ((comp?.status.equals(""))) {
                        binding.compLL.visibility=View.VISIBLE
                        binding.recent.visibility=View.VISIBLE
                        binding.compAgainst.setText(decrypt(comp?.complaintAgainst.toString()))
                        binding.date.setText(decrypt(comp?.complaintDate.toString()))
                        binding.time.setText(decrypt(comp?.complaintTime.toString()))
                        binding.complaintNumber.setText(decrypt(comp?.complaintNumber.toString()))
                        binding.tvDistrict.setText(decrypt(comp?.complaintDistrict.toString()))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        FirebaseDatabase.getInstance().reference.child("NotificationChat").limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (each in snapshot.children) {
                    var notfy = each.getValue(NotificationChat::class.java)
                    if (notfy!=null) {
                        binding.cardnotifi.visibility=View.VISIBLE
                        binding.recentnotfi.visibility=View.VISIBLE
                        binding.notificompAgainst.setText(decrypt(notfy.complaintAgainst))
                        binding.notifitime.setText(decrypt(notfy.notificationTime))
                        binding.notificomplaintNumber.setText(decrypt(notfy.complaintNumber))
                        binding.msg.setText(decrypt(notfy.notificationMsg))
                        binding.name.setText(decrypt(notfy.userName))


                        when(notfy.notificationType){
                            "c"->{
                                binding.compnum.setText("Complaint Number : ")
                                binding.aga.setText("Against")
                            }
                            "d"->{
                                binding.compnum.setText("Demand Number : ")
                                binding.aga.setText("Subject")
                            }
                            "b"->{
                                binding.compnum.setText("User Query")
                                binding.complaintNumber.visibility=View.GONE
                                binding.aga.visibility=View.GONE
                                binding.compAgainst.visibility=View.GONE
                                binding.msg.setText(decrypt(notfy.notificationMsg))
                                binding.time.setText(decrypt(notfy.notificationTime))
                                binding.name.setText(decrypt(notfy.userName))
                            }

                        }

                        binding.cardnotifi.setOnClickListener {
                            val connectivityManager =  adminHomeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                            if (isConnected){
                            when (notfy.notificationType) {
                                "c" -> {
                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(notfy.userId).child("profileValue").get()
                                        .addOnCompleteListener {
                                            var intent =
                                                Intent(context, ComplaintChatActivity::class.java)
                                            intent.putExtra("uid", notfy.userId)
                                            intent.putExtra("cid", notfy.complaintId)
                                            intent.putExtra("name", decrypt(notfy.userName))
                                            intent.putExtra("profile", it.result.value.toString())
                                            intent.putExtra("cnumber", decrypt(notfy.complaintNumber))
                                            intent.putExtra("type", "c")
                                            intent.putExtra("status", notfy.complaintStatus)
                                            intent.putExtra("against", decrypt(notfy.complaintAgainst))
                                            adminHomeScreen.startActivity(intent)
                                        }
                                }
                                "d" -> {
                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(notfy.userId).child("profileValue").get()
                                        .addOnCompleteListener {
                                            var intent =
                                                Intent(context, ComplaintChatActivity::class.java)
                                            intent.putExtra("uid", notfy.userId)
                                            intent.putExtra("cid", notfy.complaintId)
                                            intent.putExtra("name", decrypt(notfy.userName))
                                            intent.putExtra("profile", it.result.value.toString())
                                            intent.putExtra("cnumber", decrypt(notfy.complaintNumber))
                                            intent.putExtra("type", "d")
                                            intent.putExtra("status", notfy.complaintStatus)
                                            intent.putExtra("against", decrypt(notfy.complaintAgainst))
                                            adminHomeScreen.startActivity(intent)
                                        }
                                }
                                "b" -> {
                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(notfy.userId).child("profileValue").get()
                                        .addOnCompleteListener {
                                            var intent =
                                                Intent(adminHomeScreen, ChatActivity::class.java)
                                            intent.putExtra("uid", notfy.userId)
                                            intent.putExtra("name", decrypt(notfy.userName))
                                            intent.putExtra("profile", it.result.value.toString())
                                            adminHomeScreen.startActivity(intent)
                                        }
                                }
                            }
                        }
                            else{
                                Toast.makeText(adminHomeScreen,"Check your internet connection please", Toast.LENGTH_LONG).show()
                            }
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add("Change Password")
        menu.add("Change Passcode")
        menu.add("Important Links")
        menu.add("Logout")


        return super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.title?.equals("Logout") == true) {
            val builder = AlertDialog.Builder(adminHomeScreen)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, which ->
                editor.remove("value")
                editor.apply()
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

        else if(item.title?.equals("Important Links")==true){
             adminHomeScreen.navController.navigate(R.id.importantLinks)
            adminHomeScreen.title="Important Links"
        }
        else if (item.title?.equals("Change Password") == true) {
            var intent = Intent(adminHomeScreen, ForgotPassword::class.java)
            adminHomeScreen.startActivity(intent)
            adminHomeScreen.finish()
        } else if (item.title?.equals("Change Passcode") == true) {
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
                            .setValue(encrypt(dialogBinding.etPassword.text.toString()))
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
                                } else {
                                    dialogBinding.btnSignup.visibility = View.VISIBLE
                                    dialogBinding.progressbar.visibility = View.GONE
                                    Toast.makeText(
                                        adminHomeScreen, it.exception.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }


                    } else {
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