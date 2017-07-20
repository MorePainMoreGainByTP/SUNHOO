package com.sunhoo.swjtu.sunhoo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sunhoo.swjtu.sunhoo.adapters.ProductCommendAdapter;
import com.sunhoo.swjtu.sunhoo.entities.Product;
import com.sunhoo.swjtu.sunhoo.entities.User;
import com.sunhoo.swjtu.sunhoo.gouwuche.GouWuCheActivity;
import com.sunhoo.swjtu.sunhoo.message.MessageActivity;
import com.sunhoo.swjtu.sunhoo.order.OrderActivity;
import com.sunhoo.swjtu.sunhoo.productRelated.ProductCategoryActivity;
import com.sunhoo.swjtu.sunhoo.productRelated.ProductSearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, OnItemClickListener, View.OnClickListener {
    private static final String URL = "http://192.168.253.1:8080/SSM-SHFGuiding/AProductAll";
    private static final String TAG = "MainActivity";
    public static User user;

    NavigationView navigationView;
    LinearLayout headerLinearLayout;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ConvenientBanner convenientBanner;
    private FloatingActionButton productSearch, productCategory;
    private FloatingActionsMenu actionsMenu;

    private List<String> networkImages; //存放网络上的图片URL
    private String[] imagesFromNetwork = {"http://www.shuanghu-jiaju.com/images/chuangdian/banner-chuangdian.jpg",
            "http://www.shuanghu-jiaju.com/images/taozhuang/banner-taozhuang.jpg",
            "http://www.shuanghu-jiaju.com/images/chuang/banner-chuang.jpg",
            "http://www.shuanghu-jiaju.com/images/shafa/banner-shafa.jpg",
            "http://www.shuanghu-jiaju.com/images/keting/banner-keting.jpg",
            "http://www.shuanghu-jiaju.com/images/woshi/banner-woshi.jpg",
            "http://www.shuanghu-jiaju.com/images/shaonian/banner-shaonian.jpg"
    };
    List<Product> productList;
    ProductCommendAdapter productCommendAdapter;
    int lastId = 0;
    int count = 10;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getViews();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = getSharedPreferences("sunhoo", MODE_PRIVATE);
        getUser();
        initData();

    }

    private void getUser() {
        user = (User) getIntent().getSerializableExtra("user");
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerLinearLayout = (LinearLayout) navigationView.getHeaderView(0);
        headerLinearLayout.findViewById(R.id.user_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UpdateUserActivity.class));
            }
        });
        if (user != null) {
            setUserInfo();
        } else {
            System.exit(0);
        }
    }

    private void setUserInfo() {
        TextView userName = (TextView) headerLinearLayout.findViewById(R.id.userNameTxt);
        userName.setText(user.getUserName());
        TextView phone = (TextView) headerLinearLayout.findViewById(R.id.phoneTxt);
        phone.setText(user.getPhone());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", user.getPhone());
        editor.putString("password", user.getPassword());
        editor.apply();
    }

    private void getViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        productSearch = (FloatingActionButton) findViewById(R.id.productSearch);
        productCategory = (FloatingActionButton) findViewById(R.id.productCategory);
        actionsMenu = (FloatingActionsMenu) findViewById(R.id.addFloatingMenu);
        productSearch.setOnClickListener(this);
        productCategory.setOnClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }

    private void initData() {
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
                            Toast.makeText(MainActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            //加载下一页数据
                            getDataFromServer();
                        }
                    }
                }
            }
        });

        getDataFromServer();
        initImageLoader();

        // 网络加载例子
        networkImages = Arrays.asList(imagesFromNetwork);
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, networkImages).setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused}).setOnItemClickListener(MainActivity.this);
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
                        getDataFromServer();
                    }
                });
            }
        }.start();
    }

//    private void initProductList() {
//        Random random = new Random();
//        if (productList != null)
//            productList.clear();
//        else productList = new ArrayList<>();
//        currDataPage = 0;
//        String[] urls = {"http://www.shuanghu-jiaju.com/images/taozhuang/tz-7.jpg",
//                "http://www.shuanghu-jiaju.com/images/taozhuang/tz-top.jpg",
//                "http://www.shuanghu-jiaju.com/images/woshi/ws-top.jpg",
//                "http://www.shuanghu-jiaju.com/images/keting/kt-2.jpg",
//                "http://www.shuanghu-jiaju.com/images/shafa/sf-4.jpg"};
//        for (int i = 0; i < 5; i++) {
//            Product product = new Product();
//            product.setId(i + 1);
//            product.setModelID(i + 1 + "");
//            product.setOnSale(true);
//            product.setPrice(random.nextDouble() * 10000);
//            product.setRoom(random.nextInt(100) > 50 ? "卧室" : "大厅");
//            product.setSales(random.nextInt(1000));
//            product.setSize(random.nextInt(100) + "*" + random.nextInt(100) + "*" + random.nextInt(100) + "*");
//            product.setStyle(random.nextInt(100) > 50 ? "中式" : "欧式");
//            product.setType(random.nextInt(100) > 50 ? "床" : "其他家具");
//            product.setUrl(urls[i]);
//            productList.add(product);
//        }
//        productCommendAdapter.notifyDataSetChanged();
//    }

    RequestQueue requestQueue;

    private void getDataFromServer() {
        if (productList == null)
            productList = new ArrayList<>();
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(this);
        try {
            String jsonStr = "{\"id\":" + lastId + "}";
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
                    Log.i(TAG, "count: "+count);
                    if (count > 0)
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            Log.i(TAG, "jsonArray: "+jsonArray);
                            Gson gson = new Gson();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.i(TAG, "object: "+i+"  "+object);
                                productList.add(gson.fromJson(object.toString(), Product.class));
                            }
                            lastId = productList.get(productList.size() - 1).getId();
                            productCommendAdapter.notifyDataSetChanged();
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

    //初始化网络图片缓存库
    private void initImageLoader() {
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.mipmap.sunhoo)
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gou_wu_che) {
            startActivity(new Intent(this, GouWuCheActivity.class));
        } else if (id == R.id.nav_order) {
            startActivity(new Intent(this, OrderActivity.class));
        } else if (id == R.id.nav_message) {
            startActivity(new Intent(this, MessageActivity.class));
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Toast.makeText(this, "监听到翻到第" + position + "了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
        //开始自动翻页
        convenientBanner.startTurning(5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.productCategory:
                actionsMenu.collapse();
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.product_category, null);
                new AlertDialog.Builder(this).setView(view).setNegativeButton("取消", null).create().show();
                TextView chuang = (TextView) view.findViewById(R.id.chuang);
                TextView guiZi = (TextView) view.findViewById(R.id.gui_zi);
                TextView zhuoZi = (TextView) view.findViewById(R.id.zhuo_zi);
                TextView yiZi = (TextView) view.findViewById(R.id.yi_zi);
                TextView shaFa = (TextView) view.findViewById(R.id.sha_fa);
                TextView chuangDian = (TextView) view.findViewById(R.id.chuang_dian);
                chuang.setOnClickListener(this);
                guiZi.setOnClickListener(this);
                zhuoZi.setOnClickListener(this);
                yiZi.setOnClickListener(this);
                shaFa.setOnClickListener(this);
                chuangDian.setOnClickListener(this);
                break;
            case R.id.productSearch:
                startActivity(new Intent(MainActivity.this, ProductSearchActivity.class));
                actionsMenu.collapse();
                break;
            case R.id.chuang:
                startActivity(new Intent(this, ProductCategoryActivity.class).putExtra("category", "chuang"));
                break;
            case R.id.gui_zi:
                startActivity(new Intent(this, ProductCategoryActivity.class).putExtra("category", "guiZi"));
                break;
            case R.id.zhuo_zi:
                startActivity(new Intent(this, ProductCategoryActivity.class).putExtra("category", "zhuoZi"));
                break;
            case R.id.yi_zi:
                startActivity(new Intent(this, ProductCategoryActivity.class).putExtra("category", "yiZi"));
                break;
            case R.id.sha_fa:
                startActivity(new Intent(this, ProductCategoryActivity.class).putExtra("category", "shaFa"));
                break;
            case R.id.chuang_dian:
                startActivity(new Intent(this, ProductCategoryActivity.class).putExtra("category", "chuangDian"));
                break;
        }
    }

    class NetworkImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            imageView.setImageResource(R.mipmap.sunhoo);
            ImageLoader.getInstance().displayImage(data, imageView);
        }
    }


    private long lastTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (Math.abs(System.currentTimeMillis() - lastTime) < 2000) {
                finish();
            } else {
                lastTime = System.currentTimeMillis();
            }
        }
        return true;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://加载失败
                    Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
