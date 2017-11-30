package com.tphy.zhyycs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.LCB;

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

public class HorizontalListViewAdapter extends BaseAdapter {


    private Context context;
    private List<LCB> list_user;
    private String product = "部门";


    public HorizontalListViewAdapter(Context context, List<LCB> list_user) {

        this.context = context;
        update(list_user);
    }

    public void update(List<LCB> list) {
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
        if (list_user.size() > 5) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_lcb, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position % 2 == 0) {
                holder.tv_top.setText(list_user.get(position).getName());
                holder.tv_buttom.setVisibility(View.INVISIBLE);
                holder.tv_top.setVisibility(View.VISIBLE);
            } else {
                holder.tv_buttom.setText(list_user.get(position).getName());
                holder.tv_top.setVisibility(View.INVISIBLE);
                holder.tv_buttom.setVisibility(View.VISIBLE);
            }
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_lcb2, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_top.setText(list_user.get(position).getName());
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_top, tv_buttom;

        ViewHolder(View view) {
            tv_top = (TextView) view.findViewById(R.id.tv_top);
            tv_buttom = (TextView) view.findViewById(R.id.tv_buttom);
        }
    }
}
