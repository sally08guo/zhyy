package com.tphy.zhyycs.ui.announce.bean;
import java.io.Serializable;
import java.util.List;

public class NewAnnounce implements Serializable {


    private String LoginUserCode;
    private String Title;
    private String Content;
    private String Type;
    private String range;
    private List<FileBean> lstFile;


    public String getLoginUserCode() {
        return LoginUserCode;
    }

    public void setLoginUserCode(String loginUserCode) {
        LoginUserCode = loginUserCode;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public List<FileBean> getLstFile() {
        return lstFile;
    }

    public void setLstFile(List<FileBean> lstFile) {
        this.lstFile = lstFile;
    }
}
