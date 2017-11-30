package com.tphy.zhyycs.ui.approval.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tphy.zhyycs.R;

import java.util.List;

/**
 * 项目名称：ChatDemoUI3.0
 * 创建人：cdss
 * 创建时间：2017-08-16 11:09
 * 修改人：cdss
 * 修改时间：2017-08-16 11:09
 * 修改备注：
 */

public class AttendanceAllAdapter extends BaseAdapter {


    private Activity context;
    private List<String> list;
    private int column;


    public AttendanceAllAdapter(Activity context, List<String> list) {

        this.context = context;
        this.list = list;
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

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_attend_all, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int lineNum = list.size() / column;
        for (int i = 1; i < lineNum+1; i++) {
            if (i % 2 == 0) {
                if (position >= (i - 1) * column && position < i * column) {
                    holder.linear_atten_all.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            } else {
                if (position >= (i - 1) * column && position < i * column) {
                    holder.linear_atten_all.setBackgroundColor(Color.parseColor("#f6f6f6"));
                }
            }
        }
        holder.tv_content.setText(list.get(position));
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_content;
        LinearLayout linear_atten_all;

        ViewHolder(View view) {
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            linear_atten_all = (LinearLayout) view.findViewById(R.id.linear_atten_all);
        }
    }

    public void setColumnNum(int num) {
        this.column = num;
    }
}
