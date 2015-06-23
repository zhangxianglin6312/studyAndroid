package com.zxl.niubixilitysafe.service;

import com.zxl.niubixilitysafe.R;
import com.zxl.niubixilitysafe.dao.DB.NumberDao;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.StrictMode.VmPolicy;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class ShowAddressService extends Service {
	private TelephonyManager tm;
	private PhoneStateListener listener;
	
	private WindowManager wm;
	
	private SharedPreferences sp;
	@Override
	public void onCreate() {
		// 注册电话状态的监听器
		tm  = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		super.onCreate();
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{
		private View view;
		private int[] backgrounds = {R.drawable.show_address_bg,R.drawable.show_address_bg_blue,R.drawable.show_address_bg_grey}; 
		private WindowManager.LayoutParams params;
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				//铃响状态
				String address = NumberDao.getAddress(getApplicationContext(), incomingNumber);
				view = View.inflate(getApplicationContext(), R.layout.show_address, null);
				int which = sp.getInt("which", 0);
				view.setBackgroundResource(backgrounds[which]);
				view.setOnTouchListener(new OnTouchListener() {
					int startX ;
					int startY ;
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							startX = (int) event.getRawX();
							startY = (int) event.getRawY();
							break;
						case MotionEvent.ACTION_MOVE:
							int x = (int) event.getRawX();
							int y = (int) event.getRawY();
							int dx = x - startX;
							int dy = y - startY;
							params.x += dx;
							params.y += dy;
							wm.updateViewLayout(view, params);
							startX = (int) event.getRawX();
							startY = (int) event.getRawY();
							break;
						case MotionEvent.ACTION_UP:
							
							break;
						default:
							break;
						}
						return false;
					}
				});
				
				TextView tv_show_address = (TextView) view.findViewById(R.id.tv_show_address);
				tv_show_address.setText(address);
				params = new LayoutParams();
				params.gravity = Gravity.TOP|Gravity.LEFT;// 指定当前的控件是以屏幕的左上角对其的.
				params.x = sp.getInt("lastX", 0);
				params.y = sp.getInt("lastY", 0);
				params.width = WindowManager.LayoutParams.WRAP_CONTENT;
				params.height = WindowManager.LayoutParams.WRAP_CONTENT;
				params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
				params.format = PixelFormat.TRANSLUCENT;//这句话使窗口支持透明度,然后就可以用setAlpha,drawColor等函数来设置窗口透明程度
				params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
				wm.addView(view, params);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//接听
				if(view != null){
					wm.removeView(view);
					view = null;
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				//挂断
				if(view != null){
					wm.removeView(view);
					view = null;
				}
				break;

			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
		
	}

	@Override
	public void onDestroy() {
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);// 服务停止的时候
		listener = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
