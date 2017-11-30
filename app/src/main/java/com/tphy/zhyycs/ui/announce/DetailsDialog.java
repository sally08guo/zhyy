package com.tphy.zhyycs.ui.announce;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.TextView;

import com.tphy.zhyycs.R;

/**
 * Created by Administrator on 2017\9\26 0026.
 */

public class DetailsDialog extends Dialog {

    private String content;
    private String title;
    private onConfirmListener onConfirmListener;

    public void setOnConfirmListener(DetailsDialog.onConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    private TextView tv_content;

    private TextView tv_confirm;

    private TextView tv_title;

    public DetailsDialog(@NonNull Context context, String content, String title) {
        super(context,R.style.CustomDialogTheme);
        this.content = content;
        this.title = title;
    }

    public DetailsDialog(@NonNull Context context, @StyleRes int themeResId, String content, String title) {
        super(context, themeResId);
        this.content = content;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_details);
        tv_confirm = (TextView) findViewById(R.id.alert_tv_confirm);
        tv_content = (TextView) findViewById(R.id.alert_tv_content);
        tv_title= (TextView) findViewById(R.id.alert_tv_title);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onConfirmListener!=null) {
                    onConfirmListener.confirmListener();
                }
            }
        });
        tv_content.setText(content);
        tv_title.setText(title);
    }

    public interface onConfirmListener {
        void confirmListener();
    }


}
