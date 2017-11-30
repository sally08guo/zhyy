package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\9\19 0019.
 */

public class AnnounceMentResult implements Serializable {

    private String Title;
    private String Content;
    private String SendTime;
    private String SendUserName;
    private String StickTime;
    private String isMe;
    private String Code;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
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

    public String getStickTime() {
        return StickTime;
    }

    public void setStickTime(String stickTime) {
        StickTime = stickTime;
    }

    public String getIsMe() {
        return isMe;
    }

    public void setIsMe(String isMe) {
        this.isMe = isMe;
    }
}
