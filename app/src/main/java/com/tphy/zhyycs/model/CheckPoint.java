package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\9\19 0019.
 */

public class CheckPoint implements Serializable {

    private String TemplateCode;
    private String ExecutUserCode;

    public String getTemplateCode() {
        return TemplateCode;
    }

    public void setTemplateCode(String templateCode) {
        TemplateCode = templateCode;
    }

    public String getExecutUserCode() {
        return ExecutUserCode;
    }

    public void setExecutUserCode(String executUserCode) {
        ExecutUserCode = executUserCode;
    }
}
