package com.zxl.niubixilitysafe.test;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.test.AndroidTestCase;

public class TestGetLunchActivity extends AndroidTestCase {

	public void getLunchActiviy() throws Exception {

		PackageManager pm = getContext().getPackageManager();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,
				PackageManager.GET_INTENT_FILTERS
		);
		System.out.println(resolveInfos.size());
		for (ResolveInfo info : resolveInfos) {
			String packname = info.activityInfo.packageName;
			System.out.println(packname);
			System.out.println(pm.getPackageInfo(packname, 0).applicationInfo
					.loadLabel(pm));
			System.out.println("--------");
		}

	}
}
