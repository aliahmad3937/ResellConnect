package com.cc.resellconnect.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.cc.resellconnect.R
import com.cc.resellconnect.databinding.FragmentChannelsBinding
import com.cc.resellconnect.models.CometChatUser
import com.cc.resellconnect.ui.activities.MainActivity
import com.cc.resellconnect.utils.Constants
import com.cc.resellconnect.utils.MyApplication
import com.cc.resellconnect.utils.Utils
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.cometchat.pro.uikit.ui_resources.utils.ErrorMessagesUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.kaopiz.kprogresshud.KProgressHUD


class ChannelsFragment : Fragment() {
    private lateinit var binding: FragmentChannelsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val TAG = "ChannelsFragment"
    private var progressHUD: KProgressHUD? = null
    private lateinit var mContext:MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChannelsBinding.inflate(inflater, container, false)

        if (MyApplication.isGuest) {
            binding.layoutNoUser.visibility = View.GONE
            binding.lock.visibility = View.VISIBLE
        } else {
            binding.layoutNoUser.visibility = View.VISIBLE
            binding.lock.visibility = View.GONE
            firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        Log.v(TAG, "uid :${auth.uid.toString()}")

        progressHUD = Utils.getProgressDialog(mContext, "Creating...")

        if (mContext.tier != null && mContext.tier == "free") {
            binding.lock.visibility = View.VISIBLE
            binding.layoutNoUser.visibility = View.GONE
        } else {
            binding.lock.visibility = View.GONE
            binding.layoutNoUser.visibility = View.VISIBLE
        }



        binding.btnEnterChat.setOnClickListener {
            openBottomsheet()
        }


        binding.btnChatRules.setOnClickListener {
            mContext.goToCometChatRules()
        }


        binding.btnBeginnerGuide.setOnClickListener {
            mContext.goToCometChatRules2()
        }
    }

        return binding.root
    }

    private fun openBottomsheet() {
        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(requireContext() , R.style.DialogStyle)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.bottom_sheet_channels, null)

        // on below line we are creating a variable for our button
        // which we are using to dismiss our dialog.
        val btnSave = view.findViewById<MaterialButton>(R.id.btn_save)
        val uname = view.findViewById<EditText>(R.id.et_username)

        // on below line we are adding on click listener
        // for our dismissing the dialog button.
        btnSave.setOnClickListener {
            Log.v(TAG, "btn click")
            // on below line we are calling a dismiss
            // method to close our dialog.
            if (uname.text.toString().isNotEmpty()) {
                Log.v(TAG, "not empty")
                val user = User()
                user.uid = "user${System.currentTimeMillis()}"
                user.name = uname.text.toString()
                progressHUD!!.show()
                dialog.dismiss()
                CometChat.createUser(
                    user,
                    Constants.AUTH_KEY,
                    object : CometChat.CallbackListener<User>() {
                        override fun onSuccess(user: User) {
                            Log.v(TAG, "success user")
                            createUserInFirestore(user)

                            login(user.uid)
                        }
                        override fun onError(e: CometChatException) {
                            progressHUD!!.dismiss()
                            Log.v(TAG, "error :${e.code}")
                        }
                    })
            }else{
                Log.v(TAG, "empty")
            }


        }
        // below line is use to set cancelable to avoid
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)

        // on below line we are setting
        // content view to our view.
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // on below line we are calling
        // a show method to display a dialog.
        dialog.show()
    }

    private fun createUserInFirestore(user: User) {
        val user = CometChatUser(
            user.name,
            user.uid,
            auth.uid.toString()
        )

        firestore
            .collection("Chat Usernames")
            .document(auth.uid.toString())
            .set(user)
            .addOnSuccessListener {
               // Utils.showToast(requireContext(),"user created successfully!")
            }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity

    }


    private fun login(uid: String) {
        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(uid, Constants.AUTH_KEY, object : CometChat.CallbackListener<User?>() {
                override fun onSuccess(user: User?) {
                    progressHUD!!.dismiss()
                    findNavController().navigate(R.id.action_channelsFragment_to_cometChatGroupList)
                    Log.e("TAG7", "Comet Chat login Success: ")
//                startActivity(Intent(this@LoginCometChatUser, CometChatUI::class.java))
//                finish()
                }

                override fun onError(e: CometChatException) {
                    progressHUD!!.dismiss()
                    // inputLayout!!.isEndIconVisible = true
                    Log.e("TAG9", "onError login: " + e.code)
//                findViewById<View>(R.id.loginProgress).visibility = View.GONE
//                ErrorMessagesUtils.cometChatErrorMessage(this@LoginCometChatUser, e.code)
                }
            })
        }
    }


}