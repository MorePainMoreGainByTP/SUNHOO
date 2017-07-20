package com.sunhoo.swjtu.sunhoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sunhoo.swjtu.sunhoo.MainActivity.user;

/**
 * Created by tangpeng on 2017/7/20.
 */

public class UpdateUserActivity extends AppCompatActivity {
    private static final String TAG = "UpdateUserActivity";
    private static final String URL = "http://192.168.253.1:8080/SSM-SHFGuiding/AUpdate";

    String province, city, detail;
    String preProvince, preCity, preDetail, preUserName;
    TextInputLayout address, userName;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        setToolBar();
        getViews();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("个人信息管理");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void getViews() {
        address = (TextInputLayout) findViewById(R.id.addressWrapper);
        userName = (TextInputLayout) findViewById(R.id.userNameWrapper);
    }

    public void onChooseAddress(View v) {
        startActivityForResult(new Intent(this, AddressActivity.class), 2);
    }

    public void onSave(View v) {
        String userNameStr = userName.getEditText().getText().toString().trim();
        String addressStr = address.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(userNameStr)) {
            userName.setError("输入用户名");
        } else if (TextUtils.isEmpty(addressStr)) {
            address.setError("选择地址");
        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            preUserName = user.getUserName();
            preProvince = user.getAddressProvince();
            preCity = user.getAddressCity();
            preDetail = user.getAddressDetail();

            user.setUserName(userNameStr);
            user.setAddressProvince(province);
            user.setAddressCity(city);
            user.setAddressDetail(detail);
            Gson gson = new Gson();
            try {
                JSONObject jsonObject = new JSONObject(gson.toJson(user));
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        int state = 0;
                        try {
                            state = jsonObject.getInt("state");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(2);
                        }
                        Log.i(TAG, "返回的jsonObject: " + jsonObject);
                        switch (state) {
                            case 0:
                                handler.sendEmptyMessage(2);
                                break;
                            case 1:
                                handler.sendEmptyMessage(1);
                                break;
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
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    province = data.getStringExtra("province");
                    city = data.getStringExtra("city");
                    detail = data.getStringExtra("address");
                    address.getEditText().setText(province + "-" + city + "-" + detail);
                    break;
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (progressDialog == null)
                        progressDialog = Utils.getProgressDialog(UpdateUserActivity.this, null, "保存中...");
                    progressDialog.show();
                    break;
                case 1://成功
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(UpdateUserActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 2://失败
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    user.setUserName(preUserName);
                    user.setAddressProvince(preProvince);
                    user.setAddressCity(preCity);
                    user.setAddressDetail(preDetail);
                    Toast.makeText(UpdateUserActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
