package com.example.shopclient.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.shopclient.Tabs.loginTab;
import com.example.shopclient.Tabs.registerTab;

public class PageAdapter extends FragmentPagerAdapter {
    private int numoftabs;
    public PageAdapter(FragmentManager fm,int numoftabs){
        super(fm);
        this.numoftabs = numoftabs;
    }
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new loginTab();
            case 1:
                return new registerTab();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return numoftabs;
    }
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
