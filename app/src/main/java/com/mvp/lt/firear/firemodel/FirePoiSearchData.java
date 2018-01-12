package com.mvp.lt.firear.firemodel;

import java.util.List;

/**
 * PoiSearchData
 * 
 * @记录Poi搜索半径及结果集

 */
public class FirePoiSearchData {

	private int mRadius;

	private List<FirePoiItem> mPois;

	public void setRadius(int radius) {
		mRadius = radius;
	}

	public void setPois(List<FirePoiItem> pois) {
		mPois = pois;
	}

	public int getRadius() {
		return mRadius;
	}

	public List<FirePoiItem> getPois() {
		return mPois;
	}

	public void clearPois() {
		if (null != mPois) {
			mPois.clear();
		}
		mPois = null;
	}

}
