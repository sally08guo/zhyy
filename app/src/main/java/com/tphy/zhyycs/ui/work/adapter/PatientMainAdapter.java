package com.tphy.zhyycs.ui.work.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.Project;

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

public class PatientMainAdapter extends BaseAdapter {

    private Context context;
    private List<Project> list;

    public PatientMainAdapter(Context context, List<Project> list) {
        this.context = context;
        updata(list);
    }

    public void updata(List<Project> list) {
        if (list != null) {
            this.list = list;
        } else {
            this.list = new ArrayList<>();
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
        Project project = list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_xm_name.setText(project.getName());   // 项目名称
        holder.tv_xm_lcb.setText(project.getPmsName());     // 里程碑名称
        holder.tv_senduser.setText(project.getSendUserName());  // 发起人
        holder.tv_sendtime.setText(project.getSendTime());
        switch (Integer.valueOf(project.getStar())) {
            case 0:
                holder.tv_xing_one.setText("☆");
                holder.tv_xing_two.setText("☆");
                holder.tv_xing_three.setText("☆");
                holder.tv_xing_four.setText("☆");
                holder.tv_xing_five.setText("☆");
                break;
            case 1:
                holder.tv_xing_one.setText("★");
                holder.tv_xing_two.setText("☆");
                holder.tv_xing_three.setText("☆");
                holder.tv_xing_four.setText("☆");
                holder.tv_xing_five.setText("☆");
                break;
            case 2:
                holder.tv_xing_one.setText("★");
                holder.tv_xing_two.setText("★");
                holder.tv_xing_three.setText("☆");
                holder.tv_xing_four.setText("☆");
                holder.tv_xing_five.setText("☆");
                break;
            case 3:
                holder.tv_xing_one.setText("★");
                holder.tv_xing_two.setText("★");
                holder.tv_xing_three.setText("★");
                holder.tv_xing_four.setText("☆");
                holder.tv_xing_five.setText("☆");
                break;
            case 4:
                holder.tv_xing_one.setText("★");
                holder.tv_xing_two.setText("★");
                holder.tv_xing_three.setText("★");
                holder.tv_xing_four.setText("★");
                holder.tv_xing_five.setText("☆");
                break;
            case 5:
                holder.tv_xing_one.setText("★");
                holder.tv_xing_two.setText("★");
                holder.tv_xing_three.setText("★");
                holder.tv_xing_four.setText("★");
                holder.tv_xing_five.setText("★");
                break;
        }
        holder.tv_daiban.setText("待办(" + project.getActionRequiredCount() + "/" + project.getAllCount() + ")"); //待办任务
        holder.tv_daishenhe.setText("待审(" + project.getCheckRequiredCount() + ")");
        if (project.getActionRequiredCount().equals("0")) {
            holder.tv_daiban.setTextColor(context.getResources().getColor(R.color.recall_white_54));
        }
        if (project.getCheckRequiredCount().equals("0")) {
            holder.tv_daishenhe.setTextColor(context.getResources().getColor(R.color.recall_white_54));
        }

        if (project.getIsAttention().equals("1")) {  // 关注
            holder.lyt_bg.setBackgroundResource(R.drawable.main_item_blue);
        }else{
            holder.lyt_bg.setBackgroundResource(R.drawable.main_item_bg);
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView tv_xm_name, tv_xm_lcb, tv_senduser, tv_sendtime, tv_xing_one, tv_xing_two, tv_xing_three, tv_xing_four, tv_xing_five;
        TextView tv_daiban, tv_daishenhe;
        LinearLayout lyt_bg;

        ViewHolder(View view) {
            tv_xm_name = (TextView) view.findViewById(R.id.tv_xm_name);
            tv_xm_lcb = (TextView) view.findViewById(R.id.tv_xm_lcb);
            tv_senduser = (TextView) view.findViewById(R.id.tv_senduser);
            tv_sendtime = (TextView) view.findViewById(R.id.tv_sendtime);
            tv_xing_one = (TextView) view.findViewById(R.id.tv_xing_one);
            tv_xing_two = (TextView) view.findViewById(R.id.tv_xing_two);
            tv_xing_three = (TextView) view.findViewById(R.id.tv_xing_three);
            tv_xing_four = (TextView) view.findViewById(R.id.tv_xing_four);
            tv_xing_five = (TextView) view.findViewById(R.id.tv_xing_five);
            tv_daiban = (TextView) view.findViewById(R.id.tv_daiban);
            tv_daishenhe = (TextView) view.findViewById(R.id.tv_daishenhe);
            lyt_bg= (LinearLayout) view.findViewById(R.id.lyt_bg);
        }
    }
}
