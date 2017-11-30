package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\9\18 0018.
 */

public class Role implements Serializable {

    private String Code;
    private String Name;
    private String PYDM;


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
}
