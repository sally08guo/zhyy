package com.tphy.zhyycs.ui.approval.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.AttendGroup;
import com.tphy.zhyycs.ui.approval.adapter.MyExpandableAdapter;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 考勤详情
 */

public class AttendanceInfoActivity extends Activity implements View.OnClickListener {

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
    @BindView(R.id.listview)
    ExpandableListView listview;
    @BindView(R.id.tv_date_start)
    TextView tvDateStart;
    @BindView(R.id.lyt_dept)
    LinearLayout lytDept;
    @BindView(R.id.lyt_nodata)
    LinearLayout lytNoData;

    private MyExpandableAdapter adapter;
    private List<AttendGroup> list_group = new ArrayList<>();
    private String str_user_code = "", str_user_name = "", str_date = "";
    private Common common;
    private Dialog pd;
    private SharedPreferences sp;
    private Context context;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("AttendanceInfoActivity")) {
                GetPunchCollectByUser();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_info);
        ButterKnife.bind(this);

        if (null != getIntent().getStringExtra("UserCode")) {
            str_user_code = getIntent().getStringExtra("UserCode");
        }
        if (null != getIntent().getStringExtra("UserName")) {
            str_user_name = getIntent().getStringExtra("UserName");
        }
        if (null != getIntent().getStringExtra("Date")) {
            str_date = getIntent().getStringExtra("Date");
        }

        initView();
        GetPunchCollectByUser();

        IntentFilter itf = new IntentFilter("AttendanceInfoActivity");
        registerReceiver(receiver, itf);
    }

    private void initView() {
        context = AttendanceInfoActivity.this;
        common = new Common(this);
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        tvTitle.setText("统计");
//        imgRight.setImageResource(R.drawable.sqxq);
//        imgRight.setVisibility(View.VISIBLE);
        lytDept.setVisibility(View.GONE);

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        tvDateStart.setText(mYear + "-" + mMonth);
        //设置 属性 去掉默认向下的箭头
        listview.setGroupIndicator(null);
    }

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.tv_date_start, R.id.img_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.tv_date_start:
                common.showMonth(this, tvDateStart, "1");
                break;
            case R.id.img_right:
//                Intent it = new Intent(context, ApplicationRecordActivity.class);
//                startActivity(it);
                break;
        }
    }

    /**
     * 我参与的任务
     */
    private void GetPunchCollectByUser() {
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("Date", tvDateStart.getText().toString());
        String url = DemoApplication.serviceUrl + "/GetPunchCollectByUser";
        params.put("paraJson", Common.MaptoJson(pa));

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
                                list_group = new Gson().fromJson(result, new TypeToken<List<AttendGroup>>() {
                                }.getType());
                                Log.e("数组的长度", list_group.size() + "");
                                if (list_group.size() == 0) {
                                    lytNoData.setVisibility(View.VISIBLE);
                                    listview.setVisibility(View.GONE);
                                } else {
                                    lytNoData.setVisibility(View.GONE);
                                    listview.setVisibility(View.VISIBLE);
                                    adapter = new MyExpandableAdapter(context, list_group);
                                    listview.setAdapter(adapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
