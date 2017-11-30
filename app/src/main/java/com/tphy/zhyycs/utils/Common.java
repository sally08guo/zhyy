package com.tphy.zhyycs.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.Manifest;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.adapter.SelectProductAdpater;
import com.tphy.zhyycs.adapter.SelectProjectAdpater;
import com.tphy.zhyycs.adapter.SelectTaskTypeAdpater;
import com.tphy.zhyycs.model.LCB;
import com.tphy.zhyycs.model.Product;
import com.tphy.zhyycs.model.Project;
import com.tphy.zhyycs.ui.announce.AnnoucePushActivity;
import com.tphy.zhyycs.ui.work.activity.CreateDemandActivity;
import com.tphy.zhyycs.ui.work.activity.CreateProjectActivity;
import com.tphy.zhyycs.ui.work.activity.CreateTaskActivity;
import com.tphy.zhyycs.widget.circleview.*;
import com.tphy.zhyycs.widget.circleview.TimePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;


public class Common {

    private WeakReference<Activity> activityWeakReference;
    private WeakReference<Fragment> fragmentWeakReference;

    private Toast toast;

    private TimePickerDialog timePickerDialog;

    public Common(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    public Common(Fragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    private PopupWindow popupWindow;
    private NumberPicker yearicker, monthicker, dayicker;
    private String year, month, day;
    private Activity activity;
    private SharedPreferences sp;

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

    public static String getDate(Date Date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(Date);
    }

    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 安装APK程序代码
     */
    public static void installFile(Context context, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri = FileProvider.getUriForFile(context, "com.tphy.zhyycs.fileprovider", file);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        }
    }

    public static String getNetTime() throws Exception {
        URL url = new URL("http://www.baidu.com");
        URLConnection uc = url.openConnection();// 生成连接对象
        uc.connect(); // 发出连接
        long ld = uc.getDate(); // 取得网站日期时间
        Date date = new Date(ld); // 转换为标准时间对象
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(date);
    }

    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

    public static String getDbPath(Context context) {
        return "/data/data/" + context.getPackageName() + "/databases/";
    }

    public static String getDbName() {
        return "cdss.db";
    }


    /**
     * 判断sdcard卡上的文件是否存在 存在，若不完整，则删除之
     */
    public static boolean isExistForSdcard(Context context, String filePath, String fileSize) {
        boolean b = false;
        File f = new File(filePath);
        if (f.exists()) {
            if (fileSize.equals(f.length() + "")) {
                b = true;
            } else {
                f.delete();
            }
        }
        return b;
    }

    /**
     * @param DATE1 起止时间
     * @param DATE2 终止时间
     * @return 判断时间的大小
     */
    public static boolean compare_date(String DATE1, String DATE2) {

        String dates[] = DATE2.split("-");
        String olddates[] = DATE1.split("-");

        if (Long.parseLong(dates[0]) == Long.parseLong(olddates[0])) {

            if (Integer.valueOf(dates[1]).intValue() == Integer.valueOf(olddates[1]).intValue()) {
                if (Integer.valueOf(dates[2]).intValue() >= Integer.valueOf(olddates[2]).intValue()) {
                    return true;
                }
            } else if (Integer.valueOf(dates[1]) > Integer.valueOf(olddates[1])) {
                return true;
            }
        } else if (Long.parseLong(dates[0]) > Long.parseLong(olddates[0])) {
            return true;
        }
        return false;
    }

    public static boolean isMobileNum(String mobiles) {

        Pattern p = Pattern.compile("^[1][3578][0-9]{9}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }


    /**
     * 获取本地图片
     *
     * @param pathString
     * @return
     */
    public static Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File("/sdcard/bunner/" + pathString);
            Log.e("获取本地的图片", "file");
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile("/sdcard/bunner/" + pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return bitmap;
    }

    /**
     * @param urlpath
     * @return Bitmap 根据图片url获取图片对象
     */
    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 读取html源码
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static byte[] readStream(InputStream inputStream) throws Exception {

        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        inputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }


    public static String testGetHtml(String urlpath) throws Exception {

        //版本4.0后需加这个，不然就报错android.os.NetworkOnMainThreadException
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        URL url = new URL(urlpath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            byte[] data = readStream(inputStream);
            String html = new String(data, "gb2312");
            int st = html.indexOf("名称:");
            String s = html.split("名称")[1];
            String ss = s.split("规格型号")[0];
            String sss = ss.split("<dd>")[1];
            String ssss = sss.split("</dd>")[0];

            return ssss;
        }

        return null;

    }


    /**
     * 图片本地存储
     *
     * @param bitmap
     * @param url
     */
    public static void savePicture(Bitmap bitmap, String url) {
        File f = new File("/sdcard/bunner/", url);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 判断本地文件是否存在   false 存在
     *
     * @param path
     * @return
     */
    public static boolean initDownPath(String path) {
        File file = new File("/sdcard/bunner/" + path);
        Log.e("本地的图片路径", "file");
        if (!file.exists()) {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }


    /**
     * 弹出时间选择
     */
    public void showDateDialog(Activity activity, final TextView tv) {
        Calendar d = Calendar.getInstance(Locale.CHINA);
        // 创建一个日历引用d，通过静态方法getInstance() 从指定时区 Locale.CHINA 获得一个日期实例
        Date myDate = new Date();
        // 创建一个Date实例
        d.setTime(myDate);
        // 设置日历的时间，把一个新建Date实例myDate传入
        int year = d.get(Calendar.YEAR);
        int month = d.get(Calendar.MONTH);
        int day = d.get(Calendar.DAY_OF_MONTH);
        //初始化默认日期year, month, day
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            /**
             * 点击确定后，在这个方法中获取年月日
             */
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                tv.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, year, month, day);
        datePickerDialog.setMessage("请选择日期");
        datePickerDialog.show();
    }

    /**
     * 弹出时间选择
     */
    public void showDateDialog2(final Activity activity) {
        Calendar d = Calendar.getInstance(Locale.CHINA);
        // 创建一个日历引用d，通过静态方法getInstance() 从指定时区 Locale.CHINA 获得一个日期实例
        Date myDate = new Date();
        // 创建一个Date实例
        d.setTime(myDate);
        // 设置日历的时间，把一个新建Date实例myDate传入
        int year = d.get(Calendar.YEAR);
        int month = d.get(Calendar.MONTH);
        int day = d.get(Calendar.DAY_OF_MONTH);
        //初始化默认日期year, month, day
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            /**
             * 点击确定后，在这个方法中获取年月日
             */
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Intent it=new Intent("attendance_new_date");
                Bundle bundle=new Bundle();
                bundle.putInt("year",year);
                bundle.putInt("month",(monthOfYear + 1));
                bundle.putInt("day",dayOfMonth);
                it.putExtras(bundle);
                activity.sendBroadcast(it);

            }
        }, year, month, day);
        datePickerDialog.setMessage("请选择日期");
        datePickerDialog.show();
    }


    public void showTimePicker(FragmentActivity activity, final TextView textView) {
        SlideDateTimeListener listener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String time = mFormatter.format(date);
                textView.setText(time);
                textView.setCompoundDrawables(null, null, null, null);
            }

            @Override
            public void onDateTimeCancel() {
            }
        };
        SlideDateTimePicker.Builder builder = new SlideDateTimePicker.Builder(activity.getSupportFragmentManager());
        builder.setListener(listener);
        builder.setMinDate(new Date());
        builder.setInitialDate(new Date());
        builder.setIs24HourTime(true)
                .build()
                .show();
    }


    /**
     * 弹出时间选择
     */
    public void showMonth(final Activity activity, final TextView tv, final String type) {
        Calendar d = Calendar.getInstance(Locale.CHINA);
        // 创建一个日历引用d，通过静态方法getInstance() 从指定时区 Locale.CHINA 获得一个日期实例
        new DateDialog(activity, 0, new DateDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth) {
                tv.setText(startYear + "-" + (startMonthOfYear + 1));
                if (type.equals("1")) {
                    Intent it = new Intent("AttendanceInfoActivity");
                    activity.sendBroadcast(it);
                } else {
                    Intent it = new Intent("AttendanceAllActivity");
                    activity.sendBroadcast(it);
                }
            }
        }, d.get(Calendar.YEAR), d.get(Calendar.MONTH), d.get(Calendar.DATE)).show();

    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            backgroundAlpha(1f);
        }
    }

    /**
     * 主页弹出菜单
     */
    public void showPopupWindow(final Activity activity, final ImageView iv) {
        this.activity = activity;
        sp = activity.getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_task, null);
        popupWindow = new PopupWindow(view, 180, 120);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout lyt_task_fk = (LinearLayout) view.findViewById(R.id.lyt_task_fk);
        LinearLayout lyt_xm = (LinearLayout) view.findViewById(R.id.lyt_xm);
        LinearLayout lyt_task = (LinearLayout) view.findViewById(R.id.lyt_task);
        LinearLayout lyt_gonggao = (LinearLayout) view.findViewById(R.id.lyt_gonggao);
        int i = 0;
        if (null != sp.getString("Permission", "") && !sp.getString("Permission", "").contains("InsertFeedBack")) {
            lyt_task_fk.setVisibility(View.GONE);
            i += 1;
        }
        lyt_task_fk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 问题反馈
                Intent it = new Intent(activity, CreateDemandActivity.class);
                activity.startActivity(it);
                popupWindow.dismiss();
            }
        });

        if (null != sp.getString("Permission", "") && !sp.getString("Permission", "").contains("InsertProject")) {
            lyt_xm.setVisibility(View.GONE);
            i += 1;
        }

        lyt_xm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 项目
                Intent it1 = new Intent(activity, CreateProjectActivity.class);
                activity.startActivity(it1);
                popupWindow.dismiss();
            }
        });

        if (null != sp.getString("Permission", "") && !sp.getString("Permission", "").contains("InsertTask")) {
            lyt_task.setVisibility(View.GONE);
            i += 1;
        }


        lyt_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 任务
                Intent it2 = new Intent(activity, CreateTaskActivity.class);
                activity.startActivity(it2);
                popupWindow.dismiss();
            }
        });
        if (null != sp.getString("Permission", "") && !sp.getString("Permission", "").contains("InsertNotice")) {
            lyt_gonggao.setVisibility(View.GONE);
            i += 1;
        }

        lyt_gonggao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, AnnoucePushActivity.class));
                popupWindow.dismiss();
            }
        });
        // 点击空白处时，隐藏掉pop窗口
        backgroundAlpha(0.6f);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //popupWindow.showAsDropDown(iv, 10, -50);
        popupWindow.showAtLocation(iv, Gravity.TOP | Gravity.LEFT, iv.getLeft() - 180, iv.getTop() - 350 + i * 105);
        popupWindow.setOnDismissListener(new poponDismissListener());
    }


    /**
     * 弹出菜单
     */
    public void showWindow(final Activity activity, final ImageView iv, final String project_coed, final String project_name) {
        this.activity = activity;
        sp = activity.getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_task, null);
        popupWindow = new PopupWindow(view, 180, 120);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout lyt_task_fk = (LinearLayout) view.findViewById(R.id.lyt_task_fk);
        LinearLayout lyt_xm = (LinearLayout) view.findViewById(R.id.lyt_xm);
        LinearLayout lyt_task = (LinearLayout) view.findViewById(R.id.lyt_task);
        LinearLayout lyt_gonggao = (LinearLayout) view.findViewById(R.id.lyt_gonggao);
        lyt_xm.setVisibility(View.GONE);
        lyt_gonggao.setVisibility(View.GONE);
        int i = 0;
        if (null != sp.getString("Permission", "") && !sp.getString("Permission", "").contains("InsertFeedBack")) {
            lyt_task_fk.setVisibility(View.GONE);
            i += 1;
        }
        lyt_task_fk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 任务反馈
                Intent it = new Intent(activity, CreateDemandActivity.class);
                it.putExtra("project_code", project_coed);
                it.putExtra("project_name", project_name);
                activity.startActivity(it);
                popupWindow.dismiss();
            }
        });

        if (null != sp.getString("Permission", "") && !sp.getString("Permission", "").contains("InsertTask")) {
            lyt_task.setVisibility(View.GONE);
            i += 1;
        }
        lyt_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 任务
                Intent it2 = new Intent(activity, CreateTaskActivity.class);
                it2.putExtra("project_code", project_coed);
                it2.putExtra("project_name", project_name);
                activity.startActivity(it2);
                popupWindow.dismiss();
            }
        });
        // 点击空白处时，隐藏掉pop窗口
        backgroundAlpha(0.6f);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //popupWindow.showAsDropDown(iv, 10, -50);
        popupWindow.showAtLocation(iv, Gravity.TOP | Gravity.LEFT, iv.getLeft() - 180, iv.getTop() - 135 + i * 105);
        popupWindow.setOnDismissListener(new poponDismissListener());
    }


    /**
     * 弹出菜单
     */
    public void showGridItem(final Activity activity, final View v, final int position, final String project_code, final String IsAttention, final String role) {
        this.activity = activity;
        sp = activity.getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_grid, null);
        popupWindow = new PopupWindow(view, 180, 120);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout lyt_project_sc = (LinearLayout) view.findViewById(R.id.lyt_project_sc);
        LinearLayout lyt_project_xg = (LinearLayout) view.findViewById(R.id.lyt_project_xg);
        if (!role.contains("UpdateProject")) {
            lyt_project_xg.setVisibility(View.GONE);
        }

        if (!role.contains("UpdateProject")) {
            lyt_project_xg.setVisibility(View.GONE);
        }
        lyt_project_xg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, CreateProjectActivity.class);
                it.putExtra("project_code", project_code);
                activity.startActivity(it);
                popupWindow.dismiss();
            }
        });

        lyt_project_sc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                Map<String, String> pa = new HashMap<>();

                pa.put("LoginUserCode", sp.getString("Code", ""));
                pa.put("ProjectCode", project_code);
                if (IsAttention.equals("1")) {
                    pa.put("op", "0");
                } else if (IsAttention.equals("0")) {
                    pa.put("op", "1");
                }
                params.put("paraJson", Common.MaptoJson(pa));
                String url = DemoApplication.serviceUrl + "/AttentionProject";
                OkHttpUtils.post()
                        .url(url)
                        .params(params)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                String success = "", result = "", msg = "";
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getString("success");
                                    msg = jsonObject.getString("msg");
                                    if (success.equals("true")) {
                                        if (IsAttention.equals("1")) {
                                            Toast.makeText(activity, "取消关注", Toast.LENGTH_SHORT).show();
                                        } else if (IsAttention.equals("0")) {
                                            Toast.makeText(activity, "收藏成功", Toast.LENGTH_SHORT).show();
                                        }
                                        Intent it = new Intent("Reflash");
                                        activity.sendBroadcast(it);
                                    } else {
                                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                    }
                                    popupWindow.dismiss();
                                } catch (Exception e) {
                                }
                            }
                        });
            }
        });
        // 点击空白处时，隐藏掉pop窗口
        backgroundAlpha(0.6f);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //popupWindow.showAsDropDown(iv, 10, -50);
        int[] location = new int[2];
        v.getLocationInWindow(location);
        if (position % 2 == 0) {
            popupWindow.showAtLocation(v, Gravity.RIGHT | Gravity.TOP, location[0] + 220, location[1] + 20);
        } else {
            popupWindow.showAtLocation(v, Gravity.LEFT | Gravity.TOP, location[0] - 300, location[1] + 20);
        }
        Log.e("坐标信息", "X:" + location[0] + ",Y:" + location[1]);
        popupWindow.setOnDismissListener(new poponDismissListener());
    }


    /**
     * 弹出菜单
     */
    public void showSelectP(final Activity activity, final List<Project> list, final EditText ed, final String type) {
        this.activity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.item_select_project, null);
        popupWindow = new PopupWindow(view, 180, 120);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(1000);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("项目列表");
        SwipeMenuListView listview = (SwipeMenuListView) view.findViewById(R.id.listview);
        LinearLayout empty = (LinearLayout) view.findViewById(R.id.empty);
        if (list.size() > 0) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
        SelectProjectAdpater adapter = new SelectProjectAdpater(activity, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("项目", "22222222222");
                ed.setText(list.get(position).getName());
                if (type.equals("1")) {
                    Intent it = new Intent("Select_Project_Demand");
                    it.putExtra("name", list.get(position).getName());
                    it.putExtra("code", list.get(position).getCode());
                    activity.sendBroadcast(it);
                } else if (type.equals("2")) {
                    Intent it = new Intent("Select_Project_Task");
                    it.putExtra("name", list.get(position).getName());
                    it.putExtra("code", list.get(position).getCode());
                    activity.sendBroadcast(it);
                }

                popupWindow.dismiss();
            }
        });
        // 点击空白处时，隐藏掉pop窗口
        backgroundAlpha(0.6f);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(ed, Gravity.CENTER, ed.getLeft(), ed.getTop());
        popupWindow.setOnDismissListener(new poponDismissListener());
    }


    /**
     * 弹出菜单
     */
    public void showSelectPd(final Activity activity, final List<Product> list, final EditText ed, final String type) {
        this.activity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.item_select_project, null);
        popupWindow = new PopupWindow(view, 180, 120);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("模块列表");
        SwipeMenuListView listview = (SwipeMenuListView) view.findViewById(R.id.listview);
        LinearLayout empty = (LinearLayout) view.findViewById(R.id.empty);
        if (list.size() > 0) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
        SelectProductAdpater adapter = new SelectProductAdpater(activity, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("点击了模块", "aaaaaaa");
                ed.setText(list.get(position).getName());
                if (type.equals("1")) {
                    Intent it = new Intent("Select_Product_Demand");
                    it.putExtra("name", list.get(position).getName());
                    it.putExtra("code", list.get(position).getCode());
                    activity.sendBroadcast(it);
                } else if (type.equals("2")) {
                    Intent it = new Intent("Select_Product_Task");
                    it.putExtra("name", list.get(position).getName());
                    it.putExtra("code", list.get(position).getCode());
                    activity.sendBroadcast(it);
                }
                popupWindow.dismiss();
            }
        });
        // 点击空白处时，隐藏掉pop窗口
        backgroundAlpha(0.6f);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new poponDismissListener());
    }


    /**
     * 弹出菜单
     */
    public void showSelectTaskType(final Activity activity, final List<LCB> list, final TextView ed) {
        this.activity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.item_select_project, null);
        popupWindow = new PopupWindow(view, 180, 120);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("类型");
        SwipeMenuListView listview = (SwipeMenuListView) view.findViewById(R.id.listview);
        LinearLayout empty = (LinearLayout) view.findViewById(R.id.empty);
        if (list.size() > 0) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
        SelectTaskTypeAdpater adapter = new SelectTaskTypeAdpater(activity, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ed.setText(list.get(position).getName());
                Intent it = new Intent("TaskType");
                it.putExtra("name", list.get(position).getName());
                it.putExtra("code", list.get(position).getCode());
                activity.sendBroadcast(it);
                popupWindow.dismiss();
            }
        });
        // 点击空白处时，隐藏掉pop窗口
        backgroundAlpha(0.6f);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new poponDismissListener());
    }

    public static String params(String key, String value) {
        String strInput = "";
        try {
            // 输入的JSON字符串(键值对的形式)
            strInput = new JSONStringer().object().key(key).value(value).endObject().toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return strInput;
    }

    public static String params(String key, String value, String key2, String value2) {
        String strInput = "";
        try {
            // 输入的JSON字符串(键值对的形式)
            strInput = new JSONStringer().object().key(key).value(value).key(key2).value(value2).endObject().toString();
            Log.e("strInput", strInput);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return strInput;
    }

    public static String params(String key, String value, String key2, String value2, String key3, String value3, String key4, String value4, String key5, String value5) {
        String strInput = "";
        try {
            // 输入的JSON字符串(键值对的形式)
            strInput = new JSONStringer().object().key(key).value(value).key(key2).value(value2)
                    .key(key3).value(value3).key(key4).value(value4).key(key5).value(value5).endObject().toString();
            Log.e("strInput", strInput);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return strInput;
    }

    /**
     * Map 数组转化成json
     *
     * @param map
     * @return
     */
    public static String MaptoJson(Map<String, String> map) {
        Set<String> keys = map.keySet();
        String key = "";
        String value = "";
        StringBuffer jsonBuffer = new StringBuffer();
        jsonBuffer.append("{");
        for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
            key = (String) it.next();
            value = map.get(key);
            if (value.contains("{")) {
                jsonBuffer.append("\"" + key + "\":" + value + "");
            } else {
                jsonBuffer.append("\"" + key + "\":\"" + value + "\"");
            }
            if (it.hasNext()) {
                jsonBuffer.append(",");
            }
        }
        jsonBuffer.append("}");
        return jsonBuffer.toString();
    }

    /**
     * @param path 图片路径
     * @return
     * @将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     * @author QQ986945193
     * @Date 2015-01-26
     */
    public static String imageToBase64(String path) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(path);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        // 返回Base64编码过的字节数组字符串
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static void CompresBitmap(Activity at, int img) {
        // png图片
        Bitmap bitmap = BitmapFactory.decodeResource(at.getResources(), img);
        try {
            // 保存压缩图片到本地
            File file = new File(Environment.getExternalStorageDirectory(), "aaa.jpg");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fs);
            fs.flush();
            fs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据Uri获取文件的绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param fileUri
     */
    public static String getFileAbsolutePath(Activity context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            }, 1);

            return false;
        }

        return true;
    }

}
