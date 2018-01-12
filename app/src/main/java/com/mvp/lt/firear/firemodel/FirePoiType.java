package com.mvp.lt.firear.firemodel;

/**
 * Poi分类
 * <p>
 * 枚举类型
 */
public enum FirePoiType {
    FireMn("消防员"), ForestRanger("护林员"), UAV("无人机");

    private final String value;

    FirePoiType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
