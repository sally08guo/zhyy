package com.tphy.zhyycs;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.tphy.zhyycs.ui.announce.AnnouceDetailsActivity;
import com.tphy.zhyycs.ui.attandence.AttendInfoActivity;
import com.tphy.zhyycs.ui.attandence.QRCodeActivity;
import com.tphy.zhyycs.ui.work.activity.TaskDetailsActivity;
import com.tphy.zhyycs.ui.work.activity.TaskReviewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017\9\20 0020.
 */

public class MyJPushReceiver extends BroadcastReceiver {
    private static final String TAG = "TalkReceiver";
    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Log.d(TAG, "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
                .getAction())) {
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
            String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);

            System.out.println("收到了自定义消息@@消息内容是:" + content);
            System.out.println("收到了自定义消息@@消息extra是:" + extra);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
                .getAction())) {
            System.out.println("收到了通知");
            String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            System.out.println("收到了自定义:" + content);
            System.out.println("收到了自定义:" + extra);
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
                .getAction())) {
            String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String Module = "", Code = "", Type = "", ProjectCode = "";
            //**************解析推送过来的json数据并存放到集合中 begin******************
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(extra);
                Module = jsonObject.getString("Module");
                Code = jsonObject.getString("Code");
                Type = jsonObject.getString("Type");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (Module.equals("Task")) {   //任务
                if (Type.equals("Check")) { //审核
                    try {
                        ProjectCode = jsonObject.getString("ProjectCode");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Intent it = new Intent(context, TaskReviewActivity.class);
                    it.putExtra("TaskCode", Code);
                    it.putExtra("ProjectCode", ProjectCode);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                } else if (Type.equals("Execute")) {  //待办任务
                    Intent it = new Intent(context, TaskDetailsActivity.class);
                    it.putExtra("task_type", "1");
                    it.putExtra("TaskCode", Code);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                } else if (Type.equals("Read")) {  // 查看详情
                    Intent it = new Intent(context, TaskDetailsActivity.class);
                    it.putExtra("TaskCode", Code);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                }
            } else if (Module.equals("Notice")) {  // 公告
                if (Type.equals("Read")) {  //读
                    Intent it = new Intent(context, AnnouceDetailsActivity.class);
                    it.putExtra("NoticeCode", Code);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                }

            } else if (Module.equals("Attendance")) { // 考勤
                if (Type.equals("Check")) {  // 审核
                    Intent it = new Intent(context, AttendInfoActivity.class);
                    it.putExtra("task_code", Code);
                    it.putExtra("type", "1");
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                }
//                else if (Type.equals("Read")) {   // 读
//                    Intent it = new Intent(context, ApproveInfoActivity.class);
//                    it.putExtra("code", Code);
//                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(it);
//                } else if (Type.equals("CC")) {   // 抄送
//                    Intent it = new Intent(context, ApproveInfoActivity.class);
//                    it.putExtra("code", Code);
//                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(it);
//                }
            } else if (Module.equals("QRCodePunch")) {
                if (Type.equals("Execute")) {
                    Intent qrIntent = new Intent(context, QRCodeActivity.class);
                    qrIntent.putExtra("userQRCodeCode", Code);
                    qrIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(qrIntent);
                } else if (Type.equals("OneHasPunch")) {
                    Intent qrIntent = new Intent(context, QRCodeActivity.class);
                    qrIntent.putExtra("userQRCodeCode", Code);
                    qrIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(qrIntent);
                }
            }

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }
}
