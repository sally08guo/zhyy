package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\10\20 0020.
 */

public class MonthAttendance implements Serializable {


    private String PName;
    private String Count;
    private int[] lstDay;

    public String getPName() {
        return PName;
    }

    public void setPName(String PName) {
        this.PName = PName;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public int[] getLstDay() {
        return lstDay;
    }

    public void setLstDay(int[] lstDay) {
        this.lstDay = lstDay;
    }
}
