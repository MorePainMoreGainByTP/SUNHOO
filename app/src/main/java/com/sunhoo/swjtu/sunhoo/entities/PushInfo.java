package com.sunhoo.swjtu.sunhoo.entities;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by tangpeng on 2017/7/23.
 */

public class PushInfo extends DataSupport implements Serializable {
    protected int id;
    protected String information;
    protected String date;
    protected String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormatDate() {
        return new Timestamp(Long.parseLong(date)).toString();
    }

    public PushInfo() {
    }

    public PushInfo(int id, String information, String date, String title) {
        this.id = id;
        this.information = information;
        this.date = date;
        this.title = title;
    }

    @Override
    public String toString() {
        return "PushInfo{" +
                "id=" + id +
                ", information='" + information + '\'' +
                ", date='" + date + '\'' +
                ", title='" + title + '\'' +
                "} " + super.toString();
    }
}
