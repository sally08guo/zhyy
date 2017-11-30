package com.tphy.zhyycs.server;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownThread extends Thread {

    private String url;
    private String name;
    private String path;

    private Context context;
    private Handler handler;

    private int fileSize;
    private int downLoadFileSize;
    private String fileString;

    public DownThread(Context context, Handler handler, String url, String name, String path) {
        this.context = context;
        this.handler = handler;
        this.url = url;
        this.name = name;
        this.path = path;
    }

    @Override
    public void run() {
        Looper.prepare();
        try {
            downFile(url, name, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Looper.loop();
    }

    // 下载
    private void downFile(String url, String name, String path) throws IOException {
        URL myURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
        try {
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();
        } catch (Exception e) {
            sendMsg(-1, null, 0, 0);
            Toast.makeText(context, "网络连接超时！请稍候重试！", Toast.LENGTH_SHORT).show();
        }
        int state = conn.getResponseCode();
        if (state == 200) {
            boolean flag = false;
            InputStream is = conn.getInputStream();
            // 根据http头部属性获取文件大小
            fileSize = conn.getContentLength();
            if (fileSize <= 0) {
                Toast.makeText(context, "无法获知文件大小", Toast.LENGTH_SHORT).show();
            }
            if (is == null) {
                Toast.makeText(context, "stream is null", Toast.LENGTH_SHORT).show();
            }
            if (!flag) {
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                try {
                    fileString = path + name;
                    File myTempFile = new File(fileString);
                    myTempFile.createNewFile();

                    FileOutputStream fos = new FileOutputStream(myTempFile);
                    // 把数据存入路径+文件名
                    byte buf[] = new byte[1024];
                    downLoadFileSize = 0;
                    // *******************************更新通知*******************************
                    sendMsg(0, null, 0, 0);

                    int tmpNumread = 0;
                    do {
                        // 循环读取
                        int numread = is.read(buf);
                        if (numread == -1) {
                            break;
                        }
                        fos.write(buf, 0, numread);
                        tmpNumread += numread;
                        downLoadFileSize += numread;
                        // 这里减少更新时间
                        if (tmpNumread > 20000) {
                            // *******************************更新通知进度条***************************
                            sendMsg(1, null, downLoadFileSize, fileSize);
                            tmpNumread = 0;
                        }
                    } while (true);
                    is.close();
                    // 更新通知下载完成
                    sendMsg(2, fileString, downLoadFileSize, fileSize);
                    if (null != conn) {
                        conn.disconnect();
                    }
                } catch (Exception ex) {
                    Toast.makeText(context, "请查看应用存储权限是否开启！", Toast.LENGTH_SHORT).show();
                    sendMsg(-1, null, 0, 0);
                }
            } else {
                fileSize = 0;
                flag = true;
            }
        } else {
            Toast.makeText(context, "网络连接失败！请稍候重试！", Toast.LENGTH_SHORT).show();
            sendMsg(-1, null, 0, 0);
        }
    }

    private void sendMsg(int flag, String fileString, int downLoadFileSize, int fileSize) {
        Message msg = new Message();
        msg.what = flag;
        Bundle bundle = new Bundle();
        bundle.putString("fileString", fileString);
        bundle.putInt("downLoadFileSize", downLoadFileSize);
        bundle.putInt("fileSize", fileSize);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
