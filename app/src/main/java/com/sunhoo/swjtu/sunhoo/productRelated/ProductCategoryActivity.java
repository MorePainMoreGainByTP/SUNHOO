package com.sunhoo.swjtu.sunhoo.productRelated;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.adapters.ProductCommendAdapter;
import com.sunhoo.swjtu.sunhoo.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tangpeng on 2017/7/19.
 */

public class ProductCategoryActivity extends AppCompatActivity {

    String type;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    List<Product> productList;
    ProductCommendAdapter productCommendAdapter;
    private int currDataPage;

    ProgressDialog progressDialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(ProductCategoryActivity.this);
                        progressDialog.setMessage("加载中...");
                        progressDialog.create();
                    }
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                    break;
                case 2:
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);
        type = getIntent().getStringExtra("category");
        setActionBar();
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String title = "全部";
        switch (type) {
            case "chuang":
                title = "床";
                break;
            case "guiZi":
                title = "柜子";
                break;
            case "zhuoZi":
                title = "桌子";
                break;
            case "yiZi":
                title = "椅子";
                break;
            case "shaFa":
                title = "沙发";
                break;
            case "chuangDian":
                title = "床垫";
                break;
        }
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
                        if (currDataPage == 5) {
                            //没数据了
                            Toast.makeText(ProductCategoryActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            //加载下一页数据
                            //拼接url
                            currDataPage += 1;
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    handler.sendEmptyMessage(1);
                                    SystemClock.sleep(2000);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //停止刷新
                                            swipeRefreshLayout.setRefreshing(false);
                                            getDataFromServer();
                                        }
                                    });
                                    handler.sendEmptyMessage(2);
                                }
                            }.start();
                        }
                    }
                }
            }
        });
    }


    private void initProductList() {
        currDataPage = 0;
        String[] urls = {"http://www.shuanghu-jiaju.com/images/taozhuang/tz-7.jpg",
                "http://www.shuanghu-jiaju.com/images/taozhuang/tz-top.jpg",
                "http://www.shuanghu-jiaju.com/images/woshi/ws-top.jpg",
                "http://www.shuanghu-jiaju.com/images/keting/kt-2.jpg",
                "http://www.shuanghu-jiaju.com/images/shafa/sf-4.jpg"};
        if (productList != null)
            productList.clear();
        else productList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setId(i + 1);
            product.setModelID(i + 1 + "");
            product.setOnSale(true);
            product.setPrice(random.nextDouble() * 10000);
            product.setRoom(random.nextInt(100) > 50 ? "卧室" : "大厅");
            product.setSales(random.nextInt(1000));
            product.setSize(random.nextInt(100) + "*" + random.nextInt(100) + "*" + random.nextInt(100) + "*");
            product.setStyle(random.nextInt(100) > 50 ? "中式" : "欧式");
            product.setType(random.nextInt(100) > 50 ? "床" : "其他家具");
            product.setUrl(urls[i]);
            productList.add(product);
        }
        productCommendAdapter.notifyDataSetChanged();
    }

    private void getDataFromServer() {
        Random random = new Random();
        if (productList == null)
            productList = new ArrayList<>();
        String[] urls = {"http://www.shuanghu-jiaju.com/images/taozhuang/tz-7.jpg",
                "http://www.shuanghu-jiaju.com/images/taozhuang/tz-top.jpg",
                "http://www.shuanghu-jiaju.com/images/woshi/ws-top.jpg",
                "http://www.shuanghu-jiaju.com/images/keting/kt-2.jpg",
                "http://www.shuanghu-jiaju.com/images/shafa/sf-4.jpg"};
        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setId(i + 1);
            product.setModelID(i + 1 + "");
            product.setOnSale(true);
            product.setPrice(random.nextDouble() * 10000);
            product.setRoom(random.nextInt(100) > 50 ? "卧室" : "大厅");
            product.setSales(random.nextInt(1000));
            product.setSize(random.nextInt(100) + "*" + random.nextInt(100) + "*" + random.nextInt(100) + "*");
            product.setStyle(random.nextInt(100) > 50 ? "中式" : "欧式");
            product.setType(random.nextInt(100) > 50 ? "床" : "其他家具");
            product.setUrl(urls[i]);
            productList.add(product);
        }
        productCommendAdapter.notifyDataSetChanged();
    }

    private void refreshCourses() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                SystemClock.sleep(2000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //停止刷新
                        swipeRefreshLayout.setRefreshing(false);
                        initProductList();
                    }
                });
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
