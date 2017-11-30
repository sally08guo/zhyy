package com.tphy.zhyycs.ui.approval.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.AttendRecord;
import com.tphy.zhyycs.ui.approval.adapter.RecordAdapter;
import com.tphy.zhyycs.ui.attandence.AttendInfoActivity;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
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
 * 申请记录
 */

public class ApplicationRecordActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    @BindView(R.id.rb_attend_fq)
    RadioButton rbAttendFq;
    @BindView(R.id.rb_attend_dsp)
    RadioButton rbAttendDsp;
    @BindView(R.id.rb_attend_ysp)
    RadioButton rbAttendYsp;
    @BindView(R.id.rb_attend_cs)
    RadioButton rbAttendCs;
    @BindView(R.id.rg_task)
    RadioGroup rgTask;
    @BindView(R.id.listview)
    SwipeMenuListView listview;
    @BindView(R.id.al_swipe_fresh)
    SwipeRefreshLayout alSwipeFresh;
    @BindView(R.id.empty)
    LinearLayout empty;

    private Dialog pd;
    private SharedPreferences sp;
    Activity context;
    RecordAdapter adapter;
    Common common;
    private List<AttendRecord> list_attend_fq = new ArrayList<>();
    private List<AttendRecord> list_attend_dsp = new ArrayList<>();
    private List<AttendRecord> list_attend_ysp = new ArrayList<>();
    private List<AttendRecord> list_attend_cs = new ArrayList<>();
    private String str_type = "4";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("new_record")) {
                GetAttendanceList(str_type);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_record);
        ButterKnife.bind(this);

        initView();
        GetAttendanceList("4");
        alSwipeFresh.setOnRefreshListener(this);
        alSwipeFresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        IntentFilter itf = new IntentFilter("new_record");
        registerReceiver(receiver, itf);
    }

    private void initView() {
        context = ApplicationRecordActivity.this;
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        common = new Common(context);
        sp = context.getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        tvTitle.setText("考勤申请记录");

        rgTask.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_attend_fq:  // 我申请的
                        str_type = "4";
                        GetAttendanceList("4");
                        break;
                    case R.id.rb_attend_dsp: //待审批
                        str_type = "1";
                        GetAttendanceList("1");
                        break;
                    case R.id.rb_attend_ysp: //已审批
                        str_type = "2";
                        GetAttendanceList("2");
                        break;
                    case R.id.rb_attend_cs:  //抄送我的
                        str_type = "3";
                        GetAttendanceList("3");
                        break;
                }
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(context, AttendInfoActivity.class);
                AttendRecord inf = null;
                if (str_type.equals("4")) {
                    inf = list_attend_fq.get(position);
                } else if (str_type.equals("1")) {
                    inf = list_attend_dsp.get(position);
                    it.putExtra("type", "1");
                } else if (str_type.equals("2")) {
                    inf = list_attend_ysp.get(position);
                } else {
                    inf = list_attend_cs.get(position);
                }
                it.putExtra("task_code", inf.getCode());
                startActivity(it);
            }
        });
    }

    /**
     * 考勤申请记录
     */
    private void GetAttendanceList(final String op) {
        list_attend_fq.clear();
        list_attend_dsp.clear();
        list_attend_ysp.clear();
        list_attend_cs.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("op", op);
        pa.put("PName", "");
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("输入的参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetAttendanceList";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pd.dismiss();
                        alSwipeFresh.setRefreshing(false);
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
                                if (op.equals("1")) { //待审批
                                    list_attend_dsp = new Gson().fromJson(result, new TypeToken<List<AttendRecord>>() {
                                    }.getType());
                                    adapter = new RecordAdapter(context, list_attend_dsp);
                                    if (list_attend_dsp.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                } else if (op.equals("2")) {   // 已审核
                                    list_attend_ysp = new Gson().fromJson(result, new TypeToken<List<AttendRecord>>() {
                                    }.getType());
                                    adapter = new RecordAdapter(context, list_attend_ysp);
                                    if (list_attend_ysp.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                } else if (op.equals("3")) {   // 抄送
                                    list_attend_cs = new Gson().fromJson(result, new TypeToken<List<AttendRecord>>() {
                                    }.getType());
                                    adapter = new RecordAdapter(context, list_attend_cs);
                                    if (list_attend_cs.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    list_attend_fq = new Gson().fromJson(result, new TypeToken<List<AttendRecord>>() {
                                    }.getType());
                                    adapter = new RecordAdapter(context, list_attend_fq);
                                    if (list_attend_fq.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                }
                                listview.setAdapter(adapter);
                                pd.dismiss();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                            alSwipeFresh.setRefreshing(false);

                        } catch (Exception e) {
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        GetAttendanceList(str_type);
    }

    @OnClick({R.id.tv_back_img, R.id.tv_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
