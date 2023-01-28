package com.cc.resellconnect.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.cc.resellconnect.ui.fragments.Cards;
import com.cc.resellconnect.ui.fragments.ResellOpportunities;
import com.cc.resellconnect.ui.fragments.Sneakers;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new Sneakers();
            case 1:
                return new ResellOpportunities();
            case 2:
                return new Cards();

            default:
                return new Sneakers();
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

