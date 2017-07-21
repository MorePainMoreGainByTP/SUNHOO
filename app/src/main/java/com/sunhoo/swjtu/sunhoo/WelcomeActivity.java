package com.sunhoo.swjtu.sunhoo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by tangpeng on 2017/7/18.
 */

public class WelcomeActivity extends AppCompatActivity {

    private TextView timer;
    private int currTime = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    currTime--;
                    if (currTime <= 0) {
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        timer.setText(currTime + "s");
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        timer = (TextView) findViewById(R.id.timer);
        timer.setText(currTime + "s");
        handler.sendEmptyMessageDelayed(1, 1000);
    }
}
