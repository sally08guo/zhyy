package com.tphy.zhyycs.ui.calendar.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleRecyclerView;
import com.jimmy.common.base.app.BaseFragment;
import com.jimmy.common.bean.Schedule;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.approval.AttendanceFragment;
import com.tphy.zhyycs.ui.calendar.adapter.ScheduleAdapter;
import com.tphy.zhyycs.utils.ActivityUtils;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class ScheduleFragment extends BaseFragment implements OnCalendarClickListener {

    private ScheduleLayout slSchedule;
    private RelativeLayout rLNoTask;

    private ScheduleAdapter mScheduleAdapter;
    private List<Schedule> scheduleList;
    private String loginUserCode;
    private ActivityUtils activityUtils;
    private boolean isWeekend;
    private String dateNowStr;
    private boolean isFirst = true;
    private int mCurrentSelectYear;
    private int mCurrentSelectMonth;
    private int mCurrentSelectDay;

//    public static ScheduleFragment getInstance() {
//        return new ScheduleFragment();
//    }

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    protected void bindView() {
        scheduleList = new ArrayList<>();
        slSchedule = searchViewById(R.id.slSchedule);
        rLNoTask = searchViewById(R.id.rl_NoTask);
        slSchedule.setOnCalendarClickListener(this);
        SharedPreferences preferences = getParentFragment().getActivity().getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        loginUserCode = preferences.getString("Code", "");
        activityUtils = new ActivityUtils(this);
        initScheduleList();
    }

    @Override
    protected void initData() {
        super.initData();
        initDate();
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        setCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClickDate(int year, int month, int day) {
        setCurrentSelectDate(year, month, day);
//        resetScheduleList();
        String m;
        if (month < 9) {
            m = "0" + (month + 1);
        } else {
            m = String.valueOf(month + 1);
        }
        String d;
        if (day < 10) {
            d = "0" + day;
        } else {
            d = String.valueOf(day);
        }
        String date = String.valueOf(year) + "-" + m + "-" + d;
        isWeekend = dateToWeek(date);
        Log.e("WQ", "Date是===》" + date);
        getTodaySchedules(date);

    }

    @Override
    public void onPageChange(int year, int month, int day) {

    }

    private void initScheduleList() {
        ScheduleRecyclerView rvScheduleList = slSchedule.getSchedulerRecyclerView();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvScheduleList.setLayoutManager(manager);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        rvScheduleList.setItemAnimator(itemAnimator);
        mScheduleAdapter = new ScheduleAdapter(getContext(), scheduleList);
        rvScheduleList.setAdapter(mScheduleAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst) {
            Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH) + 1;
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            String day;
            if (mDay < 10) {
                day = "0" + mDay;
            } else {
                day = String.valueOf(mDay);
            }
            dateNowStr = mYear + "-" + mMonth + "-" + day;
            getTodaySchedules(dateNowStr);
            Log.e("WQ", "当天的日期是===》" + dateNowStr);
            isFirst = false;
        } else {
            String m;
            if (mCurrentSelectMonth < 9) {
                m = "0" + (mCurrentSelectMonth + 1);
            } else {
                m = String.valueOf(mCurrentSelectMonth + 1);
            }
            String d;
            if (mCurrentSelectDay < 10) {
                d = "0" + mCurrentSelectDay;
            } else {
                d = String.valueOf(mCurrentSelectDay);
            }
            String date = String.valueOf(mCurrentSelectYear) + "-" + m + "-" + d;
            isWeekend = dateToWeek(date);
            Log.e("WQ", "Date是===》" + date);
            getTodaySchedules(date);
        }

    }

    /*
    * 添加plan*/

    private void setCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
        if (parentFragment instanceof AttendanceFragment) {
            ((AttendanceFragment) parentFragment).resetMainTitleDate(year, month, day);
        }
    }


    private void getTodaySchedules(final String date) {
        if (!scheduleList.isEmpty()) {
            scheduleList.clear();
        }
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", loginUserCode, "Date", date));
        String url = DemoApplication.serviceUrl + "/GetTodayPunch";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        activityUtils.showToast(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                JSONArray json = jsonObject.getJSONArray("result");
                                int finished = 0;
                                int tastNum = 0;
                                for (int i = 0; i < json.length(); i++) {
                                    Schedule schedule = new Schedule();
                                    JSONObject plan = json.getJSONObject(i);
                                    schedule.setPlanplace(plan.getString("Location"));/*新字段*/
                                    String pName = plan.getString("Name");/*新字段*/
                                    schedule.setPName(pName);
//                                    schedule.setPlanStartTime(plan.getString("StartTime"));
//                                    schedule.setPlancontent(plan.getString("Reason"));
                                    String code = plan.getString("Code");/*新字段*/
                                    String sendTime = plan.getString("SendTime");/*新字段*/
                                    schedule.setSendTime(sendTime);
//                                    String status = plan.getString("STATUS");
                                    schedule.setCode(code);
                                    schedule.setPlanStartTime("***");
                                    schedule.setPlanEndTime("***");
//                                    schedule.setPlanEndTime(plan.getString("EndTime"));
//                                    schedule.setReason(plan.getString("Reason"));
//                                    schedule.setSTATUS(status);
//                                    String signInTime = plan.getString("SignInTime");
//                                    if (null == signInTime) {
//                                        signInTime = "";
//                                    }
//                                    String signOffTime = plan.getString("SignOffTime");
//                                    if (null == signInTime) {
//                                        signOffTime = "";
//                                    }
//                                    schedule.setSignInTime(signInTime);
//                                    schedule.setSignOffTime(signOffTime);
//                                    /*判断选择日期是否为当天，防止非当天打卡*/
//                                    schedule.setIsToday(date.equals(dateNowStr));
                                    scheduleList.add(schedule);
//                                    if (date.equals(dateNowStr) && code.equals("0") && status.equals("0")) {
//                                        if (parentFragment instanceof AttendanceFragment) {
//                                            ((AttendanceFragment) parentFragment).setDakaType("1");
//                                            ((AttendanceFragment) parentFragment).updateCheckingState("上班点击签到");
//                                        }
//                                    } else if (date.equals(dateNowStr) && code.equals("0") && status.equals("1")) {
//                                        if (parentFragment instanceof AttendanceFragment) {
//                                            ((AttendanceFragment) parentFragment).setDakaType("2");
//                                            ((AttendanceFragment) parentFragment).updateCheckingState("下班点击签离");
//                                        }
//                                    } else if (date.equals(dateNowStr) && code.equals("0") && status.equals("2")) {
//                                        if (parentFragment instanceof AttendanceFragment) {
//                                            ((AttendanceFragment) parentFragment).setDakaType("5");
//                                            ((AttendanceFragment) parentFragment).updateCheckingState("已签离");
//                                        }
//                                    }
//
//                                    if (pName.equals("加班") || pName.equals("外勤") || pName.equals("0")) {
//                                        tastNum++;
//                                        if (status.equals("2")) {
//                                            finished++;
//                                        }
//                                    }
                                }

                                if (scheduleList.isEmpty()) {
                                    rLNoTask.setVisibility(View.VISIBLE);
                                } else {
                                    rLNoTask.setVisibility(View.GONE);
                                    mScheduleAdapter.setmSchedules(scheduleList);
                                }
                                if (parentFragment instanceof AttendanceFragment) {
                                    ((AttendanceFragment) parentFragment).updateTaskInfo(tastNum, finished);
                                }
                            } else {
                                activityUtils.showToast(msg);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /*判断是否是双休日*/
    private boolean dateToWeek(String datetime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
//        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return 0 == w || 6 == w;
    }
}