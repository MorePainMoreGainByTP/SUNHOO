package com.sunhoo.swjtu.sunhoo.order;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.sunhoo.swjtu.sunhoo.Utils;
import com.sunhoo.swjtu.sunhoo.adapters.DetailOrderAdapter;
import com.sunhoo.swjtu.sunhoo.entities.DetailOrder;
import com.sunhoo.swjtu.sunhoo.entities.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/7/22.
 */

public class DetailOrderActivity extends AppCompatActivity {
    private static final String URL = BASE_URL + "/AGetOrderItem";
    private static final String URL2 = BASE_URL + "/AUpdateOrder";
    private static final String TAG = "DetailOrderActivity";

    TextView customerName, customerPhone, address, orderId, orderTime, orderPay, noWifi, loadFail;
    LinearLayout allInfo;
    RecyclerView recyclerView;
    Order order;
    List<DetailOrder> detailOrderList;
    DetailOrderAdapter detailOrderAdapter;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog2;
    RequestQueue requestQueue;
    Intent intent;
    int position;
    String type;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://开启对话框
                    if (progressDialog == null) {
                        progressDialog = Utils.getProgressDialog(DetailOrderActivity.this, null, "载入中...");
                    }
                    progressDialog.show();
                    break;
                case 1://获取数据成功
                    setVisibility(2);
                    detailOrderAdapter.notifyDataSetChanged();
                    dismissDialog();
                    break;
                case 2://获取数据失败
                    setVisibility(0);
                    Toast.makeText(DetailOrderActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    break;
                case 3:
                    setVisibility(1);
                    Toast.makeText(DetailOrderActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    break;
                case 4://开启对话框
                    if (progressDialog2 == null) {
                        progressDialog2 = Utils.getProgressDialog(DetailOrderActivity.this, null, "操作中...");
                    }
                    progressDialog2.show();
                    break;
                case 5:
                    Toast.makeText(DetailOrderActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                    dismissDialog2();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case 6:
                    Toast.makeText(DetailOrderActivity.this, "取消失败", Toast.LENGTH_SHORT).show();
                    dismissDialog2();
                    break;
                case 7:
                    Toast.makeText(DetailOrderActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    dismissDialog2();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case 8:
                    Toast.makeText(DetailOrderActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    dismissDialog2();
                    break;
            }
        }
    };

    private void setVisibility(int type) {
        allInfo.setVisibility(View.INVISIBLE);
        loadFail.setVisibility(View.INVISIBLE);
        noWifi.setVisibility(View.INVISIBLE);
        switch (type) {
            case 0:
                loadFail.setVisibility(View.VISIBLE);
                break;
            case 1:
                noWifi.setVisibility(View.VISIBLE);
                break;
            case 2:
                allInfo.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void dismissDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void dismissDialog2() {
        if (progressDialog2 != null)
            progressDialog2.dismiss();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setToolBar();
        intent = getIntent();
        order = (Order) intent.getSerializableExtra("order");
        position = intent.getIntExtra("position", 0);
        type = intent.getStringExtra("type");
        requestQueue = Volley.newRequestQueue(this);
        getViews();
        setViews();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("订单详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getViews() {
        customerName = (TextView) findViewById(R.id.customer);
        customerPhone = (TextView) findViewById(R.id.customer_phone);
        address = (TextView) findViewById(R.id.address);
        orderId = (TextView) findViewById(R.id.order_id);
        orderTime = (TextView) findViewById(R.id.order_time);
        orderPay = (TextView) findViewById(R.id.order_pay);
        noWifi = (TextView) findViewById(R.id.no_wifi);
        loadFail = (TextView) findViewById(R.id.load_fail);
        allInfo = (LinearLayout) findViewById(R.id.all_info);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }


    private void setViews() {
        customerName.setText(order.getCustomer());
        customerPhone.setText(order.getCustomerPhone());
        address.setText(order.getAddress());
        orderId.setText("" + order.getId());
        orderTime.setText(order.getFormatDate());
        orderPay.setText(order.getFormatPrice());
        orderPay.setText(order.getFormatPrice());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        detailOrderList = new ArrayList<>();
        detailOrderAdapter = new DetailOrderAdapter(detailOrderList);
        recyclerView.setAdapter(detailOrderAdapter);

        getDataFromServer();
    }

    private void getDataFromServer() {
        handler.sendEmptyMessage(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonStr = "{\"id\":" + order.getId() + "}";
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    Log.i(TAG, "jsonObject: " + jsonObject);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            int count = 0;
                            try {
                                count = jsonObject.getInt("count");
                                if (count <= 0) {
                                    handler.sendEmptyMessage(2);
                                } else {
                                    Gson gson = new Gson();
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        detailOrderList.add(gson.fromJson(jsonArray.getString(i), DetailOrder.class));
                                    }
                                    handler.sendEmptyMessage(1);
                                }
                                Log.i(TAG, "返回的jsonObject: " + jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(2);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            handler.sendEmptyMessage(3);
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(3);
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_order, menu);
        MenuItem delete = menu.getItem(1);
        MenuItem cancel = menu.getItem(0);
        if (order.getType().equals("待确认")) {
            delete.setVisible(false);
            cancel.setVisible(true);
        } else if (order.getType().equals("进行中")) {
            delete.setVisible(false);
            cancel.setVisible(false);
        } else {
            delete.setVisible(true);
            cancel.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.cancel:
                if (allInfo.getVisibility() == View.INVISIBLE)
                    break;
                new AlertDialog.Builder(this).setMessage("是否取消此订单？").setNegativeButton("否", null).setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelOrder();
                    }
                }).create().show();
                break;
            case R.id.delete:
                if (allInfo.getVisibility() == View.INVISIBLE)
                    break;
                new AlertDialog.Builder(DetailOrderActivity.this).setMessage("是否删除该订单？").setNegativeButton("否", null).setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteOrder();
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelOrder() {
        doSomething("已取消", 5, 6);
    }

    private void deleteOrder() {
        doSomething("已删除", 7, 8);
    }

    private void doSomething(final String newType, final int signal1, final int signal2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(4);
                Gson gson = new Gson();
                try {
                    Map<Object, Object> map = new HashMap<>();
                    map.put("order", order);
                    map.put("newType", newType);
                    JSONObject jsonObject = new JSONObject(gson.toJson(map));
                    Log.i(TAG, "jsonObject: " + jsonObject);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL2, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            int state = 0;
                            try {
                                state = jsonObject.getInt("state");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (state == 0) {
                                handler.sendEmptyMessage(signal2);  //失败
                            } else if (state == 1) {
                                handler.sendEmptyMessage(signal1);  //成功
                            }
                            Log.i(TAG, "返回的jsonObject: " + jsonObject);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            handler.sendEmptyMessage(3);
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(3);
                }
            }
        }).start();
    }
}
