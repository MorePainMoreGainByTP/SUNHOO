package com.sunhoo.swjtu.sunhoo.entities;

/**
 * Created by tangpeng on 2017/7/22.
 */

public class DetailOrder extends Product {
    protected int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public DetailOrder(Product product, int num) {
        this(product.getId(), product.getType(), product.getModelID(), product.getSize(), product.getPrice(), product.getStyle(), product.getRoom(), product.getSales(), product.isOnSale(), product.getUrl(), num);
    }


    public DetailOrder(int id, String type, String modelID, String size, double price, String style, String room, int sales, boolean onSale, String url, int num) {
        super(id, type, modelID, size, price, style, room, sales, onSale, url);
        this.num = num;
    }

    public String getFormatPrice() {
        return "¥" + String.format("%.2f", price);
    }
}
