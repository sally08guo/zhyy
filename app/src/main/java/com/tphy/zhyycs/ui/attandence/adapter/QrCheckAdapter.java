package com.tphy.zhyycs.ui.attandence.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.approval.bean.SignInInfor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class QrCheckAdapter extends RecyclerView.Adapter<QrCheckAdapter.QrCheckHolder> {

    private final Context context;
    private List<SignInInfor> signInInfors;

    public QrCheckAdapter(Context context, List<SignInInfor> list) {
        this.context = context;
        this.signInInfors = list;
    }

    public void updateList(List<SignInInfor> list) {
        this.signInInfors = list;
        notifyDataSetChanged();
    }

    public void insertItem(SignInInfor item) {
        this.signInInfors.add(0, item);
        notifyItemInserted(0);
//        liveChecks.size() - 0
        notifyItemRangeChanged(0, signInInfors.size());
    }

    @Override
    public QrCheckHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_qr_signin, parent, false);
        return new QrCheckHolder(view);
    }

    @Override
    public void onBindViewHolder(QrCheckHolder holder, int position) {
        SignInInfor signInInfor = signInInfors.get(position);
        String name = signInInfor.getName();
        String phoneType = signInInfor.getPhoneType();
        String sendTime = signInInfor.getSendTime();
        boolean screen = signInInfor.isScreen();
        holder.tv_name.setText(name);
        String ways = "";
        if (screen) {
            ways = "通过大屏签到";
        }
        if (null == sendTime || ("").equals(sendTime)) {
            sendTime = "未签到";
            holder.ic_no.setBounds(0, 0, holder.ic_no.getMinimumWidth(), holder.ic_no.getMinimumHeight());
            holder.tv_name.setCompoundDrawables(holder.ic_no, null, null, null);
        } else {
            holder.ic_signin.setBounds(0, 0, holder.ic_signin.getMinimumWidth(), holder.ic_signin.getMinimumHeight());
            holder.tv_name.setCompoundDrawables(holder.ic_signin, null, null, null);
            if ("B".equals(phoneType)) {
                holder.ic_buqian.setBounds(0, 0, holder.ic_buqian.getMinimumWidth(), holder.ic_buqian.getMinimumHeight());
            } else {
                ways = "通过手机签到";
            }
        }
//        switch (state) {
//            case "0":
//                /*正常签到图标*/
//
//                break;
//            case "1":
//                /*未签到图标*/
//
//                break;
//            case "2":
//                /*补签签到图标*/
//
//                break;
//        }
        if (!"未签到".equals(sendTime)) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            Date sendDate = null;
            try {
                sendDate = format.parse(sendTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormatter = new SimpleDateFormat("HH:mm:ss");
            sendTime = mFormatter.format(sendDate);
        }
        holder.tv_time.setText(sendTime);
        holder.tv_ways.setText(ways);
    }

    @Override
    public int getItemCount() {
        return signInInfors.size();
    }

    public class QrCheckHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.kq_item_name)
        TextView tv_name;
        @BindView(R.id.kq_item_time)
        TextView tv_time;
        @BindView(R.id.kq_item_ways)
        TextView tv_ways;
        @BindDrawable(R.mipmap.ic_buqian)
        Drawable ic_buqian;
        @BindDrawable(R.mipmap.ic_signin)
        Drawable ic_signin;
        @BindDrawable(R.mipmap.ic_no)
        Drawable ic_no;

        public QrCheckHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}