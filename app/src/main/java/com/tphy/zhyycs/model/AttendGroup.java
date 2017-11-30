package com.tphy.zhyycs.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017\11\7 0007.
 */

public class AttendGroup implements Serializable {

    private String Count;

    private String TypeName;

    private List<AttendChild> PunchList;


    public List<AttendChild> getPunchList() {
        return PunchList;
    }

    public void setPunchList(List<AttendChild> punchList) {
        PunchList = punchList;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }
}
