package com.sunhoo.swjtu.sunhoo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.sunhoo.swjtu.sunhoo.entities.Product;
import com.sunhoo.swjtu.sunhoo.entities.ShoppingCarProduct;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/7/20.
 */

public class Utils {
    private static final String TAG = "Utils";
    public static List<Activity> activities = new ArrayList<>();

    public static ProgressDialog getProgressDialog(Context context, String title, String msg) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(msg);
        if (title != null)
            dialog.setTitle(title);
        dialog.create();
        return dialog;
    }

    public static void addProduct2ShoppingCar(Product product, int num) {
        ShoppingCarProduct shoppingCarProduct = new ShoppingCarProduct(product, num);
        Log.i(TAG, "shoppingCarProduct: " + shoppingCarProduct);
        List<ShoppingCarProduct> oldShoppingCarProducts = DataSupport.select("*").where("serverId = ?", "" + shoppingCarProduct.getServerId()).find(ShoppingCarProduct.class);
        ShoppingCarProduct oldShoppingCarProduct = null;
        if (oldShoppingCarProducts.size() > 0)
            oldShoppingCarProduct = oldShoppingCarProducts.get(0);
        Log.i(TAG, "oldShoppingCarProduct: " + oldShoppingCarProduct);
        if (oldShoppingCarProduct != null) {
            oldShoppingCarProduct.setNum(oldShoppingCarProduct.getNum() + shoppingCarProduct.getNum());
            oldShoppingCarProduct.update(oldShoppingCarProduct.getId());
        } else {
            shoppingCarProduct.save();
        }
    }

    public static List<Activity> getActivities() {
        return activities;
    }

    public static void setActivities(List<Activity> activities) {
        Utils.activities = activities;
    }

    public static void removeActivity(Activity activity) {
        for (Activity activity1 : activities) {
            if (activity1 == activity) {
                activities.remove(activity);
                break;
            }
        }
    }

    public static void addActivity(Activity activity) {
        if(!activityExits(activity.getClass().getSimpleName()))
            activities.add(activity);
    }

    public static boolean activityExits(String activityName) {
        for (Activity activity1 : activities) {
            if (activity1.getClass().getSimpleName().equals(activityName)) {
                return true;
            }
        }
        return false;
    }
}
