package com.japnoor.anticorruptionadmin

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.japnoor.anticorruptionadmin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesDetails: SharedPreferences
    lateinit var editor: Editor
    lateinit var editorDetails: Editor
    var acceptedcc=""
    var resolvedcc=""
    var rejectedcc=""
    var totalcc=""

    var adminEmail: String = ""
    var adminPass: String = ""
    var adminPasscode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        acceptedcc=intent.getStringExtra("acceptedcc").toString()
//        resolvedcc=intent.getStringExtra("resolvedcc").toString()
//        rejectedcc=intent.getStringExtra("rejectedcc").toString()
//        totalcc=intent.getStringExtra("totalcc").toString()
//
//        println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV")
//        println(acceptedcc)
//        println(resolvedcc)
//        println(rejectedcc)
//        println(totalcc)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        sharedPreferences=getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)
        editor=sharedPreferences.edit()

        sharedPreferencesDetails=getSharedPreferences("Details", MODE_PRIVATE)
        editorDetails=sharedPreferencesDetails.edit()

        adminEmail=sharedPreferencesDetails.getString("adminEmail","").toString()
        adminPass=sharedPreferencesDetails.getString("adminPass","").toString()
        adminPasscode=sharedPreferencesDetails.getString("adminPasscode","").toString()


        binding.tvForgotPassword.setOnClickListener {
            intent=Intent(this,ForgotPassword::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            //validations

            if (binding.etUsername.text.isNullOrEmpty()) {
                binding.etUsername.requestFocus()
                binding.etUsername.error = "Enter Username"
            } else if (binding.etPassword.text.isNullOrEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = "Enter Password"
            }
            else if(!(binding.etUsername.text.toString().equals("eagleeyevigilance@gmail.com"))){
                Toast.makeText(this, "Wrong Email", Toast.LENGTH_SHORT).show()
            }
            else {
                var dialog=Dialog(this)
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected) {
                    dialog.setContentView(R.layout.dialog_loading)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.setCancelable(false)
                    dialog.show()
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        binding.etUsername.text.toString(),
                        binding.etPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                           dialog.dismiss()
                            FirebaseDatabase.getInstance().reference.child("Admin").child("adminPass").setValue(binding.etPassword.text.toString())
                            editor.putString("value", "1")
                            editor.apply()
                            var intent = Intent(this, AdminHomeScreen::class.java)
                            intent.putExtra("adminEmail", adminEmail)
                            intent.putExtra("adminPass", adminPass)
                            intent.putExtra("adminPasscode", adminPasscode)
                            startActivity(intent)
                            finish()
                        } else if (it.exception.toString()
                                .equals("com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.")
                        ) {
                            Toast.makeText(this, "Your does not exists", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        } else if (it.exception.toString()
                                .equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.")
                        ) {
                            Toast.makeText(this, "Not a valid email", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        } else if (it.exception.toString()
                                .equals("com.google.firebase.FirebaseException: An internal error has occurred. [ Read error:ssl=0xbfe13d98: I/O error during system call, Software caused connection abort ]")
                        ) {
                            Toast.makeText(
                                this,
                                "Check Your Internet Connection Please",
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()
                        } else if (it.exception.toString()
                                .equals("com.google.firebase.FirebaseException: An internal error has occurred. [ Read error:ssl=0xc7b4dc08: I/O error during system call, Software caused connection abort ]/O error during system call, Software caused connection abort ]")
                        ) {
                            Toast.makeText(
                                this,
                                "Check Your Internet Connection Please",
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()
                        } else if (it.exception.toString()
                                .equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.")
                        ) {
                            Toast.makeText(this, "Wrong Password", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        }
                    }

                }
                else{
                    dialog.dismiss()
                    Toast.makeText(this, "Check your internet connection please", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}