package com.zxl.niubixilitysafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.zxl.niubixilitysafe.CallSmsSafeActivity;
import com.zxl.niubixilitysafe.R;
import com.zxl.niubixilitysafe.dao.DB.BlackNumberDao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallFireWallService extends Service {

	private TelephonyManager tm;
	private MyPhoneListener listener;
	private BlackNumberDao dao;

	@Override
	public void onCreate() {
		// 注册电话状态的监听器
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneListener();
		dao = new BlackNumberDao(this);
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class MyPhoneListener extends PhoneStateListener {
		private long startTime;
		private long endTime;
		private String phoneNumber;
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				endTime = System.currentTimeMillis();
				long dxtime = endTime - startTime;
				if(dxtime<3000){
					//弹出来提示  提示用户这是一个响一声的电话.
					//查询这个号码 是否在黑名单数据库里面
					if(dao.find(phoneNumber)){
						return;
					}
					
					showNotification(phoneNumber);
				}
				
				break;

			case TelephonyManager.CALL_STATE_RINGING:
				startTime = System.currentTimeMillis();//获取到零响的时候 的时间
				phoneNumber = incomingNumber;
				// 判断 incomingnumber是否是黑名单号码 拦截模式是0 或者 2
				String mode = dao.findMode(incomingNumber);
				if ("0".equals(mode) || "2".equals(mode)) {
					// 挂断电话.
					endCall();
					// 挂断电话后 呼叫记录的产生 不是立刻产生 异步的操作.

					// 内容观察者.
					getContentResolver().registerContentObserver(
							CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(),incomingNumber));
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}

	}
	
	private void showNotification(String incomingNumber) {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.notification, "发现一个响一声电话", System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		//notification.contentIntent 点击notification的时候 激活哪个界面
		Intent intent = new Intent(this,CallSmsSafeActivity.class);
		intent.putExtra("number", incomingNumber);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "神马护卫提醒您", "拦截到一个骚扰电话", contentIntent);
		nm.notify(0, notification);
	}

	private class MyObserver extends ContentObserver {
		private String incomingNumber;
		public MyObserver(Handler handler,String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
			//取消内容观察者.
			getContentResolver().unregisterContentObserver(this);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE); // 服务停止的时候
																// 取消电话状态的监听
		listener = null;
	}

	/**
	 * 删除呼叫记录
	 * 
	 * @param incomingNumber
	 */

	public void deleteCallLog(String incomingNumber) {
		// 得到呼叫记录的内容提供者
		Uri callloguri = Uri.parse("content://call_log/calls");
		Cursor cursor = getContentResolver().query(callloguri,
				new String[] { "_id" }, "number=?",
				new String[] { incomingNumber }, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			getContentResolver().delete(callloguri, "_id=?",
					new String[] { id });
		}
		cursor.close();
	}

	public void endCall() {
		try {
			Method method = Class.forName("android.os.ServiceManager")
					.getMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null,
					new Object[] { TELEPHONY_SERVICE });
			ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
			iTelephony.endCall();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
