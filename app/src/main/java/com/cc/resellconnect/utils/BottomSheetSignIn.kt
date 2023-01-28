package com.cc.resellconnect.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat.startActivity
import com.cc.resellconnect.R
import com.cc.resellconnect.ui.activities.SubscriptionActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth


class BottomSheetSignIn {

     fun show(
        context: Context,
        @LayoutRes layout: Int,
        fullScreen: Boolean = true,
        expand: Boolean = true,
        callBack:(email:String,pass:String) -> Unit,

    ) {
        val dialog = BottomSheetDialog(context)
        dialog.setOnShowListener {
            val bottomSheet: FrameLayout =
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
                    ?: return@setOnShowListener
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            if (fullScreen && bottomSheet.layoutParams != null) {
                showFullScreenBottomSheet(bottomSheet)
            }

            if (!expand) return@setOnShowListener

            bottomSheet.setBackgroundResource(android.R.color.transparent)
            expandBottomSheet(bottomSheetBehavior)
        }

        @SuppressLint("InflateParams") // dialog does not need a root view here
        val inflater  = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val sheetView = inflater.inflate(layout, null)
         val email = sheetView.findViewById<TextInputLayout>(R.id.email)
         val pass = sheetView.findViewById<TextInputLayout>(R.id.password)
        sheetView.findViewById<ImageView>(R.id.imageView2).setOnClickListener {
            dialog.dismiss()
        }
         sheetView.findViewById<MaterialButton>(R.id.login).setOnClickListener {
             if (checkValidation(email,pass)) {
                 dialog.dismiss()
                 callBack(email.editText!!.text.toString() , pass.editText!!.text.toString())
             }
         }

         sheetView.findViewById<TextView>(R.id.textView3).setOnClickListener {
             BottomSheetResetPassword().show(context,R.layout.bottom_sheet_reset_password)
         }

        dialog.setContentView(sheetView)

        dialog.show()

    }

    fun checkValidation(email: TextInputLayout, pass: TextInputLayout): Boolean {
        var result: Boolean = true

        if (email.editText!!.text.toString().isEmpty()) {
           email.error = "Email Required!"
            result = false
        }else{
            if(!Utils.isEmailValid(email.editText!!.text.toString())){
                email.error = "Enter Valid Email!"
                result = false
            }
        }

        if (pass.editText!!.text.toString().isEmpty()) {
            pass.error = "Password Required!"
            result = false
        }


        return result
    }

    private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = Resources.getSystem().displayMetrics.heightPixels - 50
        bottomSheet.layoutParams = layoutParams
    }

    private fun expandBottomSheet(bottomSheetBehavior: BottomSheetBehavior<FrameLayout>) {
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }



}