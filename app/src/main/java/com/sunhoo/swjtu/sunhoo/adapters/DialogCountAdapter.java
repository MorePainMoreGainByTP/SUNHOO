package com.sunhoo.swjtu.sunhoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.entities.OrderItem;

import java.util.List;

/**
 * Created by tangpeng on 2017/3/5.
 */

public class DialogCountAdapter extends RecyclerView.Adapter<DialogCountAdapter.ViewHolder> {
    private List<OrderItem> orderItemList;
    private List<String> productInfo;
    private Context context;

    public DialogCountAdapter(List<OrderItem> orderItemList, List<String> productInfo) {
        this.orderItemList = orderItemList;
        this.productInfo = productInfo;
    }

    @Override
    public DialogCountAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null)
            context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_count, parent, false);
        return new ViewHolder(view);
    }

    //每次item进入屏幕时 调用
    @Override
    public void onBindViewHolder(DialogCountAdapter.ViewHolder holder, int position) {
        OrderItem product = orderItemList.get(position);
        holder.productInfo.setText(productInfo.get(position));
        holder.productNum.setText("" + product.getNumber());
        holder.productPrice.setText("¥" + String.format("%.2f", product.getPrice()));
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productInfo;
        TextView productPrice;
        TextView productNum;

        public ViewHolder(View itemView) {//itemView是 每项数据的根布局
            super(itemView);
            productNum = (TextView) itemView.findViewById(R.id.product_num);
            productInfo = (TextView) itemView.findViewById(R.id.product_info);
            productPrice = (TextView) itemView.findViewById(R.id.product_price);
        }
    }
}
