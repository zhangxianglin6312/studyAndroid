package com.zxl.niubixilitysafe.ui;

import com.zxl.niubixilitysafe.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_content;
	private CheckBox cb;
	
	
	public SettingView(Context context) {
		super(context);
		setupView(context);
		
		
	}
	
	//更改tv_content的内容
	public void setContent(String content){
		tv_content.setText(content);
	}
	
	//更改cb的状态
	public void setChecked(boolean checked){
		cb.setChecked(checked);
	}
	
	//判断当前view对象是否被选择
	public boolean isChecked(){
		return cb.isChecked();
	}

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView(context);
		//初始化一下里面显示的内容
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.setting_view);
		String title = a.getString(R.styleable.setting_view_title);
		String content = a.getString(R.styleable.setting_view_content);
		tv_title.setText(title);
		tv_content.setText(content);
		a.recycle(); //防止多个自定义控件 使用相同的参数.
	}

	/**
	 * 初始化view对象
	 * @param context
	 */
	private void setupView(Context context){
		View view = View.inflate(context, R.layout.setting_view, this);
		tv_title = (TextView) view.findViewById(R.id.tv_myview_title);
		tv_content = (TextView) view.findViewById(R.id.tv_myview_content);
		cb = (CheckBox) findViewById(R.id.cb_myview_status);
		
	}
	
	
	
	
}


