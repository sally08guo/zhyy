package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\9\21 0021.
 */

public class ShenHeMen implements Serializable {


    private String IsCCUser;
    private String AppralUserCode;

    public String getIsCCUser() {
        return IsCCUser;
    }

    public void setIsCCUser(String isCCUser) {
        IsCCUser = isCCUser;
    }

    public String getAppralUserCode() {
        return AppralUserCode;
    }

    public void setAppralUserCode(String appralUserCode) {
        AppralUserCode = appralUserCode;
    }
}
