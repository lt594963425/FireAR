package com.mvp.lt.firear.util;

import android.content.Intent;
import android.net.Uri;

import com.mvp.lt.firear.AppManager;

/**
 * $name
 *
 * @author ${LiuTao}
 * @date 2018/1/10/010
 */

public class PhoneUtil {


    /**
     * 跳至拨号界面
     *
     * @param phoneNumber 电话号码
     */
    public static void callDial(final String phoneNumber) {
        AppManager.getInstance().startActivity(getDialIntent(phoneNumber, true));
    }
    /**
     * 获取跳至拨号界面意图
     *
     * @param phoneNumber 电话号码
     * @param isNewTask   是否开启新的任务栈
     * @return 跳至拨号界面意图
     */
    public static Intent getDialIntent(final String phoneNumber, final boolean isNewTask) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        return getIntent(intent, isNewTask);
    }


    /**
     * 调用拨号功能,跳过拨号界面
     * @param phone 电话号码
//     */
//    public static  void call(String phone) {
////        Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));
////        startActivity(intent);
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_CALL);
//        //打电话具体需要的数据
//        intent.setData(Uri.parse("tel:15974255013"));
//        //开始这个企图
//        AppManager.getInstance().startActivity(intent);
//    }


    /**
     * 拨打电话
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.CALL_PHONE" />}</p>
     *
     * @param phoneNumber 电话号码
     */
    public static void call(final String phoneNumber) {
        AppManager.getInstance().startActivity(getCallIntent(phoneNumber, true));
    }
    /**
     * 获取拨打电话意图
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.CALL_PHONE" />}</p>
     *
     * @param phoneNumber 电话号码
     * @param isNewTask   是否开启新的任务栈
     * @return 拨打电话意图
     */
    public static Intent getCallIntent(final String phoneNumber, final boolean isNewTask) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
        return getIntent(intent, isNewTask);
    }
    private static Intent getIntent(final Intent intent, final boolean isNewTask) {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }
}
