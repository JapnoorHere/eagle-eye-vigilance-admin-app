package com.japnoor.anticorruptionadmin

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.japnoor.anticorruptionadmin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
     var adminEmail : String =""
     var adminPass : String =""
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        sharedPreferences=getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)
        editor=sharedPreferences.edit()

        adminEmail="anticorruptionpunjab75@gmail.com"
        adminPass="111111"
        binding.btnLogin.setOnClickListener {
            //validations
            if (binding.etUsername.text.isNullOrEmpty()) {
                binding.etUsername.requestFocus()
                binding.etUsername.error = "Enter Username"
            } else if (binding.etPassword.text.isNullOrEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = "Enter Password"
            } else if(binding.etUsername.text.toString().equals(adminEmail)&&binding.etPassword.text.toString().equals(adminPass)){
                editor.putString("value","1")
                editor.apply()
                var intent=Intent(this,AdminHomeScreen::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Enter valid email or password",Toast.LENGTH_LONG).show()
            }
        }
    }
}