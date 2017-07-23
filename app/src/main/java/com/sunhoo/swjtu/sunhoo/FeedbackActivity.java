package com.sunhoo.swjtu.sunhoo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sunhoo.swjtu.sunhoo.entities.FeedbackInfo;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;
import static com.sunhoo.swjtu.sunhoo.MainActivity.user;

/**
 * Created by tangpeng on 2017/7/20.
 */

public class FeedbackActivity extends AppCompatActivity {
    private static final String URL = BASE_URL + "/AAddFeedbackInfo";
    EditText title, content;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://开启对话框
                    if (progressDialog == null) {
                        progressDialog = Utils.getProgressDialog(FeedbackActivity.this, null, "提交中...");
                    }
                    progressDialog.show();
                    break;
                case 1://提交成功
                    dismissDialog();
                    Toast.makeText(FeedbackActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 2://获取数据失败
                    Toast.makeText(FeedbackActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    break;
                case 3:
                    Toast.makeText(FeedbackActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    break;
            }
        }
    };

    private void dismissDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        requestQueue = Volley.newRequestQueue(this);
        setToolBar();
        getViews();
    }

    private void getViews() {
        title = (EditText) findViewById(R.id.feedback_title);
        content = (EditText) findViewById(R.id.feedback_content);
    }

    public void onCommit(View v) {
        String titleStr = title.getText().toString().trim();
        String contentStr = content.getText().toString().trim();
        if (TextUtils.isEmpty(titleStr)) {
            Toast.makeText(this, "输入标题", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(contentStr)) {
            Toast.makeText(this, "输入正文", Toast.LENGTH_SHORT).show();
        } else {//提交反馈
            doCommit(titleStr, contentStr);
        }
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("反馈");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void doCommit(String title, String content) {
        final FeedbackInfo feedbackInfo = new FeedbackInfo();
        feedbackInfo.setUserID(user.getId());
        feedbackInfo.setInformation(content);
        feedbackInfo.setTitle(title);
        handler.sendEmptyMessage(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                try {
                    JSONObject jsonObject = new JSONObject(gson.toJson(feedbackInfo));
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            int state = 0;
                            try {
                                state = jsonObject.getInt("state");
                                if (state == 1) {
                                    handler.sendEmptyMessage(1);
                                } else {
                                    handler.sendEmptyMessage(2);
                                }
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
                    handler.sendEmptyMessage(2);
                }
            }
        }).start();
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
