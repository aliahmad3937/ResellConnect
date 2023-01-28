package com.cc.resellconnect.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kaopiz.kprogresshud.KProgressHUD
import java.util.regex.Pattern

object Utils {
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun getProgressDialog(
        context: Context?,
        message: String?
    ): KProgressHUD {
        return KProgressHUD.create(context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setLabel(message)
    }

    @JvmStatic
    fun showToast(context: Context, message:String){
        Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show()
    }


}

fun Fragment.showBottomSheetDialog(
    @LayoutRes layout: Int,
    @IdRes recyViewToSet: Int? = null,
    textToSet: String? = null,
    fullScreen: Boolean = true,
    expand: Boolean = true
) {
    val dialog = BottomSheetDialog(context!!)
    dialog.setOnShowListener {
        val bottomSheet: FrameLayout = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet) ?: return@setOnShowListener
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        if (fullScreen && bottomSheet.layoutParams != null) { showFullScreenBottomSheet(bottomSheet) }

        if (!expand) return@setOnShowListener

        bottomSheet.setBackgroundResource(android.R.color.transparent)
        expandBottomSheet(bottomSheetBehavior)
    }



    @SuppressLint("InflateParams") // dialog does not need a root view here
    val sheetView = layoutInflater.inflate(layout, null)
    recyViewToSet?.also {
        sheetView.findViewById<TextView>(it).text = textToSet
    }
//
//    sheetView.findViewById<ImageView>(R.id.closeButton)?.setOnClickListener {
//        dialog.dismiss()
//    }

    dialog.setContentView(sheetView)
    dialog.show()
}

private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
    val layoutParams = bottomSheet.layoutParams
    layoutParams.height = Resources.getSystem().displayMetrics.heightPixels - 100
    bottomSheet.layoutParams = layoutParams
}

private fun expandBottomSheet(bottomSheetBehavior: BottomSheetBehavior<FrameLayout>) {
    bottomSheetBehavior.skipCollapsed = true
    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
}