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
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.japnoor.anticorruption.admin.AnnouncementAdapter
import com.japnoor.anticorruptionadmin.databinding.AnnouncementDialogBinding
import com.japnoor.anticorruptionadmin.databinding.AnnouncementItemBinding
import com.japnoor.anticorruptionadmin.databinding.FragmentAnnouncementBinding
import com.japnoor.anticorruptionadmin.databinding.PasscodeDialogBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AnnouncementFragment : Fragment(),AnnouncementsClick {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adminHomeScreen: AdminHomeScreen
    lateinit var announcementsList: ArrayList<Announcements>
    lateinit var announcementAdapter: AnnouncementAdapter
    lateinit var sharedPreferencesDetails: SharedPreferences
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
        setHasOptionsMenu(true)

        var binding =FragmentAnnouncementBinding.inflate(layoutInflater,container,false)
        adminHomeScreen=activity as AdminHomeScreen
        sharedPreferencesDetails=adminHomeScreen.getSharedPreferences("Details",
            AppCompatActivity.MODE_PRIVATE)
        editorDetails=sharedPreferencesDetails.edit()
        announcementsList= ArrayList()
        announcementAdapter= AnnouncementAdapter(adminHomeScreen,announcementsList,this)

        FirebaseDatabase.getInstance().reference.child("Announcements").addValueEventListener(object : ValueEventListener,
            AnnouncementsClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                announcementsList.clear()
                for(each in snapshot.children){
                    var announcemnt = each.getValue(Announcements::class.java)
                    if(announcemnt!=null ){
                        announcementsList.add(announcemnt)
                    }
                    announcementsList.reverse()
                    announcementAdapter= AnnouncementAdapter(adminHomeScreen,announcementsList,this)
                    binding.recyclerView.layoutManager=LinearLayoutManager(adminHomeScreen)
                    binding.recyclerView.adapter=announcementAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onUsersClick(announcements: Announcements) {
               var  dialog=Dialog(adminHomeScreen)
                var dialogB=AnnouncementDialogBinding.inflate(layoutInflater)
                dialog.setContentView(dialogB.root)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                    dialogB.subject.setText(decrypt(announcements.subject.toString()))
                    dialogB.detail.setText(decrypt(announcements.detail.toString()))
                dialogB.btnDone.setOnClickListener {
                    if(dialogB.subject.text.toString().isNullOrEmpty()){
                        dialogB.subject.error="Cannot be Empty"
                    }
                    else if(dialogB.detail.text.toString().isNullOrEmpty()){
                        dialogB.detail.error="Cannot be Empty"
                    }
                    else {
                        dialogB.progressbar.visibility=View.VISIBLE
                        dialogB.btnDone.visibility=View.GONE
                         var announceMap = mutableMapOf<String,Any>()
                             announceMap["subject"]=encrypt(dialogB.subject.text.toString())
                             announceMap["detail"]=encrypt(dialogB.detail.text.toString())
                         FirebaseDatabase.getInstance().reference.child("Announcements").child(announcements.announcementsId).updateChildren(announceMap).addOnCompleteListener {
                             dialogB.progressbar.visibility=View.GONE
                             dialogB.btnDone.visibility=View.VISIBLE
                             dialog.dismiss()
                         }
                    }
                    }
                }

        })

         binding.add.setOnClickListener {
             var dialog=Dialog(adminHomeScreen)
             var dialogB=AnnouncementDialogBinding.inflate(layoutInflater)
             dialog.setContentView(dialogB.root)
             dialog.show()
             dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
             dialogB.btnDone.setOnClickListener {
                 val connectivityManager =
                     adminHomeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                 val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                 val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                 if (isConnected) {
                 if (dialogB.subject.text.toString().isNullOrEmpty()) {
                     dialogB.subject.error = "Cannot be Empty"
                 } else if (dialogB.detail.text.toString().isNullOrEmpty()) {
                     dialogB.detail.error = "Cannot be Empty"
                 } else {
                     dialogB.progressbar.visibility = View.VISIBLE
                     dialogB.btnDone.visibility = View.GONE
                     val currentTimeMillis = System.currentTimeMillis()
                     val formatter = SimpleDateFormat("dd/MM/yyyy-HH:mm")
                     val dateTime = Date(currentTimeMillis)
                     val time = formatter.format(dateTime)
                     var announcementsId =
                         FirebaseDatabase.getInstance().reference.child("Announcements")
                             .push().key.toString()
                     var announcements = Announcements(
                         encrypt(dialogB.subject.text.toString()),
                         encrypt(dialogB.detail.text.toString()),
                         encrypt(time.toString()),
                         announcementsId
                     )
                     FirebaseDatabase.getInstance().reference.child("Announcements")
                         .child(announcementsId).setValue(announcements).addOnCompleteListener {
                         dialogB.progressbar.visibility = View.GONE
                         dialogB.btnDone.visibility = View.VISIBLE
                         dialog.dismiss()
                     }

                 }

             }
                 else{
                     Toast.makeText(adminHomeScreen,"Check your internet connection please", Toast.LENGTH_LONG).show()
                 }

             }

         }
        return binding.root
    }

    override fun onUsersClick(announcements: Announcements) {
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