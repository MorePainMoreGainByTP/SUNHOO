package com.sunhoo.swjtu.sunhoo.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.entities.Order;

import java.util.List;

/**
 * Created by tangpeng on 2017/3/5.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> orderList;
    private Context context;
    private String type;

    public OrderAdapter(List<Order> orderList, String type) {
        this.orderList = orderList;
        this.type = type;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null)
            context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    //每次item进入屏幕时 调用
    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, final int position) {
        Order order = orderList.get(position);
        holder.orderTime.setText(order.getDate());
        switch (type) {
            case "待确认":
                holder.cancel.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context).setMessage("是否取消此订单？").setNegativeButton("否", null).setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelOrder(position);
                            }
                        }).create().show();
                    }
                });
                break;
            case "进行中":
                holder.cancel.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                break;
            case "已完成":
            case "已取消":
                holder.cancel.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(position);
                    }
                });
                break;
        }
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));

        holder.payPrice.setText("¥" + String.format("%.2f", order.getAllPrice()));
        holder.orderNum.setText("" + order.getAllCount());
    }

    private void cancelOrder(int position) {

    }

    private void deleteOrder(int position) {

    }
    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderTime;
        TextView cancel;
        ImageButton delete;
        RecyclerView recyclerView;
        TextView payPrice;
        TextView orderNum;

        public ViewHolder(View itemView) {//itemView是 每项数据的根布局
            super(itemView);
            orderTime = (TextView) itemView.findViewById(R.id.time);
            cancel = (TextView) itemView.findViewById(R.id.cancel);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            orderNum = (TextView) itemView.findViewById(R.id.product_num);
            payPrice = (TextView) itemView.findViewById(R.id.pay);
        }
    }
}
