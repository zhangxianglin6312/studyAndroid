package com.zxl.niubixilitysafe.receiver;

import com.zxl.niubixilitysafe.LostFindActivity;
import com.zxl.niubixilitysafe.R;
import com.zxl.niubixilitysafe.dao.DB.NumberDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.sax.StartElementListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

/**
 * 打电话出去的广播接收者
 * @author Administrator
 *
 */
public class OutCallReceiver extends BroadcastReceiver {
	private int[] backgrounds = {R.drawable.show_address_bg,R.drawable.show_address_bg_blue,R.drawable.show_address_bg_grey}; 
	private WindowManager.LayoutParams params;
	private View view;
	private WindowManager wm;
	@Override
	public void onReceive(Context context, Intent intent) {
		String number = getResultData();
		if("1001001".equals(number)){
			Intent lostFindIntent = new Intent(context, LostFindActivity.class);
			lostFindIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//
			context.startActivity(lostFindIntent);
			setResultData(null);//终止外拨的广播事件.
		}
		
		//铃响状态
		String address = NumberDao.getAddress(context, number);
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		view = View.inflate(context, R.layout.show_address, null);
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
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
		
		new Thread(){
			public void run() {
				try {
					Thread.sleep(3000);
					if(view != null){
						wm.removeView(view);
						view = null;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}

}
