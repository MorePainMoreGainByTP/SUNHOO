package com.sunhoo.swjtu.sunhoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.entities.DetailOrder;

import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/3/5.
 */

public class DetailOrderAdapter extends RecyclerView.Adapter<DetailOrderAdapter.ViewHolder> {

    private List<DetailOrder> orderList;
    private Context context;

    public DetailOrderAdapter(List<DetailOrder> orderList) {
        this.orderList = orderList;
    }

    @Override
    public DetailOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null)
            context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_detail_order, parent, false);
        return new ViewHolder(view);
    }

    //每次item进入屏幕时 调用
    @Override
    public void onBindViewHolder(DetailOrderAdapter.ViewHolder holder, int position) {
        DetailOrder order = orderList.get(position);
        Glide.with(context).load(BASE_URL + order.getUrl()).placeholder(R.mipmap.sunhoo_logo3).into(holder.imageView);
        holder.typeStyleRoom.setText(order.getType() + "-" + order.getStyle() + "-" + order.getRoom());
        holder.numSizeModel.setText(order.getNum() + "  " + order.getSize() + "  " + order.getModelID());
        holder.price.setText(order.getFormatPrice());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView typeStyleRoom;
        TextView numSizeModel;
        TextView price;

        public ViewHolder(View itemView) {//itemView是 每项数据的根布局
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            typeStyleRoom = (TextView) itemView.findViewById(R.id.type_style_room);
            numSizeModel = (TextView) itemView.findViewById(R.id.num_size_model);
            price = (TextView) itemView.findViewById(R.id.price);
        }
    }
}
