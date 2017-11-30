package com.tphy.zhyycs.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\10\12 0012.
 */

public class Logg implements Serializable {

    private String Record;   // 任务步骤

    public String getRecord() {
        return Record;
    }

    public void setRecord(String record) {
        Record = record;
    }
}
