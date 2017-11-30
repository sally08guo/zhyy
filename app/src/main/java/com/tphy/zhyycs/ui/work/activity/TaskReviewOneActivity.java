package com.tphy.zhyycs.ui.work.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.Task;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.MyDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2017\9\2 0002.  测试-审核
 */

public class TaskReviewOneActivity extends Activity implements View.OnClickListener {


    @BindView(R.id.tv_back_img)
    TextView tvBackImg;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_right)
    ImageView imgRight;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.btn_right_2)
    Button btnRight2;
    @BindView(R.id.tv_task_content)
    TextView tvTaskContent;
    @BindView(R.id.tv_task_senduser)
    TextView tvTaskSenduser;
    @BindView(R.id.tv_file_name)
    TextView tvFileName;
    @BindView(R.id.lyt_file)
    LinearLayout lytFile;
    @BindView(R.id.v_file_bottom)
    View vFileBottom;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_yc)
    Button btnYc;
    private SharedPreferences sp;
    Common common;
    Context context;
    private Dialog pd;
    private List<Task> list_task = new ArrayList<>();
    private String str_taskcode = "", str_reason = "", str_info = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskreviewone);
        ButterKnife.bind(this);
        if (null != getIntent().getStringExtra("TaskCode")) {
            str_taskcode = getIntent().getStringExtra("TaskCode");
        }
        if (null != getIntent().getStringExtra("TaskInfo")) {
            str_info = getIntent().getStringExtra("TaskInfo");
        }
        initView();
        GetTaskInfo();

    }

    private void initView() {
        common = new Common(this);
        context = TaskReviewOneActivity.this;
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);

        if (str_info.equals("TaskInfo")) {
            tvTitle.setText("详情");
            btnSave.setVisibility(View.GONE);
            btnYc.setVisibility(View.GONE);
        } else {
            tvTitle.setText("审核");
            btnRight.setVisibility(View.VISIBLE);
            btnRight.setText("驳回");
        }
    }


    /**
     * 获取任务详情
     */
    private void GetTaskInfo() {
        list_task.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("TaskCode", str_taskcode);
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetTaskInfo";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("onError", "aaaa");
                        pd.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success = "", result = "", msg = "";
                        Log.e("回传的数据", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            result = jsonObject.getString("result");
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                list_task = new Gson().fromJson(result, new TypeToken<List<Task>>() {
                                }.getType());
                                tvTaskContent.setText(list_task.get(0).getContent());
                                tvTaskSenduser.setText(list_task.get(0).getSendUserName());
                                if (list_task.get(0).getSendFile().equals("")) {
                                    lytFile.setVisibility(View.GONE);
                                } else {
                                    tvFileName.setText(list_task.get(0).getSendFile());
                                }
                                pd.dismiss();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }


    /**
     * 审核任务
     */
    private void CPCheckTask(String ispass) {
        pd.show();
        HashMap<String, String> params = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("LoginUserCode", sp.getString("Code", ""));
        param.put("Remark", str_reason);
        param.put("TaskCode", str_taskcode);
        param.put("IsPass", ispass);
        Gson gson = new Gson();
        params.put("paraJson", gson.toJson(param));
        Log.e("输出的参数", gson.toJson(param));
        String url = DemoApplication.serviceUrl + "/CPCheckTask";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pd.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        pd.dismiss();
                        String success = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                Toast.makeText(context, "完成操作", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent("new_task_list");
                                sendBroadcast(it);
                                Intent it2 = new Intent("new_information");
                                sendBroadcast(it2);
                                Intent it3 = new Intent("PatientMain");
                                sendBroadcast(it3);
                                finish();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }


    /**
     * 测试远程解决
     */
    private void RemoteResolved() {
        pd.show();
        HashMap<String, String> params = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("LoginUserCode", sp.getString("Code", ""));
        param.put("TaskCode", str_taskcode);
        param.put("Remark", str_reason);
        Gson gson = new Gson();
        params.put("paraJson", gson.toJson(param));
        Log.e("输出的参数", gson.toJson(param));
        String url = DemoApplication.serviceUrl + "/RemoteResolved";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pd.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        pd.dismiss();
                        String success = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                Toast.makeText(context, "完成操作", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent("new_task_list");
                                sendBroadcast(it);
                                Intent it2 = new Intent("new_information");
                                sendBroadcast(it2);
                                Intent it3 = new Intent("PatientMain");
                                sendBroadcast(it3);
                                finish();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    @OnClick({R.id.btn_yc, R.id.tv_back, R.id.tv_file_name, R.id.tv_back_img, R.id.btn_save, R.id.btn_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.btn_save:   // 通过
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
                final MyDialog dialog = new MyDialog(this, 700, 700, view, R.style.dialog);
                ImageView iv = (ImageView) view.findViewById(R.id.iv_dismiss);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                TextView tv = (TextView) view.findViewById(R.id.tv_content);
                tv.setText("备注");
                final EditText edt = (EditText) view.findViewById(R.id.edt_remark);
                Button btn = (Button) view.findViewById(R.id.btn_save);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_reason = edt.getText().toString().trim();
                        CPCheckTask("1");
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
                break;
            case R.id.btn_right:  // 驳回

                View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
                final MyDialog dialog1 = new MyDialog(this, 700, 700, view1, R.style.dialog);
                ImageView iv1 = (ImageView) view1.findViewById(R.id.iv_dismiss);
                iv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                TextView tv1 = (TextView) view1.findViewById(R.id.tv_content);
                tv1.setText("原因");
                final EditText edt1 = (EditText) view1.findViewById(R.id.edt_remark);
                Button btn1 = (Button) view1.findViewById(R.id.btn_save);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edt1.getText().toString().trim().equals("")) {
                            Toast.makeText(context, "原因不能填空", Toast.LENGTH_SHORT).show();
                        } else {
                            str_reason = edt1.getText().toString().trim();
                            CPCheckTask("0");
                        }
                        dialog1.dismiss();
                    }
                });
                dialog1.show();
                dialog1.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
                break;
            case R.id.btn_yc:   // 远程解决

                View view2 = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
                final MyDialog dialog2 = new MyDialog(this, 700, 700, view2, R.style.dialog);
                ImageView iv2 = (ImageView) view2.findViewById(R.id.iv_dismiss);
                iv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });
                TextView tv2 = (TextView) view2.findViewById(R.id.tv_content);
                tv2.setText("备注");
                final EditText edt2 = (EditText) view2.findViewById(R.id.edt_remark);
                Button btn2 = (Button) view2.findViewById(R.id.btn_save);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_reason = edt2.getText().toString().trim();
                        RemoteResolved();
                        dialog2.dismiss();
                    }
                });
                dialog2.show();
                dialog2.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
                break;
            case R.id.tv_file_name:   // 下载附件
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(DemoApplication.fileUrl + list_task.get(0).getSendFileUrl());
                intent.setData(content_url);
                startActivity(intent);
                break;
        }

    }
}
