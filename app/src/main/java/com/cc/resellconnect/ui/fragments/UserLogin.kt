package com.cc.resellconnect.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cc.resellconnect.R
import com.cc.resellconnect.databinding.FragmentUserLoginBinding
import com.cc.resellconnect.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.kaopiz.kprogresshud.KProgressHUD

class UserLogin : Fragment() {
    private lateinit var binding: FragmentUserLoginBinding
    private lateinit var auth: FirebaseAuth
    private var progressHUD: KProgressHUD? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater , R.layout.fragment_user_login, container, false)
        auth = FirebaseAuth.getInstance()
        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.loading))


        binding.login.setOnClickListener{
            if (checkValidation()) {
                progressHUD?.show()
                auth.signInWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                    .addOnCompleteListener{
                    if(it.isSuccessful){
                        progressHUD?.dismiss()
                        findNavController().navigate(R.id.action_userLogin_to_resellConnect)
                    }else{
                        Utils.showToast(requireContext(), "Fail")
                        progressHUD?.dismiss()
                    }
                }
            }

        }


        binding.textView3.setOnClickListener {

//            auth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(this, OnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG)
//                            .show()
//                    } else {
//                        Toast.makeText(this, "Unable to send reset mail", Toast.LENGTH_LONG)
//                            .show()
//                    }
//                })

        }




        return binding.root
    }

    fun checkValidation(): Boolean {
        var result: Boolean = true

        if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmail.error = "Email Required!"
            result = false
        }else{
            if(!Utils.isEmailValid(binding.etEmail.text.toString())){
                binding.etEmail.error = "Enter Valid Email!"
                result = false
            }
        }

        if (binding.etPassword.text.toString().isEmpty()) {
            binding.etPassword.error = "Password Required!"
            result = false
        }


        return result
    }

    override fun onResume() {
        super.onResume()

        if(auth.currentUser != null){
            findNavController().navigate(R.id.action_userLogin_to_resellConnect)
        }

    }

}