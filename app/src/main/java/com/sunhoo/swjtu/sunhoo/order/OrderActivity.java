package com.sunhoo.swjtu.sunhoo.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sunhoo.swjtu.sunhoo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/7/19.
 */

public class OrderActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<OrderFragment> orderFragments;
    private ArrayList<String> tabTitles;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setToolBar();
        getViews();
        setViews();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的订单");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getViews() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void setViews() {
        tabTitles = new ArrayList<>();
        tabTitles.add("待确认");
        tabTitles.add("进行中");
        tabTitles.add("已完成");
        tabTitles.add("已取消");

        orderFragments = new ArrayList<>();
        orderFragments.add(OrderFragment.newInstance(tabTitles.get(0)));  //待确认 0
        orderFragments.add(OrderFragment.newInstance(tabTitles.get(1)));  //进行中 1
        orderFragments.add(OrderFragment.newInstance(tabTitles.get(2)));  //已完成 2
        orderFragments.add(OrderFragment.newInstance(tabTitles.get(3)));  //已取消 2

        tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(3)));

        OrderViewPagerAdapter viewPagerAdapter = new OrderViewPagerAdapter(getSupportFragmentManager(), orderFragments, tabTitles);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);   //需在setAdapter之后，第二个参数设置是否 自动刷新fragment的布尔值
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
