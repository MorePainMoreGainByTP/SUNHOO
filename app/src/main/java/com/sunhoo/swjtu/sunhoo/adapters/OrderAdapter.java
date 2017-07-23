package com.sunhoo.swjtu.sunhoo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.Utils;
import com.sunhoo.swjtu.sunhoo.entities.Order;
import com.sunhoo.swjtu.sunhoo.order.DetailOrderActivity;
import com.sunhoo.swjtu.sunhoo.order.OrderActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;

/**
 * Created by tangpeng on 2017/3/5.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private static final String URL = BASE_URL + "/AUpdateOrder";
    private List<Order> orderList;
    private Context context;
    private String type;
    private ProgressDialog progressDialog;
    RequestQueue requestQueue;

    public OrderAdapter(List<Order> orderList, String type, Context context) {
        this.orderList = orderList;
        this.type = type;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://开启对话框
                    if (progressDialog == null) {
                        progressDialog = Utils.getProgressDialog(context, null, "操作中...");
                    }
                    progressDialog.show();
                    break;
                case 1:
                    Toast.makeText(context, "取消成功", Toast.LENGTH_SHORT).show();
                    orderList.remove(msg.arg1);
                    notifyDataSetChanged();
                    dismissDialog();
                    break;
                case 2:
                    Toast.makeText(context, "取消失败", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    break;
                case 3:
                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                    orderList.remove(msg.arg1);
                    notifyDataSetChanged();
                    dismissDialog();
                    break;
                case 4:
                    Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    break;
                case 5:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    break;
            }
        }
    };

    private void dismissDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    //每次item进入屏幕时 调用
    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, final int position) {
        final Order order = orderList.get(position);
        holder.orderTime.setText(order.getFormatDate());
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
                        new AlertDialog.Builder(context).setMessage("是否删除该订单？").setNegativeButton("否", null).setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteOrder(position);
                            }
                        }).create().show();
                    }
                });
                break;
        }
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(new ImgGalleryAdapter(order.getUrls()));
        holder.payPrice.setText(order.getFormatPrice());
        holder.orderNum.setText(order.getProductNumStr());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OrderActivity) context).startActivityForResult(new Intent(context, DetailOrderActivity.class).putExtra("order", order)
                        .putExtra("position",position).putExtra("type",type), 1);
            }
        });
    }

    private void cancelOrder(final int position) {
        doSomething(position, "已取消", 1, 2);
    }

    public void cancelOrder2(int position) {
        Message message = new Message();
        message.arg1 = position;
        message.what = 1;
        handler.sendMessage(message);
    }

    private void deleteOrder(int position) {
        doSomething(position, "已删除", 3, 4);
    }

    public void deleteOrder2(int position) {
        Message message = new Message();
        message.arg1 = position;
        message.what = 3;
        handler.sendMessage(message);
    }

    private void doSomething(final int position, final String newType, final int signal1, final int signal2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
                Gson gson = new Gson();
                try {
                    Map<Object, Object> map = new HashMap<>();
                    map.put("order", orderList.get(position));
                    map.put("newType", newType);
                    JSONObject jsonObject = new JSONObject(gson.toJson(map));
                    Log.i(TAG, "jsonObject: " + jsonObject);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            int state = 0;
                            try {
                                state = jsonObject.getInt("state");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (state == 0) {
                                handler.sendEmptyMessage(signal2);
                            } else if (state == 1) {
                                Message message = new Message();
                                message.what = signal1;
                                message.arg1 = position;
                                handler.sendMessage(message);
                            }
                            Log.i(TAG, "返回的jsonObject: " + jsonObject);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            handler.sendEmptyMessage(5);
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(5);
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView orderTime;
        TextView cancel;
        ImageButton delete;
        RecyclerView recyclerView;
        TextView payPrice;
        TextView orderNum;

        public ViewHolder(View itemView) {//itemView是 每项数据的根布局
            super(itemView);
            item = (LinearLayout) itemView;
            orderTime = (TextView) itemView.findViewById(R.id.time);
            cancel = (TextView) itemView.findViewById(R.id.cancel);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            orderNum = (TextView) itemView.findViewById(R.id.product_num);
            payPrice = (TextView) itemView.findViewById(R.id.pay);
        }
    }
}
