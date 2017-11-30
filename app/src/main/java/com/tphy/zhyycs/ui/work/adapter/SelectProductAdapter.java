package com.tphy.zhyycs.ui.work.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.LCB;
import com.tphy.zhyycs.model.Product;

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

public class SelectProductAdapter extends BaseAdapter {


    private Context context;
    private List<Product> list_user;


    public SelectProductAdapter(Context context, List<Product> list_user) {

        this.context = context;
        update(list_user);
    }

    public void update(List<Product> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_selectlcb, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkbox.isChecked()) {
                    list_user.get(position).setChecked(true);
                } else {
                    list_user.get(position).setChecked(false);
                }
            }
        });

        if (list_user.get(position).isChecked()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }
        holder.tv_username.setText(list_user.get(position).getName());
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_username;
        CheckBox checkbox;

        ViewHolder(View view) {
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }
}
