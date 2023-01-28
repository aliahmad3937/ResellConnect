package com.cc.resellconnect.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cc.resellconnect.R
import com.cc.resellconnect.adapters.OnBoardingSliderAdapter
import com.cc.resellconnect.utils.SavedPreference
import com.google.firebase.auth.FirebaseAuth
import com.smarteist.autoimageslider.SliderView
import kotlin.collections.ArrayList

class OnBoradingActivity : AppCompatActivity() {
    lateinit var slider:SliderView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_borading)
        slider = findViewById(R.id.onBoardingslider)

        setSlider(
            slider,
            arrayListOf(
             R.drawable.image1,
             R.drawable.image2,
             R.drawable.image3,
             R.drawable.image4,
             R.drawable.image5,
             R.drawable.image6
            )
        )


    }


    fun setSlider(sliderView: SliderView, list: ArrayList<Int>){



        // passing this array list inside our adapter class.

        // passing this array list inside our adapter class.
        val adapter = OnBoardingSliderAdapter(this, list ){
            SavedPreference.setOnBoardSeen(this,true)
            startActivity(Intent(this,LogInActivity::class.java))
            finish()
        }



        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        // below method is used to
        // setadapter to sliderview.

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter)

        // below method is use to set
        // scroll time in seconds.

        // below method is use to set
        // scroll time in seconds.
        sliderView.scrollTimeInSec = 2

        // to set it scrollable automatically
        // we use below method.

        // to set it scrollable automatically
        // we use below method.
        sliderView.isAutoCycle = false

        // to start autocycle below method is used.

        // to start autocycle below method is used.
        //   sliderView.startAutoCycle()
    }

}