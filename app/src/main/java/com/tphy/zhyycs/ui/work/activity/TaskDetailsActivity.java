package com.tphy.zhyycs.ui.work.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import com.tphy.zhyycs.model.JCD;
import com.tphy.zhyycs.model.Logg;
import com.tphy.zhyycs.model.Task;
import com.tphy.zhyycs.ui.work.adapter.LogAdpater;
import com.tphy.zhyycs.ui.work.adapter.TaskJcdAdapter;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.MyDialog;
import com.tphy.zhyycs.utils.MyListView;
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
 * 项目名称：任务详情
 * 创建人：cdss
 * 创建时间：2017-08-16 16:17
 * 修改人：cdss
 * 修改时间：2017-08-16 16:17
 * 修改备注：
 */

public class TaskDetailsActivity extends Activity implements View.OnClickListener {

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
    @BindView(R.id.tv_task_name)
    TextView tvTaskName;
    @BindView(R.id.tv_task_status)
    TextView tvTaskStatus;
    @BindView(R.id.tv_task_modul)
    TextView tvTaskModul;
    @BindView(R.id.tv_task_senduser)
    TextView tvTaskSenduser;
    @BindView(R.id.tv_date_start)
    TextView tvDateStart;
    @BindView(R.id.tv_date_end)
    TextView tvDateEnd;
    @BindView(R.id.tv_content)
    EditText tvContent;
    @BindView(R.id.tv_file_name)
    TextView tvFileName;
    @BindView(R.id.lyt_file)
    LinearLayout lytFile;
    @BindView(R.id.v_file_bottom)
    View vFileBottom;
    @BindView(R.id.list_jcd)
    MyListView listJcd;
    @BindView(R.id.list_log)
    MyListView listLog;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.tv_fail_remark)
    TextView tvFailRemark;
    @BindView(R.id.v_fail_remark)
    View vFailRemark;
    private List<Task> list_task = new ArrayList<>();
    private List<Logg> list_log = new ArrayList<>();
    private List<String> list_str_log = new ArrayList<>();
    private List<JCD> list_jcd = new ArrayList<>();
    private TaskJcdAdapter adapter;
    private LogAdpater logAdpater;
    private Dialog pd;
    private SharedPreferences sp;
    private Activity activity;
    Common common;
    private String str_usercode = "", str_taskcode = "", str_task_type = "", modify = "", str_projectcode = "", str_projectname = "", str_productname = "", str_taskstatus = "";
    public static String modify2 = "";
    private String str_fail_remark = "", StatusName = "";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Task_Submit")) {
                View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit, null);
                final MyDialog dialog = new MyDialog(activity, 700, 700, view, R.style.dialog);
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
                        FinishCheckPoint(edt.getText().toString().trim());
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskdetails);
        ButterKnife.bind(this);
        if (null != getIntent().getStringExtra("LoginUserCode")) {
            str_usercode = getIntent().getStringExtra("LoginUserCode");
        }
        if (null != getIntent().getStringExtra("TaskCode")) {
            str_taskcode = getIntent().getStringExtra("TaskCode");
        }
        if (null != getIntent().getStringExtra("task_type")) {
            str_task_type = getIntent().getStringExtra("task_type");
        }
        if (null != getIntent().getStringExtra("modify")) {
            modify = getIntent().getStringExtra("modify");
        }
        if (null != getIntent().getStringExtra("ProjectCode")) {
            str_projectcode = getIntent().getStringExtra("ProjectCode");
        }
        if (null != getIntent().getStringExtra("ProjectName")) {
            str_projectname = getIntent().getStringExtra("ProjectName");
        }
        if (null != getIntent().getStringExtra("ProductName")) {
            str_productname = getIntent().getStringExtra("ProductName");
        }
        if (null != getIntent().getStringExtra("TaskStatus")) {
            str_taskstatus = getIntent().getStringExtra("TaskStatus");
        }
        if (null != getIntent().getStringExtra("StatusName")) {
            StatusName = getIntent().getStringExtra("StatusName");
        }
        initView();
        GetTaskInfo();
    }

    private void initView() {
        activity = TaskDetailsActivity.this;
        common = new Common(this);
        tvTitle.setText("详情(编号:" + str_taskcode + ")");

        if (modify.equals("modify")) {
            imgRight.setImageResource(R.drawable.main_xg);
            imgRight.setVisibility(View.VISIBLE);
        }
        if (str_task_type.equals("1")) {
            btnRight.setText("驳回");
            btnRight.setVisibility(View.VISIBLE);
        }
        pd = CustomProgressDialog.createLoadingDialog(activity, "正在加载中...");
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);

        tvDateEnd.setEnabled(false);
        tvContent.setEnabled(false);

        IntentFilter itf = new IntentFilter("Task_Submit");
        registerReceiver(receiver, itf);
    }

    /**
     * 获取任务详情
     */
    private void GetTaskInfo() {
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
                                for (int i = 0; i < list_task.size(); i++) {
                                    list_task.get(i).setProjectCode(str_projectcode);
                                    if (list_task.get(i).getIsCurrent().equals("1") && list_task.get(i).getExecutUserCode().equals(sp.getString("OriginalCode", ""))) {
                                        Log.e("aaaaa", list_task.get(i).getIsCurrent());
                                        str_task_type = "1";
                                        btnRight.setText("驳回");
                                        btnRight.setVisibility(View.VISIBLE);
                                    }
                                }
                                list_task.get(0).setProjectName(str_projectname);
                                list_task.get(0).setProductName(str_productname);
                                adapter = new TaskJcdAdapter(activity, list_task, str_task_type);
                                listJcd.setAdapter(adapter);
                                if (list_task.get(0).getContent().length() > 10) {
                                    tvTaskName.setText(list_task.get(0).getContent().substring(0, 9));
                                } else {
                                    tvTaskName.setText(list_task.get(0).getContent());
                                }
                                tvTaskStatus.setText(str_taskstatus);
                                tvTaskModul.setText(list_task.get(0).getProjectName() + ">" + list_task.get(0).getProductName());
                                tvTaskSenduser.setText(list_task.get(0).getSendUserName());
                                tvDateStart.setText(list_task.get(0).getStartTime());
                                tvDateEnd.setText(list_task.get(0).getPlanDate());
                                tvContent.setText(list_task.get(0).getContent());
                                if (list_task.get(0).getSendFile().equals("")) {
                                    lytFile.setVisibility(View.GONE);
                                } else {
                                    tvFileName.setText(list_task.get(0).getSendFile());
                                }
                                GetTaskEditRecordArray();
                            } else {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * 任务操作记录
     */
    private void GetTaskEditRecordArray() {

        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("TaskCode", str_taskcode);
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetTaskEditRecordArray";
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
                        String success = "", result = "", msg = "";
                        Log.e("回传的数据", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            result = jsonObject.getString("result");
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                list_log = new Gson().fromJson(result, new TypeToken<List<Logg>>() {
                                }.getType());
                                for (int i = 0; i < list_log.size(); i++) {
                                    list_str_log.add(list_log.get(i).getRecord());
                                    if (list_log.get(i).getRecord().contains("不通过")) {
                                        str_fail_remark = list_log.get(i).getRecord();
                                    }
                                }
                                logAdpater = new LogAdpater(TaskDetailsActivity.this, list_str_log);
                                listLog.setAdapter(logAdpater);
                                if (!str_fail_remark.equals("")) {
                                    vFailRemark.setVisibility(View.VISIBLE);
                                    tvFailRemark.setVisibility(View.VISIBLE);
                                    tvFailRemark.setText(str_fail_remark);
                                }
                                pd.dismiss();
                            } else {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * 完成检查点
     */
    private void FinishCheckPoint(String remark) {

        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("TaskCode", str_taskcode);
        pa.put("Remark", remark);
        pa.put("FileBytes", "");
        pa.put("FileName", "");
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/FinishCheckPoint";
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
                        String success = "", result = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            pd.dismiss();
                            if (success.equals("true")) {
                                Toast.makeText(activity, "完成操作", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent("new_task_list");
                                sendBroadcast(it);
                                Intent it2 = new Intent("new_information");
                                sendBroadcast(it2);
                                Intent it3 = new Intent("PatientMain");
                                sendBroadcast(it3);
                                finish();
                            } else {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * 任务验收不通过
     */
    private void NotAcceptedTask(String remark) {

        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("TaskCode", str_taskcode);
        pa.put("Remark", remark);
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/NotAcceptedTask";
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
                        String success = "", result = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            pd.dismiss();
                            if (success.equals("true")) {
                                Toast.makeText(activity, "完成操作", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent("new_task_list");
                                sendBroadcast(it);
                                Intent it2 = new Intent("new_information");
                                sendBroadcast(it2);
                                Intent it3 = new Intent("PatientMain");
                                sendBroadcast(it3);
                                finish();
                            } else {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * 更新任务
     */
    private void UpdateTask() {

        pd.show();
        HashMap<String, String> params = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("Code", str_taskcode);
        if (!list_task.get(0).getPlanDate().equals(tvDateEnd.getText().toString().trim())) {
            param.put("PlanDate", tvDateEnd.getText().toString().trim());
        }
        if (!list_task.get(0).getContent().equals(tvContent.getText().toString().trim())) {
            param.put("Content", tvContent.getText().toString().trim());
        }
        param.put("lstCheckPoint", list_jcd);

        Gson gson = new Gson();
        Map<String, Object> pa = new HashMap<>();
        pa.put("Task", gson.toJson(param));
        String str = "{\"LoginUserCode\":\"" + sp.getString("Code", "") + "\"," + gson.toJson(pa).substring(1);
        params.put("paraJson", str);
        Log.e("输出的参数修改任务", str);

        String url = DemoApplication.serviceUrl + "/UpdateTask";
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
                                Toast.makeText(activity, "修改任务成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } catch (Exception e) {

                        }

                    }
                });
    }

    @OnClick({R.id.btn_right, R.id.tv_back_img, R.id.tv_back, R.id.btn_save, R.id.tv_file_name, R.id.tv_date_end, R.id.img_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.btn_save:
                UpdateTask();
                break;
            case R.id.tv_file_name:   // 下载附件
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(DemoApplication.fileUrl + list_task.get(0).getSendFileUrl());
                intent.setData(content_url);
                startActivity(intent);
                break;
            case R.id.tv_date_end:
                common.showDateDialog(this, tvDateEnd);
                break;
            case R.id.img_right:
                if (StatusName.contains("不通过")) {
                    Toast.makeText(activity, "驳回的任务不能修改!", Toast.LENGTH_SHORT).show();
                } else {
                    btnSave.setVisibility(View.VISIBLE);
                    btnSave.setText("修改");
                    tvDateEnd.setEnabled(true);
                    tvContent.setEnabled(true);
                    modify2 = modify;
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_right:
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
                tv.setText("原因");
                final EditText edt = (EditText) view.findViewById(R.id.edt_remark);
                Button btn = (Button) view.findViewById(R.id.btn_save);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edt.getText().toString().trim().equals("")) {
                            Toast.makeText(activity, "原因不能填空", Toast.LENGTH_SHORT).show();
                        } else {
                            NotAcceptedTask(edt.getText().toString().trim());
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 101:  //
                    String number = data.getStringExtra("number");
                    String username = data.getStringExtra("user_name");
                    String usercode = data.getStringExtra("user_code");
                    if (!list_task.get(Integer.valueOf(number)).getExecutUserName().equals(username)) {
                        JCD jcd = new JCD();
                        jcd.setTemplateCode(list_task.get(Integer.valueOf(number)).getCpCode());
                        jcd.setExecutUserCode(usercode);
                        list_jcd.add(jcd);
                    }
                    list_task.get(Integer.valueOf(number)).setExecutUserName(username);
                    adapter.update(list_task);

                    break;

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modify2 = "";
        unregisterReceiver(receiver);
    }
}
