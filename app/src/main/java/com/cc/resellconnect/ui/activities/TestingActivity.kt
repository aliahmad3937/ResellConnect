package com.cc.resellconnect.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.cc.resellconnect.R
import com.cc.resellconnect.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class TestingActivity : AppCompatActivity() {

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    private lateinit var adapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager = findViewById<ViewPager>(R.id.viewPager)


        adapter = ViewPagerAdapter(supportFragmentManager);
        viewPager!!.adapter = adapter;

        // It is used to join TabLayout with ViewPager.
        tabLayout!!.setupWithViewPager(viewPager!!);

    }
}