package com.sunhoo.swjtu.sunhoo.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunhoo.swjtu.sunhoo.R;
import com.sunhoo.swjtu.sunhoo.entities.PushInfoLocal;
import com.sunhoo.swjtu.sunhoo.message.DetailMessageActivity;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by tangpeng on 2017/3/5.
 */

public class PushInfoLocalAdapter extends RecyclerView.Adapter<PushInfoLocalAdapter.ViewHolder> {

    private List<PushInfoLocal> pushLocalList;
    private Context context;

    public PushInfoLocalAdapter(List<PushInfoLocal> pushLocalList) {
        this.pushLocalList = pushLocalList;
    }

    @Override
    public PushInfoLocalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null)
            context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_push_info, parent, false);
        return new ViewHolder(view);
    }

    //每次item进入屏幕时 调用
    @Override
    public void onBindViewHolder(final PushInfoLocalAdapter.ViewHolder holder, final int position) {
        final PushInfoLocal pushLocal = pushLocalList.get(position);
        holder.title.setText(pushLocal.getTitle());
        holder.content.setText(pushLocal.getInformation());
        holder.time.setText(pushLocal.getFormatDate());
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DetailMessageActivity.class).putExtra("pushLocal", pushLocal));
                holder.redPoint.setVisibility(View.INVISIBLE);
            }
        });
        if (pushLocal.isRead()) {
            holder.redPoint.setVisibility(View.INVISIBLE);
        } else {
            holder.redPoint.setVisibility(View.VISIBLE);
        }
        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popupWindow(position);
                return true;
            }
        });
    }

    PopupWindow popupWindow;

    private void popupWindow(final int position) {
        popupWindow = new PopupWindow(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View parent = layoutInflater.inflate(R.layout.activity_message, null);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        View contentView = layoutInflater.inflate(R.layout.popup_push_local, null);
        Button delete = (Button) contentView.findViewById(R.id.delete);
        Button cancel = (Button) contentView.findViewById(R.id.cancel);
        popupWindow.setContentView(contentView);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setMessage("是否删除该通知？").setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                    }
                }).setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                }).create().show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    private void deleteMessage(int position) {
        DataSupport.delete(PushInfoLocal.class, pushLocalList.get(position).getId());
        pushLocalList.remove(position);
        notifyDataSetChanged();
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    public int getItemCount() {
        return pushLocalList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout item;
        ImageView redPoint;
        TextView title;
        TextView content;
        TextView time;
        TextView detail;

        public ViewHolder(View itemView) {//itemView是 每项数据的根布局
            super(itemView);
            item = (RelativeLayout) itemView;
            redPoint = (ImageView) itemView.findViewById(R.id.red_point);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            time = (TextView) itemView.findViewById(R.id.time);
            detail = (TextView) itemView.findViewById(R.id.detail);
        }
    }
}
