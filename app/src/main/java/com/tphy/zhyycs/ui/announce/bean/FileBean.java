package com.tphy.zhyycs.ui.announce.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017\10\28 0028.
 */

public class FileBean implements Serializable {
    /* "FileName": "文件名",
            "FileBytes": "base64"*/
    private String FileName;
    private String FileBytes;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileBytes() {
        return FileBytes;
    }

    public void setFileBytes(String fileBytes) {
        FileBytes = fileBytes;
    }
}
