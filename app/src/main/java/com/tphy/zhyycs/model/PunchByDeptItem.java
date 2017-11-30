package com.tphy.zhyycs.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017\11\22 0022.
 */

public class PunchByDeptItem implements Serializable {

    /**
     * 打卡类型
     */
    private String TypeName;

    private String DeptName;

    /**
     * 打卡人员
     */
    private List<UserList> UserList;

    /**
     * 签到人数
     */
    private String AllCount;

    /**
     * 补签人数
     */
    private String BQCount;


    public String getAllCount() {
        return AllCount;
    }

    public void setAllCount(String allCount) {
        AllCount = allCount;
    }

    public String getBQCount() {
        return BQCount;
    }

    public void setBQCount(String BQCount) {
        this.BQCount = BQCount;
    }

    public String getDeptName() {
        return DeptName;
    }

    public void setDeptName(String deptName) {
        DeptName = deptName;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public List<com.tphy.zhyycs.model.UserList> getUserList() {
        return UserList;
    }

    public void setUserList(List<com.tphy.zhyycs.model.UserList> userList) {
        UserList = userList;
    }
}
