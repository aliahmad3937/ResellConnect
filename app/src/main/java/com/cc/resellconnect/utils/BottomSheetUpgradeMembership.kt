package com.cc.resellconnect.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.cc.resellconnect.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth


class BottomSheetUpgradeMembership {

     fun show(
        context: Context,
        @LayoutRes layout: Int,
        fullScreen: Boolean = true,
        expand: Boolean = true,
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


        sheetView.findViewById<ImageView>(R.id.imageView2).setOnClickListener {
            dialog.dismiss()
        }

         sheetView.findViewById<MaterialButton>(R.id.btn_account).setOnClickListener {

             val browserIntent = Intent(
                 Intent.ACTION_VIEW,
                 Uri.parse("https://app.resellconnect.com/login")
             )
             context.startActivity(browserIntent)
         }



        dialog.setContentView(sheetView)

        dialog.show()

    }


    private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = Resources.getSystem().displayMetrics.heightPixels - 80
        bottomSheet.layoutParams = layoutParams
    }

    private fun expandBottomSheet(bottomSheetBehavior: BottomSheetBehavior<FrameLayout>) {
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }



}