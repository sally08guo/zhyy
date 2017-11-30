package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\9\21 0021.
 */

public class Information implements Serializable {

    private String Code;

    private String Content;

    private String SendTime;

    private String Type;

    private String ProjectName;

    private String ProductName;

    private String ProjectCode;

    private String ProductCode;

    private String IsCPCheck;

    private String CPCheckUserCode;

    private String CPCheckUserName;

    private String StatusName;

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getCPCheckUserName() {
        return CPCheckUserName;
    }

    public void setCPCheckUserName(String CPCheckUserName) {
        this.CPCheckUserName = CPCheckUserName;
    }

    public String getCPCheckUserCode() {
        return CPCheckUserCode;
    }

    public void setCPCheckUserCode(String CPCheckUserCode) {
        this.CPCheckUserCode = CPCheckUserCode;
    }

    public String getIsCPCheck() {
        return IsCPCheck;
    }

    public void setIsCPCheck(String isCPCheck) {
        IsCPCheck = isCPCheck;
    }

    public String getProjectCode() {
        return ProjectCode;
    }

    public void setProjectCode(String projectCode) {
        ProjectCode = projectCode;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
