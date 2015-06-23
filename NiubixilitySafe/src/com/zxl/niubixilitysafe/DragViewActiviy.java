package com.zxl.niubixilitysafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DragViewActiviy extends Activity {
	private TextView tv_dragview;
	private ImageView iv_dragview_location;
	
	private Display display;
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drag_view);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		display = getWindowManager().getDefaultDisplay();
		tv_dragview = (TextView) findViewById(R.id.tv_dragview);
		iv_dragview_location = (ImageView) findViewById(R.id.iv_dragview_location);
		
		int lastX = sp.getInt("lastX", 0);
		int lastY = sp.getInt("lastY", 0);
		int tv_lastX = sp.getInt("tv_lastX", 0);
		int tv_lastY = sp.getInt("tv_lastY", 0);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) iv_dragview_location.getLayoutParams();
		params.leftMargin = lastX;
		params.topMargin = lastY;
		iv_dragview_location.setLayoutParams(params);
		
		RelativeLayout.LayoutParams tv_params = (android.widget.RelativeLayout.LayoutParams) tv_dragview.getLayoutParams();
		tv_params.leftMargin = tv_lastX;
		tv_params.topMargin = tv_lastY;
		tv_dragview.setLayoutParams(tv_params);
		
		iv_dragview_location.setOnTouchListener(new OnTouchListener() {
			int startX;// 记录第一次手指在窗体中的位置.
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					 int moveX = (int) event.getRawX();
					 int moveY = (int) event.getRawY();
					 int dx = moveX-startX;
					 int dy = moveY-startY;
					 
					// 立刻的更新 imageview在窗体中的位置.
					int l = iv_dragview_location.getLeft();
					int t = iv_dragview_location.getTop(); // 得到imageview在窗体中上面的坐标
					int r = iv_dragview_location.getRight();
					int b = iv_dragview_location.getBottom();
					
					
					int new_l = l+dx;
					int new_t = t+dy; 
					int new_r = r+dx;
					int new_b = b+dy;
					if(new_l<0 || new_t<0 ||new_r>display.getWidth()|| new_b>(display.getHeight()-60)){
						break;
					}
					
					 iv_dragview_location.layout(new_l, new_t,new_r, new_b);
					 int tv_height = tv_dragview.getBottom()
								- tv_dragview.getTop();
					 if(moveY>(display.getHeight()/2)){
						 tv_dragview.layout(tv_dragview.getLeft(), 0, tv_dragview.getRight(),tv_height);
					 }else{
						 tv_dragview.layout(tv_dragview.getLeft(), (display.getHeight()-60-tv_height), tv_dragview.getRight(),(display.getHeight()-60));
					 }
					// 重新记录新的手指的初始位置.
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					 break;
				case MotionEvent.ACTION_UP:
					int lastX = iv_dragview_location.getLeft();
					int lastY = iv_dragview_location.getTop();
					int tv_lastX = tv_dragview.getLeft();
					int tv_lastY = tv_dragview.getTop();
					Editor editor = sp.edit();
					editor.putInt("lastX", lastX);
					editor.putInt("lastY", lastY);
					editor.putInt("tv_lastX", tv_lastX);
					editor.putInt("tv_lastY", tv_lastY);
					editor.commit();
					break;

				default:
					break;
				}
				return false;
			}

		});
		iv_dragview_location.setOnClickListener(new OnClickListener() {
			private long firstTime; 
			
			@Override
			public void onClick(View v) {
				long secondeTime = System.currentTimeMillis();
				if((secondeTime - firstTime)<500){
					int screenWidth = display.getWidth();
					int screenHeight = display.getHeight();
					int tvWidth = iv_dragview_location.getRight() - iv_dragview_location.getLeft();
					int tvHeight = iv_dragview_location.getBottom() - iv_dragview_location.getTop();
					
					int l = (screenWidth-tvWidth)/2;
					int t = (screenHeight-tvHeight)/2;
					int r = (screenWidth+tvWidth)/2;
					int b = (screenHeight+tvHeight)/2;
					
					iv_dragview_location.layout(l, t, r, b);
					int lastX = iv_dragview_location.getLeft();
					int lastY = iv_dragview_location.getTop();
					int tv_lastX = tv_dragview.getLeft();
					int tv_lastY = tv_dragview.getTop();
					Editor editor = sp.edit();
					editor.putInt("lastX", lastX);
					editor.putInt("lastY", lastY);
					editor.putInt("tv_lastX", tv_lastX);
					editor.putInt("tv_lastY", tv_lastY);
					editor.commit();
				}else{
					firstTime = secondeTime;
				}
			}
		});
	}
}
