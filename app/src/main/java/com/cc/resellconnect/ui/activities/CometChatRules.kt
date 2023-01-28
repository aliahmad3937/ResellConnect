package com.cc.resellconnect.ui.activities

import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cc.resellconnect.R
import com.cc.resellconnect.databinding.ActivityCometChatRulesBinding
import com.cc.resellconnect.utils.RetrivePDFfromUrl
import com.cc.resellconnect.utils.Utils
import com.google.firebase.firestore.FirebaseFirestore
import com.kaopiz.kprogresshud.KProgressHUD
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class CometChatRules : AppCompatActivity() {
    lateinit var binding: ActivityCometChatRulesBinding
    var inputStream: InputStream? = null
    private var progressHUD: KProgressHUD? = null

    lateinit var retrivePDFfromUrl: RetrivePDFfromUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWhiteStatusBarColor()
        progressHUD = Utils.getProgressDialog(this, getString(R.string.loading))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comet_chat_rules)

           val title = intent.getStringExtra("title")
           val url = intent.getStringExtra("url")

        binding.tvTitle.text = title

     //   binding.pdfView.fromAsset("chat_rule.pdf").load()

        if(url == "chat_rule.pdf" || url == "brginner_guide.pdf") {
            binding.pdfView.fromAsset(url).load()
        }
        else {
                    retrivePDFfromUrl = RetrivePDFfromUrl(
                            inputStream,
                            binding.pdfView,
                            url.toString(),
                        progressHUD!!
                        )
                        retrivePDFfromUrl.execute()

        }
        binding.icClose.setOnClickListener {
            finish()
        }
    }

    private fun setWhiteStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(  R.anim.animate_slides_down, 0)
    }

}

