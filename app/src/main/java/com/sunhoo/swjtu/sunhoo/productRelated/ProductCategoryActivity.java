package com.sunhoo.swjtu.sunhoo.productRelated;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.adapters.ProductCommendAdapter;
import com.sunhoo.swjtu.sunhoo.entities.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/7/19.
 */

public class ProductCategoryActivity extends AppCompatActivity {
    private static final String TAG = "ProductCategoryActivity";
    private static final String URL = BASE_URL+"/AProductCategory";

    String category;
    String type;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Product> productList;
    ProductCommendAdapter productCommendAdapter;

    int lastId = 0;
    int count = 10;
    RequestQueue requestQueue;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://加载失败
                    Toast.makeText(ProductCategoryActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(ProductCategoryActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ProductCategoryActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3://加载成功
                    productCommendAdapter.notifyDataSetChanged();
                    break;
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);
        category = getIntent().getStringExtra("category");
        setActionBar();

        refreshCourses();
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (category) {
            case "chuang":
                type = "床";
                break;
            case "guiZi":
                type = "柜子";
                break;
            case "zhuoZi":
                type = "桌子";
                break;
            case "yiZi":
                type = "椅子";
                break;
            case "shaFa":
                type = "沙发";
                break;
            case "chuangDian":
                type = "床垫";
                break;
        }
        toolbar.setTitle(type);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getViews();
        setViews();
    }

    private void getViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }

    private void setViews() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        productCommendAdapter = new ProductCommendAdapter(productList);
        recyclerView.setAdapter(productCommendAdapter);

        //设置下拉旋转箭头的颜色
        swipeRefreshLayout.setColorSchemeResources(new int[]{R.color.colorAccent, R.color.greenyellow, R.color.orange});
        swipeRefreshLayout.setProgressViewOffset(true, 0, 100);//设置加载圈是否有缩放效果，后两个参数是展示的位置y轴坐标
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCourses();
            }
        });
        //上滑加载更多
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                int lastItemPosition = 0;
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                }
                if (productCommendAdapter != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastItemPosition + 1 == productCommendAdapter.getItemCount()) {
                        //访问网络加载下一页数据
                        if (count < 10) {
                            //没数据了
                            handler.sendEmptyMessage(1);
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            //加载下一页数据
                            new Thread() {
                                @Override
                                public void run() {
                                    getDataFromServer();
                                }
                            }.start();
                        }
                    }
                }
            }
        });
    }

    private void getDataFromServer() {
        if (productList == null)
            productList = new ArrayList<>();
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(this);
        try {
            String jsonStr = "{\"id\":" + lastId
                    + "," + "\"type\":" + type
                    + "}";
            JSONObject jsonObject = new JSONObject(jsonStr);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        count = jsonObject.getInt("count");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(0);
                    }
                    Log.i(TAG, "返回的jsonObject: " + jsonObject);
                    Log.i(TAG, "count: " + count);
                    if (count > 0)
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            Log.i(TAG, "jsonArray: " + jsonArray);
                            Gson gson = new Gson();
                            if (lastId == 0)
                                productList.clear();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.i(TAG, "object: " + i + "  " + object);
                                productList.add(gson.fromJson(object.toString(), Product.class));
                            }
                            lastId = productList.get(productList.size() - 1).getId();
                            handler.sendEmptyMessage(3);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(0);
                        }
                    switch (count) {
                        case 0:
                            handler.sendEmptyMessage(1);
                            break;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "onErrorResponse: ", volleyError);
                    handler.sendEmptyMessage(2);
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshCourses() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                lastId = 0;
                count = 10;
                getDataFromServer();
            }
        }.start();
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
