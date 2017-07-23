package com.sunhoo.swjtu.sunhoo.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.Utils;
import com.sunhoo.swjtu.sunhoo.entities.PushInfoLocal;

/**
 * Created by tangpeng on 2017/7/23.
 */

public class DetailMessageActivity extends AppCompatActivity {
    PushInfoLocal pushInfoLocal;
    TextView title, content, time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);
        setToolBar();
        pushInfoLocal = (PushInfoLocal) getIntent().getSerializableExtra("pushLocal");
        pushInfoLocal.setRead(true);
        pushInfoLocal.update(pushInfoLocal.getId());
        getViews();
        setViews();
    }

    private void getViews() {
        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        time = (TextView) findViewById(R.id.time);
    }

    private void setViews() {
        title.setText(pushInfoLocal.getTitle());
        content.setText(pushInfoLocal.getInformation());
        time.setText(pushInfoLocal.getFormatDate());
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("通知详情");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.removeActivity(this);
    }
}
