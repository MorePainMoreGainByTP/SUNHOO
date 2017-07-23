package com.sunhoo.swjtu.sunhoo.entities;

import java.io.Serializable;

/**
 * Created by tangpeng on 2017/7/18.
 */

public class User implements Serializable {
    private int id;
    private String userName;
    private String password;
    private String addressProvince;
    private String addressCity;
    private String addressDetail;
    private String phone;
    private boolean onUse;
    private String intro;
    private String channelId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddressProvince() {
        return addressProvince;
    }

    public void setAddressProvince(String addressProvince) {
        this.addressProvince = addressProvince;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isOnUse() {
        return onUse;
    }

    public void setOnUse(boolean onUse) {
        this.onUse = onUse;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public User() {
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public User(int id, String userName, String password, String addressProvince, String addressCity, String addressDetail, String phone, boolean onUse, String intro, String channelId) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.addressProvince = addressProvince;
        this.addressCity = addressCity;
        this.addressDetail = addressDetail;
        this.phone = phone;
        this.onUse = onUse;
        this.intro = intro;
        this.channelId = channelId;
    }
}
