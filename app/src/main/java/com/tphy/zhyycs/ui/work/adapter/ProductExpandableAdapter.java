package com.tphy.zhyycs.ui.work.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.AttendGroup;
import com.tphy.zhyycs.model.ProductExpand;

import java.util.List;

/**
 * Created by Administrator on 2017\11\6 0006.
 */

public class ProductExpandableAdapter extends BaseExpandableListAdapter {

    private List<ProductExpand> groupArray;
    private Context context;

    public ProductExpandableAdapter(Context context, List<ProductExpand> groupArray) {
        this.context = context;
        this.groupArray = groupArray;
    }

    @Override
    public int getGroupCount() {
        return groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupArray.get(groupPosition).getListProduct().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupArray.get(groupPosition).getListProduct().get(childPosition);
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
        holder.tv_kq_type.setText(groupArray.get(groupPosition).getPName());
        holder.tv_kq_time.setVisibility(View.GONE);

        if (isExpanded) {
            holder.iv_jiantou.setImageResource(R.drawable.arrow_right_large2);
        } else {
            holder.iv_jiantou.setImageResource(R.drawable.arrow_right_large);
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_selectlcb, null);
            holder = new ChildHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkbox.isChecked()) {
                    groupArray.get(groupPosition).getListProduct().get(childPosition).setChecked(true);
                } else {
                    groupArray.get(groupPosition).getListProduct().get(childPosition).setChecked(false);
                }
            }
        });

        if ( groupArray.get(groupPosition).getListProduct().get(childPosition).isChecked()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }
        holder.tv_username.setText( groupArray.get(groupPosition).getListProduct().get(childPosition).getName());

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
        TextView tv_username;
        CheckBox checkbox;

        public ChildHolder(View view) {
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }
}
