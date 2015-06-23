package com.zxl.niubixilitysafe.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtil {

	/**
	 * 判断服务是否处于开启状态
	 * @param context
	 * @param classname
	 * @return
	 */
	public static boolean  isServiceRunning(Context context , String classname) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo>  infos = am.getRunningServices(100);
		for(RunningServiceInfo info : infos){
			if(classname.equals(info.service.getClassName()))
					return true;
		}
		
		
		return false;
	}
}
