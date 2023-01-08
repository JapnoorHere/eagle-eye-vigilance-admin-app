package com.japnoor.anticorruptionadmin

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.japnoor.anticorruptionadmin.databinding.FragmentAdminDemandBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminDemandFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAdminDemandBinding
    lateinit var adminHomeScreen:AdminHomeScreen


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
        adminHomeScreen=activity as AdminHomeScreen
        binding=FragmentAdminDemandBinding.inflate(layoutInflater,container,false)

        binding.cardTotal.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminTotalDemand)

        }
        binding.cardAccepted.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminAcceptedDemand)

        }
        binding.cardResolved.setOnClickListener{
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminresolvedDemand)

        }
        binding.cardRejected.setOnClickListener {
            adminHomeScreen.navController.navigate(R.id.action_adminDemandFragment_to_adminRejectedDemand)

        }
        return binding.root
    }
}