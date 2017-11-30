package com.tphy.zhyycs.ui.calendar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jimmy.common.bean.Schedule;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.approval.activity.AttendanceItemActivity;
import com.tphy.zhyycs.ui.attandence.AttendInfoActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.PlanViewHolder> implements View.OnClickListener {

    private final Context mContext;
    private List<Schedule> mSchedules;
    private Intent intent;

    public ScheduleAdapter(Context context, List<Schedule> list) {
        mContext = context;
//        BaseFragment mBaseFragment = baseFragment;
        this.mSchedules = list;
    }

    public void setmSchedules(List<Schedule> list) {
        this.mSchedules = list;
        notifyDataSetChanged();
    }

    @Override
    public ScheduleAdapter.PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduleAdapter.PlanViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_kq, parent, false));
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.PlanViewHolder planholder, int position) {
        Schedule schedule = mSchedules.get(position);
        String startTime = schedule.getPlanStartTime();
        String endTime = schedule.getPlanEndTime();
        String place = schedule.getPlanplace();
        String pName = schedule.getPName();
        final String code = schedule.getCode();
        String sendTime = schedule.getSendTime();
//        String signInTime = schedule.getSignInTime();
//        String signOffTime = schedule.getSignOffTime();
//        String starttime;
//        String endtime;
//        if (code.equals("0")) {
//            starttime = "09:00 上午";
//            endtime = "18:00 下午";
//            content = "全天打卡";
//            place = "";
//        } else {
        if (!sendTime.equals("未签到")) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date sendDate = null;
            try {
                sendDate = format.parse(sendTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormatter = new SimpleDateFormat("HH:mm:ss");
            sendTime = mFormatter.format(sendDate);
        }

/*这两个是任务起止时间的textview*/
        planholder.tv_time.setText(startTime);
        planholder.tv_endtime.setText(endTime);
/*这两个是任务起止时间的textview*/
        planholder.tv_content.setText(pName);
        planholder.tv_place.setText(place);
        planholder.tv_signinTime.setText("签到时间："+sendTime);

//        if (!"".equals(signInTime)) {
//            String hms = getHMS(signInTime);
//            planholder.tv_signinTime.setText("签到：" + hms);
//        } else {
//            planholder.tv_signinTime.setText(signInTime);
//        }
//
//        if (!"".equals(signOffTime)) {
//            String hms = getHMS(signOffTime);
//            planholder.tv_signOffTime.setText("签退：" + hms);
//        } else {
//            planholder.tv_signinTime.setText(signOffTime);
//        }

           /*如果不是当天不让他打卡*/
//        if (status.equals("2")) {
//            planholder.tv_qian.setClickable(false);
//        }
//        intent = new Intent(mContext, AttendanceItemActivity.class);
//        intent.putExtra("AttendanceCode", code);
//        if ("请假".equals(pName)) {
//            planholder.tv_qian.setVisibility(View.VISIBLE);
//            planholder.tv_content.setTextColor(Color.parseColor("#ff5858"));
//            planholder.state_view.setBackgroundColor(Color.parseColor("#ff5858"));
//            planholder.state_area.setBackgroundColor(Color.parseColor("#ffe6e6"));
//            planholder.tv_place.setTextColor(Color.parseColor("#ff7171"));
//            planholder.tv_qian.setTextColor(Color.parseColor("#ff5858"));
//            planholder.tv_qian.setText("请假");
//            planholder.tv_qian.setBackgroundDrawable(null);
//            planholder.tv_qian.setOnClickListener(this);
//            planholder.tv_qian.setClickable(false);
//        } else if ("加班".equals(pName) || "外勤".equals(pName)) {
//            planholder.tv_qian.setVisibility(View.VISIBLE);
//            if ("加班".equals(pName)) {
//                planholder.tv_content.setTextColor(Color.parseColor("#c37700"));
//                planholder.state_view.setBackgroundColor(Color.parseColor("#c37700"));
//                planholder.state_area.setBackgroundColor(Color.parseColor("#f6ebd9"));
//                planholder.tv_place.setTextColor(Color.parseColor("#ca8a36"));
//            } else {
//                planholder.tv_content.setTextColor(Color.parseColor("#015198"));
//                planholder.state_view.setBackgroundColor(Color.parseColor("#015198"));
//                planholder.state_area.setBackgroundColor(Color.parseColor("#dae5ee"));
//                planholder.tv_place.setTextColor(Color.parseColor("#5b82b1"));
//            }
//            planholder.tv_qian.setTextColor(Color.parseColor("#015198"));
//            if (status.equals("0")) {
//                intent.putExtra("LeiBie", "3");
//                planholder.tv_qian.setText("签到");
//                planholder.tv_qian.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_qiandao));
//                planholder.tv_qian.setOnClickListener(this);
//                planholder.tv_qian.setClickable(false);
//
//            } else if (status.equals("1")) {
//                intent.putExtra("LeiBie", "4");
//                planholder.tv_qian.setText("签离");
//                planholder.tv_qian.setTextColor(Color.parseColor("#ff5858"));
//                planholder.tv_qian.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_qiantui));
//                planholder.tv_qian.setOnClickListener(this);
//                planholder.tv_qian.setClickable(false);
//
//            } else if (status.equals("2")) {
//                planholder.tv_qian.setText("已结束");
//                planholder.tv_qian.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_qianwan));
//                planholder.tv_qian.setTextColor(Color.parseColor("#999999"));
//                planholder.tv_qian.setOnClickListener(this);
//                planholder.tv_qian.setClickable(false);
//
//            }
//        } else if ("坐班".equals(pName) || "项目".equals(pName) || "其他".equals(pName)) {
//            planholder.tv_qian.setVisibility(View.VISIBLE);
//            if (pName.equals("坐班")) {
//                planholder.tv_qian.setText("坐班");
//
//            } else if (pName.equals("项目")) {
//                planholder.tv_qian.setText("项目");
//
//            } else if (pName.equals("其他")) {
//                planholder.tv_qian.setText("其他");
//
//            }
//            planholder.tv_content.setTextColor(Color.parseColor("#333333"));
//            planholder.state_view.setBackgroundColor(Color.parseColor("#333333"));
//            planholder.state_area.setBackgroundColor(Color.parseColor("#ffffff"));
//            planholder.tv_place.setTextColor(Color.parseColor("#999999"));
//            planholder.tv_qian.setBackgroundDrawable(null);
//            planholder.tv_qian.setOnClickListener(this);
//            planholder.tv_qian.setClickable(false);
//        } else if ("0".equals(pName) || "0".equals(code)) {
//            planholder.tv_content.setTextColor(Color.parseColor("#333333"));
//            planholder.state_view.setBackgroundColor(Color.parseColor("#333333"));
//            planholder.state_area.setBackgroundColor(Color.parseColor("#ffffff"));
//            planholder.tv_place.setTextColor(Color.parseColor("#999999"));
//            planholder.tv_qian.setVisibility(View.GONE);
//            if (status.equals("0")) {
//                intent.putExtra("LeiBie", "1");
//                planholder.tv_qian.setText("签到");
//                planholder.tv_qian.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_qiandao));
//                planholder.tv_qian.setTextColor(Color.parseColor("#015198"));
//                planholder.tv_qian.setOnClickListener(this);
//                planholder.tv_qian.setClickable(today);
//
//            } else if (status.equals("1")) {
//                intent.putExtra("LeiBie", "2");
//                planholder.tv_qian.setText("签离");
//                planholder.tv_qian.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_qiantui));
//                planholder.tv_qian.setTextColor(Color.parseColor("#ff5858"));
//                planholder.tv_qian.setOnClickListener(this);
//                planholder.tv_qian.setClickable(today);
//
//            } else if (status.equals("2")) {
//                planholder.tv_qian.setText("已结束");
//                planholder.tv_qian.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_qianwan));
//                planholder.tv_qian.setTextColor(Color.parseColor("#999999"));
//                planholder.tv_qian.setOnClickListener(this);
//                planholder.tv_qian.setClickable(false);
//            }
//        }
//        if (!"0".equals(pName)) {
//            planholder.kq_plan.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(mContext, AttendInfoActivity.class);
//                    intent.putExtra("task_code", code);
//                    Log.e("传递的code", code);
//                    mContext.startActivity(intent);
//                }
//            });
//        }

    }


    @Override
    public int getItemCount() {
        return mSchedules.size();
    }

    @Override
    public void onClick(View view) {
        if (null != intent) {
            mContext.startActivity(intent);
        }
    }


    protected class PlanViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_time;
        private final View state_view;
        private final LinearLayout state_area;
        private final TextView tv_content;
        private final TextView tv_place;
        private final RelativeLayout kq_plan;
        private final TextView tv_endtime;
        private final TextView tv_qian;
        private final TextView tv_signinTime;
        private final TextView tv_signOffTime;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.kq_time);
            state_view = itemView.findViewById(R.id.kq_state_view);
            state_area = (LinearLayout) itemView.findViewById(R.id.kq_state_area);
            tv_content = (TextView) itemView.findViewById(R.id.kq_tv_content);
            tv_place = (TextView) itemView.findViewById(R.id.kq_tv_place);
            kq_plan = (RelativeLayout) itemView.findViewById(R.id.kq_plan);
            tv_endtime = (TextView) itemView.findViewById(R.id.kq_endtime);
            tv_qian = (TextView) itemView.findViewById(R.id.kq_tv_qian);
            tv_signinTime = (TextView) itemView.findViewById(R.id.kq_tv_siginTime);
            tv_signOffTime = (TextView) itemView.findViewById(R.id.kq_tv_sigoffTime);
        }
    }


    private String getHMS(String fullFomattime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String hMS = "";
        try {
            Date d = formatter.parse(fullFomattime);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            hMS = format.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hMS;

    }

}
