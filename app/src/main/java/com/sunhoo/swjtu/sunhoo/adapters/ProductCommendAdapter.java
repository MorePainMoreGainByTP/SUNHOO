package com.sunhoo.swjtu.sunhoo.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.entities.Product;
import com.sunhoo.swjtu.sunhoo.productRelated.ProductDetailActivity;

import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/3/5.
 */

public class ProductCommendAdapter extends RecyclerView.Adapter<ProductCommendAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;

    public ProductCommendAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public ProductCommendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null)
            context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_single_product, parent, false);
        return new ViewHolder(view);
    }

    //每次item进入屏幕时 调用
    @Override
    public void onBindViewHolder(ProductCommendAdapter.ViewHolder holder, final int position) {
        final Product product = productList.get(position);
        //Glide很不错的图片加载库，自动完成图片压缩，可以从本地、网上、和资源id中加载图片，
        // 使用起来非常简单，只需要一句话,load里面可以是URI、资源ID，路径，into里面存放一个imageView实例
        Glide.with(context).load(BASE_URL + product.getUrl()).placeholder(R.mipmap.sunhoo_logo3).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.productInfo.setText(product.getModelID() + "," + product.getSize() + "," + product.getStyle());
        holder.productPrice.setText("¥" + String.format("%.2f", product.getPrice()));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView productInfo;
        TextView productPrice;

        public ViewHolder(View itemView) {//itemView是 每项数据的根布局
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.product_image);
            productInfo = (TextView) itemView.findViewById(R.id.product_info);
            productPrice = (TextView) itemView.findViewById(R.id.product_price);
        }
    }
}
