package com.sunhoo.swjtu.sunhoo.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by tangpeng on 2017/7/21.
 */

public class Order implements Serializable {
    private int id;
    private String date;
    private double allPrice;
    private String note;    //备注
    private int userID;
    private int allCount;
    private String type;
    private String customer;
    private String customerPhone;
    private String address;
    private List<String> urls;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public double getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(double allPrice) {
        this.allPrice = allPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Order() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFormatDate() {
        return new Timestamp(Long.parseLong(date)).toString();
    }

    public String getFormatPrice() {
        return "¥" + String.format("%.2f", allPrice);
    }

    public String getProductNumStr() {
        return "共" + allCount + "件商品";
    }

}
