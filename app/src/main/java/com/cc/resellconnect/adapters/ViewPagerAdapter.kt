package com.cc.resellconnect.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.cc.resellconnect.ui.fragments.Cards
import com.cc.resellconnect.ui.fragments.ResellOpportunities
import com.cc.resellconnect.ui.fragments.Sneakers

class ViewPagerAdapter(
     fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) fragment = Sneakers() else if (position == 1) fragment =
            ResellOpportunities() else if (position == 2) fragment = Cards()

        return fragment!!
    }


    override fun getCount():Int = 3

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) title = "Sneakers" else if (position == 1) title =
            "Resell\nopportunities" else if (position == 2) title = "Cards"
        return title
    }
}