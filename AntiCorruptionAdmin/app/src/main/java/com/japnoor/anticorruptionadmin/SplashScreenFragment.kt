package com.japnoor.anticorruptionadmin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
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
        FirebaseDatabase.getInstance().reference.child("Admin")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (each in snapshot.children) {
                        var adminDetail = each.getValue(String::class.java)
                        adminList.add(adminDetail.toString())
                        println("Admin ->" + adminList)

                    }
                    adminEmail = adminList[0]
                    adminPass = adminList[1]
                    adminPasscode = adminList[2]
                    editorDetails.putString("adminEmail",adminEmail)
                    editorDetails.putString("adminPass",adminPass)
                    editorDetails.putString("adminPasscode",adminPasscode)
                    editorDetails.apply()
                    editorDetails.commit()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPreferences.contains("value")) {
                splashScreenActivity.navController.navigate(R.id.passcodeFragment)
            } else {
                            var intent = Intent(splashScreenActivity,LoginActivity::class.java)
                            splashScreenActivity.startActivity(intent)
                            splashScreenActivity.finish()
            }

        }, 2500)



        return binding.root
    }

}