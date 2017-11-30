package com.tphy.zhyycs.ui.information.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.Information;

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

public class InformationAdapter extends BaseAdapter {


    private Activity context;
    private List<Information> list;

    public InformationAdapter(Activity context, List<Information> list) {

        this.context = context;
        update(list);
    }

    public void update(List<Information> list) {
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
        Information information = list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_information, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (information.getType().equals("1")) {//待审核
            holder.tv_menu.setText(information.getProjectName() + ">" + information.getProductName());
            holder.tv_type.setText(information.getStatusName());
            holder.tv_type.setTextColor(context.getResources().getColor(R.color.b_shenhe));
        } else if (information.getType().equals("2")) {//待办
            holder.tv_menu.setText(information.getProjectName() + ">" + information.getProductName());
            holder.tv_type.setText(information.getStatusName());
            holder.tv_type.setTextColor(context.getResources().getColor(R.color.b_daiban));
        } else if (information.getType().equals("3")) {//待审批考勤
            holder.tv_menu.setText(information.getProjectName());
            holder.tv_type.setText(information.getStatusName());
            holder.tv_type.setTextColor(context.getResources().getColor(R.color.btn_logout_pressed));
        } else if (information.getType().equals("4")) {// 公告
            holder.tv_menu.setText("公告");
            holder.tv_type.setText("未读");
            holder.tv_type.setTextColor(context.getResources().getColor(R.color.btn_logout_normal));
        }
        if (information.getContent().length() > 8) {
            holder.tv_content.setText("编号:" + information.getCode() + "    " + information.getContent().substring(0, 7));
        } else {
            holder.tv_content.setText("编号:" + information.getCode() + "    " + information.getContent());
        }
        holder.tv_time.setText(information.getSendTime());
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_menu, tv_content, tv_type, tv_time;

        ViewHolder(View view) {
            tv_menu = (TextView) view.findViewById(R.id.tv_menu);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_type = (TextView) view.findViewById(R.id.tv_type);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
        }
    }
}
