package com.zxl.niubixilitysafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.zxl.niubixilitysafe.vo.AppInfo;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppInfoProvider {

	/**
	 * 返回系统里面所有的安装的应用程序的信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();

		List<PackageInfo> packinfos = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
						| PackageManager.GET_PERMISSIONS);

		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		for (PackageInfo packinfo : packinfos) {
			AppInfo appinfo = new AppInfo();
			String version = packinfo.versionName;
			appinfo.setVersion(version);

			String[] permissions = packinfo.requestedPermissions;

			if (permissions != null && permissions.length > 0) {
				for (String permiss : permissions) {
					if ("android.permission.READ_PHONE_STATE".equals(permiss)) {
						appinfo.setPri(true);
					} 
					if ("android.permission.INTERNET".equals(permiss)) {
						appinfo.setNetwork(true);
					} 
					if ("android.permission.SEND_SMS".equals(permiss)
							|| "android.permission.CALL_PHONE".equals(permiss)) {
						appinfo.setMoney(true);
					}
				}
			}
			String packname = packinfo.packageName;
			appinfo.setPackname(packname);

			Drawable appicon = packinfo.applicationInfo.loadIcon(pm);
			appinfo.setAppicon(appicon);

			String appname = packinfo.applicationInfo.loadLabel(pm).toString();
			appinfo.setAppname(appname);

			appinfo.setUserapp(filterApp(packinfo.applicationInfo));

			appinfos.add(appinfo);
			appinfo = null;

		}

		return appinfos;
	}

	/**
	 * 三方应用的过滤器
	 * 
	 * @param info
	 * @return true 三方应用(用户自己安装的应用) false 系统应用
	 */
	public static boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
}
