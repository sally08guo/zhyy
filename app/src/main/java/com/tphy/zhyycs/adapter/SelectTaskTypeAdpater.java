package com.tphy.zhyycs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.LCB;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：添加成员
 * 创建人：cdss
 * 创建时间：2017-08-16 11:09
 * 修改人：cdss
 * 修改时间：2017-08-16 11:09
 * 修改备注：
 */

public class SelectTaskTypeAdpater extends BaseAdapter {


    private Context context;
    private List<LCB> list;

    public SelectTaskTypeAdpater(Context context, List<LCB> list) {

        this.context = context;
        update(list);
    }

    public void update(List<LCB> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_hospital, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_hospital_name.setText(list.get(position).getName());

        return convertView;
    }

    public static class ViewHolder {
        TextView tv_hospital_name;
        ImageView img;

        ViewHolder(View view) {
            tv_hospital_name = (TextView) view.findViewById(R.id.tv_hospital_name);
            img = (ImageView) view.findViewById(R.id.img_icon);
        }
    }
}
