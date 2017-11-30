package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\10\18 0018.
 */

public class AttendInfo implements Serializable {
    /**
     * 编号
     */
    private String Code;
    /**
     * 开始时间
     */
    private String StartTime;
    /**
     * 结束时间
     */
    private String EndTime;
    /**
     * 提出人
     */
    private String SendUserName;
    /**
     * 职务
     */
    private String DutyName;
    /**
     * 发起时间
     */
    private String SendTime;
    /**
     * 类型
     */
    private String LeiBie;
    /**
     * 地点
     */
    private String Location;
    /**
     * 时长
     */
    private String LongTime;
    /**
     * 原因
     */
    private String Reason;
    /**
     * 审批人
     */
    private String AppralUserName;
    /**
     * 审批时间
     */
    private String CheckTime;
    /**
     * 0未审核，1通过，2不通过
     */
    private String IsCheck;
    /**
     * 审批意见
     */
    private String Remark;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
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

    public String getSendUserName() {
        return SendUserName;
    }

    public void setSendUserName(String sendUserName) {
        SendUserName = sendUserName;
    }

    public String getDutyName() {
        return DutyName;
    }

    public void setDutyName(String dutyName) {
        DutyName = dutyName;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        SendTime = sendTime;
    }

    public String getLeiBie() {
        return LeiBie;
    }

    public void setLeiBie(String leiBie) {
        LeiBie = leiBie;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLongTime() {
        return LongTime;
    }

    public void setLongTime(String longTime) {
        LongTime = longTime;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getAppralUserName() {
        return AppralUserName;
    }

    public void setAppralUserName(String appralUserName) {
        AppralUserName = appralUserName;
    }

    public String getCheckTime() {
        return CheckTime;
    }

    public void setCheckTime(String checkTime) {
        CheckTime = checkTime;
    }

    public String getIsCheck() {
        return IsCheck;
    }

    public void setIsCheck(String isCheck) {
        IsCheck = isCheck;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
