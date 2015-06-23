package com.zxl.niubixilitysafe;

import java.util.ArrayList;
import java.util.List;

import com.zxl.niubixilitysafe.engine.AppInfoProvider;
import com.zxl.niubixilitysafe.util.DensityUtil;
import com.zxl.niubixilitysafe.vo.AppInfo;

import android.R.dimen;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {
	private static final String TAG = "AppManagerActivity";
	private TextView tv_availmem;
	private TextView tv_availsd;
	private ListView lv_appmanger;
	private List<AppInfo> appinfos;

	private List<AppInfo> userAppinfos;
	private List<AppInfo> systemAppinfos;

	private LinearLayout ll_loading;

	private PopupWindow popupwindow;

	private String clickedpackname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_manager);
		tv_availmem = (TextView) findViewById(R.id.tv_appmanger_availmem);
		tv_availsd = (TextView) findViewById(R.id.tv_appmanger_availsd);
		lv_appmanger = (ListView) findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_appmanger_loading);
		// 初始化 剩余内存 剩余sd卡
		initTextView();

		// 初始化listview里面的内容

		lv_appmanger.setOnItemClickListener(new OnItemClickListener() {

			// 第二个参数 view 代表的是listview里面的每一个显示的条目
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				dismissPopupWindow();

				Object obj = lv_appmanger.getItemAtPosition(position);
				if (obj instanceof AppInfo) {
					AppInfo appInfo = (AppInfo) obj;

					clickedpackname = appInfo.getPackname();
					View contentview = View.inflate(getApplicationContext(),
							R.layout.popup_item, null);
					LinearLayout ll_share = (LinearLayout) contentview
							.findViewById(R.id.ll_share);
					ll_share.setOnClickListener(AppManagerActivity.this);
					LinearLayout ll_start = (LinearLayout) contentview
							.findViewById(R.id.ll_start);
					ll_start.setOnClickListener(AppManagerActivity.this);
					LinearLayout ll_uninstall = (LinearLayout) contentview
							.findViewById(R.id.ll_uninstall);
					// 把是否是用户信息 存在 卸载的view对象里面

					ll_uninstall.setTag(appInfo.isUserapp());

					ll_uninstall.setOnClickListener(AppManagerActivity.this);
					popupwindow = new PopupWindow(contentview, DensityUtil
							.dip2px(AppManagerActivity.this, 200), DensityUtil
							.dip2px(AppManagerActivity.this, 80));
					popupwindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));

					int[] location = new int[2];
					view.getLocationInWindow(location);

					popupwindow.showAtLocation(
							view,
							Gravity.TOP | Gravity.LEFT,
							location[0]
									+ DensityUtil.dip2px(
											AppManagerActivity.this, 60),
							location[1]);

					ScaleAnimation sa = new ScaleAnimation(0.2f, 1.0f, 0.2f,
							1.0f);
					sa.setDuration(400);
					contentview.startAnimation(sa);
				}

			}
		});

		lv_appmanger.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				dismissPopupWindow();

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
			}
		});

		fillData();

	}

	private void fillData() {
		new AsyncTask<Void, Void, List<AppInfo>>() {

			@Override
			protected void onPreExecute() {
				ll_loading.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<AppInfo> result) {
				ll_loading.setVisibility(View.INVISIBLE);

				userAppinfos = new ArrayList<AppInfo>();
				systemAppinfos = new ArrayList<AppInfo>();

				for (AppInfo appinfo : result) {
					if (appinfo.isUserapp()) {
						userAppinfos.add(appinfo);
					} else {
						systemAppinfos.add(appinfo);
					}
				}
				lv_appmanger.setAdapter(new AppManagerAdapter());
				super.onPostExecute(result);
			}

			@Override
			protected List<AppInfo> doInBackground(Void... params) {
				appinfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);

				return appinfos;
			}

		}.execute();
	}

	public void initTextView() {
		StatFs mDataFileStats = new StatFs("/data");
		StatFs mSDCardFileStats = new StatFs(Environment
				.getExternalStorageDirectory().toString());

		long mDataFileSize = mDataFileStats.getAvailableBlocks()
				* mDataFileStats.getBlockSize();
		long mSDCardFileSize = mSDCardFileStats.getAvailableBlocks()
				* mSDCardFileStats.getBlockSize();
		tv_availmem.setText("内存可用: "
				+ Formatter.formatFileSize(this, mDataFileSize));
		tv_availsd.setText("sd卡可用: "
				+ Formatter.formatFileSize(this, mSDCardFileSize));
	}

	private void dismissPopupWindow() {
		if (popupwindow != null && popupwindow.isShowing()) {
			popupwindow.dismiss();
		}
	}

	private class AppManagerAdapter extends BaseAdapter {

		public int getCount() {
			return userAppinfos.size() + 1 + systemAppinfos.size() + 1;// 用来显示两个标签
		}

		public Object getItem(int position) {
			if (position == 0) {
				return position;
			} else if (position <= userAppinfos.size()) {
				int newpostion = position - 1;
				return userAppinfos.get(newpostion);
			} else if (position == userAppinfos.size() + 1) {
				return position;
			} else {
				int newpostion = position - userAppinfos.size() - 2;
				return systemAppinfos.get(newpostion);

			}
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.GRAY);
				tv.setTextSize(18);
				tv.setText("用户程序 ( " + userAppinfos.size() + " )");
				return tv;
			} else if (position <= userAppinfos.size()) {// 显示用户程序
				int newpostion = position - 1;
				View view;
				ViewHolder holder;
				if (convertView == null || convertView instanceof TextView) {
					view = View.inflate(getApplicationContext(),
							R.layout.app_manager_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) view
							.findViewById(R.id.iv_appmanger_icon);
					holder.tv_name = (TextView) view
							.findViewById(R.id.tv_appmanger_name);
					holder.tv_version = (TextView) view
							.findViewById(R.id.tv_appmanger_version);
					holder.ll = (LinearLayout) view
							.findViewById(R.id.ll_appmanager);
					view.setTag(holder);
				} else {
					view = convertView;
					holder = (ViewHolder) view.getTag();
				}
				AppInfo appinfo = userAppinfos.get(newpostion);
				holder.iv_icon.setImageDrawable(appinfo.getAppicon());
				holder.tv_name.setText(appinfo.getAppname());
				holder.tv_version.setText("版本: " + appinfo.getVersion());

				holder.ll.removeAllViews();// 避免使用历史缓存view对象的时候 里面有view对象

				if (appinfo.isMoney()) {
					ImageView iv = new ImageView(getApplicationContext());
					iv.setImageResource(R.drawable.money);
					holder.ll.addView(iv);
				}
				if (appinfo.isNetwork()) {
					ImageView iv = new ImageView(getApplicationContext());
					iv.setImageResource(R.drawable.wifi);
					holder.ll.addView(iv);
				}
				if (appinfo.isPri()) {
					ImageView iv = new ImageView(getApplicationContext());
					iv.setImageResource(R.drawable.pri);
					holder.ll.addView(iv);
				}

				return view;

			} else if (position == (userAppinfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.GRAY);
				tv.setTextSize(18);
				tv.setText("系统程序 ( " + systemAppinfos.size() + " )");
				return tv;

			} else {

				int newpostion = position - userAppinfos.size() - 2;

				View view;
				ViewHolder holder;
				if (convertView == null || convertView instanceof TextView) {
					view = View.inflate(getApplicationContext(),
							R.layout.app_manager_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) view
							.findViewById(R.id.iv_appmanger_icon);
					holder.tv_name = (TextView) view
							.findViewById(R.id.tv_appmanger_name);
					holder.tv_version = (TextView) view
							.findViewById(R.id.tv_appmanger_version);
					holder.ll = (LinearLayout) view
							.findViewById(R.id.ll_appmanager);
					view.setTag(holder);
				} else {
					view = convertView;
					holder = (ViewHolder) view.getTag();
				}
				AppInfo appinfo = systemAppinfos.get(newpostion);
				holder.iv_icon.setImageDrawable(appinfo.getAppicon());
				holder.tv_name.setText(appinfo.getAppname());
				holder.tv_version.setText("版本: " + appinfo.getVersion());

				holder.ll.removeAllViews();// 避免使用历史缓存view对象的时候 里面有view对象

				if (appinfo.isMoney()) {
					ImageView iv = new ImageView(getApplicationContext());
					iv.setImageResource(R.drawable.money);
					holder.ll.addView(iv);
				}
				if (appinfo.isNetwork()) {
					ImageView iv = new ImageView(getApplicationContext());
					iv.setImageResource(R.drawable.wifi);
					holder.ll.addView(iv);
				}
				if (appinfo.isPri()) {
					ImageView iv = new ImageView(getApplicationContext());
					iv.setImageResource(R.drawable.pri);
					holder.ll.addView(iv);
				}

				return view;

			}

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissPopupWindow();

	}

	private static class ViewHolder {
		private ImageView iv_icon;
		private TextView tv_name;
		private TextView tv_version;
		private LinearLayout ll;
	}

	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_share:
			Log.i(TAG, "分享" + clickedpackname);
			shareApk();
			break;

		case R.id.ll_start:
			Log.i(TAG, "启动" + clickedpackname);
			startApk();

			break;
		case R.id.ll_uninstall:
			boolean isuserapp = (Boolean) v.getTag();
			if (!isuserapp) {
				Toast.makeText(this, "系统应用需要root权限才能被卸载", 0).show();
				return;
			}
			Log.i(TAG, "卸载" + clickedpackname);
			uninstallApk();
			break;
		}

	}

	/**
	 * 分享一个应用程序
	 */
	private void shareApk() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra(Intent.EXTRA_TEXT, "推荐你使用一款软件包名为:"+clickedpackname);
		intent.putExtra(Intent.EXTRA_SUBJECT, "很好用的软件");
		intent.setType("text/plain");
		startActivity(intent);
		
		
	}

	/**
	 * 开启一个应用程序
	 */
	private void startApk() {

		
		
		
		// 得到当前包名里面可以被启动的activity
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					clickedpackname, PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityinfos = info.activities;
			if (activityinfos != null & activityinfos.length > 0) {
				ActivityInfo activity = activityinfos[0];// 一般来说
															// activity数组里面的第一个activity就是具有启动属性的activity
				Intent intent = new Intent();
				intent.setClassName(clickedpackname, activity.name);
				startActivity(intent);
			}

		} catch (NameNotFoundException e) {
			Toast.makeText(this, "应用程序不能被启动", 0).show();
			e.printStackTrace();
		}

	}

	/**
	 * 卸载一个应用程序
	 */
	private void uninstallApk() {
		// 判断是否是系统的应用
		// 如果是系统应用 我们就提示用户 不能被卸载.
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DELETE);

		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + clickedpackname));
		// startActivity(intent);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 重新更新列表
		initTextView();
		fillData();
	}

}
