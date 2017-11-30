package com.tphy.zhyycs.ui.attandence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.AttendType;
import com.tphy.zhyycs.model.AttendanceRequest;
import com.tphy.zhyycs.model.ModelResult;
import com.tphy.zhyycs.model.ShenHeMen;
import com.tphy.zhyycs.model.User;
import com.tphy.zhyycs.model.UserRange;
import com.tphy.zhyycs.ui.SelectUserActivity;
import com.tphy.zhyycs.ui.attandence.adapter.DialogAdapter;
import com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity;
import com.tphy.zhyycs.utils.ActivityUtils;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.tphy.zhyycs.widget.circleview.TimePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class NewAttendActivity extends BaseAppCompatActivity {
    @BindView(R.id.toolbar_tv_title)
    TextView toolbar_tv_title;
    @BindView(R.id.kq_et_content)
    EditText kq_et_content;
    @BindView(R.id.kq_et_destination)
    EditText kq_et_destination;
    @BindView(R.id.kq_tv_leibie)
    TextView kq_tv_leibie;
    @BindView(R.id.kq_tv_type)
    TextView kq_tv_type;
    @BindView(R.id.kq_cb_allday)
    CheckBox kq_cb_allday;
    @BindView(R.id.kq_tv_start)
    TextView kq_tv_start;
    @BindView(R.id.kq_tv_end)
    TextView kq_tv_end;
    @BindView(R.id.kq_tv_xiangmu)
    TextView kq_tv_xiangmu;
    @BindView(R.id.kq_tv_renshi)
    TextView kq_tv_renshi;
    @BindView(R.id.kq_renshi)
    RelativeLayout kq_renshi;
    @BindView(R.id.kq_tv_zong)
    TextView kq_tv_zong;
    @BindView(R.id.kq_zong)
    RelativeLayout kq_zong;
    @BindView(R.id.kq_tv_choochao)
    TextView kq_tv_choochao;
    @BindView(R.id.kq_tv_commit)
    TextView kq_tv_commit;
    @BindView(R.id.kq_duration)
    LinearLayout kq_duration;
    @BindView(R.id.kq_et_duration)
    EditText kq_et_duration;
    @BindView(R.id.kq_leaveType)
    RelativeLayout kq_leavType;
    @BindView(R.id.kq_tv_child)
    TextView kq_tv_child;
    @BindView(R.id.kq_allday)
    RelativeLayout allday;
    @BindDrawable(R.drawable.btn_circleadd)
    Drawable btn_circleadd;
    @BindView(R.id.tv_shenhe)
    TextView tv_shenhe;
    private int leibieP = -1;
    private int commonP = -1;
    private ActivityUtils activityUtils;
    private boolean ischooseStart;
    private boolean isChooseEnd;
    private Date startDate;
    private Date endDate;
    private Dialog pd;
    private List<ModelResult> modelResultList;
    private String[] CommonItems;
    //    private String[] jiabanItmes = new String[]{"0", "1", "2", "3"};
    private List<String> typelist;
    private TimePickerDialog timePickerDialog;
    //    private String little = "";
    private int xiangmuP = -1;
    private int renshiP = -1;
    private int zongp = -1;
    private List<User> list_chao;
    //    private List<AttendInfo> list_info;
    private final String[] fatherItems = new String[]{"请假", "加班", "外勤", "坐班", "项目", "其他"};
    private final String[] fatherCodes = new String[]{"0", "10", "9", "11", "12", "13"};
    private List<ShenHeMen> lstAppral;
    private List<AttendanceRequest> strAttendance;
    private List<String> codelist;
    private String[] commonCodes;
    private String str_attend_code;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_new_attend;
    }

    @Override
    protected void initViewsAndEvents() {
        toolbar_tv_title.setText("新建计划");
        activityUtils = new ActivityUtils(this);
        list_chao = new ArrayList<>();
        modelResultList = new ArrayList<>();
        lstAppral = new ArrayList<>();
        strAttendance = new ArrayList<>();
//        list_info = new ArrayList<>();
        if (null != getIntent().getStringExtra("Attend_Code")) {  // 申请code
            str_attend_code = getIntent().getStringExtra("Attend_Code");
        }
        pd = CustomProgressDialog.createLoadingDialog(this, "正在加载中...");
        pd.show();
        getTypes();
        initViews();


    }

    private void initViews() {
        btn_circleadd.setBounds(0, 0, btn_circleadd.getMinimumWidth(), btn_circleadd.getMinimumHeight());
        timePickerDialog = new TimePickerDialog(this);
        kq_cb_allday.setChecked(true);
        kq_et_duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (leibieP != 2) {
                    getDays();
                }
            }
        });
        kq_cb_allday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                kq_et_duration.setText("0");
                startDate = null;
                endDate = null;
                isChooseEnd = false;
                ischooseStart = false;
                kq_tv_start.setText(getResources().getString(R.string.choose_time));
                kq_tv_start.setCompoundDrawables(null, null, btn_circleadd, null);
                kq_tv_end.setText(getResources().getString(R.string.choose_time));
                kq_tv_end.setCompoundDrawables(null, null, btn_circleadd, null);
            }
        });
    }

    @Override
    protected int getToolbarType() {
        return BALD_TOOLBAR;
    }

    @OnClick({R.id.kq_tv_leibie, R.id.kq_tv_start, R.id.kq_tv_end, R.id.kq_tv_choochao, R.id.kq_tv_commit,
            R.id.kq_tv_type, R.id.kq_tv_xiangmu, R.id.kq_tv_renshi, R.id.kq_tv_zong, R.id.toolbar_iv_back, R.id.toolbar_tv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kq_tv_leibie:
//                String[] fatherItems = new String[]{"请假", "加班", "外勤", "坐班", "项目", "其他"};
                showDialog(fatherItems, kq_tv_leibie, "大", leibieP, "选类");
                break;
            case R.id.kq_tv_start:
                if (leibieP != -1) {
                    startTimePicker(kq_tv_start, 0);
                } else {
                    activityUtils.showToast("请先选择类别");
                }
                break;
            case R.id.kq_tv_end:
                if (leibieP != -1) {
                    startTimePicker(kq_tv_end, 1);
                } else {
                    activityUtils.showToast("请先选择类别");
                }
                break;
            case R.id.kq_tv_choochao:
                toContactActivity();
                break;
            case R.id.kq_tv_commit:
                sendApply();
                break;
            case R.id.kq_tv_type:
                showDialog(CommonItems, kq_tv_type, "小", commonP, "选类");
                break;
            case R.id.kq_tv_xiangmu:
                String XIANGMU = "项目";
                readAndShow(kq_tv_xiangmu, 0, XIANGMU, xiangmuP);
                break;
            case R.id.kq_tv_renshi:
                String RENSHI = "人事";
                readAndShow(kq_tv_renshi, 1, RENSHI, renshiP);
                break;
            case R.id.kq_tv_zong:
                String ZONG = "总";
                readAndShow(kq_tv_zong, 2, ZONG, zongp);
                break;
            case R.id.toolbar_iv_back:
                finish();
                break;
            case R.id.toolbar_tv_back:
                finish();
                break;
        }
    }

    private void toContactActivity() {
        Intent it = new Intent(this, SelectUserActivity.class);
        it.putExtra("type", "抄送人");
        it.putExtra("user", (Serializable) list_chao);
        startActivityForResult(it, 101);
    }

    private void readAndShow(TextView textView, int listPosition, String type, int itemPosition) {
        List<UserRange> lstUser1 = modelResultList.get(listPosition).getLstUser();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < lstUser1.size(); i++) {
            String name = lstUser1.get(i).getName();
            names.add(name);
        }
        String[] items = names.toArray(new String[names.size()]);
        showDialog(items, textView, type, itemPosition, "选人");
    }

    private void startTimePicker(final TextView textView, final int type) {
        String timePickerType;
        if (isAlldayEnabled()) {
            if (isAlldayChecked()) {
                timePickerType = "时段";
            } else {
                timePickerType = "整天";
            }
        } else {
            timePickerType = "切换";
        }
        switch (timePickerType) {
            case "时段":
                timePickerDialog.showDatePickerDialog();
                timePickerDialog.setTimePickerDialogInterface(new TimePickerDialog.TimePickerDialogInterface() {
                    @Override
                    public void positiveListener(String year, String month, String day) {
                        int m = Integer.valueOf(month) + 1;
                        if (m < 10) {
                            month = "0" + m;
                        } else {
                            month = m + "";
                        }
                        String date = year + "-" + month + "-" + day;
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        if (type == 0) {
                            String finaldate = date + " 09:00";
                            textView.setText(finaldate);
                            textView.setCompoundDrawables(null, null, null, null);
                            ischooseStart = true;
                            try {
                                startDate = format.parse(date + " 00:00");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String finaldate = date + " 18:00";
                            textView.setText(finaldate);
                            textView.setCompoundDrawables(null, null, null, null);
                            isChooseEnd = true;
                            try {
                                endDate = format.parse(date + " 23:59");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        judgeTime();
                    }

                    @Override
                    public void negativeListener() {

                    }
                });
                break;
            case "整天":
                showTimePicker(textView, type);
                break;
            case "切换":
                showTimePicker(textView, type);
                break;
        }
    }

    private void judgeTime() {
        if (isChooseEnd && ischooseStart) {
            long time1 = startDate.getTime();
            long time2 = endDate.getTime();
            if (time2 < time1) {
                isChooseEnd = false;
                ischooseStart = false;
                startDate = null;
                endDate = null;
                activityUtils.showToast("结束时间不能早于开始时间");
                kq_tv_start.setText(getResources().getString(R.string.choose_time));
                kq_tv_start.setCompoundDrawables(null, null, btn_circleadd, null);
                kq_tv_end.setText(getResources().getString(R.string.choose_time));
                kq_tv_end.setCompoundDrawables(null, null, btn_circleadd, null);
                kq_et_duration.setText("0");
            } else {
                String s = activityUtils.formatDuring(startDate, endDate, 0);
                if (!s.contains("分钟")) {
                    if (kq_et_duration.getVisibility() == View.GONE) {
                        kq_et_duration.setVisibility(View.VISIBLE);
                    }
                    kq_et_duration.setText(s);
                } else {
                    if (kq_et_duration.getVisibility() == View.VISIBLE) {
                        kq_et_duration.setVisibility(View.GONE);
                    }
//                    little = s;
                    kq_renshi.setVisibility(View.GONE);
                    kq_zong.setVisibility(View.GONE);
                    activityUtils.showToast(s);
                }
            }
        }
    }

    private boolean isAlldayEnabled() {
        return allday.getVisibility() == View.VISIBLE;
    }

    private boolean isAlldayChecked() {
        return kq_cb_allday.isChecked();
    }

    @SuppressWarnings("ConstantConditions")
    private void showDialog(final String[] items, final TextView textView, final String flag, int p, final String type) {
        DialogAdapter dialogAdapter = new DialogAdapter(this, items, p);
        @SuppressLint("InflateParams") View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_list, null, false);
        ListView listView = (ListView) dialogView.findViewById(R.id.dialog_listview);
        listView.setAdapter(dialogAdapter);
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme).create();
        alertDialog.show();
        //noinspection ConstantConditions
        alertDialog.getWindow().setContentView(dialogView);
        dialogAdapter.setOnItemClickListener(new DialogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                textView.setText(items[position]);
                textView.setCompoundDrawables(null, null, null, null);
                alertDialog.dismiss();
                if (type.equals("选类")) {
                    switch (flag) {
                        case "大":
                            refreshViews(items, position);
                            break;
                        case "小":
                            commonP = position;
                            break;
                    }
                } else {
                    savePosition(flag, position);
                }
            }
        });
        dialogAdapter.setRadioOnCheckedListener(new DialogAdapter.RadioOnCheckedListener() {
            @Override
            public void onChecked(View view, int position) {
                textView.setText(items[position]);
                textView.setCompoundDrawables(null, null, null, null);
                alertDialog.dismiss();
                if (type.equals("选类")) {
                    switch (flag) {
                        case "大":
                            refreshViews(items, position);
                            break;
                        case "小":
                            commonP = position;
                            break;
                    }
                } else {
                    savePosition(flag, position);
                }

            }
        });
    }

    private void savePosition(String type, int position) {
        switch (type) {
            case "项目":
                xiangmuP = position;
                break;
            case "人事":
                renshiP = position;
                break;
            case "总":
                zongp = position;
                break;
            /*case "类型":
                typePosition = position;
                type_done = true;
                break;*/
        }
    }

    private void refreshViews(String[] items, int position) {
        if (leibieP == -1) {
            leibieP = position;
        } else {
            if (leibieP != position) {
                commonP = -1;
                leibieP = position;
                kq_tv_type.setText(getResources().getString(R.string.kq_leixing));
                kq_tv_type.setCompoundDrawables(null, null, btn_circleadd, null);
                if (ischooseStart) {
                    startDate = null;
                    kq_tv_start.setText(getResources().getString(R.string.kq_choo_time));
                    kq_tv_start.setCompoundDrawables(null, null, btn_circleadd, null);
                    ischooseStart = false;
                }
                if (isChooseEnd) {
                    endDate = null;
                    kq_tv_end.setText(getResources().getString(R.string.kq_choo_time));
                    kq_tv_end.setCompoundDrawables(null, null, btn_circleadd, null);
                    isChooseEnd = false;
                }
                kq_et_duration.setText("0");
                if (kq_renshi.getVisibility() == View.VISIBLE) {
                    kq_renshi.setVisibility(View.GONE);
                }
                if (kq_zong.getVisibility() == View.VISIBLE) {
                    kq_zong.setVisibility(View.GONE);
                }
            }
        }

        if (position == 0 || position == 1) {
            showLeaveType(position, items[position]);
            allday.setVisibility(View.VISIBLE);
        } else {
            if (position == 2) {
                allday.setVisibility(View.GONE);
                kq_leavType.setVisibility(View.GONE);
            } else {
                allday.setVisibility(View.VISIBLE);
                kq_leavType.setVisibility(View.GONE);
            }
        }
    }

    private void showLeaveType(int position, String s) {
        allday.setVisibility(View.VISIBLE);
        kq_leavType.setVisibility(View.VISIBLE);
        kq_tv_child.setText(String.format(getResources().getString(R.string.kq_leaveType), s));
        switch (position) {
            case 0:
                CommonItems = typelist.toArray(new String[typelist.size()]);
                commonCodes = codelist.toArray(new String[codelist.size()]);
                break;
            case 1:
                CommonItems = new String[]{"晚班", "法定节假日加班", "周末加班"};
                commonCodes = new String[]{"1", "2", "3"};
                break;
        }
    }

    private void showTimePicker(final TextView textView, final int type) {
        SlideDateTimeListener listener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String time = mFormatter.format(date);
                textView.setText(time);
                textView.setCompoundDrawables(null, null, null, null);
                if (type == 0) {
                    ischooseStart = true;
                    startDate = date;
                } else {
                    isChooseEnd = true;
                    endDate = date;
                }
                judgeTime();
            }

            @Override
            public void onDateTimeCancel() {
            }
        };
        SlideDateTimePicker.Builder builder = new SlideDateTimePicker.Builder(getSupportFragmentManager());
        builder.setListener(listener);
        builder.setMinDate(new Date());
        if (type == 0) {
            if (startDate != null) {
                builder.setInitialDate(startDate);
            } else {
                builder.setInitialDate(new Date());
            }
        } else {
            if (startDate != null) {
                builder.setInitialDate(endDate);
            } else {
                builder.setInitialDate(new Date());
            }
        }
        builder.setIs24HourTime(true)
                .build()
                .show();
    }

    private void getTypes() {
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("codeTables", "attendance_type"));
        String url = DemoApplication.serviceUrl + "/GetCodeListByName";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        activityUtils.showToast(e.toString());
                        pd.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<AttendType> attendTypes;
                        String success, result, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                result = jsonObject.getString("result");
                                JSONObject js = new JSONObject(result);
                                attendTypes = new Gson().fromJson(js.getString("attendance_type"), new TypeToken<List<AttendType>>() {
                                }.getType());
                                typelist = new ArrayList<>();
                                codelist = new ArrayList<>();
//                                jiaBanTypelist = new ArrayList<>();
//                                List<String> jiaBanCodelist = new ArrayList<>();
                                for (int i = 0; i < attendTypes.size(); i++) {
                                    String name = attendTypes.get(i).getName();
                                    String pName = attendTypes.get(i).getPName();
                                    if (pName.equals("请假")) {
                                        typelist.add(name);
                                        codelist.add(attendTypes.get(i).getCode());
                                    }
//                                    if (pName.equals("加班")) {
//                                        jiaBanTypelist.add(name);
//                                        jiaBanCodelist.add(attendTypes.get(i).getCode());
//                                    }
                                }
//                                String[] leaveType = typelist.toArray(new String[typelist.size()]);
//                                String[] leaveCodes = codelist.toArray(new String[codelist.size()]);
//                                String[] jiabanType = jiaBanTypelist.toArray(new String[jiaBanTypelist.size()]);
//                                String[] jiabanCode = jiaBanCodelist.toArray(new String[jiaBanCodelist.size()]);
                                getModel();
                            } else {
                                activityUtils.showToast(msg);
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getModel() {
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", code, "TypeCode", "3"));
        String url = DemoApplication.serviceUrl + "/GetAttendanceTemplateByTypeCode";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        activityUtils.showToast("获取模板失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        modelResultList.clear();
                        String success, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                pd.dismiss();
                                JSONArray array = jsonObject.getJSONArray("result");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject model = array.getJSONObject(i);
                                    ModelResult modelResult = new ModelResult();
                                    modelResult.setDays(model.getDouble("Days"));
                                    modelResult.setName(model.getString("Name"));
                                    modelResult.setTypeCode(model.getString("TypeCode"));
                                    JSONArray lstUser = model.getJSONArray("lstUser");
                                    List<UserRange> userRanges = new ArrayList<>();
                                    for (int j = 0; j < lstUser.length(); j++) {
                                        JSONObject user = lstUser.getJSONObject(j);
                                        UserRange userRange = new UserRange();
                                        userRange.setCode(user.getString("Code"));
                                        userRange.setName(user.getString("Name"));
                                        userRanges.add(userRange);
                                    }
                                    modelResult.setLstUser(userRanges);
                                    modelResultList.add(modelResult);
                                    if (userRanges.size() == 1) {
                                        autoChoose(i, userRanges.get(0).getName());
                                    }
                                }
//                                if (!modelResultList.isEmpty()) {
//                                    List<UserRange> lstUser = modelResultList.get(0).getLstUser();
//                                    if (!lstUser.isEmpty()) {
//                                        if (lstUser.size() == 1) {
//                                            tv_xiangmu.setText(lstUser.get(0).getName());
//                                        }
//                                    }
//                                }
                            } else {
                                Toast.makeText(NewAttendActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void autoChoose(int Position, String name) {
        switch (Position) {
            case 0:
                xiangmuP = 0;
                kq_tv_xiangmu.setText(name);
                kq_tv_xiangmu.setCompoundDrawables(null, null, null, null);
                break;
            case 1:
                renshiP = 0;
                kq_tv_renshi.setText(name);
                kq_tv_renshi.setCompoundDrawables(null, null, null, null);
                break;
            case 2:
                zongp = 0;
                kq_tv_zong.setText(name);
                kq_tv_zong.setCompoundDrawables(null, null, null, null);
                break;
        }

    }


    private void getDays() {
        double renshiDays = 0.0;
        double zongDays = 0.0;
        if (!modelResultList.isEmpty()) {
            if (modelResultList.size() == 3) {
                renshiDays = modelResultList.get(1).getDays();
                zongDays = modelResultList.get(2).getDays();
            }
        }
        String days = kq_et_duration.getText().toString().trim();
        boolean isCan = !days.equals("");
        if (isCan) {
            double v = activityUtils.convertToDouble(days, -1);
            if (v >= 0.0) {
                if (v > renshiDays && v < zongDays) {
                    kq_renshi.setVisibility(View.VISIBLE);
                    kq_zong.setVisibility(View.GONE);
                } else if (v >= zongDays) {
                    kq_renshi.setVisibility(View.VISIBLE);
                    kq_zong.setVisibility(View.VISIBLE);
                } else {
                    kq_renshi.setVisibility(View.GONE);
                    kq_zong.setVisibility(View.GONE);
                }
            } else {
                activityUtils.showToast("天数须为数字且不能小于0");
            }

        } else {
            activityUtils.showToast("请先填写请假天数");
        }
    }


    private void sendApply() {
        lstAppral.clear();
        strAttendance.clear();
        boolean duration = !kq_et_duration.getText().toString().trim().equals("");
        boolean reasons = !kq_et_content.getText().toString().trim().equals("");
        boolean location = !kq_et_destination.getText().toString().trim().equals("");
        boolean isAllTypeDone;
        if (leibieP == -1) {
            isAllTypeDone = false;
        } else if (leibieP == 0 || leibieP == 1) {
            if (commonP == -1) {
                isAllTypeDone = false;
            } else {
                isAllTypeDone = true;
            }
        } else {
            isAllTypeDone = true;
        }
        if (location && duration && reasons && isAllTypeDone && ischooseStart && isChooseEnd && (xiangmuP != -1)) {
            if (!list_chao.isEmpty()) {
                ShenHeMen chaosong = new ShenHeMen();
                chaosong.setIsCCUser("1");
                User user = list_chao.get(0);
                chaosong.setAppralUserCode(user.getCode());
                lstAppral.add(chaosong);
            }
            ShenHeMen xiangmu = new ShenHeMen();
            xiangmu.setIsCCUser("0");
            List<UserRange> lstUser = modelResultList.get(0).getLstUser();
            String xiangmuCode = lstUser.get(xiangmuP).getCode();
            xiangmu.setAppralUserCode(xiangmuCode);
            lstAppral.add(xiangmu);
            if (kq_renshi.getVisibility() == View.VISIBLE) {
                ShenHeMen renshi = new ShenHeMen();
                renshi.setIsCCUser("0");
                List<UserRange> lstUser1 = modelResultList.get(1).getLstUser();
                String renshiCode = lstUser1.get(renshiP).getCode();
                renshi.setAppralUserCode(renshiCode);
                lstAppral.add(renshi);
            }
            if (kq_zong.getVisibility() == View.VISIBLE) {
                ShenHeMen zong = new ShenHeMen();
                zong.setIsCCUser("0");
                List<UserRange> lstUser2 = modelResultList.get(2).getLstUser();
                String zongCode = lstUser2.get(zongp).getCode();
                zong.setAppralUserCode(zongCode);
                lstAppral.add(zong);
            }
            AttendanceRequest attendanceRequest = new AttendanceRequest();
//            private final String[] fatherItems = new String[]{"请假", "加班", "外勤", "坐班", "项目", "其他"};
            if (leibieP == 0) {
                attendanceRequest.setTypeCode(commonCodes[commonP]);
                attendanceRequest.setIsHoliday("0");
            } else if (leibieP == 1) {
                attendanceRequest.setTypeCode("10");
                attendanceRequest.setIsHoliday(commonCodes[commonP]);
            } else {
                attendanceRequest.setTypeCode(fatherCodes[leibieP]);
                attendanceRequest.setIsHoliday("0");
            }
            if (kq_cb_allday.isChecked()) {
                attendanceRequest.setStartTime(kq_tv_start.getText().toString().trim());
                attendanceRequest.setEndTime(kq_tv_end.getText().toString().trim());
            } else {
                attendanceRequest.setStartTime(kq_tv_start.getText().toString().trim());
                attendanceRequest.setEndTime(kq_tv_end.getText().toString().trim());
            }
            attendanceRequest.setLocation(kq_et_destination.getText().toString().trim());
            attendanceRequest.setLong(kq_et_duration.getText
                    ().toString().trim());
            attendanceRequest.setReason(kq_et_content.getText().toString());
            attendanceRequest.setLstAppral(lstAppral);
            strAttendance.add(attendanceRequest);
            pd = CustomProgressDialog.createLoadingDialog(this, "正在提交...");
            pd.show();
            HashMap<String, String> params = new HashMap<>();
            Map<String, Object> param = new HashMap<>();
            param.put("LoginUserCode", code);
            param.put("strAttendance", strAttendance);
            Gson gson = new Gson();
            params.put("paraJson", gson.toJson(param));
            Log.e("输出的参数", gson.toJson(param));

            String url = DemoApplication.serviceUrl + "/NewAttendance";
            OkHttpUtils.post()
                    .url(url)
                    .params(params)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (e instanceof SocketTimeoutException) {
                                activityUtils.showToast("网络连接超时");
                            } else {
                                activityUtils.showToast(e.toString());
                            }
                            pd.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            String success, msg;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                success = jsonObject.getString("success");
                                msg = jsonObject.getString("msg");
                                if (success.equals("true")) {
                                    pd.dismiss();
                                    activityUtils.showToast("申请已提交");
                                    Intent it = new Intent("new_information");
                                    sendBroadcast(it);
                                    finish();
                                } else {
                                    pd.dismiss();
                                    activityUtils.showToast(msg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            activityUtils.showToast("请完善申请信息");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            switch (resultCode) {
                case CHAO_CODE:
                    list_chao.clear();
                    //noinspection unchecked
                    list_chao = (List<User>) data.getSerializableExtra("new_add_user");
                    User user1 = list_chao.get(0);
                    String chao = user1.getName();
                    kq_tv_choochao.setText(chao);
                    kq_tv_choochao.setCompoundDrawables(null, null, null, null);
                    break;
            }
        }
    }
}
