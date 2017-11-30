package com.tphy.zhyycs.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017\9\21 0021.
 */

public class AttendanceRequest implements Serializable {


    private String TypeCode;
    private String StartTime;
    private String EndTime;
    private String Long;
    private String IsHoliday;
    private String Location;
    private String Reason;
    private List<ShenHeMen> lstAppral;

    public String getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(String typeCode) {
        TypeCode = typeCode;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getIsHoliday() {
        return IsHoliday;
    }

    public void setIsHoliday(String isHoliday) {
        IsHoliday = isHoliday;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public List<ShenHeMen> getLstAppral() {
        return lstAppral;
    }

    public void setLstAppral(List<ShenHeMen> lstAppral) {
        this.lstAppral = lstAppral;
    }
}
