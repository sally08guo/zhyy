package com.tphy.zhyycs.ui.work.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.User;

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

public class AddUserAdpater2 extends BaseAdapter {


    private Context context;
    private List<User> list;
    private String mapcode;

    public AddUserAdpater2(Context context, List<User> list, String code) {

        this.context = context;
        this.mapcode = code;
        update(list);
    }

    public void update(List<User> list) {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_adduser, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_username.setText(list.get(position).getName());
        holder.iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //list.remove(position);
                Intent it = new Intent("DelUser");
                it.putExtra("mapcode", mapcode);
                it.putExtra("listcode", position + "");
                context.sendBroadcast(it);
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_username;
        ImageView iv_del;

        ViewHolder(View view) {
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            iv_del = (ImageView) view.findViewById(R.id.iv_del);

        }
    }
}
