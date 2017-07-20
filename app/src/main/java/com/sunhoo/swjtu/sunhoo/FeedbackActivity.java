package com.sunhoo.swjtu.sunhoo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by tangpeng on 2017/7/20.
 */

public class FeedbackActivity extends AppCompatActivity {

    EditText title, content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
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

        }
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("反馈");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
