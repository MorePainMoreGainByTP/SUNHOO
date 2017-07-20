package com.sunhoo.swjtu.sunhoo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sunhoo.swjtu.sunhoo.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by tangpeng on 2017/7/18.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static final String URL = "http://192.168.253.1:8080/SSM-SHFGuiding/ARegister";

    TextInputLayout userName, address, phone, pass, passAgain, checkCode;
    TextView checkCodeTxt;
    String userNameStr;
    String addressStr;
    String province, city, detail;
    String phoneStr;
    String passStr;
    String passAgainStr;
    String checkCodeStr;

    ProgressDialog progressDialog;
    private boolean boolShowInDialog = true;
    private EventHandler eventHandler;
    private RequestQueue requestQueue;

    int timer = 61;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                timer--;
                if (timer > 0) {
                    checkCodeTxt.setText(timer + "s");
                    checkCodeTxt.setTextColor(getResources().getColor(R.color.gray));
                    handler.sendEmptyMessageDelayed(1, 1000);
                } else {
                    checkCodeTxt.setClickable(true);
                    checkCodeTxt.setText("获取验证码");
                    checkCodeTxt.setTextColor(getResources().getColor(R.color.colorBlue));
                }
            }
            if (msg.what == 2) {//开启等待对话框
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(RegisterActivity.this);
                    progressDialog.setMessage("注册中...");
                    progressDialog.create();
                }
                if (!progressDialog.isShowing())
                    progressDialog.show();
            }
            if (msg.what == 4) {//注册失败
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            if (msg.what == 5) {//注册成功
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                finish();
            }
            if (msg.what == 6) {//账号已存在
                Toast.makeText(RegisterActivity.this, "账号已存在", Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            if (msg.what == 3) {//验证码错误
                checkCode.setError("验证失败");
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        handleSMSS();

        setToolBar();
        getViews();

        //创建 网络请求队列
        requestQueue = Volley.newRequestQueue(this);

        if (Build.VERSION.SDK_INT >= 23) {
            int readPhone = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            int receiveSms = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
            int readSms = checkSelfPermission(Manifest.permission.READ_SMS);
            int readContacts = checkSelfPermission(Manifest.permission.READ_CONTACTS);
            int readSdcard = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            int requestCode = 0;
            ArrayList<String> permissions = new ArrayList<String>();
            if (readPhone != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 0;
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (receiveSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 1;
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if (readSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 2;
                permissions.add(Manifest.permission.READ_SMS);
            }
            if (readContacts != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 3;
                permissions.add(Manifest.permission.READ_CONTACTS);
            }
            if (readSdcard != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 4;
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (requestCode > 0) {
                String[] permission = new String[permissions.size()];
                this.requestPermissions(permissions.toArray(permission), requestCode);
                return;
            }
        }
        handleSMSS();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        handleSMSS();
    }

    private void handleSMSS() {
        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        SMSSDK.setAskPermisionOnReadContact(boolShowInDialog);
        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(3);
                } else {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        //回调完成
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            //提交验证码成功
                            Log.i(TAG, "提交验证码成功: ");
                            checkCode.setError("");
                            checkCode.setErrorEnabled(false);
                            handler.sendEmptyMessage(2);
                            jsonObjectRequest();
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            //获取验证码成功
                            Log.i(TAG, "获取验证码成功: ");
                        } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                            //返回支持发送验证码的国家列表
                        } else {
                            handler.sendEmptyMessage(3);
                        }
                    } else {
                        handler.sendEmptyMessage(3);
                    }
                }
            }
        };
        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    private void getViews() {
        userName = (TextInputLayout) findViewById(R.id.userNameWrapper);
        address = (TextInputLayout) findViewById(R.id.addressWrapper);
        phone = (TextInputLayout) findViewById(R.id.phoneNumberWrapper);
        pass = (TextInputLayout) findViewById(R.id.passwordWrapper);
        passAgain = (TextInputLayout) findViewById(R.id.passwordAgainWrapper);
        checkCode = (TextInputLayout) findViewById(R.id.checkCodeWrapper);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onChooseAddress(View v) {
        startActivityForResult(new Intent(this, AddressActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    province = data.getStringExtra("province");
                    city = data.getStringExtra("city");
                    detail = data.getStringExtra("address");
                    address.getEditText().setText(province + "-" + city + "-" + detail);
                    break;
            }
        }
    }


    //测试Volley的jsonObjectRequest请求返回结果是JSON格式
    public void jsonObjectRequest() {
        Gson gson = new Gson();
        User user = new User();
        user.setUserName(userNameStr);
        user.setPassword(passStr);
        user.setPhone(phoneStr);
        user.setAddressProvince(province);
        user.setAddressCity(city);
        user.setAddressDetail(detail);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson.toJson(user));
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("字符串转json异常");
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    int state = jsonObject.getInt("state");
                    switch (state) {
                        case 0:
                            handler.sendEmptyMessage(4);
                            break;
                        case 1:
                            handler.sendEmptyMessage(5);
                            break;
                        case 2:
                            handler.sendEmptyMessage(6);
                            break;
                        default:
                            handler.sendEmptyMessage(4);
                            break;
                    }

                } catch (JSONException e) {
                    handler.sendEmptyMessage(4);
                    e.printStackTrace();
                }
                Log.i(TAG, "jsonObject: " + jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handler.sendEmptyMessage(4);
                Log.e(TAG, "onErrorResponse: ", volleyError);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void onRegister(View v) {
        userNameStr = userName.getEditText().getText().toString().trim();
        addressStr = address.getEditText().getText().toString().trim();
        phoneStr = phone.getEditText().getText().toString().trim();
        passStr = pass.getEditText().getText().toString().trim();
        passAgainStr = passAgain.getEditText().getText().toString().trim();
        checkCodeStr = checkCode.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(userNameStr)) {
            userName.setError("用户名");
        } else if (TextUtils.isEmpty(addressStr)) {
            address.setError("地址");
        } else if (TextUtils.isEmpty(phoneStr)) {
            phone.setError("手机号");
        } else if (TextUtils.isEmpty(passStr)) {
            pass.setError("密码");
        } else if (TextUtils.isEmpty(passAgainStr)) {
            passAgain.setError("确认密码");
            if (!passStr.equals(passAgainStr)) {
                passAgain.setError("密码不一致");
                return;
            }
        } else if (TextUtils.isEmpty(checkCodeStr)) {
            checkCode.setError("验证码错误");
        } else {
            SMSSDK.submitVerificationCode("+86", phoneStr, checkCodeStr);
        }
    }

    //获取验证码
    public void onCheckCode(View v) {
        checkCodeTxt = (TextView) v;
        String str = phone.getEditText().getText().toString();
        if (!TextUtils.isEmpty(str) && str.length() == 11) {
            //Toast.makeText(this, "onCheckCode" + str, Toast.LENGTH_SHORT).show();
            SMSSDK.getVerificationCode("+86", str);   //请求验证码
            checkCodeTxt.setClickable(false);
            checkCode.setError("");
            checkCode.setErrorEnabled(false);
            timer = 61;
            handler.sendEmptyMessage(1);
        } else {
            Toast.makeText(this, "无效手机号", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

}
