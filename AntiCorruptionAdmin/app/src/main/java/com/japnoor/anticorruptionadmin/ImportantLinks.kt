package com.japnoor.anticorruptionadmin

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.japnoor.anticorruptionadmin.databinding.FragmentImportantLinksBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ImportantLinks : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adminHomeScreen: AdminHomeScreen

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
        var binding = FragmentImportantLinksBinding.inflate(layoutInflater, container, false)

        val textView = binding.teleList
        val text = "Telephone no. of Police Officers >>"
        val spannable = SpannableString(text)
        spannable.setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        textView.text = spannable

        binding.teleList.setOnClickListener {
            var intent=Intent(adminHomeScreen,PdfActivity::class.java)
            adminHomeScreen.startActivity(intent)
        }

        binding.punjabGovernment.setOnClickListener {
            var intent = Intent(adminHomeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://punjab.gov.in/")
            adminHomeScreen.startActivity(intent)
        }
        binding.vigilanceBureau.setOnClickListener {
            var intent = Intent(adminHomeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://vigilancebureau.punjab.gov.in/")
            adminHomeScreen.startActivity(intent)
        }
        binding.punjabpolice.setOnClickListener {
            var intent = Intent(adminHomeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://www.punjabpolice.gov.in/")
            adminHomeScreen.startActivity(intent)
        }

//        binding.punjabRighTo.setOnClickListener {
//            var intent = Intent(adminHomeScreen, LinksActivity::class.java)
//            intent.putExtra("url", "https://cybercrime.punjabpolice.gov.in/")
//            adminHomeScreen.startActivity(intent)
//        }


        binding.punjabArmedPolice.setOnClickListener {
            var intent = Intent(adminHomeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://armedpolice.punjab.gov.in//")
            adminHomeScreen.startActivity(intent)
        }
        binding.punjabPoliceAcademy.setOnClickListener {
            var intent = Intent(adminHomeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://www.highcourtchd.gov.in/index.php/")
            adminHomeScreen.startActivity(intent)
        }


        return binding.root
    }


}