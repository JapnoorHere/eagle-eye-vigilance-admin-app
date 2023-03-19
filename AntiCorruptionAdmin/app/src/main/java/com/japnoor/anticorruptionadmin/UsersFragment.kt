package com.japnoor.anticorruptionadmin

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.protobuf.Value
import com.japnoor.anticorruptionadmin.databinding.FragmentUsersBinding
import com.japnoor.anticorruptionadmin.databinding.PasscodeDialogBinding
import com.japnoor.anticorruptionadmin.databinding.ShowUserDeatailsBinding
import com.japnoor.anticorruptionadmin.demand.DemandLetter
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UsersFragment : Fragment(), UsersClick {
    private var param1: String? = null
    private var param2: String? = null
    var arrayList: ArrayList<Users> = ArrayList()
    lateinit var adapter: UserListAdapter
    lateinit var database: FirebaseDatabase
    lateinit var userref: DatabaseReference
    lateinit var compref: DatabaseReference
    lateinit var demref: DatabaseReference
    lateinit var sharedPreferencesDetails: SharedPreferences
    lateinit var editorDetails: SharedPreferences.Editor
    lateinit var adminHomeScreen: AdminHomeScreen
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var encryptionKey: String? =null
    var secretKeySpec: SecretKeySpec? =null

    private fun encrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun decrypt(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
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
        var forgot = ForogotPasscode()
        encryptionKey=forgot.key()
        secretKeySpec = SecretKeySpec(encryptionKey!!.toByteArray(), "AES")
        adminHomeScreen = activity as AdminHomeScreen
        sharedPreferencesDetails =
            adminHomeScreen.getSharedPreferences("Details", AppCompatActivity.MODE_PRIVATE)
        editorDetails = sharedPreferencesDetails.edit()
        var binding = FragmentUsersBinding.inflate(layoutInflater, container, false)
        database = FirebaseDatabase.getInstance()
        userref = database.reference.child("Users")
        compref = database.reference.child("Complaints")
        demref = database.reference.child("Demand Letter")
        setHasOptionsMenu(true)
        sharedPreferences = adminHomeScreen.getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
        binding.shimmer.startShimmer()
        binding.search.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.search.right - binding.search.compoundDrawables[2].bounds.width())) {
                    binding.search.text.clear()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
        userref.addValueEventListener(object : ValueEventListener, UsersClick {
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList.clear()
                for (eachUser in snapshot.children) {
                    val user = eachUser.getValue(Users::class.java)
                    if (user != null && user.userStatus == "0") {
                        arrayList.add(user)
                    }
                    arrayList.reverse()
                    adapter = UserListAdapter(adminHomeScreen, arrayList, this)
                    binding.recyclerView.layoutManager = LinearLayoutManager(adminHomeScreen)
                    binding.recyclerView.adapter = adapter
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.search.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            var filteredList = ArrayList<Users>()
                            for (item in arrayList) {
                                if (decrypt(item.name).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                    || decrypt(item.email).toLowerCase()
                                        .contains(s.toString().toLowerCase())
                                )
                                    filteredList.add(item)
                            }
                            adapter.FilteredList(filteredList)
                        }
                    })
                }
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onUsersClick(users: Users) {

                var dialog = Dialog(requireContext())
                var dialogBinding = ShowUserDeatailsBinding.inflate(layoutInflater)
                dialog.setContentView(dialogBinding.root)
                dialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                var complaintCount = 0
                var demandCount = 0
                compref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (eachComplaint in snapshot.children) {
                            var comp = eachComplaint.getValue(Complaints::class.java)
                            if (comp != null && users.userId.equals(comp.userId)) {
                                complaintCount += 1
                                println("Count0     -> " + complaintCount.toString())
                                dialogBinding.complaints.setText(complaintCount.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                demref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (eachdem in snapshot.children) {
                            var dem = eachdem.getValue(DemandLetter::class.java)
                            if (dem != null && users.userId.equals(dem.userId)) {
                                demandCount += 1
                                dialogBinding.demand.setText(demandCount.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
                when (users.profileValue) {
                    "1" -> dialogBinding.Profile.setImageResource(R.drawable.man1)
                    "2" -> dialogBinding.Profile.setImageResource(R.drawable.man2)
                    "3" -> dialogBinding.Profile.setImageResource(R.drawable.man3)
                    "4" -> dialogBinding.Profile.setImageResource(R.drawable.man4)
                    "5" -> dialogBinding.Profile.setImageResource(R.drawable.girl1)
                    "6" -> dialogBinding.Profile.setImageResource(R.drawable.girl2)
                    "7" -> dialogBinding.Profile.setImageResource(R.drawable.girl3)
                    "8" -> dialogBinding.Profile.setImageResource(R.drawable.girl4)
                }


                dialogBinding.cardComplaint.setOnClickListener {
                    val connectivityManager =
                        adminHomeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                    if (isConnected) {
                        dialog.dismiss()
                        var bundle = Bundle()
                        bundle.putString("uid", users.userId.toString())
                        adminHomeScreen.navController.navigate(R.id.userComplaintFragment, bundle)
                    } else {
                        Toast.makeText(
                            adminHomeScreen,
                            "Check your internet connection please",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                dialogBinding.cardDemand.setOnClickListener {
                    val connectivityManager =
                        adminHomeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                    if (isConnected) {
                        dialog.dismiss()
                        var bundle = Bundle()
                        bundle.putString("uid", users.userId.toString())
                        adminHomeScreen.navController.navigate(R.id.userDemandFragment, bundle)
                    } else {
                        Toast.makeText(
                            adminHomeScreen,
                            "Check your internet connection please",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
                dialogBinding.cardEmail.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf(decrypt(users.email)))
                    intent.type = "message/rfc822"
                    adminHomeScreen.startActivity(Intent.createChooser(intent, "Select email"))
                }

                dialogBinding.name.setText(decrypt(users.name))
                dialogBinding.email.setText(decrypt(users.email))
                dialogBinding.birthdate.setText(decrypt(users.birthdate))
                dialogBinding.date.setText(decrypt(users.userDate))
                println("Count-> " + complaintCount.toString())

                dialogBinding.block.setOnClickListener {
                    val connectivityManager =
                        adminHomeScreen.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                    if (isConnected) {
                        var bottomSheet = BottomSheetDialog(requireContext())
                        bottomSheet.setContentView(R.layout.dialog_delete_users)
                        bottomSheet.show()
                        var tvYes = bottomSheet.findViewById<TextView>(R.id.tvYes)
                        var tvNo = bottomSheet.findViewById<TextView>(R.id.tvNo)

                        tvNo?.setOnClickListener {
                            bottomSheet.dismiss()
                        }
                        tvYes?.setBackgroundResource(R.drawable.yes_btn_red)
                        tvYes?.setOnClickListener {
                            val currentTime = System.currentTimeMillis()
                            userref.child(users.userId).child("userStatus")
                                .setValue(currentTime.toString())
                            adminHomeScreen.navController.navigate(R.id.usersFragment)
                            bottomSheet.dismiss()
                            dialog.dismiss()
                        }
                    } else {
                        Toast.makeText(
                            adminHomeScreen,
                            "Check your internet connection please",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                dialog.show()
            }
        })

        return binding.root
    }

    override fun onUsersClick(users: Users) {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add("Blocked Users")
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

        }
        else if(item.title?.equals("Change Password")==true){
            var intent=Intent(adminHomeScreen,ForgotPassword::class.java)
            adminHomeScreen.startActivity(intent)
            adminHomeScreen.finish()
        }

        else if (item.title?.equals("Blocked Users") == true) {
            adminHomeScreen.navController.navigate(R.id.blockedUsersFragment)
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
                                }
                                else {
                                    dialogBinding.btnSignup.visibility = View.VISIBLE
                                    dialogBinding.progressbar.visibility = View.GONE
                                    Toast.makeText(adminHomeScreen, it.exception.toString(), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    else {
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