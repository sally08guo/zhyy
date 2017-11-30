package com.tphy.zhyycs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.User;

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

public class SelectUserOneAdapter extends BaseAdapter {


    private Context context;
    private List<User> list_user;
    private String product = "天鹏";


    public SelectUserOneAdapter(Context context, List<User> list_user) {

        this.context = context;
        update(list_user);
    }

    public void update(List<User> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_selectuser_one, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        User user = list_user.get(position);
        holder.tv_username.setText(user.getName());
        holder.tv_usercode.setText(user.getDUTY());
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_username, tv_usercode;

        ViewHolder(View view) {
            tv_usercode = (TextView) view.findViewById(R.id.tv_usercode);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
        }
    }
}
