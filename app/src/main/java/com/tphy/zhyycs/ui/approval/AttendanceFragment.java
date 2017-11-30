package com.tphy.zhyycs.ui.approval;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jimmy.common.util.ToastUtils;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.approval.activity.AttendanceInfoActivity;
import com.tphy.zhyycs.ui.approval.adapter.MyPagerAdapter;
import com.tphy.zhyycs.ui.approval.bean.QRCodeTypes;
import com.tphy.zhyycs.ui.attandence.NewAttendActivity;
import com.tphy.zhyycs.ui.calendar.fragment.ScheduleFragment;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.StringCallback;
import com.tphy.zhyycs.widget.circleview.CirclePercentView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 考勤
 * Created by Administrator on 2017\9\21 0021.
 */

public class AttendanceFragment extends Fragment {


    private Unbinder unbinder;
    @BindView(R.id.tv_back_img)
    TextView tv_backImag;
    @BindView(R.id.tv_back)
    TextView mTvBackImg;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.img_right)
    ImageView mImgRight;
    @BindView(R.id.btn_right)
    Button mBtnRight;
    @BindView(R.id.btn_right_2)
    Button mBtnRight2;
    @BindView(R.id.kq_view_pager)
    ViewPager viewPager;
    @BindView(R.id.kq_tv_month)
    TextView tv_month;
    @BindView(R.id.kq_tv_qingjia)
    TextView kq_qingjia;
    @BindView(R.id.kq_tv_jiaban)
    TextView kq_jiaban;
    @BindView(R.id.kq_tv_waiqin)
    TextView kq_waiqin;
    @BindString(R.string.jiaban)
    String jiaban;
    @BindString(R.string.qingjia)
    String qingjia;
    @BindString(R.string.waiqin)
    String waiqin;
    private Context context;
    private List<View> topviews;
    private String[] mMonthText;
    private String loginUserCode;
    private MyPagerAdapter myPagerAdapter;
    private boolean isRenShi;
    private List<QRCodeTypes> qrCodeTypesList;
    private boolean canSendQRCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_attendance_manager, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initData() {
        try {
            InputStream is = context.getAssets().open("holiday.json");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            Map<String, int[]> sAllHolidays;
            sAllHolidays = new Gson().fromJson(baos.toString(), new TypeToken<Map<String, int[]>>() {
            }.getType());
            CalendarUtils.getInstance(context).setAllHolidays(sAllHolidays);
        } catch (IOException e) {
            e.printStackTrace();
        }
        topviews = new ArrayList<>();
//        monthAttendanceList = new ArrayList<>();
        SharedPreferences preferences = getActivity().getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        loginUserCode = preferences.getString("Code", "");
        String permission = preferences.getString("Permission", "");
        isRenShi = permission.contains("SignedSupplement");
        canSendQRCode = permission.contains("SendQRCode");
//        @SuppressLint("InflateParams") View view0 = LayoutInflater.from(context).inflate(R.layout.top_view, null, false);
        @SuppressLint("InflateParams") View view2 = LayoutInflater.from(context).inflate(R.layout.top_view2, null, false);
//        @SuppressLint("InflateParams") View view1 = LayoutInflater.from(context).inflate(R.layout.top_view, null, false);
//        @SuppressLint("InflateParams") View view_bulu = LayoutInflater.from(context).inflate(R.layout.top_view_bulu, null, false);
        TextView tv_showQR = (TextView) view2.findViewById(R.id.kq_tv_showQr);
        TextView tv_buqian = (TextView) view2.findViewById(R.id.kq_tv_showbuQian);
        if (canSendQRCode) {
            tv_showQR.setVisibility(View.VISIBLE);
        } else {
            tv_showQR.setVisibility(View.GONE);
        }
        if (isRenShi) {
            tv_buqian.setVisibility(View.VISIBLE);
        } else {
            tv_buqian.setVisibility(View.GONE);
        }
//        topviews.add(view0);
        topviews.add(view2);
//        topviews.add(view1);
//        if (isRenShi) {
//            topviews.add(view_bulu);
//            iv_indicator.setImageDrawable(indi_4_1);
//        } else {
//            iv_indicator.setImageDrawable(indicator_middle);
//        }
        mMonthText = getContext().getResources().getStringArray(R.array.calendar_month);
        qrCodeTypesList = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*新建考勤，暂时隐藏*/
        mImgRight.setVisibility(View.GONE);
        tv_backImag.setVisibility(View.GONE);
        mTvBackImg.setText("详情");
        mImgRight.setImageResource(R.drawable.btn_add);
        mTvTitle.setText("考勤");
        context = getActivity();
        initData();
        initView();

    }

    private void initView() {
        myPagerAdapter = new MyPagerAdapter(topviews, context);
        viewPager.setAdapter(myPagerAdapter);
        myPagerAdapter.setLoginUserCode(loginUserCode);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                View view = topviews.get(position);
//                current = position;
//                if (position == 1) {
//                    if (isRenShi) {
//                        iv_indicator.setImageDrawable(indi_4_1);
//                    } else {
//                        iv_indicator.setImageDrawable(indicator_middle);
//                    }
//                } else {
//                    TextView tv_percent = (TextView) view.findViewById(R.id.kq_tv_percent);
//                    CirclePercentView circlePercentView = (CirclePercentView) view.findViewById(R.id.kq_percentview);
//                    TextView tv_plan = (TextView) view.findViewById(R.id.kq_tv_plan);
//                    if (position == 0) {
//                        tv_plan.setText("已完成");
//                        if (isRenShi) {
//                            iv_indicator.setImageDrawable(indi_4_0);
//                        } else {
//                            iv_indicator.setImageDrawable(indicator_left);
//                        }
//                    } else if (position == 2) {
//                        tv_plan.setText("未完成");
//                        if (isRenShi) {
//                            iv_indicator.setImageDrawable(indi_4_2);
//                        } else {
//                            iv_indicator.setImageDrawable(indicator_right);
//                        }
//                    } else if (position == 3) {
//                        iv_indicator.setImageDrawable(indi_4_3);
//                    }
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.kq_frame, scheduleFragment);
        transaction.commit();

        mTvBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), AttendanceInfoActivity.class);
                startActivity(it);
            }
        });
        getQRCodeTypes();
    }

    public void resetMainTitleDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        if (year == calendar.get(Calendar.YEAR) &&
                month == calendar.get(Calendar.MONTH) &&
                day == calendar.get(Calendar.DAY_OF_MONTH)) {
            tv_month.setText(mMonthText[month]);
        } else {
            if (year == calendar.get(Calendar.YEAR)) {
                tv_month.setText(mMonthText[month]);
            } else {
                tv_month.setText(String.format("%s%s", String.format(getString(R.string.calendar_year), year),
                        mMonthText[month]));
            }
        }
        String m;
        if (month < 9) {
            m = "0" + (month + 1);
        } else {
            m = String.valueOf(month + 1);
        }
        String date = String.valueOf(year) + "-" + m;
        getMonthAttendances(date);
//        setCurrentSelectDate(year, month + 1, day);
    }

    public void updateTaskInfo(int total, int finished) {
        int percent = 0;
        if (total != 0) {
            percent = finished * 100 / total;
        }
        List<View> viewList = myPagerAdapter.getViewList();
        for (int i = 0; i < viewList.size(); i++) {
            if (i != 1) {
                View view = viewList.get(i);
                TextView tv_percent = (TextView) view.findViewById(R.id.kq_tv_percent);
                CirclePercentView circlePercentView = (CirclePercentView) view.findViewById(R.id.kq_percentview);
                TextView tv_plan = (TextView) view.findViewById(R.id.kq_tv_plan);
                if (i == 0) {
                    tv_plan.setText("已完成");
                    if (total != 0) {
                        String text = finished + "/" + total;
                        tv_percent.setText(text);
                        circlePercentView.setPercent(100 - percent);
                    } else {
                        tv_percent.setText("0");
                        circlePercentView.setPercent(100);
                    }

                } else {
                    tv_plan.setText("未完成");
                    if (total != 0) {
                        String text = (total - finished) + "/" + total;
                        tv_percent.setText(text);
                        circlePercentView.setPercent(percent);
                    } else {
                        tv_percent.setText("0");
                        circlePercentView.setPercent(100);
                    }

                }
            }
        }

    }

    public void updateCheckingState(String state) {
        if (null != myPagerAdapter) {
            View view = myPagerAdapter.getViewList().get(1);
            TextView tv_state = (TextView) view.findViewById(R.id.kq_tv_state);
            tv_state.setText(state);
        }
    }

//    private void setCurrentSelectDate(int year, int month, int day) {
//        mCurrentSelectYear = year;
//        mCurrentSelectMonth = month;
//        mCurrentSelectDay = day;
//    }

    @OnClick(R.id.img_right)
    public void pupout() {
//        NewAttendActivity
        Intent intent = new Intent(context, NewAttendActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getMonthAttendances(String date) {
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", loginUserCode, "Date", date));
        String url = DemoApplication.serviceUrl + "/GetMonthAttendanceByUser";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        ToastUtils.showToast(getContext(), e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                kq_jiaban.setText("加班：0次");
                                kq_qingjia.setText("请假：0次");
                                kq_waiqin.setText("外勤：0次");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String pName = object.getString("PName");
                                    String count = object.getString("Count");
                                    switch (pName) {
                                        case "加班":
                                            kq_jiaban.setText(String.format(jiaban, count));
                                            break;
                                        case "请假":
                                            kq_qingjia.setText(String.format(qingjia, count));
                                            break;
                                        case "外出":
                                            kq_waiqin.setText(String.format(qingjia, count));
                                            break;
                                        case "外勤":
                                            kq_waiqin.setText(String.format(qingjia, count));
                                            break;
                                    }
                                }
                            } else {
                                ToastUtils.showToast(getContext(), msg);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

//    public void setDakaType(String type) {
//        if (null != myPagerAdapter) {
//            myPagerAdapter.setDakaLeiBie(type);
//        }
//    }

    private void getQRCodeTypes() {
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", "");
        String url = DemoApplication.serviceUrl + "/GetQRCodeType";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        ToastUtils.showToast(getContext(), e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                JSONArray result = jsonObject.getJSONArray("result");
                                qrCodeTypesList.clear();
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject object = result.getJSONObject(i);
                                    String code = object.getString("Code");
                                    String name = object.getString("Name");
                                    String pydm = object.getString("PYDM");
                                    QRCodeTypes qrCodeTypes = new QRCodeTypes();
                                    qrCodeTypes.setCode(code);
                                    qrCodeTypes.setName(name);
                                    qrCodeTypes.setPYDM(pydm);
                                    qrCodeTypesList.add(qrCodeTypes);
                                }
                                if (null != myPagerAdapter) {
                                    myPagerAdapter.setQrCodeTypes(qrCodeTypesList);
                                }
                            } else {
                                ToastUtils.showToast(getContext(), msg);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
