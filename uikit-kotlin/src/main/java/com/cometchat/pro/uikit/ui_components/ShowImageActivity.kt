package com.cometchat.pro.uikit.ui_components

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cometchat.pro.models.MediaMessage
import com.cometchat.pro.uikit.OnSwipeTouchListener
import com.cometchat.pro.uikit.R

class ShowImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)
          setWhiteStatusBarColor()
        Glide.with(this).asBitmap().load((CommonData.baseMessage as MediaMessage).attachment.fileUrl).into(object : SimpleTarget<Bitmap?>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                findViewById<ImageView>(R.id.display_img).setImageBitmap(resource)
            }
        })

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.display_img).setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                super.onSwipeLeft();

            }

            override fun onSwipeRight() {
              finish()
                super.onSwipeRight();
            }
        });
    }

    private fun setWhiteStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }
    }
}