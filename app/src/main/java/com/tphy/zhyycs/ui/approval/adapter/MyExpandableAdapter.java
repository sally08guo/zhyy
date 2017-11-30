package com.tphy.zhyycs.ui.approval.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.AttendGroup;

import java.util.List;

/**
 * Created by Administrator on 2017\11\6 0006.
 */

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private List<AttendGroup> groupArray;
    private Context context;

    public MyExpandableAdapter(Context context, List<AttendGroup> groupArray) {
        this.context = context;
        this.groupArray = groupArray;
    }

    @Override
    public int getGroupCount() {
        return groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupArray.get(groupPosition).getPunchList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupArray.get(groupPosition).getPunchList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandlist_group, null);
            holder = new GroupHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.tv_kq_type.setText(groupArray.get(groupPosition).getTypeName());
        holder.tv_kq_time.setText(groupArray.get(groupPosition).getCount());

        if (isExpanded) {
            holder.iv_jiantou.setImageResource(R.drawable.arrow_right_large2);
        } else {
            holder.iv_jiantou.setImageResource(R.drawable.arrow_right_large);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandlist_item, null);
            holder = new ChildHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.tv_time_in.setText("签到时间：" + groupArray.get(groupPosition).getPunchList().get(childPosition).getSendTime());
        if (!groupArray.get(groupPosition).getPunchList().get(childPosition).getLocation().equals("")) {
            holder.tv_location.setText("地点:" + groupArray.get(groupPosition).getPunchList().get(childPosition).getLocation());
        } else {
            holder.tv_location.setVisibility(View.GONE);
        }
        if (!groupArray.get(groupPosition).getPunchList().get(childPosition).getRemark().equals("")) {
            holder.tv_remark.setText("备注：" + groupArray.get(groupPosition).getPunchList().get(childPosition).getRemark());
        } else {
            holder.tv_remark.setVisibility(View.GONE);
        }
        if (childPosition % 2 == 0) {
            holder.lyt.setBackgroundResource(R.color.common_bg);
        } else {
            holder.lyt.setBackgroundResource(R.color.white);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        private TextView tv_kq_type, tv_kq_time;
        private ImageView iv_jiantou;

        public GroupHolder(View view) {
            tv_kq_type = (TextView) view.findViewById(R.id.tv_kq_type);
            tv_kq_time = (TextView) view.findViewById(R.id.tv_kq_time);
            iv_jiantou = (ImageView) view.findViewById(R.id.iv_jiantou);
        }
    }

    class ChildHolder {
        private TextView tv_time_in, tv_location, tv_remark;
        private LinearLayout lyt;

        public ChildHolder(View view) {
            tv_time_in = (TextView) view.findViewById(R.id.tv_time_in);
            tv_location = (TextView) view.findViewById(R.id.tv_location);
            tv_remark = (TextView) view.findViewById(R.id.tv_remark);
            lyt = (LinearLayout) view.findViewById(R.id.lyt);
        }
    }
}
