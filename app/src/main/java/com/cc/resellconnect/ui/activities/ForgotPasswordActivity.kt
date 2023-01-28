package com.cc.resellconnect.ui.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cc.resellconnect.R
import com.cc.resellconnect.databinding.ActivityForgotPasswordBinding
import com.cc.resellconnect.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import gun0912.tedimagepicker.util.ToastUtil


class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgotPasswordBinding


    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)



        binding.btnSubmit.setOnClickListener {
            val view: View? = this.currentFocus
            if (checkValidation()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(binding.etEmail.text.toString())
                    .addOnSuccessListener {

                        // on below line getting current view.


                        // on below line checking if view is not null.
                        if (view != null) {
                            // on below line we are creating a variable
                            // for input manager and initializing it.
                            val inputMethodManager =
                                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                            // on below line hiding our keyboard.
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)

                            // displaying toast message on below line.
                                                    Snackbar.make(
                           binding.btnSubmit,
                            getString(R.string.reset),
                            Snackbar.LENGTH_LONG
                        ).show();
                        }


                        binding.etEmail.setText("")

                    }
            }
        }

        binding.icClose.setOnClickListener {
            finish()

        }
    }


    fun checkValidation(): Boolean {
        var result: Boolean = true
        if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmail.error = "Email Required!"
            result = false
        } else {
            if (!Utils.isEmailValid(binding.etEmail.text.toString())) {
                binding.etEmail.error = "Enter Valid Email!"
                result = false
            }
        }

        return result
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(  R.anim.animate_slides_down, 0)
    }
}