package com.tphy.zhyycs.ui.work.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.LCB;
import com.tphy.zhyycs.ui.SelectUserOneActivity;
import com.tphy.zhyycs.ui.work.activity.TaskReviewActivity;

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

public class TaskReviewAdapter extends BaseAdapter {


    private Activity context;
    private List<LCB> list;

    public TaskReviewAdapter(Activity context, List<LCB> list) {

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LCB task = list.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_taskreview, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_jcd_name.setText(task.getName());
        holder.tv_jcd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        holder.lyt_jcd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, SelectUserOneActivity.class);
                it.putExtra("number", position + "");
                it.putExtra("ProjectCode", TaskReviewActivity.str_project_code);
                context.startActivityForResult(it, 101);
            }
        });
        if (null != task.getUsercode() && !task.getUsercode().equals("")) {
            holder.lyt_jcd.setVisibility(View.GONE);
            holder.lyt_user.setVisibility(View.VISIBLE);
            holder.tv_jcd_user.setText(task.getUsername());
        } else {
            holder.lyt_user.setVisibility(View.GONE);
            holder.lyt_jcd.setVisibility(View.VISIBLE);
        }
        holder.iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(position).setUsername("");
                list.get(position).setUsercode("");
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_jcd_name, tv_jcd, tv_jcd_user;
        LinearLayout lyt_jcd, lyt_user;
        ImageView iv_del;

        ViewHolder(View view) {
            tv_jcd_name = (TextView) view.findViewById(R.id.tv_jcd_name);
            tv_jcd = (TextView) view.findViewById(R.id.tv_jcd);
            lyt_jcd = (LinearLayout) view.findViewById(R.id.lyt_jcd);
            tv_jcd_user = (TextView) view.findViewById(R.id.tv_jcd_user);
            lyt_user = (LinearLayout) view.findViewById(R.id.lyt_user);
            iv_del = (ImageView) view.findViewById(R.id.iv_del);
        }
    }

}
