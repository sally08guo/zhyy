package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\9\21 0021.
 */

public class UserRange implements Serializable {

    private String  Code;
    private String  Name;

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
}
