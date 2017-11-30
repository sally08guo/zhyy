package com.tphy.zhyycs.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

public class ActivityUtils {

    private WeakReference<Activity> activityWeakReference;
    private WeakReference<Fragment> fragmentWeakReference;

    private Toast toast;

    public ActivityUtils(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    public ActivityUtils(Fragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    private
    @Nullable
    Activity getActivity() {

        if (activityWeakReference != null) return activityWeakReference.get();
        if (fragmentWeakReference != null) {
            Fragment fragment = fragmentWeakReference.get();
            return fragment == null ? null : fragment.getActivity();
        }
        return null;
    }

    //吐司
    public void showToast(CharSequence msg) {
        Activity activity = getActivity();
        if (activity != null) {
            if (toast == null) toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
            toast.setText(msg);
            toast.show();
        }
    }

    //跳转activity
    @SuppressWarnings("SameParameterValue")
    public void showToast(int resId) {
        Activity activity = getActivity();
        if (activity != null) {
            String msg = activity.getString(resId);
            showToast(msg);
        }
    }

    public void startActivity(Class<? extends Activity> clazz) {
        Activity activity = getActivity();
        if (activity == null) return;
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
    }

    /**
     * Unfortunately Android doesn't have an official API to retrieve the height of
     * StatusBar. This is just a way to hack around, may not work on some devices.
     *
     * @return The height of StatusBar.
     */
    public int getStatusBarHeight() {
        Activity activity = getActivity();
        if (activity == null) return 0;

        Resources resources = getActivity().getResources();
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //获取屏幕宽度
    public int getScreenWidth() {
        Activity activity = getActivity();
        if (activity == null) return 0;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    //获取屏幕高度
    public int getScreenHeight() {
        Activity activity = getActivity();
        if (activity == null) return 0;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    //隐藏软键盘
    public void hideSoftKeyboard() {
        Activity activity = getActivity();
        if (activity == null) return;

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public double getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    /*字符转化成小数*/
    public static double convertToDouble(String number, double defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /*获取时间间隔*/

    public static String formatDuring(long mss, int i) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        if (days != 0 && hours != 0) {
            return String.format("%.1f", (float) days + (float) hours / 24);
        }
        if (days != 0 && hours == 0) {
            return String.valueOf(days);
        }
        if (days == 0 && hours != 0) {
            return String.valueOf(String.format("%.1f", (float) hours / 24));
        }
        if (days == 0 && hours == 0 && minutes != 0 && i == 0) {
            return minutes + "分钟";
        }
        if (days == 0 && hours == 0 && i == 1) {
            return String.format("%.1f", (float) days + (float) hours / 24);
        }
        return "不足一分钟";
    }

    /**
     * @param begin 时间段的开始
     * @param end   时间段的结束
     * @return 输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     * @author fy.zhang
     */
    public static String formatDuring(Date begin, Date end, int i) {
        return formatDuring(end.getTime() - begin.getTime(), i);
    }

    /*
     * Java文件操作 获取文件扩展名
     * */
    public String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /*
    * Java文件操作 获取不带扩展名的文件名
    * */
    public String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

}
