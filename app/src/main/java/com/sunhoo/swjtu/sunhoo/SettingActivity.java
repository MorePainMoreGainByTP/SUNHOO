package com.sunhoo.swjtu.sunhoo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import static com.sunhoo.swjtu.sunhoo.MainActivity.PUSH_KEY;

/**
 * Created by tangpeng on 2017/7/19.
 */

public class SettingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    CheckBox checkBox;
    ProgressDialog progressDialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://开启对话框
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(SettingActivity.this);
                        progressDialog.setMessage("检查中...");
                        progressDialog.create();
                    }
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                    break;
                case 2://登录成功
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(SettingActivity.this, "已是最新版", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getViews();
        setViews();
        setToolBar();
        sharedPreferences = getSharedPreferences("sunhoo",MODE_PRIVATE);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getViews() {
        checkBox = (CheckBox) findViewById(R.id.switch_message);
    }

    private void setViews() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    Toast.makeText(SettingActivity.this, "接收通知信息", Toast.LENGTH_SHORT).show();
                    PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, PUSH_KEY);
                    editor.putBoolean("receiveMsg",true);
                } else {
                    Toast.makeText(SettingActivity.this, "关闭通知信息", Toast.LENGTH_SHORT).show();
                    PushManager.stopWork(getApplicationContext());
                    editor.putBoolean("receiveMsg",false);
                }
            }
        });
    }

    public void onAppUpdate(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
                SystemClock.sleep(2000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(2);
                    }
                });
            }
        }).start();
    }

    public void onFeedback(View v) {
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    public void onExit(View v) {
        new AlertDialog.Builder(this).setMessage("退出登录？").setNegativeButton("取消", null).setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                finish();
            }
        }).create().show();
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
