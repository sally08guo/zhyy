package com.tphy.zhyycs.ui.attandence;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.tphy.zhyycs.model.AttendInfo;
import com.tphy.zhyycs.ui.attandence.adapter.AttendInfoShrAdapter;
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
 * Created by Administrator on 2017\10\19 0019.   考勤审核
 */

public class AttendInfoActivity extends Activity implements View.OnClickListener {

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
    @BindView(R.id.kq_et_content)
    TextView kqEtContent;
    @BindView(R.id.kq_et_senduser)
    TextView kqEtSenduser;
    @BindView(R.id.lyt_sqr)
    LinearLayout lytSqr;
    @BindView(R.id.kq_et_destination)
    TextView kqEtDestination;
    @BindView(R.id.kq_tv_leibie)
    TextView kqTvLeibie;
    @BindView(R.id.kq_tv_start)
    TextView kqTvStart;
    @BindView(R.id.kq_tv_end)
    TextView kqTvEnd;
    @BindView(R.id.kq_et_duration)
    TextView kqEtDuration;
    @BindView(R.id.kq_duration)
    LinearLayout kqDuration;
    @BindView(R.id.listview)
    MyListView listview;
    @BindView(R.id.kq_tv_commit)
    TextView kqTvCommit;

    private Dialog pd;
    private AttendInfoShrAdapter adapter;
    private List<AttendInfo> list_info = new ArrayList<>();
    private SharedPreferences sp;
    private Activity context;
    private String task_code = "", type = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_info);
        ButterKnife.bind(this);
        if (null != getIntent().getStringExtra("task_code")) {
            task_code = getIntent().getStringExtra("task_code");
        }
        if (null != getIntent().getStringExtra("type")) {
            type = getIntent().getStringExtra("type");
        }
        initView();
        GetAttendanceInfo();
    }

    private void initView() {
        context = AttendInfoActivity.this;
        pd = CustomProgressDialog.createLoadingDialog(this, "正在加载中...");
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        if (type.equals("1")) {
            tvTitle.setText("审核");
            btnRight.setText("驳回");
            btnRight.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText("详情");
            kqTvCommit.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.kq_tv_commit, R.id.btn_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back_img:
            case R.id.tv_back:
                finish();
                break;
            case R.id.kq_tv_commit:  // 审核通过
                CheckAttendance("1", "");
                break;
            case R.id.btn_right:  // 驳回
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
                final MyDialog dialog = new MyDialog(this, 800, 800, view, R.style.dialog);
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
                            CheckAttendance("2", edt.getText().toString().trim());
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }

    /**
     * 考勤申请详情
     */
    private void GetAttendanceInfo() {
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("AttendanceCode", task_code);
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetAttendanceInfo";
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
                            result = jsonObject.getString("result");
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                list_info = new Gson().fromJson(result, new TypeToken<List<AttendInfo>>() {
                                }.getType());
                                AttendInfo info = list_info.get(0);
                                kqEtContent.setText(info.getReason());
                                kqEtSenduser.setText(info.getSendUserName());
                                kqEtDestination.setText(info.getLocation());
                                Log.e("WQ", "----获取的类别是====>" + info.getLeiBie());
                                kqTvLeibie.setText(info.getLeiBie());
                                kqEtDuration.setText(info.getLongTime());
                                kqTvStart.setText(info.getStartTime());
                                kqTvEnd.setText(info.getEndTime());
                                adapter = new AttendInfoShrAdapter(context, list_info);
                                listview.setAdapter(adapter);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();
                        } catch (Exception e) {
                        }
                    }
                });
    }


    /**
     * 审批考勤申请
     */
    private void CheckAttendance(String IsPass, String Remark) {
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("AttendanceCode", task_code);
        pa.put("IsPass", IsPass);
        pa.put("Remark", Remark);
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/CheckAttendance";
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
                            if (success.equals("true")) {
                                Toast.makeText(context, "审核成功", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent("new_information");
                                sendBroadcast(it);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                            finish();
                            pd.dismiss();
                        } catch (Exception e) {
                        }
                    }
                });
    }


}
