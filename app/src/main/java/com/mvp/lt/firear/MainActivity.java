package com.mvp.lt.firear;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mvp.lt.firear.ar.ui.MixActivity;
import com.mvp.lt.firear.ar.ui.OriginActivity;
import com.mvp.lt.firear.util.PhoneUtil;
import com.mvp.lt.firear.util.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.mvp.lt.firear.Constants.IS_DEBUG;
import static com.mvp.lt.firear.Constants.TIME_WAIT_EXIT;

public class MainActivity extends OriginActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.outdoor_scene_btn)
    Button mOutdoorSceneBtn;
    @BindView(R.id.call_btn)
    Button mCallBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.outdoor_scene_btn, R.id.call_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.outdoor_scene_btn:
                startActivity(new Intent(this, MixActivity.class));
                break;
            case R.id.call_btn:
                System.out.println("===呼叫老婆的按钮启动了");
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.request(Manifest.permission.CALL_PHONE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    //让可以去打电话的应用去帮我完成这个事情
                                    PhoneUtil.call("15974255013");
                                } else {
                                    Toast.makeText(MainActivity.this, "没有权限", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });

                break;
        }
    }

    /* 退出程序判定. */
    private AtomicBoolean isExit = new AtomicBoolean(false);

    @Override
    public void onBackPressed() {
        if (isExit.compareAndSet(false, true)) {
            // 询问是否退出程序
            ToastUtil.showShort("再按一次退出");
            // 待2.5s后再按退出程序
            Timer exitTimer = new Timer(true);
            exitTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit.set(false);
                }
            }, TIME_WAIT_EXIT);
        } else {
            // 退出程序
            AppManager.getInstance().exit();
        }
        if (IS_DEBUG) {
            Log.d(TAG, "--OnBackPressed()--");
        }
    }

//    /**
//     * 定位信息更新回调
//     */
//    @Override
//    public void onLocationSucceeded(AMapLocation amapLocation) {
//        super.onLocationSucceeded(amapLocation);
//        // 去掉省会级的地址信息
//        String address = mLocationPoint.getAddress();
//        if (null != address) {
//            if (address.length() >= 3)
//                address = address.substring(3);
//        } else {
//            address = "";
//        }
//        if (ALocationController.Is_Frist_Locate
//                && AppManager.getInstance().getNetConnectedState()) {
//            if (IS_DEBUG) {
//                Log.e("TAG", address);
//            }
//            ALocationController.Is_Frist_Locate = false;
//        }
//    }
}
