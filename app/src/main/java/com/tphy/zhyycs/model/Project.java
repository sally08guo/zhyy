package com.tphy.zhyycs.model;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称：ChatDemoUI3.0
 * 创建人：cdss
 * 创建时间：2017-08-17 11:26
 * 修改人：cdss
 * 修改时间：2017-08-17 11:26
 * 修改备注：
 */

public class Project implements Serializable {

    /**
     * 项目名
     */
    private String Name;
    /**
     * 创建人
     */
    private String SendUserName;
    /**
     * 创建时间
     */
    private String SendTime;
    /**
     * 里程碑名称
     */
    private String pmsName;
    /**
     * 项目代码
     */
    private String Code;
    /**
     * 简拼
     */
    private String PYDM;
    /**
     * 项目内容
     */
    private String Content;
    /**
     * 项目成员
     */
    private List<User> list_user;

    /**
     * 星级
     */
    private String Star;

    /**
     * 待办任务数
     */
    private String ActionRequiredCount;

    /**
     * 待审核任务数
     */
    private String CheckRequiredCount;

    /**
     * 所有项目下任务数
     */
    private String AllCount;

    private String IsCurrent;

    private String StatusName;

    private String PMCode;

    /**
     * 关注 1关注 0不关注
     */
    private String IsAttention;

    private String ProjectPermission;

    public String getProjectPermission() {
        return ProjectPermission;
    }

    public void setProjectPermission(String projectPermission) {
        ProjectPermission = projectPermission;
    }

    public String getIsAttention() {
        return IsAttention;
    }

    public void setIsAttention(String isAttention) {
        IsAttention = isAttention;
    }

    public String getAllCount() {
        return AllCount;
    }

    public void setAllCount(String allCount) {
        AllCount = allCount;
    }

    public String getIsCurrent() {
        return IsCurrent;
    }

    public void setIsCurrent(String isCurrent) {
        IsCurrent = isCurrent;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getPMCode() {
        return PMCode;
    }

    public void setPMCode(String PMCode) {
        this.PMCode = PMCode;
    }

    public String getPmsName() {
        return pmsName;
    }

    public void setPmsName(String pmsName) {
        this.pmsName = pmsName;
    }

    public String getActionRequiredCount() {
        return ActionRequiredCount;
    }

    public void setActionRequiredCount(String actionRequiredCount) {
        ActionRequiredCount = actionRequiredCount;
    }

    public String getCheckRequiredCount() {
        return CheckRequiredCount;
    }

    public void setCheckRequiredCount(String checkRequiredCount) {
        CheckRequiredCount = checkRequiredCount;
    }

    public String getStar() {
        return Star;
    }

    public void setStar(String star) {
        Star = star;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSendUserName() {
        return SendUserName;
    }

    public void setSendUserName(String sendUserName) {
        SendUserName = sendUserName;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        SendTime = sendTime;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getPYDM() {
        return PYDM;
    }

    public void setPYDM(String PYDM) {
        this.PYDM = PYDM;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public List<User> getList_user() {
        return list_user;
    }

    public void setList_user(List<User> list_user) {
        this.list_user = list_user;
    }
}
