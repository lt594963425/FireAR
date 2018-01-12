package com.mvp.lt.firear.firemodel;


import com.mvp.lt.firear.R;

import java.util.HashMap;
import java.util.Map;

/**
 * PoiTypeMatcher:根据字符串硬编码匹配Poi类型及图片
 *
 */
public class FirePoiTypeMatcher {

	private static String mLabelType;

	private static Map<String, FirePoiType> typeMatcher = new HashMap<String, FirePoiType>();
	private static Map<String, Icon> iconMatcher = new HashMap<String, Icon>();

	/**
	 * 图片类型
	 * 
	 * @author Jinhu
	 * @date 2016年5月12日
	 */
	public static class Icon {
		int mTypeIcon;
		int mBackgroundIcon;

		public Icon(int typeIc, int bgIc) {
			mTypeIcon = typeIc;
			mBackgroundIcon = bgIc;
		}

		public int getType() {
			return mTypeIcon;
		}

		public int getBackground() {
			return mBackgroundIcon;
		}
	}

	static {
		initTypeMatcher();
		initIconMatcher();
	}

	private static void initTypeMatcher() {
		typeMatcher.put("消防员", FirePoiType.FireMn);
		typeMatcher.put("护林员", FirePoiType.ForestRanger);
		typeMatcher.put("无人机", FirePoiType.UAV);

	}

	private static void initIconMatcher() {
		iconMatcher.put("消防员", new Icon(R.mipmap.ic_fireman,
				R.drawable.shaper_ar_marker_btn_rect));
		iconMatcher.put("护林员", new Icon(R.mipmap.ic_forestranger,
				R.drawable.shaper_ar_marker_btn_rect_b));
		iconMatcher.put("无人机", new Icon(R.mipmap.ic_aircraft,
				R.drawable.shaper_ar_marker_btn_rect_b));

	}

	public static String getCurrentLableName() {
		return mLabelType;
	}

	/**
	 * 获取Poi类型
	 * 
	 * @param labelName
	 * @return
	 */
	public static String getPoiType(String labelName) {
		mLabelType = labelName;
		FirePoiType poiType = typeMatcher.get(labelName);
		if (null == poiType)
			return null;
		return poiType.getValue();
	}

	/**
	 * 获取Poi图片
	 * 
	 * @param lableName
	 * @return
	 */
	public static Icon getPoiIcon(String lableName) {
		Icon icon = iconMatcher.get(lableName);
		if (null == icon)
			return new Icon(R.drawable.ic_map_like,
					R.drawable.shaper_ar_marker_btn_rect);
		return icon;
	}
}