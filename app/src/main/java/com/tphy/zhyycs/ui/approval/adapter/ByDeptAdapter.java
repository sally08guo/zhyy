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
import com.tphy.zhyycs.model.PunchByDeptItem;

import java.util.List;

/**
 * Created by Administrator on 2017\11\6 0006.
 */

public class ByDeptAdapter extends BaseExpandableListAdapter {

    private List<PunchByDeptItem> groupArray;
    private Context context;

    public ByDeptAdapter(Context context, List<PunchByDeptItem> groupArray) {
        this.context = context;
        this.groupArray = groupArray;
    }

    @Override
    public int getGroupCount() {
        return groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupArray.get(groupPosition).getUserList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupArray.get(groupPosition).getUserList().get(childPosition);
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
        holder.tv_kq_type.setText(groupArray.get(groupPosition).getDeptName() + "  (" + groupArray.get(groupPosition).getTypeName() + ")");
        holder.tv_kq_time.setText("共签:" + groupArray.get(groupPosition).getAllCount() + "  补签:" + groupArray.get(groupPosition).getBQCount());

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
            convertView = LayoutInflater.from(context).inflate(R.layout.expandbydept_item, null);
            holder = new ChildHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.tv_name.setText(groupArray.get(groupPosition).getUserList().get(childPosition).getUserName());
        if (null != groupArray.get(groupPosition).getUserList().get(childPosition).getSendTime()) {
            String d = groupArray.get(groupPosition).getUserList().get(childPosition).getSendTime();
            String time = d.substring(d.length() - 8);
            holder.tv_time.setText(time);
            if (time.compareTo("09:00:00") > 0 && groupArray.get(groupPosition).getTypeName().equals("上班签到")) {
                holder.tv_status.setText("迟到");
                holder.iv_status.setImageResource(R.drawable.quan_cha);
            } else if (time.compareTo("18:00:00") < 0 && groupArray.get(groupPosition).getTypeName().equals("下班签到")) {
                holder.tv_status.setText("早退");
                holder.iv_status.setImageResource(R.drawable.quan_cha);
            } else {
                holder.tv_status.setText(groupArray.get(groupPosition).getTypeName());
                holder.iv_status.setImageResource(R.drawable.quan_gou);
            }
        } else {
            holder.tv_status.setText("未签到");
            holder.iv_status.setImageResource(R.drawable.quan_jian);
        }
        if (groupArray.get(groupPosition).getUserList().get(childPosition).getPhoneType().equals("B")) {
            holder.tv_from.setText("补签");
        } else {
            holder.tv_from.setVisibility(View.INVISIBLE);
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
        private TextView tv_name, tv_status, tv_time, tv_from;
        private ImageView iv_status;
        private LinearLayout lyt;

        public ChildHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_from = (TextView) view.findViewById(R.id.tv_from);
            iv_status = (ImageView) view.findViewById(R.id.iv_status);
            lyt = (LinearLayout) view.findViewById(R.id.lyt);
        }
    }
}
