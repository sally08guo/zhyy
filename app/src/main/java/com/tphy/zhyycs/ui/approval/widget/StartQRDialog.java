package com.tphy.zhyycs.ui.approval.widget;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.approval.bean.QRCodeTypes;

import java.util.List;

public class StartQRDialog extends AlertDialog {

    private onChooseListener onChooseListener;
    private Context context;
    private List<QRCodeTypes> types;

    public void setOnChooseListener(StartQRDialog.onChooseListener onChooseListener) {
        this.onChooseListener = onChooseListener;
    }

//    private TextView tv_confirm;
//    private ListView listView;

    public StartQRDialog(@NonNull Context context, List<QRCodeTypes> strings) {
        super(context, R.style.CustomDialogTheme);
        this.context = context;
        this.types = strings;
    }

    public StartQRDialog(@NonNull Context context, @StyleRes int themeResId, List<QRCodeTypes> strings) {
        super(context, themeResId);
        this.context = context;
        this.types = strings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dilog_start_qr);
        TextView tv_confirm = (TextView) findViewById(R.id.alert_tv_confirm);
        ImageView iv_cancel = (ImageView) findViewById(R.id.alert_iv_cancel);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.alert_dia_group);
        TextView tv_cancel = (TextView) findViewById(R.id.alert_tv_cancel);
//        listView = (ListView) findViewById(R.id.qr_dia_list);
//        QRDialogAdapter qrDialogAdapter = new QRDialogAdapter(context, types);
//        listView.setAdapter(qrDialogAdapter);
        for (int i = 0; i < types.size(); i++) {
            RadioButton radioButton = new RadioButton(context);
            QRCodeTypes qrCodeTypes = types.get(i);
            String name = qrCodeTypes.getName();
            radioButton.setText(name);
            radioGroup.addView(radioButton, i);
        }
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onChooseListener != null) {
                    int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                    int i = radioGroup.indexOfChild(radioGroup.findViewById(checkedRadioButtonId));
                    onChooseListener.confirmListener(i);
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChooseListener != null) {
                    onChooseListener.cancleListenner();
                }
            }
        });
    }

    public interface onChooseListener {
        void confirmListener(int position);

        void cancleListenner();
    }




}
