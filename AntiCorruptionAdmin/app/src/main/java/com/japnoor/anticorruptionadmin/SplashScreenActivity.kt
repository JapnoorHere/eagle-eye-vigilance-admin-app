package com.japnoor.anticorruptionadmin

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.database.*
import com.google.protobuf.Value
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.ActivitySplashScreennBinding
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class SplashScreenActivity : AppCompatActivity() {

    var adminPasscode : String=""
    lateinit var binding : ActivitySplashScreennBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivitySplashScreennBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController=findNavController(R.id.navController)

    }


}