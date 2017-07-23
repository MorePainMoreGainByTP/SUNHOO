package com.sunhoo.swjtu.sunhoo.entities;

/**
 * Created by tangpeng on 2017/7/23.
 */

public class PushInfoLocal extends PushInfo {
    private boolean read;
    private int serverId;

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public PushInfoLocal() {
    }

    public PushInfoLocal(PushInfo pushInfo) {
        this(pushInfo.getId(), pushInfo.getInformation(), pushInfo.getDate(), pushInfo.getTitle(), false, pushInfo.getId());
    }

    public PushInfoLocal(boolean read, int serverId) {
        this.read = read;
        this.serverId = serverId;
    }

    public PushInfoLocal(int id, String information, String date, String title, boolean read, int serverId) {
        super(id, information, date, title);
        this.read = read;
        this.serverId = serverId;
    }

    @Override
    public String toString() {
        return "PushInfoLocal{" +
                " id=" + getId() +
                ",date=" + getFormatDate() +
                ",information=" + getInformation() +
                ",title=" + getTitle() +
                ",read=" + read +
                ", serverId=" + serverId +
                "} ";
    }
}
