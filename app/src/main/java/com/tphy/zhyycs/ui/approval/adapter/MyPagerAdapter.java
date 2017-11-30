package com.tphy.zhyycs.ui.approval.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jimmy.common.util.ToastUtils;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.approval.bean.QRCodeTypes;
import com.tphy.zhyycs.ui.approval.widget.StartQRDialog;
import com.tphy.zhyycs.ui.attandence.BuQianActivity;
import com.tphy.zhyycs.ui.attandence.CaptureActivity;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class MyPagerAdapter extends PagerAdapter {

    private List<View> viewList;
    private Context context;
    //    private String dakaLeiBie = "1";
    private List<QRCodeTypes> qrCodeTypes;
    private String loginUserCode;

    public MyPagerAdapter(List<View> viewList, Context context) {
        this.viewList = viewList;
        this.context = context;
    }

    public void setQrCodeTypes(List<QRCodeTypes> qrCodeTypes) {
        this.qrCodeTypes = qrCodeTypes;
    }

    public void setLoginUserCode(String loginUserCode) {
        this.loginUserCode = loginUserCode;
    }

//    public void setDakaLeiBie(String leiBie) {
//        this.dakaLeiBie = leiBie;
//    }

    public List<View> getViewList() {
        return viewList;
    }

    @Override

    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewList.get(position);
        ViewParent parent = view.getParent();
        if (parent != null) {
            container.removeAllViews();
        }
        if (position == 0) {
            ImageView kq_daka = (ImageView) view.findViewById(R.id.kq_daka);
            TextView tv_showQr = (TextView) view.findViewById(R.id.kq_tv_showQr);
            TextView tv_buqian = (TextView) view.findViewById(R.id.kq_tv_showbuQian);
            kq_daka.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if ("5".equals(dakaLeiBie)) {
//                        ToastUtils.showShortToast(context, "您已经签离");
//                    } else {
                    Intent intent = new Intent(context, CaptureActivity.class);
//                        intent.putExtra("LeiBie", dakaLeiBie);
//                        intent.putExtra("AttendanceCode", "0");
                    context.startActivity(intent);
//                    }
                }
            });
            tv_showQr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (qrCodeTypes.size() > 0) {
                        showDialog(qrCodeTypes);
                    } else {
                        ToastUtils.showShortToast(context, "获取二维码类型失败");
                    }
//                    showSingleAlertDialog();
                }
            });
            tv_buqian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BuQianActivity.class);
                    context.startActivity(intent);
                }
            });
        }
//        if (position == 3) {
//            ImageView iv_buqian = (ImageView) view.findViewById(R.id.kq_iv_buqian);
//            iv_buqian.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, BuQianActivity.class);
//                    context.startActivity(intent);
//                }
//            });
//        }
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    private void showDialog(final List<QRCodeTypes> items) {
        final StartQRDialog startQRDialog = new StartQRDialog(context, items);
        startQRDialog.show();
        startQRDialog.setCanceledOnTouchOutside(false);
        startQRDialog.setOnChooseListener(new StartQRDialog.onChooseListener() {

            @Override
            public void confirmListener(int position) {
                if (position > -1) {
                    String qRCodeTypeCode = qrCodeTypes.get(position).getCode();
                    sendQrCode(qRCodeTypeCode);
                    startQRDialog.dismiss();
                } else {
                    ToastUtils.showShortToast(context,"请选择类型");
                }
            }

            @Override
            public void cancleListenner() {
                startQRDialog.dismiss();
            }
        });

    }

//    private void showDialogNew(List<String> items) {
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_start_qr2, null);
//        ListView listView = (ListView) view.findViewById(R.id.qr_dia_list);
//        ImageView iv_cancel = (ImageView) view.findViewById(R.id.alert_iv_cancel);
//        TextView tv_confirm = (TextView) view.findViewById(R.id.alert_tv_confirm);
//        final QRDialogAdapter qrDialogAdapter = new QRDialogAdapter(context, items);
//        listView.setAdapter(qrDialogAdapter);
//        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//        alertDialog.show();
//        alertDialog.getWindow().setContentView(view);
//        iv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//        tv_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("WQ", "选择的position是==》" + qrDialogAdapter.getPosition());
//                alertDialog.dismiss();
//            }
//        });
//    }

    // 单选提示框
    private AlertDialog alertDialog;

    public void showSingleAlertDialog() {
        final String[] items = {"上班签到", "晨会签到", "下班"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.title_custom, null);
        alertBuilder.setCustomTitle(view);
        alertBuilder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
                Toast.makeText(context, items[index], Toast.LENGTH_SHORT).show();
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码

                // 关闭提示框
                alertDialog.dismiss();
            }
        });
        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 业务逻辑代码

                // 关闭提示框
                alertDialog.dismiss();
            }
        });
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void sendQrCode(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", loginUserCode, "QRCodeTypeCode", code));
        String url = DemoApplication.serviceUrl + "/SendQRCode";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        ToastUtils.showToast(getContext(), e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                ToastUtils.showShortToast(context, "二维码已推送");
                            } else {
                                ToastUtils.showToast(context, msg);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
