package com.sunhoo.swjtu.sunhoo.entities;

import java.io.Serializable;

/**
 * Created by tangpeng on 2017/7/21.
 */

public class OrderItem implements Serializable {
    private int id;
    private int orderID;
    private int productID;
    private int number;
    private double price;
    private double allPrice;
    private String note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    @Override
    public String toString() {
        return "OrderItem [id=" + id + ", orderID=" + orderID + ", productID=" + productID + ", number=" + number
                + ", price=" + price + ", allPrice=" + allPrice + ", note=" + note + "]";
    }

    public OrderItem(int id, int orderID, int productID, int number, double price, double allPrice, String note) {
        super();
        this.id = id;
        this.orderID = orderID;
        this.productID = productID;
        this.number = number;
        this.price = price;
        this.allPrice = allPrice;
        this.note = note;
    }

    public OrderItem() {
        // TODO Auto-generated constructor stub
    }
}
