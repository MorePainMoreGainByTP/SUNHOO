package com.sunhoo.swjtu.sunhoo;

import android.Manifest;
import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/7/18.
 */

public class ForgetPasswordActivity extends AppCompatActivity {
    private static final String URL = BASE_URL + "/AForget";
    private static final String TAG = "ForgetPasswordActivity";

    TextInputLayout phone, newPassword, newPasswordAgain, checkCode;
    TextView checkCodeTxt;
    String phoneStr;
    String passStr;
    String passAgainStr;
    String checkCodeStr;
    ProgressDialog progressDialog;
    private EventHandler eventHandler;
    private RequestQueue requestQueue;
    private boolean boolShowInDialog = true;
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
                    progressDialog = new ProgressDialog(ForgetPasswordActivity.this);
                    progressDialog.setMessage("操作中...");
                    progressDialog.create();
                }
                if (!progressDialog.isShowing())
                    progressDialog.show();
            }
            if (msg.what == 3) {//验证码错误
                checkCode.setError("验证失败");
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            if (msg.what == 4) {//修改失败
                Toast.makeText(ForgetPasswordActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            if (msg.what == 5) {//修改成功
                Toast.makeText(ForgetPasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                finish();
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
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

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("忘记密码");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getViews() {
        phone = (TextInputLayout) findViewById(R.id.phoneNumberWrapper);
        newPassword = (TextInputLayout) findViewById(R.id.passwordWrapper);
        newPasswordAgain = (TextInputLayout) findViewById(R.id.passwordAgainWrapper);
        checkCode = (TextInputLayout) findViewById(R.id.checkCodeWrapper);
        checkCodeTxt = (TextView) findViewById(R.id.displayTimeTxt);
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
                    Toast.makeText(ForgetPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                            commitInfo();
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

    private void commitInfo() {
        try {
            String jsonStr = "{\"phone\":" + phoneStr + ",\"password\":" + passStr + "}";
            JSONObject jsonObject = new JSONObject(jsonStr);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    int state = 0;
                    try {
                        state = jsonObject.getInt("state");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(4);
                    }
                    Log.i(TAG, "返回的jsonObject: " + jsonObject);
                    if (state == 1) {
                        handler.sendEmptyMessage(5);
                    } else {
                        handler.sendEmptyMessage(4);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "onErrorResponse: ", volleyError);
                    handler.sendEmptyMessage(4);
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(4);
        }
    }


    public void onCommit(View v) {
        phoneStr = phone.getEditText().getText().toString().trim();
        passStr = newPassword.getEditText().getText().toString().trim();
        passAgainStr = newPasswordAgain.getEditText().getText().toString().trim();
        checkCodeStr = checkCode.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(phoneStr)) {
            phone.setError("手机号");
        } else if (TextUtils.isEmpty(passStr)) {
            newPassword.setError("密码");
        } else if (TextUtils.isEmpty(passAgainStr)) {
            newPasswordAgain.setError("确认密码");
        } else if (!passStr.equals(passAgainStr)) {
            newPasswordAgain.setError("密码不一致");
            return;
        } else if (TextUtils.isEmpty(checkCodeStr)) {
            checkCode.setError("验证码错误");
        } else {
            SMSSDK.submitVerificationCode("+86", phoneStr, checkCodeStr);
        }
    }

    public void onCheckCode(View v) {
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
