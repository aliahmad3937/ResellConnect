package com.cc.resellconnect.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.cc.resellconnect.R
import com.cc.resellconnect.adapters.PagerAdapter
import com.cc.resellconnect.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener


class ResellConnect : Fragment() {
    lateinit var tabLayout: TabLayout
   lateinit var viewPager: ViewPager
    private lateinit var v: View
    private lateinit var adapter: ViewPagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_resell_connect, container, false)
        tabLayout = v.findViewById<TabLayout>(R.id.tabLayout)
        viewPager = v.findViewById<ViewPager>(R.id.viewPager)

        adapter = ViewPagerAdapter(childFragmentManager);
        viewPager!!.adapter = adapter;

        // It is used to join TabLayout with ViewPager.
        tabLayout!!.setupWithViewPager(viewPager!!);




        return v
    }




}