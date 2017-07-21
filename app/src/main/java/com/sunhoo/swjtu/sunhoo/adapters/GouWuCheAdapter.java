package com.sunhoo.swjtu.sunhoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.entities.ShoppingCarProduct;
import com.sunhoo.swjtu.sunhoo.gouwuche.GouWuCheActivity;

import java.util.ArrayList;
import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/3/5.
 */

public class GouWuCheAdapter extends RecyclerView.Adapter<GouWuCheAdapter.ViewHolder> {

    private List<ShoppingCarProduct> productList;
    private ArrayList<Boolean> checked;  //checkbox的状态
    private Context context;

    public GouWuCheAdapter(List<ShoppingCarProduct> productList) {
        this.productList = productList;
        checked = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            checked.add(false);
        }
    }

    @Override
    public GouWuCheAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null)
            context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_gou_wu_che, parent, false);
        return new ViewHolder(view);
    }

    //每次item进入屏幕时 调用
    @Override
    public void onBindViewHolder(GouWuCheAdapter.ViewHolder holder, final int position) {
        final ShoppingCarProduct product = productList.get(position);
        //Glide很不错的图片加载库，自动完成图片压缩，可以从本地、网上、和资源id中加载图片，
        // 使用起来非常简单，只需要一句话,load里面可以是URI、资源ID，路径，into里面存放一个imageView实例
        Glide.with(context).load(BASE_URL + product.getUrl()).placeholder(R.mipmap.sunhoo_logo3).into(holder.imageView);
        holder.itemTitle.setText(product.getType() + "-" + product.getStyle() + "-" + product.getRoom());
        holder.productType.setText("型号：" + product.getModelID());
        holder.productSize.setText(product.getSize());
        holder.counter.setText(""+product.getNum());
        holder.productPrice.setText("¥" + String.format("%.2f", product.getPrice()));
        MyOnClickListener myOnClickListener = new MyOnClickListener(position);
        holder.add.setOnClickListener(myOnClickListener);
        holder.decline.setOnClickListener(myOnClickListener);
        holder.checkBox.setChecked(checked.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.getVisibility() == View.VISIBLE) {
                    checked.set(position, isChecked);
                    selectAll(isChecked);
                }
            }
        });
    }

    private void selectAll(boolean isChecked) {
        CheckBox selectAll = ((GouWuCheActivity) context).getSelectAll();
        if (!isChecked) {
            selectAll.setChecked(false);
        } else {
            int flag = 0;
            for (boolean check : checked) {
                if (!check) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0)//全选
            {
                selectAll.setChecked(true);
            } else {
                selectAll.setChecked(false);
            }
        }
    }

    public void setChecked(boolean isChecked) {
        for (int i = 0; i < checked.size(); i++) {
            checked.set(i, isChecked);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public ArrayList<Boolean> getChecked() {
        return checked;
    }

    public boolean isSelectNone(){
        for(boolean check:checked){
            if(check)
                return false;
        }
        return true;
    }

    public ArrayList<ShoppingCarProduct> getSelectProduct(){
        ArrayList<ShoppingCarProduct> carProducts = new ArrayList<>();
        for(int i = 0 ; i < checked.size();i++){
            if(checked.get(i)){
                carProducts.add(productList.get(i));
            }
        }
        return carProducts;
    }

    class MyOnClickListener implements View.OnClickListener {
        int position;

        public MyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_decline:
                    ShoppingCarProduct shoppingCarProduct = productList.get(position);
                    if(shoppingCarProduct.getNum() <= 1)
                        return;
                    shoppingCarProduct.setNum(shoppingCarProduct.getNum() - 1);
                    shoppingCarProduct.update(shoppingCarProduct.getId());
                    notifyDataSetChanged();
                    break;
                case R.id.btn_add:
                    ShoppingCarProduct shoppingCarProduct2 = productList.get(position);
                    shoppingCarProduct2.setNum(shoppingCarProduct2.getNum() + 1);
                    shoppingCarProduct2.update(shoppingCarProduct2.getId());
                    notifyDataSetChanged();
                    break;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView imageView;
        TextView itemTitle;
        TextView counter;
        TextView productSize;
        TextView productType;
        TextView productPrice;
        Button decline, add;

        public ViewHolder(View itemView) {//itemView是 每项数据的根布局
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            checkBox = (CheckBox) itemView.findViewById(R.id.select);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            counter = (TextView) itemView.findViewById(R.id.counter);
            productSize = (TextView) itemView.findViewById(R.id.size);
            productType = (TextView) itemView.findViewById(R.id.type);
            productPrice = (TextView) itemView.findViewById(R.id.price);
            decline = (Button) itemView.findViewById(R.id.btn_decline);
            add = (Button) itemView.findViewById(R.id.btn_add);
        }
    }
}
