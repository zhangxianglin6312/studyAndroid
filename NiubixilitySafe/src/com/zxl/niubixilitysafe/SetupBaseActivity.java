package com.zxl.niubixilitysafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public abstract class SetupBaseActivity extends Activity {
	GestureDetector gestureDetector;
	protected String tag = "SetupBaseActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gestureDetector = new GestureDetector(this, new SimpleOnGestureListener(){
			// 手指在屏幕上滑动的时候 调用的方法.
			// e1 手指第一次接触到屏幕的时候 对应的事件
			// e2 手指离开屏幕的时候 对应的事件
			// velocityx 水平方向的移动速度
			// velocityx 竖直方向的移动速度
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(Math.abs(e2.getY()-e1.getY())>100){
					Log.i(tag , "垂直方向移动过大");
					return true;
				}else if(Math.abs(velocityX)<100){
					Log.i(tag , "速度太慢了");
					return true;
				}else if(e2.getX()-e1.getX()>100){
					//上一页
					showPre();
				}else if(e1.getX()-e2.getX()>100){
					//下一页
					showNext();
				}else{
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
			
		});
	}
	
	/*
	 * 上一步
	 */
	protected abstract void showNext();
	/*
	 * 下一步
	 */
	protected abstract void showPre();
	
	protected void loadActivity(Class<?> cls){
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
