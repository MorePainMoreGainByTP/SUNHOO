package com.sunhoo.swjtu.sunhoo.productRelated;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.Utils;
import com.sunhoo.swjtu.sunhoo.adapters.ProductCommendAdapter;
import com.sunhoo.swjtu.sunhoo.entities.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/7/18.
 */

public class ProductSearchActivity extends AppCompatActivity {
    private static final String URL = BASE_URL + "/ASearchProduct";
    private static final String TAG = "ProductSearchActivity";

    EditText editText;
    TextView notFind, noWifi;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ProductCommendAdapter productCommendAdapter;
    List<Product> productList;
    int currCount = 0;
    int count = 10;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    String keyWords;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://开启对话框
                    if (progressDialog == null) {
                        progressDialog = Utils.getProgressDialog(ProductSearchActivity.this, null, "查找中...");
                    }
                    progressDialog.show();
                    break;
                case 1://获取数据成功
                    setVisibility(2);
                    productCommendAdapter.notifyDataSetChanged();
                    break;
                case 2://获取数据失败
                    setVisibility(0);
                    Toast.makeText(ProductSearchActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    setVisibility(1);
                    Toast.makeText(ProductSearchActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(ProductSearchActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void dismissDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    private void setVisibility(int type) {
        dismissDialog();
        notFind.setVisibility(View.INVISIBLE);
        noWifi.setVisibility(View.INVISIBLE);
        switch (type) {
            case 0:
                notFind.setVisibility(View.VISIBLE);
                break;
            case 1:
                noWifi.setVisibility(View.VISIBLE);
                break;
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        requestQueue = Volley.newRequestQueue(this);
        getViews();
        setViews();
    }


    private void getViews() {
        editText = (EditText) findViewById(R.id.key_words);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        notFind = (TextView) findViewById(R.id.not_find);
        noWifi = (TextView) findViewById(R.id.no_wifi);
    }

    private void setViews() {
        productList = new ArrayList<>();
        productCommendAdapter = new ProductCommendAdapter(productList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
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
                            Toast.makeText(ProductSearchActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            //加载下一页数据
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    getDataFromServer();
                                }
                            }.start();
                        }
                    }
                }
            }
        });
    }

    private void refreshCourses() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                currCount = 0;
                count = 10;
                getDataFromServer();
            }
        }.start();
    }


    private void getDataFromServer() {
        if (productList == null)
            productList = new ArrayList<>();
        try {
            String jsonStr = "{\"search\":" + keyWords + ",\"id\":" + currCount + "}";
            JSONObject jsonObject = new JSONObject(jsonStr);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        count = jsonObject.getInt("count");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(2);
                    }
                    Log.i(TAG, "返回的jsonObject: " + jsonObject);
                    Log.i(TAG, "count: " + count);
                    if (count > 0) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            Log.i(TAG, "jsonArray: " + jsonArray);
                            Gson gson = new Gson();
                            if (currCount == 0 && productList != null)
                                productList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.i(TAG, "object: " + i + "  " + object);
                                productList.add(gson.fromJson(object.toString(), Product.class));
                            }
                            currCount += count;
                            handler.sendEmptyMessage(1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(2);
                        }
                    } else {
                        if (productList.size() > 0)
                            handler.sendEmptyMessage(4);
                        else handler.sendEmptyMessage(2);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "onErrorResponse: ", volleyError);
                    handler.sendEmptyMessage(3);
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onSearch(View v) {
        keyWords = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(keyWords)) {
            getDataFromServer();
        }
        hideKeyboard();
    }

    public void onBack(View v) {
        finish();
    }

    //隐藏软键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
