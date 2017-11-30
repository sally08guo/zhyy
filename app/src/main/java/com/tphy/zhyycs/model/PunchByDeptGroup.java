package com.tphy.zhyycs.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017\11\22 0022.
 */

public class PunchByDeptGroup implements Serializable {

    /**
     * 部门
     */
    private String DeptName;

    private List<PunchByDeptItem> TypeList;

    public String getDeptName() {
        return DeptName;
    }

    public void setDeptName(String deptName) {
        DeptName = deptName;
    }

    public List<PunchByDeptItem> getTypeList() {
        return TypeList;
    }

    public void setTypeList(List<PunchByDeptItem> typeList) {
        TypeList = typeList;
    }
}
