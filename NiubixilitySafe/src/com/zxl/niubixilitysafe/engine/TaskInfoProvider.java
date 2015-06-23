package com.zxl.niubixilitysafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.zxl.niubixilitysafe.R;
import com.zxl.niubixilitysafe.vo.TaskInfo;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class TaskInfoProvider {

	public static List<TaskInfo> getTaskInfos(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<TaskInfo> taskinfos = new ArrayList<TaskInfo>();
		List<RunningAppProcessInfo> runapps = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runapp : runapps) {
			TaskInfo taskinfo = new TaskInfo();
			String packname = runapp.processName;
			taskinfo.setPackname(packname);// 设置应用程序的包名 一般来说 包名就进程名
			long memsize = am.getProcessMemoryInfo(new int[] { runapp.pid })[0]
					.getTotalPrivateDirty() * 1024;
			taskinfo.setMemsize(memsize);
			
			try {
				PackageInfo  packinfo =	pm.getPackageInfo(packname, 0);
				taskinfo.setIcon(packinfo.applicationInfo.loadIcon(pm));
				taskinfo.setAppname(packinfo.applicationInfo.loadLabel(pm).toString());
				
				if(AppInfoProvider.filterApp(packinfo.applicationInfo)){
					taskinfo.setIsusertask(true);
				}else{
					taskinfo.setIsusertask(false);
				}
				
			} catch (NameNotFoundException e) {
				//异常处理,有一些应用程序的进程名 并不对应一个apk的包
				//可能是系统的内核程序 或者是存c实现的程序
				e.printStackTrace();
				taskinfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				taskinfo.setAppname(packname);
			}
			taskinfos.add(taskinfo);
		}
		return taskinfos;

	}
}
