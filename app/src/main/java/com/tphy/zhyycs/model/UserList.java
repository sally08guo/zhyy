package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\11\22 0022.
 */

public class UserList implements Serializable {

    private String UserName;

    private String PhoneType;

    private String SendTime;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPhoneType() {
        return PhoneType;
    }

    public void setPhoneType(String phoneType) {
        PhoneType = phoneType;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        SendTime = sendTime;
    }
}
