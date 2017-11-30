package com.tphy.zhyycs.ui.work.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.CheckPoint;
import com.tphy.zhyycs.model.LCB;
import com.tphy.zhyycs.model.Task;
import com.tphy.zhyycs.ui.work.adapter.TaskReviewAdapter;
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
 * Created by Administrator on 2017\9\2 0002.  任务-审核
 */

public class TaskReviewActivity extends FragmentActivity implements View.OnClickListener {


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
    @BindView(R.id.tv_date_start)
    TextView tvDateStart;
    @BindView(R.id.tv_task_type)
    TextView tvTaskType;
    @BindView(R.id.listview)
    MyListView listview;
    @BindView(R.id.btn_save)
    Button btnSave;
    private SharedPreferences sp;
    private String str_usercode = "", str_taskcode = "", str_taskcontent = "", str_senduser = "";
    private String str_task_type = "", str_reason = "", str_user_cs_code = "", str_user_cs_name = "";
    public static String str_project_code = "";
    Common common;
    Context context;
    TaskReviewAdapter adapter;
    List<LCB> list_jcd = new ArrayList<>();
    List<LCB> list_task_type = new ArrayList<>();
    private Dialog pd;
    MyDialog dialog;
    private List<Task> list_task = new ArrayList<>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("TaskType")) {
                str_task_type = intent.getStringExtra("code");
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskreview);
        ButterKnife.bind(this);
        common = new Common(this);
        context = TaskReviewActivity.this;
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        if (null != getIntent().getStringExtra("LoginUserCode")) {
            str_usercode = getIntent().getStringExtra("LoginUserCode");
        }
        if (null != getIntent().getStringExtra("TaskCode")) {
            str_taskcode = getIntent().getStringExtra("TaskCode");
        }
        if (null != getIntent().getStringExtra("TaskContent")) {
            str_taskcontent = getIntent().getStringExtra("TaskContent");
        }
        if (null != getIntent().getStringExtra("SendUser")) {
            str_senduser = getIntent().getStringExtra("SendUser");
        }
        if (null != getIntent().getStringExtra("ProjectCode")) {
            str_project_code = getIntent().getStringExtra("ProjectCode");
        }
        if (null != getIntent().getStringExtra("CPCheckUserCode")) {
            str_user_cs_code = getIntent().getStringExtra("CPCheckUserCode");
        }
        if (null != getIntent().getStringExtra("CPCheckUserName")) {
            str_user_cs_name = getIntent().getStringExtra("CPCheckUserName");
        }
        if (!str_taskcode.equals("") && str_usercode.equals("")) {
            GetTaskInfo();
        } else {
            initView();
        }
        GetJcd();
    }

    private void initView() {
        tvTitle.setText("审核(编号:" + str_taskcode + ")");
        tvTaskContent.setText(str_taskcontent);
        tvTaskSenduser.setText(str_senduser);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setText("驳回");
        IntentFilter itf = new IntentFilter("TaskType");
        registerReceiver(receiver, itf);
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
                                str_taskcontent = list_task.get(0).getContent();
                                str_senduser = list_task.get(0).getSendUserName();
                                initView();
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
     * 获取检查点
     */
    private void GetJcd() {
        list_jcd.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("codeTables", "task_template"));
        String url = DemoApplication.serviceUrl + "/GetCodeListByName";
        Log.e("获取检查点模板", Common.params("codeTables", "task_template"));
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("onError", "aaaa");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success = "", result = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                result = jsonObject.getString("result");
                                JSONObject js = new JSONObject(result);
                                list_jcd = new Gson().fromJson(js.getString("task_template"), new TypeToken<List<LCB>>() {
                                }.getType());
                                list_jcd.get(2).setUsercode(str_user_cs_code);
                                list_jcd.get(2).setUsername(str_user_cs_name);
                                adapter = new TaskReviewAdapter(TaskReviewActivity.this, list_jcd);
                                listview.setAdapter(adapter);
                                GetTaskType();
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
     * 获取任务类型
     */
    private void GetTaskType() {
        list_task_type.clear();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("codeTables", "task_type"));
        String url = DemoApplication.serviceUrl + "/GetCodeListByName";
        Log.e("获取任务类型", Common.params("codeTables", "task_type"));
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("onError", "aaaa");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success = "", result = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                result = jsonObject.getString("result");
                                JSONObject js = new JSONObject(result);
                                list_task_type = new Gson().fromJson(js.getString("task_type"), new TypeToken<List<LCB>>() {
                                }.getType());
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
    private void CheckTask(final String type) {
        pd.show();
        HashMap<String, String> params = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("LoginUserCode", sp.getString("Code", ""));
        param.put("Remark", str_reason);
        param.put("IsPass", type);
        List<CheckPoint> prole = new ArrayList<>();
        if (type.equals("1")) {
            for (int i = 0; i < list_jcd.size(); i++) {
                CheckPoint pr = new CheckPoint();
                if (list_jcd.get(i).getUsercode().equals("")) {
                    Toast.makeText(context, "请选择检查点责任人", Toast.LENGTH_SHORT).show();
                    return;
                }
                pr.setTemplateCode(list_jcd.get(i).getCode());
                pr.setExecutUserCode(list_jcd.get(i).getUsercode());
                prole.add(pr);
            }
        }
        param.put("strCheckPoint", prole);
        param.put("TaskCode", str_taskcode);
        param.put("TaskTypeCode", str_task_type);
        param.put("PlanDate", tvDateStart.getText().toString().trim());

        Gson gson = new Gson();
        params.put("paraJson", gson.toJson(param));
        Log.e("输出的参数", gson.toJson(param));
        String url = DemoApplication.serviceUrl + "/CheckTask";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pd.dismiss();
                        Log.e("上传文件", "失败");
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

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.btn_save, R.id.tv_date_start, R.id.btn_right, R.id.tv_task_type})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.btn_save:   // 通过
                boolean bl = true;
                for (int i = 0; i < list_jcd.size(); i++) {
                    if (null != list_jcd.get(i).getUsercode() && !list_jcd.get(i).getUsercode().equals("")) {
                        bl = false;
                    }
                }
                if (tvDateStart.getText().toString().trim().length() < 8) {
                    Toast.makeText(context, "请选择计划时效!", Toast.LENGTH_SHORT).show();
                } else if (str_task_type.equals("")) {
                    Toast.makeText(context, "请选择任务类型!", Toast.LENGTH_SHORT).show();
                } else if (bl) {
                    Toast.makeText(context, "请选择检查点执行人!", Toast.LENGTH_SHORT).show();
                } else {
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
                            CheckTask("1");
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

                }
                break;
            case R.id.tv_date_start:   // 计划时效
                //common.showDateDialog(this, tvDateStart);
                common.showTimePicker(this, tvDateStart);
                break;
            case R.id.tv_task_type:    // 任务类型
                common.showSelectTaskType(this, list_task_type, tvTaskType);
                break;
            case R.id.btn_right:  // 驳回

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
                            Toast.makeText(context, "原因不能填空", Toast.LENGTH_SHORT).show();
                        } else {
                            str_reason = edt.getText().toString().trim();
                            CheckTask("0");
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
                    Log.e("返回的数据", number + "--" + username + "--" + usercode);
                    list_jcd.get(Integer.valueOf(number)).setUsercode(usercode);
                    list_jcd.get(Integer.valueOf(number)).setUsername(username);
                    adapter.update(list_jcd);
                    break;

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
