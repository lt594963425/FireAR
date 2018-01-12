package com.mvp.lt.firear.ar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.mvp.lt.firear.AppManager;
import com.mvp.lt.firear.R;
import com.mvp.lt.firear.ar.entity.ARData;
import com.mvp.lt.firear.ar.util.ARMarker;
import com.mvp.lt.firear.ar.view.ARMarkerDialog;
import com.mvp.lt.firear.ar.view.IconEditText;
import com.mvp.lt.firear.ar.view.MInfowindow;
import com.mvp.lt.firear.control.ALocationController;
import com.mvp.lt.firear.control.AMapController;
import com.mvp.lt.firear.control.APoiSearcher;
import com.mvp.lt.firear.data.DatabaseManager;
import com.mvp.lt.firear.data.GeoPointDao;
import com.mvp.lt.firear.model.AnimationFactory;
import com.mvp.lt.firear.model.GeoPoint;
import com.mvp.lt.firear.model.PoiTypeMatcher;
import com.mvp.lt.firear.util.ToastUtil;
import com.mvp.lt.firear.view.ArcMenu;
import com.mvp.lt.firear.view.GeoPointInfoDialog;
import com.mvp.lt.firear.view.MaterialDrawerLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import static com.mvp.lt.firear.Constants.EVENT_MAP_ZOOM_IN;
import static com.mvp.lt.firear.Constants.EVENT_MAP_ZOOM_OUT;
import static com.mvp.lt.firear.Constants.IS_DEBUG;
import static com.mvp.lt.firear.Constants.NO_RESULT;
import static com.mvp.lt.firear.Constants.VALUE_DEFAULT_SEARCH_RADIUS;
import static com.mvp.lt.firear.Constants.VALUE_POSITION_DRAWERITEM_OFFLINEMAP;
import static com.mvp.lt.firear.Constants.VALUE_POSITION_DRAWERITEM_RADIUS;
import static com.mvp.lt.firear.Constants.VALUE_POSITION_DRAWERITEM_USERPOINT;
import static com.mvp.lt.firear.Constants.VALUE_POSITION_DRAWERITEM_WEATHER;


/**
 * ARActivity:AR层
 */
public class MixActivity extends ARActivity implements OnClickListener, APoiSearcher.APoiSearchListener,
        AMapController.AMapStatusLinstener, MInfowindow.onInfowindowClickedLinstener, ARMarkerDialog.onNaviListener,
       GeoPointInfoDialog.OnInputConfirmListener {
    private static final String TAG = MixActivity.class.getSimpleName();

    // -------- 界面相关 --------

    /* 侧滑标题栏 . */
    /* 已封装侧滑栏及标题栏逻辑 . */
    private MaterialDrawerLayout mDrawer;

    /* 地图层. */
    private View mMapFrame;
    /* 地图层视图 . */
    private MapView mMapView;
    /* 地图Infowindow . */
    private MInfowindow minfowindow;
    /* 地图缩放按钮 . */
    private View mZoomOutBtn;
    private View mZoomInBtn;
    /* 图层按钮. */
    private ImageView mLayerBtn;

    /* 搜索标签菜单. */
    private ArcMenu mArcMenu;

    /* 进度对话框 . */
    private SweetAlertDialog mProgressDialog;

    /* Marker信息对话框 . */
    private ARMarkerDialog mMarkerDialog;

    /* 导航按钮 . */
    private View mNaviBtn;


    /* 地理信息点输入对空框 . */
    private GeoPointInfoDialog mGeoPointInfoDialog;



    // -------- 业务相关 --------

    /* Poi点搜索. */
    private APoiSearcher mPoiSearcher;

    /* Poi结果 . */
    private PoiResult mCurrentPoiResult;

    /* 地图控制 . */
    private AMapController mMapController;

    /* 地图移动锁 . */
    private AtomicBoolean mMapMoveToLock = new AtomicBoolean(true);


    /* 高德兴趣点显示锁 . */
    private AtomicBoolean mAmapPoiLock = new AtomicBoolean(false);

    /* 地理信息点数据访问接口 . */
    private GeoPointDao mGeoPointDao;

    /* 当前所有用户定义信息点 . */
    private List<GeoPoint> mGeoPointList;

    /* 数据操作锁 . */
    private AtomicBoolean mAffairLock = new AtomicBoolean(false);
    /* 定位按钮 . */
    private View mLocateBtn;
    private IconEditText mIconSearchEdt;
    // ------------------------ 初始化视图 ------------------------

    /**
     * 初始化视图组件
     */
    private void initView() {
        // ---初始化视图组件---
        // 地图层
        mMapFrame = findViewById(R.id.id_frameLayout_map);
        // 地图视图
        mMapView = (MapView) findViewById(R.id.id_mapView);
        // 初始化Infowindow
        minfowindow = new MInfowindow(this);
        // 地图缩放
        mZoomOutBtn = findViewById(R.id.id_imageView_zoomOut_btn);
        mZoomInBtn = findViewById(R.id.id_imageView_zoomIn_btn);
        //图层按钮
        mLayerBtn = (ImageView) findViewById(R.id.id_imageView_layer);
        //搜索
        mIconSearchEdt = findViewById(R.id.id_editText_search);

        // 搜索标签菜单
        mArcMenu = (ArcMenu) findViewById(R.id.id_arcMenu);
        // 定位按钮
        mLocateBtn = findViewById(R.id.id_imageView_locate_btn);
        // 导航按钮
        mNaviBtn = findViewById(R.id.id_imageView_ar_navi);

        // 侧滑标题栏
        mDrawer = (MaterialDrawerLayout) findViewById(R.id.id_layout_drawer);
        mDrawer.bindTitleBar(findViewById(R.id.id_layout_titlebar));

        mProgressDialog = new SweetAlertDialog(this,
                SweetAlertDialog.PROGRESS_TYPE);

        mMarkerDialog = new ARMarkerDialog(this);
        mMarkerDialog.setOnNaviListener(this);


        mGeoPointInfoDialog = new GeoPointInfoDialog(this);

        // 注册视图监听器
        registerViewListener();
    }

    /**
     * 初始化视图监听器
     */
    private void registerViewListener() {
        // ------注册监听器-------
        // 监听InfoWindow点击
        minfowindow.setOnInfowindowClickedLinstener(this);
        mZoomOutBtn.setOnClickListener(this);
        mZoomInBtn.setOnClickListener(this);
        mLayerBtn.setOnClickListener(this);
        mLocateBtn.setOnClickListener(this);
        mGeoPointInfoDialog.setOnInputConfirmListener(this);
        // 注册搜索栏图标触发响应
        mIconSearchEdt.setOnIconClickedListener(new IconEditText.OnIconClickedListener() {

            @Override
            public void onVoiceStart() {
                mVoiceController.startListeningByDialog(MixActivity.this);
            }

            @Override
            public void onSearchStart() {

                String keyword = mIconSearchEdt.getText().toString();
                if (null == keyword || "".equals(keyword))
                    return;

                mProgressDialog.show();
                // 清空之前PoiResult
                mPoiSearchData.clearPois();
                // 关键字搜索Poi
                mPoiSearcher.searchNearbyKeyword(mALocation, keyword,
                        mPoiSearchData.getRadius());
                mIconSearchEdt.setText("");
            }
        });
        // ----侧滑栏列表视图触发响应----
        mDrawer.setOnItemClickListener(new MaterialDrawerLayout.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    // 设置搜索半径
                    case VALUE_POSITION_DRAWERITEM_RADIUS:
                        Log.e("mDrawer,onItemClick", "位置：" + position + "");
                        // 弹出拖拉条对话框 设置搜素半径
                        SweetAlertDialog dialog = new SweetAlertDialog(MixActivity.this,
                                SweetAlertDialog.SEEK_TYPE);

                        /**
                         * 对话框滑动条更新回调
                         */
                        dialog.setOnSweetSeekBarChangeListener(new SweetAlertDialog.OnSweetSeekBarChangeListener() {
                            @Override
                            public void onSeekBarChanged(int range) {
                                mPoiSearchData.setRadius(range);
                                ARData.getInstance().setRadius(range);
                                updateView();
                            }
                        });
                        /**
                         * 对话框确认按钮响应
                         */
                        dialog.setConfirmClickListener(new OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                int dialogType = sweetAlertDialog.getAlerType();
                                switch (dialogType) {
                                    case SweetAlertDialog.PROGRESS_TYPE:
                                        // 拖动条对话框
                                        // 设置搜索半径
                                        mPoiSearchData.setRadius(sweetAlertDialog.getRadius());
                                        ARData.getInstance().setRadius(sweetAlertDialog.getRadius());
                                        break;
                                }
                                sweetAlertDialog.dismiss();
                            }
                        });
                        dialog.show();
                        dialog.setProgress(mPoiSearchData.getRadius());
                        break;
                    case VALUE_POSITION_DRAWERITEM_WEATHER:
                        // 启动WeatherActivity

                        break;
                    case VALUE_POSITION_DRAWERITEM_OFFLINEMAP:
                        // 启动OfflineMapActivity
                        break;
                    case VALUE_POSITION_DRAWERITEM_USERPOINT:

                        break;
                }
            }
        });

        // 搜索标签点击响应
        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClicked(View v, int pos) {
                mArcMenu.setUpOpenStateIcon();
                if (null == mALocation)
                    return;
                String labelName = v.getTag() + "";
                String poiType = PoiTypeMatcher.getPoiType(labelName);

                mProgressDialog.show();
                // 清空之前PoiResult
                mPoiSearchData.clearPois();
                // 搜索Poi
                if (null != poiType) {
                    // 通过分类搜索Poi 使用最大范围搜索 但显示搜索结果根据设置radius
                    mPoiSearcher.searchNearbyType(mALocation, poiType,
                            VALUE_DEFAULT_SEARCH_RADIUS);
                } else {
                    // 通过关键字搜索Poi
                    mPoiSearcher.searchNearbyKeyword(mALocation, labelName,
                            VALUE_DEFAULT_SEARCH_RADIUS);
                }
            }
        });
    }

    /**
     * 显示隐藏导航按钮
     *
     * @param show
     */
    private void showNaviBtn(boolean show) {
        if ((show && mNaviBtn.isShown()) || (!show && !mNaviBtn.isShown()))
            return;
        Animation anim = null;
        if (show) {
            anim = AnimationFactory.alphaAnimation(0f, 1f, 500);
        } else {
            anim = AnimationFactory.alphaAnimation(1f, 0f, 500);
        }
        mNaviBtn.startAnimation(anim);
        mNaviBtn.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    // ------------------------ 生命周期 ------------------------
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化视图
        initView();
        // 地图控制
        mMapController = new AMapController(this, mMapView);
        mMapController.onCreate(savedInstanceState);
        // 将地图视野移到当前所在位置
        mMapController.moveToLocation(mLocationPoint.getLatLng());
        // 添加Poi层至地图
        mMapController.addPoiOverlay(mPoiSearchData.getPois());
        // 设置地图Infowindow样式
        mMapController.setInfowindow(minfowindow);
        // 监听地图状态
        mMapController.setAMapStatusLinstener(this);

        // Poi搜索
        mPoiSearcher = new APoiSearcher(AppManager.getInstance());
        mPoiSearcher.setAPoiSearchListener(this);

        // 获取搜索数据
        if (null != mPoiSearchData) {
            ARData.getInstance().setRadius(mPoiSearchData.getRadius());
            if (null != mPoiSearchData.getPois())
                updateARIconMarkers(mPoiSearchData.getPois());
        }

        // 获得数据访问接口
        mGeoPointDao = DatabaseManager.getInstance(this).getGeoPointDao();
        mGeoPointList = mGeoPointDao.fetchAllGeoPoints();

        if (IS_DEBUG) {
            Log.d(TAG, "--OnCreated()--");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (IS_DEBUG) {
            Log.d(TAG, "--OnStarted()--");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 地图视图启动绘制
        mMapController.onResume();
        mMapMoveToLock.set(true);
        mAmapPoiLock.set(false);
        if (IS_DEBUG) {
            Log.d(TAG, "--OnResumed()--");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 地图视图停止绘制
        mMapController.onPause();
        mAmapPoiLock.set(false);
        mAffairLock.set(false);
        if (IS_DEBUG) {
            Log.d(TAG, "--OnPaused()--");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (IS_DEBUG) {
            Log.d(TAG, "--OnStoped()--");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁地图
        mMapController.onDestroy();
        if (IS_DEBUG) {
            Log.d(TAG, "--OnDestroyed()--");
        }
    }

    @Override
    public void onBackPressed() {
        AppManager.getInstance().delActivity(this);
        this.finish();
    }


    // ------------------------ 业务逻辑 ------------------------


    // ------------------------ 响应事件 ------------------------

    /**
     * 界面里按钮点击响应
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_imageView_ar_navi:
                Intent mixNavintent = new Intent(this,
                        MixNaviActivity.class);
                startActivity(mixNavintent);
                break;
            case R.id.id_imageView_zoomIn_btn:
                mMapController.zoom(EVENT_MAP_ZOOM_IN);
                break;
            case R.id.id_imageView_zoomOut_btn:
                mMapController.zoom(EVENT_MAP_ZOOM_OUT);
                break;
            case R.id.id_textView_item1:
                // 标记当前位置信息
                mGeoPointInfoDialog.show();
                break;
            case R.id.id_textView_item2:

                break;
            case R.id.id_imageView_locate_btn:
                // 地图视图移动到当前位置
                mMapController.moveToLocation(mLocationPoint.getLatLng());
        }
    }


    /**
     * 定位信息更新回调
     */
    @Override
    public void onLocationSucceeded(AMapLocation amapLocation) {
        mMapController.moveToLocation(mLocationPoint.getLatLng());
        super.onLocationSucceeded(amapLocation);
        mMapController.setLocationInfo(amapLocation);
        // 去掉省会级的地址信息
        String address = mLocationPoint.getAddress();
        if (null != address) {
            if (address.length() >= 3)
                address = address.substring(3);
        } else {
            address = "";
        }
        if (ALocationController.Is_Frist_Locate
                && AppManager.getInstance().getNetConnectedState()) {
            Log.e("TAG", address);
            ToastUtil.showShort("当前位置:" + address);
            ALocationController.Is_Frist_Locate = false;
        }
    }

    /**
     * Poi点搜索信息更新回调
     */
    @Override
    public void onPoiSearched(PoiResult result) {
        // 延迟500ms再隐藏进度对话框
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                Log.e("onPoiSearched", "更新地图层");
                if (null == result) {
                    return;
                }
                mPoiSearchData.setPois(result.getPois());
                // 处理Poi搜索
                mMapController.removeOverlay();
                List<PoiItem> pois = mPoiSearchData.getPois();
                if (null == pois || 0 == pois.size()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(NO_RESULT);
                        }
                    });

                }
                // 更新AR图层

                updateARIconMarkers(pois);
                // 再添加当前Poi结果至地图
                mMapController.addPoiOverlay(result);
            }
        }, 500);
    }

    /**
     * Marker点击回调
     */
    @Override
    protected void onMarkerTouch(ARMarker marker) {
        if (!mMarkerDialog.isShowing()) {
            // 设置导航信息
            ARData.getInstance().setDestinationMarker(marker);
            mDestinationPoint.setLatLng(marker.getLatLng());
            // 弹出Marker信息对话框
            mMarkerDialog.show();
            if (null != mTouchedMarker) {
                marker = mTouchedMarker;
                mMarkerDialog.updatePoiName(marker
                        .getName());
                mMarkerDialog.updatePoiAddress(marker
                        .getAddress());
                mMarkerDialog
                        .updatePoiTypeImg(PoiTypeMatcher
                                .getPoiIcon(PoiTypeMatcher
                                        .getCurrentLableName()));
            }
        }
    }

    /**
     * 设备水平放置回调
     */
    @Override
    protected void inHorizontal(boolean isHorizontal) {
        if (isHorizontal) {
            // 显示地图
            mMapFrame.setVisibility(View.VISIBLE);
            // 将地图视野移到当前所在位置
            if (mMapMoveToLock.compareAndSet(false, true)) {
                mMapController.moveToLocation(mLocationPoint.getLatLng());
            }
            mZoomInBtn.setVisibility(View.VISIBLE);
            mZoomOutBtn.setVisibility(View.VISIBLE);
            mLayerBtn.setVisibility(View.VISIBLE);
            mLocateBtn.setVisibility(View.VISIBLE);
            mIconSearchEdt.setVisibility(View.VISIBLE);

            mMapView.postInvalidate();
        } else {
            // 不显示地图
            mMapFrame.setVisibility(View.INVISIBLE);
            mZoomInBtn.setVisibility(View.INVISIBLE);
            mZoomOutBtn.setVisibility(View.INVISIBLE);
            mLayerBtn.setVisibility(View.INVISIBLE);
            mLocateBtn.setVisibility(View.INVISIBLE);
            mIconSearchEdt.setVisibility(View.INVISIBLE);
            mMapMoveToLock.set(false);
            if (mNaviBtn.isShown()) {
                showNaviBtn(false);
            }
        }

    }

    //	@Override
    public void onMapStatusChanged(Marker mCurrentMarker,
                                   LatLng mCurrentLatLng, AMapController.AMapStatus status) {
        switch (status) {
            case onMapClick:
                showNaviBtn(false);
                break;
            case onMarkerClick:
                GeoPoint.poiMarkerToGeoPoint(mCurrentMarker, mDestinationPoint);
                showNaviBtn(true);
                break;
            case onRegeocodeSearched:
                GeoPoint.poiMarkerToGeoPoint(mCurrentMarker, mDestinationPoint);
                showNaviBtn(true);
                break;
        }
    }

    /**
     * Infowindow 点击路线按钮,步行路径规划
     */
    @Override
    public void searchWalkRoute(Marker marker) {
        mDestinationPoint.setLatLng(marker.getPosition());
        String snippet = marker.getSnippet();
        if (null == snippet || "".equals(snippet)) {
            ToastUtil.showShort("目的地址信息不全");
            return;
        }
        mDestinationPoint.setAddress(snippet);
        mMapController.getMapQueryer().searchWalkRoute(
                mLocationPoint.getLatLng(), mDestinationPoint.getLatLng());

    }


    /**
     * 标记地点输入备注信息确认回调
     */
//	@Override
    public void onInputeConfirm(String inputTxt) {

        if (null == inputTxt)
            return;

        if (mAffairLock.get())
            return;
        mProgressDialog.show();
        mAffairLock.set(true);

        final GeoPoint point = mLocationPoint.clone();
        point.setName(inputTxt);

        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                GeoPointDao mGeoPointDao = DatabaseManager.getInstance(
                        MixActivity.this).getGeoPointDao(); // 数据库已在AppManager层打开
                mGeoPointDao.addGeoPoint(point); // 向数据库提交当前位置点
                mProgressDialog.dismiss();
                mAffairLock.set(false);
            }
        }, 500);
    }
    /**
     * 语音识别结果回调
     */
    @Override
    public void onResult(String result) {
        if (null == result || "".equals(result)) {
            return;
        }
        mIconSearchEdt.setText(result);
    }

    @Override
    public void onStartNavi() {
        Intent mixNavintent = new Intent(this,
                MixNaviActivity.class);
        startActivity(mixNavintent);
    }

}
