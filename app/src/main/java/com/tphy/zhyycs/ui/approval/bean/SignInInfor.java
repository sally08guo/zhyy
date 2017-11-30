package com.tphy.zhyycs.ui.approval.bean;

/**
 * Created by Administrator on 2017\11\20 0020.
 */

public class SignInInfor {

    /*"IsScreen":false,//是否大屏
                "Name": "崔杉杉",
                "SendTime": "",//签到时间
                "PhoneType": ""//苹果P、安卓A、补签B*/

    private boolean IsScreen;
    private String Name;
    private String SendTime;
    private String PhoneType;

    public boolean isScreen() {
        return IsScreen;
    }

    public void setScreen(boolean screen) {
        IsScreen = screen;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        SendTime = sendTime;
    }

    public String getPhoneType() {
        return PhoneType;
    }

    public void setPhoneType(String phoneType) {
        PhoneType = phoneType;
    }
}
