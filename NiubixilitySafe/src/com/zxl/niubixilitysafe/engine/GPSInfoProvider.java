package com.zxl.niubixilitysafe.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * 保证这个类是单态.
 * 
 * @author Administrator
 * 
 */
public class GPSInfoProvider {
	private static GPSInfoProvider mGpsInfoProvider;
	static Context mContext;
	private GPSInfoProvider(){}
	
	public synchronized static GPSInfoProvider getInstance(Context context){
		if(mGpsInfoProvider == null){
			mGpsInfoProvider = new GPSInfoProvider();
			mContext = context;
			LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度 
			criteria.setAltitudeRequired(true);//要求海拔信息 
			criteria.setBearingRequired(true);//要求方位信息 
	//		criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);//要求方位信息 的精确度
			criteria.setCostAllowed(true);//是否允许付费 
			criteria.setPowerRequirement(Criteria.ACCURACY_HIGH);//对电量的要求
	//		criteria.setSpeedAccuracy(criteria.ACCURACY_HIGH);//对速度的精确度
	//		criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);//对水平的精确度
			criteria.setSpeedRequired(true);//要求速度信息
	//		criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);//对垂直精度
			String provider = lm.getBestProvider(criteria, true);
			/*
			 * 60000 最短时间1分钟获取一次
			 * 最小位移获取一次
			 */
			lm.requestLocationUpdates(provider, 60000, 100, new GPSInfoProvider().new MyLocationListener());
		}
		return mGpsInfoProvider;
	}
	
	private class MyLocationListener implements LocationListener{
		
		/**
		 * 当手机的位置发生改变的时候调用的方法
		 */
		@Override
		public void onLocationChanged(Location location) {
			String longituede = "long：" + location.getLongitude(); // 经度
			String latitude = " && lati：" + location.getLatitude(); // 纬度
			String accuracy = " && accuracy：" + location.getAccuracy(); // 获取到精确度
			SharedPreferences sp = mContext.getSharedPreferences("config",
					Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("last_loaction", latitude + longituede + accuracy);
			editor.commit();
		}
		
		/**
         * GPS禁用时触发
         */
		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}
		
		/**
         * GPS开启时触发
         */
		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}
		
		/**
         * GPS状态变化时触发
         */
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	/**
	 * 返回最后一次的位置信息
	 * @return
	 */
	public String getLastLoation() {
		SharedPreferences sp = mContext.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sp.getString("last_loaction", null);
	}
}
