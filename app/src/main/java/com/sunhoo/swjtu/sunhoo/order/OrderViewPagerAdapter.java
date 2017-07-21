package com.sunhoo.swjtu.sunhoo.order;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/7/21.
 */

public class OrderViewPagerAdapter extends FragmentPagerAdapter {

    private List<OrderFragment> orderFragments;
    private ArrayList<String> tabTitles;

    public OrderViewPagerAdapter(FragmentManager fm, List<OrderFragment> orderFragments, ArrayList<String> tabTitles) {
        super(fm);
        this.orderFragments = orderFragments;
        this.tabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return orderFragments.get(position);
    }

    @Override
    public int getCount() {
        return orderFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }
}
