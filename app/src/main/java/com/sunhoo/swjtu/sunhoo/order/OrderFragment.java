package com.sunhoo.swjtu.sunhoo.order;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.entities.Order;

import java.util.ArrayList;
import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/7/21.
 */

public class OrderFragment extends Fragment {
    private static final String URL = BASE_URL + "/AGetOrderItem";
    public static final String KEY_TYPE = "key_type";

    RecyclerView recyclerView;
    String type;
    List<Order> orderList;
    Context context;
    RequestQueue requestQueue;


    public static OrderFragment newInstance(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, type);
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        context = container.getContext();
        Bundle bundle = getArguments();
        type = bundle.getString(KEY_TYPE);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        requestQueue = Volley.newRequestQueue(context);
        orderList = new ArrayList<>();

        return view;
    }

    private void getDataFromServer() {
        switch (type) {
            case "待确认":
                break;
            case "进行中":
                break;
            case "已完成":
                break;
            case "已取消":
                break;
        }
    }
}
