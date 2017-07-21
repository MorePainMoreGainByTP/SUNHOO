package com.sunhoo.swjtu.sunhoo.productRelated;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.Utils;
import com.sunhoo.swjtu.sunhoo.entities.Product;
import com.sunhoo.swjtu.sunhoo.gouwuche.GouWuCheActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/7/18.
 */

public class ProductDetailActivity extends AppCompatActivity implements OnItemClickListener, ViewPager.OnPageChangeListener, View.OnClickListener {
    private static final String URL = BASE_URL + "/AGetPic";
    private static final String TAG = "ProductDetailActivity";

    ConvenientBanner convenientBanner;
    Product product;
    TextView type, modelID, size, style, room, sales, price, counter;
    Button decline, add;
    int selectCount = 1;

    private List<String> networkImages; //存放网络上的图片URL

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://加载图片失败
                    Toast.makeText(ProductDetailActivity.this, "加载图片失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1://获取图成功
                    setConvenientBanner();
                    if (convenientBanner != null)
                        convenientBanner.notifyDataSetChanged();
                    break;
                case 2://网络连接失败
                    Toast.makeText(ProductDetailActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        product = (Product) getIntent().getSerializableExtra("product");
        getViews();
        setViews();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromServer();
            }
        }).start();
    }

    public void getViews() {
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        type = (TextView) findViewById(R.id.type);
        modelID = (TextView) findViewById(R.id.modelID);
        size = (TextView) findViewById(R.id.size);
        style = (TextView) findViewById(R.id.style);
        room = (TextView) findViewById(R.id.room);
        sales = (TextView) findViewById(R.id.sales);
        price = (TextView) findViewById(R.id.price);
        counter = (TextView) findViewById(R.id.count);
        decline = (Button) findViewById(R.id.btn_decline);
        add = (Button) findViewById(R.id.btn_add);
    }

    private void setViews() {
        counter.setText("" + selectCount);
        decline.setOnClickListener(this);
        add.setOnClickListener(this);
        type.setText(product.getType());
        style.setText(product.getStyle());
        modelID.setText(product.getModelID());
        size.setText(product.getSize());
        room.setText(product.getRoom());
        sales.setText("" + product.getSales());
        price.setText("¥" + String.format("%.2f", product.getPrice()));
    }

    private void setConvenientBanner() {
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, networkImages).setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused}).setOnItemClickListener(ProductDetailActivity.this);
    }

    public void onBack(View v) {
        finish();
    }

    private void getDataFromServer() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String jsonStr = "{\"id\":" + product.getId() + "}";
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    int count = 0;
                    try {
                        count = jsonObject.getInt("count");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(0);
                    }
                    Log.i(TAG, "返回的jsonObject: " + jsonObject);
                    if (count > 0)
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (networkImages != null)
                                networkImages.clear();
                            else networkImages = new ArrayList<>();
                            for (int i = 0; i < count; i++) {
                                networkImages.add(BASE_URL + jsonArray.getString(i));
                            }
                            handler.sendEmptyMessage(1);
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

    public void onAdd2ShoppingCar(View v) {
        Utils.addProduct2ShoppingCar(product, selectCount);
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
    }

    public void onShoppingCar(View v) {
        startActivity(new Intent(this, GouWuCheActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始自动翻页
        //convenientBanner.startTurning(5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止翻页
        //convenientBanner.stopTurning();
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_decline:
                if (selectCount <= 1)
                    return;
                selectCount--;
                updateCounter();
                break;
            case R.id.btn_add:
                selectCount++;
                updateCounter();
                break;
        }
    }

    private void updateCounter() {
        counter.setText("" + selectCount);
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
}
