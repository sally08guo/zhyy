package com.tphy.zhyycs.server;

/*
 * 下载更新类
 * 1、完成新版本的下载
 * 2、提示安装新版本
 * 
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.work.adapter.LogAdpater;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.MyDialog;
import com.tphy.zhyycs.utils.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 微转诊 检查更新版本
 *
 * @author Administrator
 */
@SuppressLint("SdCardPath")
public class UpdateThread extends Thread {
    public static String download_dir = "/sdcard/download/";

    // 版本信息
    private String appVersion;
    private String newAppVersion;
    private int old_version, new_version;

    // 新版apk文件名
    private String name = "zsytyyb.apk";
    // 更新内容
    private String content = "";
    // 新版文件的大小，主要用来判断sd卡上的新版apk是否下载完整
    private String remark = "";

    private Context context;
    private Handler handler;
    private String comeFrom, type;
    UpdataInfo info;

    // UpdateThread的构造方法
    public UpdateThread(Context context, Handler handler,
                        String comeFrom, String type) {
        this.context = context;
        this.handler = handler;
        this.comeFrom = comeFrom;
        this.type = type;
        info = new UpdataInfo();
    }

    // run()方法
    @SuppressWarnings("unused")
    @Override
    public void run() {
        // 检查网络是否连接
        boolean bb = CallWebService.isNetworkAvailable(context);
        if (bb) {
            Looper.prepare();
            // 获取服务器版本信息
            AsyncTask<String, String, UpdataInfo> task = new AsyncTask<String, String, UpdataInfo>() {

                String retcode, retmsg;// 返回码,错误描述

                @Override
                protected UpdataInfo doInBackground(String... arg0) {

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    // 调用获取版本号的服务
                    Object object = CallWebService.callService("GetAppLastVersion", params, context,
                            DemoApplication.serviceUrl);
                    String result = "";
                    if (object != null) {
                        result = ((SoapObject) object).getProperty(0).toString();
                        if (result.equals("[]")) {
                        } else {
                            PackageManager manager = context.getPackageManager();
                            try {
                                PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
                                // 版本名
                                appVersion = packageInfo.versionName;
                                Log.e("appVersion", appVersion);
                                if (appVersion.length() > 3) {
                                    old_version = Integer.valueOf(appVersion.substring(0, 1)
                                            + appVersion.substring(2, 3) + appVersion.substring(4, 5));
                                } else {
                                    old_version = Integer
                                            .valueOf(appVersion.substring(0, 1) + appVersion.substring(2, 3));
                                }

                                Log.e("old_version", old_version + "");

                            } catch (NameNotFoundException e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                retcode = jsonObject.getString("success");
                                retmsg = jsonObject.getString("msg");
                                JSONArray ja2 = jsonObject.getJSONArray("result");
                                JSONObject userjobject = (JSONObject) ja2.get(0);
                                info.setDescription(userjobject.getString("APP_UPLOG"));
                                info.setUrl(userjobject.getString("APP_DOWNURL"));
                                info.setVersion(userjobject.getString("APP_VERSION"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return info;
                }

                protected void onPostExecute(final UpdataInfo result) {

                    if (result == null || result.getVersion() == null) {

                    } else {
                        newAppVersion = result.getVersion();
                        Log.e("newAppVersion", newAppVersion);

                        if (newAppVersion.length() > 3) {
                            new_version = Integer.valueOf(newAppVersion.substring(0, 1) + newAppVersion.substring(2, 3)
                                    + newAppVersion.substring(4, 5));
                        } else {
                            new_version = Integer
                                    .valueOf(newAppVersion.substring(0, 1) + newAppVersion.substring(2, 3));
                        }

                        Log.e("new_version", new_version + "");

                        if (new_version > old_version) {
                            // 提示对话框
                            // 判断新版本的文件是否已下载过,如果下载过则比较文件大小是否下载成功的文件，若不是则删除
                            final boolean b = Common.isExistForSdcard(context, download_dir + name, remark);

                            View view = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
                            final MyDialog dialog = new MyDialog(context, view, R.style.dialog);
                            ImageView iv = (ImageView) view.findViewById(R.id.iv_dismiss);
                            iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            TextView tv = (TextView) view.findViewById(R.id.tv_content);
                            if (b) {
                                tv.setText("您已下载最新版，请安装！");
                            } else {
                                tv.setText("更新内容：");
                            }
                            MyListView listView = (MyListView) view.findViewById(R.id.list_log);
                            String[] sstr = info.getDescription().split("-");
                            List<String> list = new ArrayList<String>();
                            for (int i = 0; i < sstr.length; i++) {
                                list.add(sstr[i]);
                            }
                            LogAdpater adpater = new LogAdpater(context, list);
                            listView.setAdapter(adpater);

                            Button btn = (Button) view.findViewById(R.id.btn_save);
                            btn.setText("开始更新");
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (b) {
                                        dialog.dismiss();
                                        Common.installFile(context, download_dir + name);
                                    } else {
                                        dialog.dismiss();
                                        if (type.equals("1")) {
                                            Intent it = new Intent("DownLoad_Login");
                                            context.sendBroadcast(it);
                                        } else if (type.equals("2")) {
                                            Intent it = new Intent("DownLoad");
                                            context.sendBroadcast(it);
                                        }
                                        // 新开下载线程
                                        DownThread downThread = new DownThread(context, handler,
                                                result.getUrl(), name, download_dir);
                                        downThread.start();
                                    }
                                }
                            });
                            dialog.show();


                        } else if ("grzx".equals(comeFrom)) {

                        }
                    }
                }
            }.execute();
        }
    }

}
