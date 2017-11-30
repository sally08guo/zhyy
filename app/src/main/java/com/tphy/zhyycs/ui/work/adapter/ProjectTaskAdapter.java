package com.tphy.zhyycs.ui.work.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.Task;

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

public class ProjectTaskAdapter extends BaseAdapter {


    private Context context;
    private List<Task> list;

    public ProjectTaskAdapter(Context context, List<Task> list) {

        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_rcvtask, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.get(position).getContent().length() > 10) {
            holder.tv_task_name.setText("编号:" + list.get(position).getCode() + "    " + list.get(position).getContent().substring(0, 9));
        } else {
            holder.tv_task_name.setText("编号:" + list.get(position).getCode() + "    " + list.get(position).getContent());
        }
        holder.tv_task_user.setText(list.get(position).getStatusName());
        holder.tv_time.setText(list.get(position).getProjectName() + ">" + list.get(position).getProductName());
        holder.tv_date.setText(list.get(position).getSendTime());
        if (null != list.get(position).getTaskType() && list.get(position).getTaskType().equals("3")) {
            holder.tv_task_name.setTextColor(context.getResources().getColor(R.color.holo_red_light));
        } else {
            holder.tv_task_name.setTextColor(context.getResources().getColor(R.color.a_menu_blue));
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_task_name, tv_task_user, tv_time, tv_date, tv_task_lcb;
        LinearLayout lyt;

        ViewHolder(View view) {
            tv_task_name = (TextView) view.findViewById(R.id.tv_task_name);
            tv_task_user = (TextView) view.findViewById(R.id.tv_task_user);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
        }
    }
}
