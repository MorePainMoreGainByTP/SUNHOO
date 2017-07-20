package com.sunhoo.swjtu.sunhoo.entities;

import java.io.Serializable;

/**
 * Created by tangpeng on 2017/7/18.
 */

public class Product implements Serializable {
    private int id;
    private String type;
    private String modelID;
    private String size;
    private double price;
    private String style;
    private String room;
    private int sales;
    private boolean onSale;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModelID() {
        return modelID;
    }

    public void setModelID(String modelId) {
        this.modelID = modelId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Product(int id, String type, String modelID, String size, double price, String style, String room, int sales,
                   boolean onSale, String url) {
        super();
        this.id = id;
        this.type = type;
        this.modelID = modelID;
        this.size = size;
        this.price = price;
        this.style = style;
        this.room = room;
        this.sales = sales;
        this.onSale = onSale;
        this.url = url;
    }

    public Product() {
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", type=" + type + ", modelID=" + modelID + ", size=" + size + ", price=" + price
                + ", style=" + style + ", room=" + room + ", sales=" + sales + ", onSale=" + onSale + ", url=" + url
                + "]";
    }
}
