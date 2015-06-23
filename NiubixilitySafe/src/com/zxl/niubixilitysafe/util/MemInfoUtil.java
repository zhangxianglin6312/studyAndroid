package com.zxl.niubixilitysafe.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class MemInfoUtil {

	/**
	 * 获取正在运行的进程数目
	 * @param context
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		return infos.size();
	}
	
	/**
	 * 获取系统的剩余内存信息
	 */
	public static long getAvailMem(Context context){
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	/**
	 * 获取手机的总的内存大小
	 */
	public static long getTotalMem(){
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String result = br.readLine();
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<result.length();i++){
				char c = result.charAt(i);
				if(c<='9'&&c>='0'){
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString())*1024;
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
