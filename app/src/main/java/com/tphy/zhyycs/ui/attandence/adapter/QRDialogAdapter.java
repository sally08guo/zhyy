package com.tphy.zhyycs.ui.attandence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tphy.zhyycs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017\11\17 0017.
 */

public class QRDialogAdapter extends BaseAdapter {

    private Context context;
    private List<String> strings;
    private List<Boolean> ischeckedlist;
    private int position;
    public QRDialogAdapter(Context context, List<String> strings) {
        this.context = context;
        this.strings = strings;
        ischeckedlist = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            ischeckedlist.add(false);
        }
    }

    public int getPosition() {
        return position;
    }

    //    @Override
//    public void onBindViewHolder(QRDialogHolder holder, final int position) {
//        holder.alert_tv.setText(strings.get(position));
//        holder.alert_radio.setChecked(ischeckedlist.get(position));
//        holder.alert_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                ischeckedlist.clear();
//                for (int i = 0; i < strings.size(); i++) {
//                    if (i == position) {
//                        ischeckedlist.add(true);
//                    } else {
//                        ischeckedlist.add(false);
//                    }
//                }
//                notifyDataSetChanged();
//            }
//        });
//    }


    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public Object getItem(int i) {
        return strings.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        QRDialogHolder qrDialogHolder;
        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_item_list, viewGroup, false);
            qrDialogHolder = new QRDialogHolder();
            qrDialogHolder.alert_tv = (TextView) view.findViewById(R.id.alert_tv);
            qrDialogHolder.alert_radio = (RadioButton) view.findViewById(R.id.alert_radio);
            view.setTag(qrDialogHolder);
        } else {
            qrDialogHolder = (QRDialogHolder) view.getTag();
        }
        qrDialogHolder.alert_tv.setText(strings.get(i));
        qrDialogHolder.alert_radio.setChecked(ischeckedlist.get(i));
        qrDialogHolder.alert_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ischeckedlist.clear();
                position = i;
                for (int j = 0; j < strings.size(); j++) {
                    if (j == i) {
                        ischeckedlist.add(true);
                    } else {
                        ischeckedlist.add(false);
                    }
                }
                notifyDataSetChanged();
            }
        });
        return null;
    }

    public class QRDialogHolder {
        TextView alert_tv;
        RadioButton alert_radio;
    }


}
