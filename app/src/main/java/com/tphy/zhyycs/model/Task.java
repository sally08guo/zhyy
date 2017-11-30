package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * 项目名称：ChatDemoUI3.0
 * 创建人：cdss
 * 创建时间：2017-08-16 14:58
 * 修改人：cdss
 * 修改时间：2017-08-16 14:58
 * 修改备注：
 */

public class Task implements Serializable {

    /**
     * 任务ID
     */
    private String Code;
    /**
     * 任务描述
     */
    private String Content;
    /**
     * 提出人
     */
    private String SendUserName;
    /**
     * 计划时效
     */
    private String PlanDate;
    /**
     * 提出时间
     */
    private String SendTime;
    /**
     * 任务状态
     */
    private String StatusName;
    /**
     * 发任务时带的附件
     */
    private String SendFile;

    private String SendFileUrl;
    /**
     * 检查点执行人
     */
    private String ExecutUserName;

    /**
     * 检查点执行人code
     */
    private String ExecutUserCode;

    /**
     * 检查点名称
     */
    private String CheckPointName;

    private String CpCode;

    /**
     * 检查点状态0未做，1正在做，2已做
     */
    private String IsCurrent;

    /**
     * 检查点开始时间
     */
    private String StartTime;

    /**
     * 检查点结束时间
     */
    private String EndTime;

    /**
     * 里程碑名字
     */
    private String ProjectMilestoneName;

    private String TaskType;

    /**
     * 项目名称
     */
    private String ProjectName;

    /**
     * 模块名称
     */
    private String ProductName;

    private String ProjectCode;

    private String ProductCode;

    private String IsCPCheck;

    private String IsCheck;

    private String CPCheckUserCode;

    private String CPCheckUserName;

    public String getExecutUserCode() {
        return ExecutUserCode;
    }

    public void setExecutUserCode(String executUserCode) {
        ExecutUserCode = executUserCode;
    }

    public String getCPCheckUserCode() {
        return CPCheckUserCode;
    }

    public void setCPCheckUserCode(String CPCheckUserCode) {
        this.CPCheckUserCode = CPCheckUserCode;
    }

    public String getCPCheckUserName() {
        return CPCheckUserName;
    }

    public void setCPCheckUserName(String CPCheckUserName) {
        this.CPCheckUserName = CPCheckUserName;
    }

    public String getIsCheck() {
        return IsCheck;
    }

    public void setIsCheck(String isCheck) {
        IsCheck = isCheck;
    }

    public String getIsCPCheck() {
        return IsCPCheck;
    }

    public void setIsCPCheck(String isCPCheck) {
        IsCPCheck = isCPCheck;
    }

    public String getSendFileUrl() {
        return SendFileUrl;
    }

    public void setSendFileUrl(String sendFileUrl) {
        SendFileUrl = sendFileUrl;
    }

    public String getCpCode() {
        return CpCode;
    }

    public void setCpCode(String cpCode) {
        CpCode = cpCode;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getSendFile() {
        return SendFile;
    }

    public void setSendFile(String sendFile) {
        SendFile = sendFile;
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

    public String getTaskType() {
        return TaskType;
    }

    public void setTaskType(String taskType) {
        TaskType = taskType;
    }

    public String getProjectMilestoneName() {
        return ProjectMilestoneName;
    }

    public void setProjectMilestoneName(String projectMilestoneName) {
        ProjectMilestoneName = projectMilestoneName;
    }

    public String getCheckPointName() {
        return CheckPointName;
    }

    public void setCheckPointName(String checkPointName) {
        CheckPointName = checkPointName;
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

    public String getSendUserName() {
        return SendUserName;
    }

    public void setSendUserName(String sendUserName) {
        SendUserName = sendUserName;
    }

    public String getPlanDate() {
        return PlanDate;
    }

    public void setPlanDate(String planDate) {
        PlanDate = planDate;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        SendTime = sendTime;
    }

    public String getExecutUserName() {
        return ExecutUserName;
    }

    public void setExecutUserName(String executUserName) {
        ExecutUserName = executUserName;
    }

    public String getIsCurrent() {
        return IsCurrent;
    }

    public void setIsCurrent(String isCurrent) {
        IsCurrent = isCurrent;
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
}
