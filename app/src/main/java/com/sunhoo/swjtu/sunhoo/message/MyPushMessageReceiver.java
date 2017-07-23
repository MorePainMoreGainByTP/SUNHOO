package com.sunhoo.swjtu.sunhoo.message;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.google.gson.Gson;
import com.sunhoo.swjtu.sunhoo.Utils;
import com.sunhoo.swjtu.sunhoo.entities.PushInfo;
import com.sunhoo.swjtu.sunhoo.entities.PushInfoLocal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.sunhoo.swjtu.sunhoo.LoginActivity.BASE_URL;
import static com.sunhoo.swjtu.sunhoo.MainActivity.user;

/**
 * Created by tangpeng on 2017/7/23.
 */

public class MyPushMessageReceiver extends PushMessageReceiver {
    private static final String TAG = "MyPushMessageReceiver";
    private static final String URL = BASE_URL + "/AUpdateChannelId";

    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        //调用PushManager.startWork 后，sdk 将对 push server 发起绑定请求，这个过程是异步的。
        //如果您需要用单播推送，需要把这里获取的 channel id 上传到应用 server 中，再调用 server
        // 接口，用 channel id 给单个手机或者用户推送。
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.i(TAG, "onBind: " + responseString);
        if (errorCode == 0 && channelId != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String jsonStr = "{\"userId\":" + user.getId() + ",\"channelId\":" + channelId + "}";
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                Log.i(TAG, "jsonObject: " + jsonObject);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            int state = jsonObject.getInt("state");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, "返回的jsonObject: " + jsonObject);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
                requestQueue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * PushManager.stopWork() 的回调函数。
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
    }

    /**
     * setTags() 的回调函数。
     */
    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags + " requestId="
                + requestId;
    }

    /**
     * delTags() 的回调函数。
     */
    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode + " sucessTags="
                + sucessTags + " failTags=" + failTags + " requestId="
                + requestId;
    }

    /**
     * listTags() 的回调函数。
     */
    @Override
    public void onListTags(Context context, int errorCode,
                           List<String> tags, String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
    }

    /**
     * 接收透传消息的函数。
     */
    @Override
    public void onMessage(Context context, String message, String customContentString) {
        String messageString = "透传消息 message=" + message + " customContentString="
                + customContentString;
        Log.i(TAG, messageString);
        // 自定义内容获取方式，mykey 和 myvalue 对应透传消息推送时自定义内容中设置的键和值
        if (customContentString != null & customContentString != "") {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收通知点击的函数。
     */
    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {

        String notifyString = "通知点击 title=" + title + " description="
                + description + " customContent=" + customContentString;
        Log.i(TAG, notifyString);
        if (user != null && !Utils.activityExits(MessageActivity.class.getSimpleName())) {
            Log.i(TAG, "onNotificationClicked: 1");
            Intent intent = new Intent(context,MessageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.i(TAG, "onNotificationClicked: 2");
        }else{
            Log.i(TAG, "onNotificationClicked: 用户未初始化");
        }
/*
        String notifyString = "通知点击 title=" + title + " description="
                + description + " customContent=" + customContentString;

        // 自定义内容获取方式，mykey 和 myvalue 对应通知推送时自定义内容中设置的 键和值
        if (customContentString != null & customContentString != "") {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        */
    }

    /**
     * 接收通知到达的函数。
     *
     * @param context             上下文
     * @param title               推送的通知的标题
     * @param description         推送的通知的描述
     * @param customContentString 自定义内容，为空或者 json 字符串
     */
    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "通知到达 title=" + title + " description="
                + description + " customContent=" + customContentString;
        Log.i(TAG, notifyString);
        // 自定义内容获取方式，mykey 和 myvalue 对应通知推送时自定义内容中设置的键和值
        if (customContentString != null & customContentString != "") {
            Gson gson = new Gson();
            PushInfo pushInfo = gson.fromJson(customContentString, PushInfo.class);
            PushInfoLocal pushInfoLocal = new PushInfoLocal(pushInfo);
            if(pushInfoLocal.save()){
                Log.i(TAG, "onNotificationArrived: 保存消息成功");
            }else{
                Log.i(TAG, "onNotificationArrived: 保存消息失败");
            }
        }
    }
}
