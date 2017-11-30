package com.tphy.zhyycs.ui.work.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.Role;
import com.tphy.zhyycs.model.User;
import com.tphy.zhyycs.ui.SelectUserActivity;
import com.tphy.zhyycs.utils.MyGridView;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：ChatDemoUI3.0
 * 创建人：cdss
 * 创建时间：2017-08-16 11:09
 * 修改人：cdss
 * 修改时间：2017-08-16 11:09
 * 修改备注：
 */

public class ProjectRoleAdapter extends BaseAdapter {


    private Activity context;
    private List<Role> list;
    private AddUserAdpater2 adapter;
    private Map<String, List<User>> map;


    public ProjectRoleAdapter(Activity context, List<Role> list, Map<String, List<User>> map) {

        this.context = context;
        this.map = map;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_project_user, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_role_name.setText(list.get(position).getName());
        holder.tv_adduser.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        adapter = new AddUserAdpater2(context, map.get(list.get(position).getCode()), list.get(position).getCode());
        holder.list_roleuser.setAdapter(adapter);

        holder.lyt_adduser.setVisibility(View.VISIBLE);

        holder.lyt_adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it;
                it = new Intent(context, SelectUserActivity.class);
                it.putExtra("user", (Serializable) map.get(list.get(position).getCode()));
                it.putExtra("number", list.get(position).getCode());
                context.startActivityForResult(it, 101);
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_role_name, tv_adduser;
        MyGridView list_roleuser;
        LinearLayout lyt_adduser;

        ViewHolder(View view) {
            tv_role_name = (TextView) view.findViewById(R.id.tv_role_name);
            tv_adduser = (TextView) view.findViewById(R.id.tv_adduser);
            list_roleuser = (MyGridView) view.findViewById(R.id.list_roleuser);
            lyt_adduser = (LinearLayout) view.findViewById(R.id.lyt_adduser);
        }
    }
}
