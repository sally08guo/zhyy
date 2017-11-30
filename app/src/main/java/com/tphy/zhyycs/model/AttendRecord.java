package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\11\9 0009.
 */

public class AttendRecord implements Serializable {
    private String Code;

    private String SendTime;

    private String SendUserName;

    private String TypeName;

    private String StatusName;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        SendTime = sendTime;
    }

    public String getSendUserName() {
        return SendUserName;
    }

    public void setSendUserName(String sendUserName) {
        SendUserName = sendUserName;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }
}
