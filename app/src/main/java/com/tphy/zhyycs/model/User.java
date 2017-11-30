package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * 项目名称：ChatDemoUI3.0
 * 创建人：cdss
 * 创建时间：2017-08-15 17:56
 * 修改人：cdss
 * 修改时间：2017-08-15 17:56
 * 修改备注：
 */

public class User implements Serializable {

    private String Code;// ID
    private String Name;// 姓名
    private String PYDM;
    private String PHONENUMBER;// 电话
    private String DCODE;// 部门代码
    private String DUTY;// 职务ID
    private String DutyCode;

    private boolean checked;

    public String getDutyCode() {
        return DutyCode;
    }

    public void setDutyCode(String dutyCode) {
        DutyCode = dutyCode;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPYDM() {
        return PYDM;
    }

    public void setPYDM(String PYDM) {
        this.PYDM = PYDM;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public String getPHONENUMBER() {
        return PHONENUMBER;
    }

    public void setPHONENUMBER(String PHONENUMBER) {
        this.PHONENUMBER = PHONENUMBER;
    }

    public String getDCODE() {
        return DCODE;
    }

    public void setDCODE(String DCODE) {
        this.DCODE = DCODE;
    }

    public String getDUTY() {
        return DUTY;
    }

    public void setDUTY(String DUTY) {
        this.DUTY = DUTY;
    }
}
