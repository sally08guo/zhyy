package com.tphy.zhyycs.ui.approval.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.AttendRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：ChatDemoUI3.0
 * 创建人：cdss
 * 创建时间：2017-08-16 11:09
 * 修改人：cdss
 * 修改时间：2017-08-16 11:09
 * 修改备注：
 */

public class RecordAdapter extends BaseAdapter {


    private Activity context;
    private List<AttendRecord> list;

    public RecordAdapter(Activity context, List<AttendRecord> list) {

        this.context = context;
        update(list);
    }

    public void update(List<AttendRecord> list) {
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        AttendRecord ar = list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_information, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_content.setText(ar.getTypeName());
        holder.tv_type.setText(ar.getStatusName());
        holder.tv_menu.setText(ar.getSendUserName());
        holder.tv_time.setText(ar.getSendTime());

        return convertView;
    }

    public static class ViewHolder {
        TextView tv_menu, tv_content, tv_type, tv_time;

        ViewHolder(View view) {
            tv_menu = (TextView) view.findViewById(R.id.tv_menu);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_type = (TextView) view.findViewById(R.id.tv_type);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
        }
    }
}
