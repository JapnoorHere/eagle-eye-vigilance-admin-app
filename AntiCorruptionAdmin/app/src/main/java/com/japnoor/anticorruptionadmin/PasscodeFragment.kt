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
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hanks.passcodeview.PasscodeView
import com.japnoor.anticorruptionadmin.databinding.FragmentPasscodeBinding
import com.japnoor.anticorruptionadmin.databinding.PasscodeDialogBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PasscodeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var splashScreenActivity: SplashScreenActivity
    lateinit var sharedPreferncesDetail : SharedPreferences
    lateinit var editor: Editor

    var adminEmail: String = ""
    var adminPass: String = ""
    var adminPasscode: String = ""
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
        splashScreenActivity=activity as SplashScreenActivity

        sharedPreferncesDetail=splashScreenActivity.getSharedPreferences("Details",AppCompatActivity.MODE_PRIVATE)
        editor=sharedPreferncesDetail.edit()

            adminEmail=sharedPreferncesDetail.getString("adminEmail","").toString()
            adminPass=sharedPreferncesDetail.getString("adminPass","").toString()
            adminPasscode=sharedPreferncesDetail.getString("adminPasscode","").toString()

        var binding= FragmentPasscodeBinding.inflate(layoutInflater,container,false)
        binding.passcodeView.setPasscodeLength(5).setLocalPasscode(adminPasscode).listener=object  : PasscodeView.PasscodeViewListener{
            override fun onFail() {
                TODO("Not yet implemented")
            }

            override fun onSuccess(number: String?) {
                var intent= Intent(splashScreenActivity,AdminHomeScreen::class.java)
                splashScreenActivity.startActivity(intent)
                splashScreenActivity.finish()
            }

        }

        binding.tvForgotPassword.setOnClickListener {
            var dialog= Dialog(splashScreenActivity)
            var dialogBinding= PasscodeDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
                } else if(dialogBinding.etpass.text.toString().equals(adminPass))
                {
                    dialogBinding.tv.setText("Enter the 5 digit passcode which will help \n to keep your app safe")
                    dialogBinding.etPasswordLayout1.visibility = View.VISIBLE
                    dialogBinding.etPasswordLayout2.visibility = View.VISIBLE
                    dialogBinding.btnSignup.visibility = View.VISIBLE
                    dialogBinding.passw.visibility = View.GONE
                    dialogBinding.btn.visibility = View.GONE
                }
                else{
                    Toast.makeText(splashScreenActivity,"Wrong Password", Toast.LENGTH_LONG).show()
                }
            }

            dialogBinding.btnSignup.setOnClickListener {
                val connectivityManager =  splashScreenActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
                               editor.putString("adminPasscode",dialogBinding.etPassword.text.toString())
                               editor.apply()
                               editor.commit()
                               dialogBinding.btnSignup.visibility = View.VISIBLE
                               dialogBinding.progressbar.visibility = View.GONE
                               dialog.dismiss()
                               splashScreenActivity.navController.navigate(R.id.passcodeFragment)
                           }
                            else{
                               dialogBinding.btnSignup.visibility = View.VISIBLE
                               dialogBinding.progressbar.visibility = View.GONE
                               Toast.makeText(splashScreenActivity,it.exception.toString(),
                                   Toast.LENGTH_LONG).show()
                           }
                        }


                    }
                    else{
                        Toast.makeText(splashScreenActivity,"Check you Internet connection please",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
            dialog.show()
        }

        return binding.root
    }

}