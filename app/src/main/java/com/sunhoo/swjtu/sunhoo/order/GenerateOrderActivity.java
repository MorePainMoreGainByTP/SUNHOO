package com.sunhoo.swjtu.sunhoo.order;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
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
import com.sunhoo.swjtu.sunhoo.adapters.DialogCountAdapter;
import com.sunhoo.swjtu.sunhoo.adapters.ImgGalleryAdapter;
import com.sunhoo.swjtu.sunhoo.entities.Order;
import com.sunhoo.swjtu.sunhoo.entities.OrderItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;
import static com.sunhoo.swjtu.sunhoo.MainActivity.user;

/**
 * Created by tangpeng on 2017/7/21.
 */

public class GenerateOrderActivity extends AppCompatActivity {
    private static final String URL = BASE_URL + "/AAddOrder";
    private static final String TAG = "GenerateOrderActivity";

    List<OrderItem> orderItems;
    List<String> productInfo;
    List<String> urls;
    Intent intent;
    Order order;
    RecyclerView recyclerView, productInfoItems;
    TextInputLayout customerLayout, customerPhoneLayout, noteLayout;
    TextView productCount, allPrice;
    ProgressDialog progressDialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(GenerateOrderActivity.this, "下单失败,请重试！", Toast.LENGTH_SHORT).show();
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    break;
                case 1:
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    setResult(RESULT_OK);
                    Toast.makeText(GenerateOrderActivity.this, "恭喜您，下单成功！", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 2:
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    Toast.makeText(GenerateOrderActivity.this, "下单失败,请检查网络！", Toast.LENGTH_SHORT).show();
                    break;
                case 3://开启等待对话框
                    if (progressDialog == null) {
                        progressDialog = Utils.getProgressDialog(GenerateOrderActivity.this, null, "创建订单中...");
                    }
                    progressDialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_order);
        setToolBar();
        order = new Order();
        intent = getIntent();
        orderItems = (List<OrderItem>) intent.getSerializableExtra("list");
        productInfo = (List<String>) intent.getSerializableExtra("productInfo");
        urls = (List<String>) intent.getSerializableExtra("urls");
        getViews();
        setViews();
    }

    private void getViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        customerLayout = (TextInputLayout) findViewById(R.id.customerNameWrapper);
        customerPhoneLayout = (TextInputLayout) findViewById(R.id.customerPhoneWrapper);
        noteLayout = (TextInputLayout) findViewById(R.id.noteWrapper);
        productCount = (TextView) findViewById(R.id.product_count);
        allPrice = (TextView) findViewById(R.id.all_price);
        productInfoItems = (RecyclerView) findViewById(R.id.productInfoItems);
    }

    private void setViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new ImgGalleryAdapter(urls));
        initView();
    }

    private void initView() {
        double finalPrice = 0;
        int count = 0;
        for (OrderItem product : orderItems) {
            count += product.getNumber();
            finalPrice += product.getPrice() * product.getNumber();
        }
        DialogCountAdapter dialogCountAdapter = new DialogCountAdapter(orderItems, productInfo);
        productInfoItems.setLayoutManager(new LinearLayoutManager(this));
        productInfoItems.setAdapter(dialogCountAdapter);
        productCount.setText("共" + count + "件商品");
        allPrice.setText("¥" + String.format("%.2f", finalPrice));
        order.setAllCount(count);
        order.setAllPrice(finalPrice);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("生成订单");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onCountNow(View v) {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String customer = customerLayout.getEditText().getText().toString().trim();
        String customerPhone = customerPhoneLayout.getEditText().getText().toString().trim();
        String note = noteLayout.getEditText().getText().toString().trim();
        String address = user.getAddressProvince() + "-" + user.getAddressCity() + "-" + user.getAddressDetail();
        if (TextUtils.isEmpty(customer)) {
            customerLayout.setError("输入客户姓名");
        } else if (TextUtils.isEmpty(customerPhone)) {
            customerPhoneLayout.setError("输入客户手机号");
        } else if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "无店铺地址，请在个人信息修改！", Toast.LENGTH_SHORT).show();
        } else {
            order.setCustomer(customer);
            order.setCustomerPhone(customerPhone);
            order.setNote(note);
            order.setAddress(address);
            order.setType("待确认");
            order.setUserID(user.getId());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(3);
                    Gson gson = new Gson();

                    Map<Object, Object> info = new HashMap<Object, Object>();
                    info.put("order", order);
                    info.put("orderItems", orderItems);
                    try {
                        JSONObject jsonObject = new JSONObject(gson.toJson(info));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                int state = 0;
                                try {
                                    state = jsonObject.getInt("state");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    handler.sendEmptyMessage(0);
                                }
                                Log.i(TAG, "返回的jsonObject: " + jsonObject);
                                if (state == 1) {
                                    handler.sendEmptyMessage(1);
                                } else {
                                    handler.sendEmptyMessage(0);
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
            }).start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new AlertDialog.Builder(this).setMessage("便宜不等人，请三思而行~").setNegativeButton("我再想想", null).setPositiveButton("去意已决", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setMessage("便宜不等人，请三思而行~").setNegativeButton("我再想想", null).setPositiveButton("去意已决", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).create().show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
