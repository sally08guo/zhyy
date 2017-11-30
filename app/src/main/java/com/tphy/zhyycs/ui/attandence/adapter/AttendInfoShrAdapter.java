package com.tphy.zhyycs.ui.attandence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.AttendInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：选择成员
 * 创建人：cdss
 * 创建时间：2017-08-16 11:09
 * 修改人：cdss
 * 修改时间：2017-08-16 11:09
 * 修改备注：
 */

public class AttendInfoShrAdapter extends BaseAdapter {


    private Context context;
    private List<AttendInfo> list_user;


    public AttendInfoShrAdapter(Context context, List<AttendInfo> list_user) {

        this.context = context;
        update(list_user);
    }

    public void update(List<AttendInfo> list) {
        if (list == null) {
            this.list_user = new ArrayList<>();
        } else {
            this.list_user = list;
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return list_user.size();
    }

    @Override
    public Object getItem(int position) {
        return list_user.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_attend_shr, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_user.setText(list_user.get(position).getAppralUserName());
        if (list_user.get(position).getRemark().equals("")) {
            holder.lyt_remark.setVisibility(View.GONE);
        } else {
            holder.tv_remark.setText(list_user.get(position).getRemark());
            holder.lyt_remark.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_user, tv_remark;
        LinearLayout lyt_remark;

        ViewHolder(View view) {
            tv_user = (TextView) view.findViewById(R.id.tv_user);
            tv_remark = (TextView) view.findViewById(R.id.tv_remark);
            lyt_remark = (LinearLayout) view.findViewById(R.id.lyt_remark);
        }
    }
}
