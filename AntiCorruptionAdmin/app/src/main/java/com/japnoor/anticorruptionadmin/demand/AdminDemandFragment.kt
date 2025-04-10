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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminDemandBinding
import com.japnoor.anticorruptionadmin.databinding.PasscodeDialogBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminDemandFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAdminDemandBinding
    lateinit var adminHomeScreen: AdminHomeScreen
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesDetails: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var editorDetails: SharedPreferences.Editor


    private fun decrypt(input: String): String {
        var forgot = ForogotPasscode()
        var encryptionKey=forgot.key()
        var secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")

        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }
    private fun encrypt(input: String): String {
        var forgot = ForogotPasscode()
        var encryptionKey=forgot.key()
        var secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
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


        adminHomeScreen = activity as AdminHomeScreen

        sharedPreferencesDetails =
            adminHomeScreen.getSharedPreferences("Details", AppCompatActivity.MODE_PRIVATE)
        editorDetails = sharedPreferencesDetails.edit()
        binding = FragmentAdminDemandBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        sharedPreferences = adminHomeScreen!!.getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()

        FirebaseDatabase.getInstance().reference.child("Demand Letter")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var dpen = 0
                    var dacc = 0
                    var dres = 0
                    var drej = 0
                    for (each in snapshot.children) {
                        var dem = each.getValue(DemandLetter::class.java)
                        if (dem?.status.equals("1")) {
                            dacc++
                        } else if (dem?.status.equals("2")) {
                            dres++
                        } else if (dem?.status.equals("3")) {
                            drej++
                        } else {
                            dpen++
                        }
                    }
                    binding.tvNo1.setText(dpen.toString())
                    binding.tvNo2.setText(dres.toString())
                    binding.tvNo3.setText(dacc.toString())
                    binding.tvNo4.setText(drej.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        binding.cardTotal.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminTotalDemand)

        }
        binding.cardAccepted.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminAcceptedDemand)

        }
        binding.cardResolved.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminresolvedDemand)

        }
        binding.cardRejected.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminRejectedDemand)

        }
        FirebaseDatabase.getInstance().reference.child("Demand Letter").limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (each in snapshot.children) {
                        var dem = each.getValue(DemandLetter::class.java)
                        if ((dem?.status.equals(""))) {
                            binding.demLL.visibility = View.VISIBLE
                            binding.recent.visibility = View.VISIBLE
                            binding.demSubject.setText(decrypt(dem?.demandSubject.toString()))
                            binding.date.setText(decrypt(dem?.demandDate.toString()))
                            binding.time.setText(decrypt(dem?.demandTime.toString()))
                            binding.demNumber.setText(decrypt(dem?.demandNumber.toString()))
                            binding.demandDetails.setText(decrypt(dem?.demandDetails.toString()))
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

        } else if (item.title?.equals("Change Password") == true) {
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