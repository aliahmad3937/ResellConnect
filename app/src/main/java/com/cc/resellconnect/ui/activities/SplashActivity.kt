package com.cc.resellconnect.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.cc.resellconnect.R
import com.cc.resellconnect.utils.SavedPreference

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val notifLay = findViewById<LinearLayout>(R.id.lay_notif)
        val btnAllow = notifLay.findViewById<TextView>(R.id.tv_allow)
        val btnNotAllow = notifLay.findViewById<TextView>(R.id.tv_not_allow)

        btnAllow.setOnClickListener {
            notifLay.visibility = View.GONE
            setHandler()
        }

        btnNotAllow.setOnClickListener {
            notifLay.visibility = View.GONE
            setHandler()
        }

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (SavedPreference.getOnBoardSeen(this)) {
                notifLay.visibility = View.GONE
                setHandler()
            } else {
                notifLay.visibility = View.VISIBLE
            }
        }, 1000)
    }

    fun setHandler() {

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (SavedPreference.getOnBoardSeen(this)) {
                startActivity(Intent(this@SplashActivity, LogInActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, OnBoradingActivity::class.java))
                finish()
            }
        }, 2000)
    }

}