package com.mvp.lt.firear.firemodel;

/**
 * $name
 * 基本信息数据类
 *
 * @author ${LiuTao}
 * @date 2018/1/11/011
 */

public class FirePoiItem {
    private  String poiId;
    private  String poiName;
    private  String poiPhone;
    private  String poiDescriptor;
    private  double mlatitude;
    private  double mlongitude;
    private  double mAltitude;

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getPoiPhone() {
        return poiPhone;
    }

    public void setPoiPhone(String poiPhone) {
        this.poiPhone = poiPhone;
    }

    public String getPoiDescriptor() {
        return poiDescriptor;
    }

    public void setPoiDescriptor(String poiDescriptor) {
        this.poiDescriptor = poiDescriptor;
    }

    public double getMlatitude() {
        return mlatitude;
    }

    public void setMlatitude(double mlatitude) {
        this.mlatitude = mlatitude;
    }

    public double getMlongitude() {
        return mlongitude;
    }

    public void setMlongitude(double mlongitude) {
        this.mlongitude = mlongitude;
    }

    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(double altitude) {
        mAltitude = altitude;
    }
}
