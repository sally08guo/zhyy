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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.LCB;
import com.tphy.zhyycs.model.Logg;
import com.tphy.zhyycs.model.PunchByDeptGroup;
import com.tphy.zhyycs.model.PunchByDeptItem;
import com.tphy.zhyycs.model.Role;
import com.tphy.zhyycs.ui.approval.adapter.ByDeptAdapter;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
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
 * 考勤统计--按部门
 */

public class AttendanceByDeptActivity extends Activity implements View.OnClickListener {

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
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.lyt_dept)
    LinearLayout lytDept;
    @BindView(R.id.lyt_nodata)
    LinearLayout lytNoData;


    private ByDeptAdapter adapter;
    private List<PunchByDeptGroup> list = new ArrayList<>();
    private List<PunchByDeptItem> list_group = new ArrayList<>();
    private String dept;
    private Common common;
    private Dialog pd;
    private SharedPreferences sp;
    private Context context;
    private int mYear, mMonth, mDay;
    private List<Role> list_dept = new ArrayList<>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("attendance_new_date")) {
                Bundle bundle = intent.getExtras();
                mYear = bundle.getInt("year");
                mMonth = bundle.getInt("month");
                mDay = bundle.getInt("day");
                int i = dayForWeek(mYear + "-" + mMonth + "-" + mDay);
                tvDateStart.setText(mYear + "年" + mMonth + "月" + mDay + "日    " + getWeekDay(i));
                GetPunchCollectByDept(dept);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_info);
        ButterKnife.bind(this);

        initView();
        GetPunchCollectByDept(dept);
        IntentFilter itf2 = new IntentFilter("attendance_new_date");
        registerReceiver(receiver, itf2);
    }

    private void initView() {
        context = AttendanceByDeptActivity.this;
        common = new Common(this);
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        tvTitle.setText("统计");
//        imgRight.setImageResource(R.drawable.sqxq);
//        imgRight.setVisibility(View.VISIBLE);
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);

        tvDateStart.setText(mYear + "年" + mMonth + "月" + mDay + "日    " + getWeekDay(c.get(Calendar.DAY_OF_WEEK)));
        //设置 属性 去掉默认向下的箭头
        listview.setGroupIndicator(null);

        if (sp.getString("Permission", "").contains("AllDeptPunchCollect")) {
            dept = "all";
            GetDept();
        } else {
            dept = "";
            lytDept.setVisibility(View.GONE);
        }

    }

    /**
     * 获取部门
     */
    private void GetDept() {
        pd.show();
        list_dept.clear();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("codeTables", "dept"));
        String url = DemoApplication.serviceUrl + "/GetCodeListByName";
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
                            List<String> list_name = new ArrayList<>();
                            if (success.equals("true")) {
                                result = jsonObject.getString("result");
                                JSONObject js = new JSONObject(result);
                                list_dept = new Gson().fromJson(js.getString("dept"), new TypeToken<List<Role>>() {
                                }.getType());
                                list_name.add("全部");
                                for (int i = 0; i < list_dept.size(); i++) {
                                    list_name.add(list_dept.get(i).getName());
                                }
                                Role role = new Role();
                                role.setName("全部");
                                role.setCode("all");
                                list_dept.add(0, role);

                                String[] mItems = list_name.toArray(new String[list_name.size()]);
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(AttendanceByDeptActivity.this, android.R.layout.simple_spinner_item, mItems);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                dept = list_dept.get(0).getCode();
                                spinner.setAdapter(adapter);
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                        dept = list_dept.get(pos).getCode();
                                        GetPunchCollectByDept(dept);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
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
     * 判断是星期几
     */
    private String getWeekDay(int i) {
        String mWay = "";
        switch (i) {
            case 1:
                mWay = "星期天";
                break;
            case 2:
                mWay = "星期一";
                break;
            case 3:
                mWay = "星期二";
                break;
            case 4:
                mWay = "星期三";
                break;
            case 5:
                mWay = "星期四";
                break;
            case 6:
                mWay = "星期五";
                break;
            case 7:
                mWay = "星期六";
                break;
        }
        Log.e("星期几", mWay);
        return mWay;
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
                common.showDateDialog2(this);
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
    private void GetPunchCollectByDept(String type) {
        pd.show();
        list.clear();
        list_group.clear();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("Date", mYear + "-" + mMonth + "-" + mDay);
        pa.put("DeptCode", type);
        Log.e("考勤统计--按部门", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetPunchCollectByDept";
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
                                list = new Gson().fromJson(result, new TypeToken<List<PunchByDeptGroup>>() {
                                }.getType());

                                for (int i = 0; i < list.size(); i++) {
                                    for (int j = 0; j < list.get(i).getTypeList().size(); j++) {
                                        PunchByDeptItem p = new PunchByDeptItem();
                                        p = list.get(i).getTypeList().get(j);
                                        p.setDeptName(list.get(i).getDeptName());
                                        list_group.add(p);
                                    }
                                }

                                if (list_group.size() == 0) {
                                    lytNoData.setVisibility(View.VISIBLE);
                                    listview.setVisibility(View.GONE);
                                } else {
                                    lytNoData.setVisibility(View.GONE);
                                    listview.setVisibility(View.VISIBLE);
                                    adapter = new ByDeptAdapter(context, list_group);
                                    listview.setAdapter(adapter);
                                }
                                Log.e("数组的长度", list_group.size() + "");
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

    /**
     * 判断当前日期是星期几<br>
     */
    private int dayForWeek(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        int dayForWeek = 0;
        try {
            c.setTime(format.parse(pTime));
            dayForWeek = c.get(Calendar.DAY_OF_WEEK);
        } catch (Exception e) {

        }
        return dayForWeek;
    }
}
