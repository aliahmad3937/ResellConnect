package com.cc.resellconnect.ui.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cc.resellconnect.R
import com.cc.resellconnect.databinding.ActivityLogInBinding
import com.cc.resellconnect.databinding.FragmentUserLoginBinding
import com.cc.resellconnect.utils.MyApplication
import com.cc.resellconnect.utils.OnSwipeTouchListener
import com.cc.resellconnect.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.kaopiz.kprogresshud.KProgressHUD

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var auth: FirebaseAuth
    private var progressHUD: KProgressHUD? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWhiteStatusBarColor()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)
        auth = FirebaseAuth.getInstance()
        progressHUD = Utils.getProgressDialog(this, getString(R.string.loading))



        binding.login.setOnClickListener{
            if (checkValidation()) {
                progressHUD?.show()
                auth.signInWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            progressHUD?.dismiss()
                            startActivity(Intent(this@LogInActivity,SubscriptionActivity::class.java))
                            finish()
                        }else{
                            Utils.showToast(this@LogInActivity, "Fail")
                            progressHUD?.dismiss()
                        }
                    }
            }

        }

        binding.guess.setOnClickListener{
                progressHUD?.show()
                auth.signInAnonymously()
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            MyApplication.isGuest = true
                            progressHUD?.dismiss()
                            startActivity(Intent(this@LogInActivity,MainActivity::class.java))
                            finish()
                        }else{
                            Utils.showToast(this@LogInActivity, "Fail")
                            progressHUD?.dismiss()
                        }
                    }
        }

        binding.tvSignup.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://resellconnect.com/"))
            startActivity(browserIntent)
        }

        binding.textView3.setOnClickListener {
            startActivity(Intent(this@LogInActivity, ForgotPasswordActivity::class.java))
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up)
        }

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
            if(auth.currentUser!!.isAnonymous){
                Log.v("AnonymousUserID","id :${auth.currentUser!!.uid}")
                MyApplication.isGuest = true
                startActivity(Intent(this@LogInActivity,MainActivity::class.java))
            }else {
                startActivity(Intent(this@LogInActivity, SubscriptionActivity::class.java))
                finish()
            }
        }
    }

    private fun setWhiteStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }
    }
}