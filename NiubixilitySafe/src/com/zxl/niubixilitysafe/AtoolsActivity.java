package com.zxl.niubixilitysafe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.zxl.niubixilitysafe.engine.SmsUtil;
import com.zxl.niubixilitysafe.util.CopyAssetUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AtoolsActivity extends Activity implements OnClickListener {
	private static final int COPY_DB_FINISH = 30;
	protected static final int COPY_COMMON_DB_FINISH = 31;

	private TextView tv_atools_query_address;
	private TextView tv_atools_common_number;
	private TextView tv_atools_change_location;
	private TextView tv_atools_change_bg;
	private ProgressDialog pd;
	private SharedPreferences sp;
	
	private TextView tv_backup_sms;
	private TextView tv_restore_sms;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COPY_DB_FINISH:
				pd.dismiss();
				if ((msg.obj) != null) {
					Toast.makeText(getApplicationContext(), "拷贝数据库成功", 0)
							.show();
					// TODO:进入号码归属地查询
					loadNumberQueryUI();
				} else {
					Toast.makeText(getApplicationContext(), "拷贝数据库失败", 0)
							.show();
				}
				break;
			case COPY_COMMON_DB_FINISH:
				pd.dismiss();
				if ((msg.obj) != null) {
					Toast.makeText(getApplicationContext(), "拷贝数据库成功", 0)
							.show();
					//TODO:进入常用号码
					loadCommonNumberUI();
				} else {
					Toast.makeText(getApplicationContext(), "拷贝数据库失败", 0)
							.show();
				}
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atools);
		pd = new ProgressDialog(this);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		tv_atools_query_address = (TextView) findViewById(R.id.tv_atools_query_address);
		tv_atools_query_address.setOnClickListener(this);
		tv_atools_change_location = (TextView) findViewById(R.id.tv_atools_change_location);
		tv_atools_change_location.setOnClickListener(this);
		tv_atools_change_bg = (TextView) findViewById(R.id.tv_atools_change_bg);
		tv_atools_change_bg.setOnClickListener(this);
		tv_atools_common_number = (TextView) findViewById(R.id.tv_atools_common_number);
		tv_atools_common_number.setOnClickListener(this);
		
		tv_backup_sms = (TextView) findViewById(R.id.tv_atools_backup_sms);
		tv_restore_sms = (TextView) findViewById(R.id.tv_atools_restore_sms);

		tv_backup_sms.setOnClickListener(this);
		tv_restore_sms.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_atools_backup_sms:
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				pd = new ProgressDialog(this);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				new Thread() {
					private Handler handler;

					public void run() {

						File file = new File(
								Environment.getExternalStorageDirectory(),
								"backup.xml");
						try {
							SmsUtil.backupSms(getApplicationContext(), file, pd);
							Looper.prepare(); // 在子线程里面创建一个looper
							handler = new Handler() {
								@Override
								public void handleMessage(Message msg) {
									super.handleMessage(msg);
									pd.dismiss();
								}
							};

							Toast.makeText(getApplicationContext(), "备份成功", 0)
									.show();
							handler.sendEmptyMessage(0);
							Looper.loop();

						} catch (Exception e) {
							e.printStackTrace();
							Looper.prepare(); // 在子线程里面创建一个looper
							handler = new Handler() {
								@Override
								public void handleMessage(Message msg) {
									super.handleMessage(msg);
									pd.dismiss();
								}
							};
							Toast.makeText(getApplicationContext(), "备份 失败", 0)
									.show();
							handler.sendEmptyMessage(0);
							Looper.loop();
						}

					};
				}.start();
			} else {
				Toast.makeText(getApplicationContext(), "请检查sd卡状态", 0).show();
				return;
			}
			break;

		case R.id.tv_atools_restore_sms:
			final File restoreFile = new File(
					Environment.getExternalStorageDirectory(), "backup.xml");
			if (restoreFile.exists()) {
				pd = new ProgressDialog(this);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				new Thread() {
					public void run() {
						try {
							SmsUtil.restoreSms(getApplicationContext(),
									restoreFile, pd);
						} catch (Exception e) {
							e.printStackTrace();
						}
						pd.dismiss();
					};
				}.start();
			} else {
				Toast.makeText(this, "备份的文件不存在", 0).show();
			}

			break;
		case R.id.tv_atools_common_number:
			// 首先要去判断 是否已经有了常用号码的数据库 如果没有数据库的话 需要拷贝这个数据库到系统
			final File commfile = new File(getFilesDir(), "commonnum.db");
			if(commfile !=null && commfile.length()>0){
				// 数据库存在,直接进入常用号码查询
				loadCommonNumberUI();
			}else{
				try {
					final InputStream is = getAssets().open("commonnum.db");
					int max = is.available();
					if(max > 0){
						pd.setTitle("正在拷贝数据库...");
						pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						pd.setMax(max);
						pd.show();
						new Thread(){
							public void run() {
								File copyedfile = CopyAssetUtil.copyFile(is, commfile,pd);
								Message msg = handler.obtainMessage();
								msg.what = COPY_COMMON_DB_FINISH;
								msg.obj = copyedfile;
								handler.sendMessage(msg);
							};
						}.start();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			break;
		case R.id.tv_atools_query_address:
			// 首先要去判断 是否已经有了号码归属地的数据库 如果没有数据库的话 需要拷贝这个数据库到系统
			final File file = new File(getFilesDir(), "address.db");
			if(file !=null && file.length()>0){
				// 数据库存在,直接进入手机号码归属地查询
				loadNumberQueryUI();
			}else{
				try {
					final InputStream is = getAssets().open("naddress.db");
					int max = is.available();
					if(max > 0){
						pd.setTitle("正在拷贝数据库...");
						pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						pd.setMax(max);
						pd.show();
						new Thread(){
							public void run() {
								File copyedfile = CopyAssetUtil.copyFile(is, file,pd);
								Message msg = handler.obtainMessage();
								msg.what = COPY_DB_FINISH;
								msg.obj = copyedfile;
								handler.sendMessage(msg);
							};
						}.start();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case R.id.tv_atools_change_location:
			loadDragView();
			break;
		case R.id.tv_atools_change_bg:
			String[] items={"半透明","卫士蓝","神马灰"};
			AlertDialog.Builder builder = new Builder(this);
			builder.setIcon(R.drawable.notification);
			builder.setTitle("归属地显示框风格");
			int which = sp.getInt("which", 0);//初始化选择的条目
			builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Editor editor = sp.edit();
					editor.putInt("which", which);
					editor.commit();
					dialog.dismiss();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.create().show();
			break;
		default:
			break;
		}
	}

	/**
	 * 进入手机号码查询页面
	 */
	private void loadNumberQueryUI() {
		Intent intent = new Intent(this, NumberQueryActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 进入来电提示位置设置界面
	 */
	private void loadDragView(){
		Intent intent = new Intent(this, DragViewActiviy.class);
		startActivity(intent);
	}
	
	/**
	 * 进入常用号码查询页面
	 */
	private void loadCommonNumberUI() {
		Intent intent = new Intent(this,CommonNumberActivity.class);
		startActivity(intent);
	};
}
