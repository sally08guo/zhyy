package com.tphy.zhyycs.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017\9\18 0018.
 */

public class Prole implements Serializable {

    private String ProjectRoleCode;
    private List<String> lstUserCode;

    public String getProjectRoleCode() {
        return ProjectRoleCode;
    }

    public void setProjectRoleCode(String projectRoleCode) {
        ProjectRoleCode = projectRoleCode;
    }

    public List<String> getLstUserCode() {
        return lstUserCode;
    }

    public void setLstUserCode(List<String> lstUserCode) {
        this.lstUserCode = lstUserCode;
    }
}
