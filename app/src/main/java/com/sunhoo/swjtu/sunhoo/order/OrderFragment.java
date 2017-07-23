package com.sunhoo.swjtu.sunhoo.order;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.adapters.OrderAdapter;
import com.sunhoo.swjtu.sunhoo.entities.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;
import static com.sunhoo.swjtu.sunhoo.MainActivity.user;

/**
 * Created by tangpeng on 2017/7/21.
 */

public class OrderFragment extends Fragment {
    private static final String TAG = "OrderFragment";

    private static final String URL = BASE_URL + "/AGetOrder";
    public static final String KEY_TYPE = "key_type";

    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    String type;
    List<Order> orderList;
    Context context;
    RequestQueue requestQueue;
    ImageView look4products;
    TextView loadFail, noWifi;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(context, "获取数据失败！", Toast.LENGTH_SHORT).show();
                    setVisibility(0);
                    break;
                case 1://无数据
                    setVisibility(1);
                    break;
                case 2:
                    Toast.makeText(context, "连接服务器失败，请检查网络！", Toast.LENGTH_SHORT).show();
                    setVisibility(2);
                    break;
                case 3:
                    orderAdapter = new OrderAdapter(orderList, type,getActivity());
                    recyclerView.setAdapter(orderAdapter);
                    break;
            }
        }
    };

    private void setVisibility(int type) {
        loadFail.setVisibility(View.INVISIBLE);
        look4products.setVisibility(View.INVISIBLE);
        noWifi.setVisibility(View.INVISIBLE);
        switch (type) {
            case 0:
                loadFail.setVisibility(View.VISIBLE);
                break;
            case 1:
                look4products.setVisibility(View.VISIBLE);
                break;
            case 2:
                noWifi.setVisibility(View.VISIBLE);
                break;
        }
    }

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
        requestQueue = Volley.newRequestQueue(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromServer();
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
        orderList = new ArrayList<>();
        look4products = (ImageView) view.findViewById(R.id.look_4_products);
        loadFail = (TextView) view.findViewById(R.id.load_fail);
        noWifi = (TextView) view.findViewById(R.id.no_wifi);
        return view;
    }

    private void getDataFromServer() {
        try {
            String jsonStr = "{\"id\":" + user.getId() + ",\"type\":" + type + "}";
            JSONObject jsonObject = new JSONObject(jsonStr);
            Log.i(TAG, "jsonObject: " + jsonObject);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    int count = 0;
                    try {
                        count = jsonObject.getInt("count");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(0);
                    }
                    Log.i(TAG, "返回的jsonObject: " + jsonObject);
                    if (count > 0) {
                        try {
                            Gson gson = new Gson();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                orderList.add(gson.fromJson(jsonArray.getString(i), Order.class));
                            }
                            handler.sendEmptyMessage(3);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(0);
                        }
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    handler.sendEmptyMessage(2);
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(0);
        }
    }

    public OrderAdapter getOrderAdapter() {
        return orderAdapter;
    }
}
