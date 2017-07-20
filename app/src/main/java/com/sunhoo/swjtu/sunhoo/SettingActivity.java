package com.sunhoo.swjtu.sunhoo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by tangpeng on 2017/7/19.
 */

public class SettingActivity extends AppCompatActivity {

    CheckBox checkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getViews();
        setViews();
        setToolBar();
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
                if (isChecked) {
                    Toast.makeText(SettingActivity.this, "接收通知信息", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingActivity.this, "关闭通知信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onAppUpdate(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SettingActivity.this, "已是最新版", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public void onFeedback(View v) {
        startActivity(new Intent(this,FeedbackActivity.class));
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
