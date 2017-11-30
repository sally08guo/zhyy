package com.tphy.zhyycs.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017\9\21 0021.
 */

public class ModelResult implements Serializable {

    private double Days;
    private String Name;
    private String TypeCode;
    private List<UserRange> lstUser;
    private int savedPosition=-1;

    public int getSavedPosition() {
        return savedPosition;
    }

    public void setSavedPosition(int savedPosition) {
        this.savedPosition = savedPosition;
    }

    public double getDays() {
        return Days;
    }

    public void setDays(double days) {
        Days = days;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(String typeCode) {
        TypeCode = typeCode;
    }

    public List<UserRange> getLstUser() {
        return lstUser;
    }

    public void setLstUser(List<UserRange> lstUser) {
        this.lstUser = lstUser;
    }
}
