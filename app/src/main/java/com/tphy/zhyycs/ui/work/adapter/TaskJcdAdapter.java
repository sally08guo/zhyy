package com.tphy.zhyycs.ui.work.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.Task;
import com.tphy.zhyycs.ui.SelectUserOneActivity;
import com.tphy.zhyycs.ui.work.activity.TaskDetailsActivity;

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

public class TaskJcdAdapter extends BaseAdapter {


    private Activity context;
    private List<Task> list;
    private String type;

    public TaskJcdAdapter(Activity context, List<Task> list, String type) {

        this.context = context;
        this.type = type;
        update(list);
    }

    public void update(List<Task> list) {
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
        final Task task = list.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_taskcomment, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_jcd_name.setText(task.getCheckPointName());
        if (task.getIsCurrent().equals("0")) {
            holder.iv_jcd_status.setImageResource(R.drawable.task_wwc);
            holder.tv_time_end.setVisibility(View.VISIBLE);
            holder.tv_task_submit.setVisibility(View.GONE);
        } else if (task.getIsCurrent().equals("1")) {
            holder.iv_jcd_status.setImageResource(R.drawable.task_jx);
            if (type.equals("1")) {
                holder.tv_time_end.setVisibility(View.GONE);
                holder.tv_task_submit.setVisibility(View.VISIBLE);
            } else {
                holder.tv_time_end.setVisibility(View.VISIBLE);
                holder.tv_task_submit.setVisibility(View.GONE);
            }
        } else if (task.getIsCurrent().equals("2")) {
            holder.iv_jcd_status.setImageResource(R.drawable.task_ywc);
            holder.tv_time_end.setVisibility(View.VISIBLE);
            holder.tv_task_submit.setVisibility(View.GONE);
        }
        holder.tv_jcd_user.setText(task.getExecutUserName());
        holder.tv_time_start.setText(task.getStartTime());
        holder.tv_time_end.setText(task.getEndTime());
        holder.iv_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, SelectUserOneActivity.class);
                it.putExtra("number", position + "");
                it.putExtra("ProjectCode", task.getProjectCode());
                Log.e("项目Code", task.getProjectCode());
                context.startActivityForResult(it, 101);
            }
        });
        if (!task.getIsCurrent().equals("2") && TaskDetailsActivity.modify2.equals("modify")) {
            holder.iv_modify.setVisibility(View.VISIBLE);
        } else {
            holder.iv_modify.setVisibility(View.GONE);
        }

        holder.tv_task_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent("Task_Submit");
                context.sendBroadcast(it);
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        TextView tv_jcd_name, tv_jcd_user, tv_time_start, tv_time_end, tv_task_submit;
        ImageView iv_modify, iv_jcd_status;

        ViewHolder(View view) {
            tv_jcd_name = (TextView) view.findViewById(R.id.tv_jcd_name);
            iv_jcd_status = (ImageView) view.findViewById(R.id.iv_jcd_status);
            tv_jcd_user = (TextView) view.findViewById(R.id.tv_jcd_user);
            tv_time_start = (TextView) view.findViewById(R.id.tv_time_start);
            tv_time_end = (TextView) view.findViewById(R.id.tv_time_end);
            iv_modify = (ImageView) view.findViewById(R.id.iv_modify);
            tv_task_submit = (TextView) view.findViewById(R.id.tv_task_submit);
        }
    }

}
