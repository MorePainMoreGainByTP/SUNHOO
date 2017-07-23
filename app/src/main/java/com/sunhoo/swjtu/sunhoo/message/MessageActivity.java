package com.sunhoo.swjtu.sunhoo.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.Utils;
import com.sunhoo.swjtu.sunhoo.adapters.PushInfoLocalAdapter;
import com.sunhoo.swjtu.sunhoo.entities.PushInfoLocal;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/7/19.
 */

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";

    RecyclerView recyclerView;
    PushInfoLocalAdapter pushInfoLocalAdapter;
    List<PushInfoLocal> pushInfoLocals;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setToolBar();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setViews();
        getDataFromDB();
        Utils.addActivity(this);
        Log.i(TAG, "onCreate: ");
    }

    private void setViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pushInfoLocals = new ArrayList<>();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("通知信息");
        setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
}

    private void getDataFromDB() {
        pushInfoLocals = DataSupport.findAll(PushInfoLocal.class);
        Log.i(TAG, "pushInfoLocals: " + pushInfoLocals);
        pushInfoLocalAdapter = new PushInfoLocalAdapter(pushInfoLocals);
        recyclerView.setAdapter(pushInfoLocalAdapter);
    }

    private String getContents() {
        int time = (int) (Math.random() * 20);
        String str = "";
        for (int i = 0; i < time; i++) {
            str += "圣诞节女几十块的";
        }
        return str;
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
