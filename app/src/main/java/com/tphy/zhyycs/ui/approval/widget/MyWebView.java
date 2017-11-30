package com.tphy.zhyycs.ui.approval.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by Administrator on 2017\11\20 0020.
 */

public class MyWebView extends WebView {

    public MyWebView(Context context) {
        super(context);
//        init(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        init(context);
    }

    @SuppressLint("NewApi")
    public MyWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
//        init(context);
    }

//    public void init(Context context) {
//        // This lets the layout editor display the view.
//        if (isInEditMode()) return;
//
//    }
//


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * 使WebView不可滚动
     * */
    @Override
    public void scrollTo(int x, int y){
        super.scrollTo(0,0);
    }
}
