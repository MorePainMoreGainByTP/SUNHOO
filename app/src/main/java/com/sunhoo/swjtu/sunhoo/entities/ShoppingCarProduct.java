package com.sunhoo.swjtu.sunhoo.entities;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/7/21.
 */

public class ShoppingCarProduct extends DataSupport {
    private int id;
    protected int num;
    protected int serverId;
    protected String type;
    protected String modelID;
    protected String size;
    protected double price;
    protected String style;
    protected String room;
    protected int sales;
    protected boolean onSale;
    protected String url;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
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

    public void setModelID(String modelID) {
        this.modelID = modelID;
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

    public ShoppingCarProduct(int id, int num, int serverId, String type, String modelID, String size, double price, String style, String room, int sales, boolean onSale, String url) {
        this.id = id;
        this.num = num;
        this.serverId = serverId;
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

    public ShoppingCarProduct(Product product, int num) {
        this.num = num;
        this.serverId = product.getId();
        this.type = product.getType();
        this.modelID = product.getModelID();
        this.size = product.getSize();
        this.price = product.getPrice();
        this.style = product.getStyle();
        this.room = product.getRoom();
        this.sales = product.getSales();
        this.onSale = product.isOnSale();
        this.url = product.getUrl();
    }

    @Override
    public String toString() {
        return "ShoppingCarProduct{" +
                "id=" + id +
                ", num=" + num +
                ", serverId=" + serverId +
                ", type='" + type + '\'' +
                ", modelID='" + modelID + '\'' +
                ", size='" + size + '\'' +
                ", price=" + price +
                ", style='" + style + '\'' +
                ", room='" + room + '\'' +
                ", sales=" + sales +
                ", onSale=" + onSale +
                ", url='" + url + '\'' +
                "} " + super.toString();
    }

    public ShoppingCarProduct() {
    }
}
