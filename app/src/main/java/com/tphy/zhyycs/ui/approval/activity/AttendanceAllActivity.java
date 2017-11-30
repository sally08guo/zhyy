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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.approval.adapter.AttendanceAllAdapter;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.MyGridView;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
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

public class AttendanceAllActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


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
    @BindView(R.id.gridView)
    MyGridView gridView;
    @BindView(R.id.al_swipe_fresh)
    SwipeRefreshLayout alSwipeFresh;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.tv_date_start)
    TextView tvDateStart;
    private Common common;
    private Dialog pd;
    private SharedPreferences sp;
    private Activity context;

    private List<String> list = new ArrayList<>();
    private List<String> list_code = new ArrayList<>();
    private AttendanceAllAdapter adapter;
    private int index;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("AttendanceAllActivity")) {
                GetAttendanceCollectByUserDept();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_all);
        ButterKnife.bind(this);
        initView();

        GetAttendanceCollectByUserDept();

        alSwipeFresh.setOnRefreshListener(this);
        alSwipeFresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        IntentFilter itf = new IntentFilter("AttendanceAllActivity");
        registerReceiver(receiver, itf);
    }

    private void initView() {
        context = AttendanceAllActivity.this;
        common = new Common(this);
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        tvTitle.setText("考勤汇总");

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        tvDateStart.setText(mYear + "-" + mMonth);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position % index == 0 && position > 0) {
                    Intent it = new Intent(context, AttendanceInfoActivity.class);
                    it.putExtra("UserCode", list_code.get(position / index - 1));
                    it.putExtra("UserName", list.get(position));
                    it.putExtra("Date", tvDateStart.getText().toString());
                    Log.e("选中的code", list_code.get(position / index - 1));
                    startActivity(it);
                }
            }
        });
    }

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.tv_date_start})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.tv_date_start:
                common.showMonth(this, tvDateStart, "2");
                break;
        }
    }

    /**
     * 考勤汇总
     */
    private void GetAttendanceCollectByUserDept() {
        pd.show();
        list.clear();
        list_code.clear();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("Date", tvDateStart.getText().toString());
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("传递的参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetAttendanceCollectByUserDept";
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
                                empty.setVisibility(View.GONE);
                                list.add(" ");
                                JSONObject js = new JSONObject(result);
                                JSONArray jsonArray = new JSONArray(js.getString("typeList"));
                                index = jsonArray.length() + 1;
                                gridView.setNumColumns(index);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    list.add(jsonArray.getString(i));
                                }
                                JSONArray jsonArray1 = new JSONArray(js.getString("resultList"));
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jb = jsonArray1.getJSONObject(j);
                                    list.add(jb.getString("UserName"));
                                    list_code.add(jb.getString("UserCode"));
                                    JSONArray jsonArray2 = new JSONArray(jb.getString("CountList"));
                                    for (int k = 0; k < jsonArray2.length(); k++) {
                                        list.add(jsonArray2.getString(k));
                                    }
                                }
                                adapter = new AttendanceAllAdapter(context, list);
                                adapter.setColumnNum(index);
                                gridView.setAdapter(adapter);

                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                            alSwipeFresh.setRefreshing(false);
                            pd.dismiss();
                        } catch (Exception e) {
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        GetAttendanceCollectByUserDept();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
