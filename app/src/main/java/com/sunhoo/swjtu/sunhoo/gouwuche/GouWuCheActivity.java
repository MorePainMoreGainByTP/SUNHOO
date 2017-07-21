package com.sunhoo.swjtu.sunhoo.gouwuche;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.adapters.GouWuCheAdapter;
import com.sunhoo.swjtu.sunhoo.entities.OrderItem;
import com.sunhoo.swjtu.sunhoo.entities.ShoppingCarProduct;
import com.sunhoo.swjtu.sunhoo.order.GenerateOrderActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/7/19.
 */

public class GouWuCheActivity extends AppCompatActivity {
    private static final String TAG = "GouWuCheActivity";
    RecyclerView recyclerView;
    List<ShoppingCarProduct> productList;
    GouWuCheAdapter gouWuCheAdapter;
    private CheckBox selectAll;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gou_wu_che);
        setToolBar();
        getViews();
        getDataFromDB();
        setViews();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("购物车");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selectAll = (CheckBox) findViewById(R.id.select_all);
        imageView = (ImageView) findViewById(R.id.image_view);
    }

    private void setViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gouWuCheAdapter);
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gouWuCheAdapter.setChecked(isChecked);
            }
        });
    }

    private void getDataFromDB() {
        productList = DataSupport.findAll(ShoppingCarProduct.class);
        if (productList.size() == 0)
            imageView.setVisibility(View.VISIBLE);
        Log.i(TAG, "productList: " + productList);
        gouWuCheAdapter = new GouWuCheAdapter(productList);
    }

    public void onDeleteProduct(View v) {
        if (!gouWuCheAdapter.isSelectNone()) {
            new AlertDialog.Builder(this).setMessage("删除所选项？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doDelete();
                }
            }).create().show();
        }
    }

    private void doDelete() {
        ArrayList<Boolean> selectedCount = gouWuCheAdapter.getChecked();
        for (int i = selectedCount.size() - 1; i >= 0; i--) {
            if (selectedCount.get(i)) {
                DataSupport.delete(ShoppingCarProduct.class, productList.get(i).getId());
                selectedCount.remove(i);
                productList.remove(i);
            }
        }
        selectAll.setChecked(false);
        gouWuCheAdapter.notifyDataSetChanged();
    }

    public void onCount(View v) {
        if (!gouWuCheAdapter.isSelectNone()) {
            List<ShoppingCarProduct> selectProducts = gouWuCheAdapter.getSelectProduct();
            ArrayList<OrderItem> orderItems = generateOrderItemList(selectProducts);
            startActivityForResult(new Intent(this, GenerateOrderActivity.class).putExtra("list", orderItems)
                    .putExtra("productInfo", getProductInfo(selectProducts))
                    .putExtra("urls", getUrls(selectProducts)), 1);
            /*
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_count, null);
            initView(view);
            new AlertDialog.Builder(this).setView(view).setNegativeButton("取消", null).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doCount();
                }
            }).create().show();
            */
        }
    }

    private ArrayList<OrderItem> generateOrderItemList(List<ShoppingCarProduct> selectProducts) {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        for (ShoppingCarProduct product : selectProducts) {
            OrderItem orderItem = new OrderItem();
            orderItem.setNumber(product.getNum());
            orderItem.setPrice(product.getPrice());
            orderItem.setProductID(product.getServerId());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private ArrayList<String> getUrls(List<ShoppingCarProduct> selectProducts) {
        ArrayList<String> strings = new ArrayList<>();
        for (ShoppingCarProduct product : selectProducts) {
            strings.add(product.getUrl());
        }
        return strings;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    doDelete();
                    break;
            }
        }
    }

    private ArrayList<String> getProductInfo(List<ShoppingCarProduct> selectProducts) {
        ArrayList<String> productInfoList = new ArrayList<>();
        for (ShoppingCarProduct product : selectProducts) {
            productInfoList.add(product.getModelID() + "," + product.getSize() + "," + product.getStyle());
        }
        return productInfoList;
    }

  /*  private void initView(View view) {
        RecyclerView recyclerView1 = (RecyclerView) view.findViewById(R.id.recyclerView);
        TextView sumPrice = (TextView) view.findViewById(R.id.sum_price);
        List<ShoppingCarProduct> selectProducts = gouWuCheAdapter.getSelectProduct();
        double finalPrice = 0;
        for (ShoppingCarProduct product : selectProducts) {
            finalPrice += product.getPrice() * product.getNum();
        }
        DialogCountAdapter dialogCountAdapter = new DialogCountAdapter(selectProducts);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(dialogCountAdapter);
        sumPrice.setText("¥" + String.format("%.2f", finalPrice));
    }*/

    private void doCount() {//结算
        doDelete();
    }

    public CheckBox getSelectAll() {
        return selectAll;
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
