package com.tphy.zhyycs.ui.attandence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tphy.zhyycs.R;


public class DialogAdapter extends BaseAdapter {

    private Context context;
    private String[] items;
    private int mCheckedItem;
    private final LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private RadioOnCheckedListener radioOnCheckedListener;

    public DialogAdapter(Context context, String[] items, int mCheckedItem) {
        this.context = context;
        this.items = items;
        this.mCheckedItem = mCheckedItem;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.dialog_item_list, null, false);
            holder.alert_parent = (RelativeLayout) view.findViewById(R.id.alert_parent);
            holder.alert_tv = (TextView) view.findViewById(R.id.alert_tv);
            holder.alert_radio = (RadioButton) view.findViewById(R.id.alert_radio);

            if (i == mCheckedItem) {
                holder.alert_radio.setChecked(true);
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.alert_tv.setText(items[i]);
        holder.alert_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(holder.alert_parent, i);
            }
        });
        holder.alert_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioOnCheckedListener.onChecked(holder.alert_radio,i);
            }
        });
        return view;
    }

    public int getCheckedItem() {
        return this.mCheckedItem;
    }

    private class ViewHolder {
        private RelativeLayout alert_parent;
        private TextView alert_tv;
        private RadioButton alert_radio;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface RadioOnCheckedListener {
        void onChecked(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setRadioOnCheckedListener(RadioOnCheckedListener onCheckedListener){
        this.radioOnCheckedListener=onCheckedListener;
    }


}
