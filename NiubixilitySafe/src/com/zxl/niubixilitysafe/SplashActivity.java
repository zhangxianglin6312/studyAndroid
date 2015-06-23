package com.zxl.niubixilitysafe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.zxl.niubixilitysafe.engine.UpdateInfoParser;
import com.zxl.niubixilitysafe.service.CallFireWallService;
import com.zxl.niubixilitysafe.util.DownLoadUtil;
import com.zxl.niubixilitysafe.vo.UpdateInfoVO;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	protected static final String TAG = "SplashActivity";
	public static final int URL_ERROR = 10;
	public static final int NETWORK_ERROR = 11;
	public static final int SERVER_ERROR = 12;
	public static final int XML_ERROR = 13;
	public static final int DOWNLOAD_ERROR = 15;

	public static final int SHOW_UPDATE_DIALOG = 14;
	private TextView tv_splash_version;
	private RelativeLayout rl_splash;
	private UpdateInfoVO updateInfo;

	private ProgressDialog pd;// 进度条对话框

	private long starttime;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case URL_ERROR:
				Toast.makeText(getApplicationContext(), "服务器路径错误", 0).show();
				loadMainUI();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
				loadMainUI();
				break;
			case SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "服务器内部错误", 0).show();
				loadMainUI();
				break;
			case XML_ERROR:
				Toast.makeText(getApplicationContext(), "服务器更新信息配置错误", 0)
						.show();
				loadMainUI();
				break;
			case SHOW_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case DOWNLOAD_ERROR:
				Toast.makeText(getApplicationContext(), "下载失败", 0).show();
				loadMainUI();
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);

		AlphaAnimation aa = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.splash_alpha);
		rl_splash.startAnimation(aa);

		tv_splash_version.setText("版本:" + getVersion());

		// 1. 连接服务器 检查是否有最新的版本
		new Thread(new CheckVersionTask()).start();

	}

	/**
	 * 进入应用程序的主ui
	 */
	private void loadMainUI() {
		Intent intent = new Intent(SplashActivity.this, MainTabActivity.class);
		startActivity(intent);
		finish();
	}

	private class CheckVersionTask implements Runnable {

		public void run() {
			// 在连接服务器 获取更新新之前 ,检查是否开启了自动更新
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean autoupdate = sp.getBoolean("autoUpdate", true);// 默认值是true
			// 代表默认情况下
			// 用户不进行配置
			// 是开启自动更新检查的
			if (!autoupdate) {
				try {
					Thread.sleep(2000);
					loadMainUI();
					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// 需要这这个地方进行下延时的处理.
			// 动画需要两秒钟才能播放完毕
			starttime = System.currentTimeMillis();
			try {
				String path = getResources().getString(R.string.serverurl);
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				if (conn.getResponseCode() == 200) {

					InputStream is = conn.getInputStream();
					updateInfo = UpdateInfoParser.getUpdateInfo(is);
					long endtime = System.currentTimeMillis();
					long exectime = endtime - starttime;
					if (exectime < 2000) {
						try {
							Thread.sleep(2000 - exectime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (updateInfo == null) {
						Message msg = Message.obtain();
						msg.what = XML_ERROR;
						handler.sendMessage(msg);
					} else {
						// 判断当前客户端的版本和服务器是否相同
						if (getVersion().equals(updateInfo.getVersion())) {
							// 进入主界面
							loadMainUI();
							Log.i(TAG, "进入主界面");
						} else {
							// TODO:弹出升级对话框
							Log.i(TAG, "弹出升级对话框");
							// showUpdateDialog();
							Message msg = Message.obtain();
							msg.what = SHOW_UPDATE_DIALOG;
							handler.sendMessage(msg);

						}

					}
				} else {
					Message msg = Message.obtain();
					msg.what = SERVER_ERROR;
					handler.sendMessage(msg);
				}

			} catch (NotFoundException e) {
				Message msg = Message.obtain();
				msg.what = URL_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			} catch (MalformedURLException e) {
				Message msg = Message.obtain();
				msg.what = URL_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			} catch (IOException e) {
				Message msg = Message.obtain();
				msg.what = NETWORK_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}

		}

	}

	/**
	 * 获取当前应用程序的版本号
	 * 
	 * @return
	 */
	private String getVersion() {
		// 1.获取当前应用程序的包的apk信息
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
			return packInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// 不可能发生
			return "";
		}
	}

	/**
	 * 显示升级提醒的对话框
	 */
	public void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("升级提醒");
		builder.setMessage(updateInfo.getDescription());
		builder.setPositiveButton("升级", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "下载" + updateInfo.getApkurl());
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Toast.makeText(getApplicationContext(), "sd卡不可用,下载失败", 0)
							.show();
					loadMainUI();
					return;
				}

				pd = new ProgressDialog(SplashActivity.this);
				pd.setTitle("更新");
				pd.setMessage("正在下载");
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				new Thread() {
					public void run() {
						File file = new File(Environment
								.getExternalStorageDirectory(), "new.apk");
						file = DownLoadUtil.download(pd,
								updateInfo.getApkurl(), file.getAbsolutePath());
						if (file != null) {
							// 下载成功
							installApk(file);

						} else {
							// 下载失败
							Message msg = Message.obtain();
							msg.what = DOWNLOAD_ERROR;
							handler.sendMessage(msg);
						}
						pd.dismiss();
					}

				}.start();

			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				loadMainUI();
			}
		});

		builder.create().show();// 对话框的显示
	}

	/**
	 * 安装一个应用程序
	 * 
	 * @param file
	 */
	private void installApk(File file) {
		Intent intent = new Intent();
		/*
		 * <action android:name="android.intent.action.VIEW" /> <category
		 * android:name="android.intent.category.DEFAULT" /> <data
		 * android:scheme="content" /> <data android:scheme="file" /> <data
		 * android:mimeType="application/vnd.android.package-archive" />
		 */
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
		finish();
	}
}