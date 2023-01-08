package com.japnoor.anticorruptionadmin

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.japnoor.anticorruptionadmin.R
import com.japnoor.anticorruptionadmin.databinding.ActivitySplashScreennBinding

class SplashScreenActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: Editor

    lateinit var binding : ActivitySplashScreennBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivitySplashScreennBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var downAnim = AnimationUtils.loadAnimation(this, R.anim.down_anim)
        sharedPreferences=getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)
        editor=sharedPreferences.edit()


        binding.ivMAimg.animation = downAnim

        Handler(Looper.getMainLooper()).postDelayed({
            if(sharedPreferences.contains("value")){
             var intent=Intent(this,AdminHomeScreen::class.java)
             startActivity(intent)
             finish()
            }
            else {
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 2500)

    }
}