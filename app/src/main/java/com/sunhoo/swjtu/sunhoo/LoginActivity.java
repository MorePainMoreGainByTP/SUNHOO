package com.sunhoo.swjtu.sunhoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mob.MobSDK;
import com.sunhoo.swjtu.sunhoo.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tangpeng on 2017/7/18.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final boolean DEBUG = false;
    public static final String BASE_URL = "http://192.168.253.1:8080/SSM-SHFGuiding";
    private static final String URL = BASE_URL + "/ALogin";

    User user;

    private RequestQueue requestQueue;
    TextInputLayout phone, password;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://开启对话框
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("登陆中...");
                        progressDialog.create();
                    }
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                    break;
                case 2://登录成功
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    doLogin();
                    break;
                case 3://连接服务器失败
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
                case 4://密码错误
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "用户不存在或密码错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getViews();
        //创建 网络请求队列
        requestQueue = Volley.newRequestQueue(this);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG, "uncaughtException: ", e);
            }
        });
        sharedPreferences = getSharedPreferences("sunhoo", MODE_PRIVATE);
        if (!sharedPreferences.getString("phone", "").equals("")) {
            phone.getEditText().setText(sharedPreferences.getString("phone", ""));
            password.getEditText().setText(sharedPreferences.getString("password", ""));
        }
        MobSDK.init(this, "1f807e0a73287", "6443233fc05472d0a92f567e21b0f989");
    }

    private void getViews() {
        phone = (TextInputLayout) findViewById(R.id.phoneNumberWrapper);
        password = (TextInputLayout) findViewById(R.id.passwordWrapper);
    }

    public void onLogin(View v) {
        if (DEBUG) {
            doLogin();
            return;
        }
        String phoneStr = phone.getEditText().getText().toString().trim();
        String passwordStr = password.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(phoneStr) || phoneStr.length() != 11) {
            phone.setError("手机号有误");
        } else if (TextUtils.isEmpty(passwordStr)) {
            password.setError("输入密码");
        } else {
            checkUser(phoneStr, passwordStr);
        }
    }

    private void doLogin() {
        startActivity(new Intent(this, MainActivity.class).putExtra("user", user));
        finish();
    }

    private void checkUser(String phone, String pass) {
        user = new User();
        user.setPassword(pass);
        user.setPhone(phone);
        Gson gson = new Gson();
        try {
            handler.sendEmptyMessage(1);
            JSONObject jsonObject = new JSONObject(gson.toJson(user));
            Log.i(TAG, "jsonObject: " + jsonObject);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    int state = 0;
                    try {
                        state = jsonObject.getInt("state");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "返回的jsonObject: " + jsonObject);
                    switch (state) {
                        case 0://登录失败
                            handler.sendEmptyMessage(4);
                            break;
                        case 1:
                            Gson gson1 = new Gson();
                            try {
                                user = gson1.fromJson(jsonObject.getJSONObject("data").toString(), User.class);
                                handler.sendEmptyMessage(2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(3);
                            }
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
            handler.sendEmptyMessage(3);
        }
    }

    public void goRegister(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void goForgetPassword(View v) {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }
}
