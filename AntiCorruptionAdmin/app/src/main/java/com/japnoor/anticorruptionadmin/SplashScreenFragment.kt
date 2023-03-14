package com.japnoor.anticorruptionadmin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruptionadmin.databinding.FragmentSplashScreenBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SplashScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesDetails: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var editorDetails: SharedPreferences.Editor
    lateinit var adminRef: DatabaseReference
    lateinit var splashScreenActivity: SplashScreenActivity


    private var param1: String? = null
    private var param2: String? = null

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
        splashScreenActivity = activity as SplashScreenActivity
        var binding = FragmentSplashScreenBinding.inflate(layoutInflater, container, false)

        sharedPreferencesDetails=splashScreenActivity.getSharedPreferences("Details",AppCompatActivity.MODE_PRIVATE)
        editorDetails=sharedPreferencesDetails.edit()
        var downAnim = AnimationUtils.loadAnimation(splashScreenActivity, R.anim.down_anim)
        sharedPreferences = splashScreenActivity.getSharedPreferences(
            resources.getString(R.string.app_name),
            AppCompatActivity.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
        adminRef = FirebaseDatabase.getInstance().reference.child("Admin")

        binding.ivMAimg.animation = downAnim
        var adminEmail: String = ""
        var adminPass: String = ""
        var adminPasscode: String = ""
        var adminList = ArrayList<String>()


        FirebaseDatabase.getInstance().reference.child("Admin").child("adminEmail").get().addOnCompleteListener {
            editorDetails.putString("adminEmail", it.result.value.toString())
            editorDetails.apply()
            editorDetails.commit()
        }
        FirebaseDatabase.getInstance().reference.child("Admin").child("adminPass").get().addOnCompleteListener {
            editorDetails.putString("adminPass", it.result.value.toString())
            editorDetails.apply()
            editorDetails.commit()
        }
        FirebaseDatabase.getInstance().reference.child("Admin").child("adminPasscode").get().addOnCompleteListener {
            editorDetails.putString("adminPasscode", it.result.value.toString())
            editorDetails.apply()
            editorDetails.commit()
        }


//        var admin=Admin("eagleeyevigilance@gmail.com","111111","22222")
//
//        FirebaseDatabase.getInstance().reference.child("Admin").setValue(admin)

        var user=FirebaseAuth.getInstance().currentUser

        Handler(Looper.getMainLooper()).postDelayed(
            {
                val connectivityManager =  splashScreenActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                if (isConnected){
                    if (user!=null) {
                        splashScreenActivity.navController.navigate(R.id.passcodeFragment)
                    } else {
                        var intent = Intent(splashScreenActivity, LoginActivity::class.java)
                        splashScreenActivity.startActivity(intent)
                        splashScreenActivity.finish()
                    }
                }
                else{
                    Toast.makeText(splashScreenActivity,"Check your internet connection please", Toast.LENGTH_LONG).show()
                    var intent=Intent(splashScreenActivity,LoginActivity::class.java)
                    startActivity(intent)
                    splashScreenActivity.finish()
                }
        }, 2500)



        return binding.root
    }

}