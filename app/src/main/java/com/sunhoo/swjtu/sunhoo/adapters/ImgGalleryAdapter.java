package com.sunhoo.swjtu.sunhoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sunhoo.swjtu.sunhoo.R;

import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/3/5.
 */

public class ImgGalleryAdapter extends RecyclerView.Adapter<ImgGalleryAdapter.ViewHolder> {

    private List<String> urls;
    private Context context;

    public ImgGalleryAdapter(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public ImgGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null)
            context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_imageview, parent, false);
        return new ViewHolder(view);
    }

    //每次item进入屏幕时 调用
    @Override
    public void onBindViewHolder(ImgGalleryAdapter.ViewHolder holder, final int position) {
        final String product = urls.get(position);
        //Glide很不错的图片加载库，自动完成图片压缩，可以从本地、网上、和资源id中加载图片，
        // 使用起来非常简单，只需要一句话,load里面可以是URI、资源ID，路径，into里面存放一个imageView实例
        Glide.with(context).load(BASE_URL + urls.get(position)).placeholder(R.mipmap.sunhoo_logo3).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {//itemView是 每项数据的根布局
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }
}
