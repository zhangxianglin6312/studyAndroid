package com.zxl.niubixilitysafe.adapter;

import com.zxl.niubixilitysafe.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainTabAdapter extends BaseAdapter {

	private static String[] names = { "手机防盗", "通讯卫士", "程序管理", "进程管理", "流量统计",
			"手机杀毒", "系统加速", "高级工具", "设置中心" };

	private static int[] icons = { R.drawable.sjfd, R.drawable.txws,
			R.drawable.rjgl, R.drawable.cgl, R.drawable.llgl, R.drawable.sjsd,
			R.drawable.sjjs, R.drawable.fuc_04, R.drawable.szzx };

	private Context context;
	
	

	public MainTabAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View view = View.inflate(context, R.layout.maintab_item, null);
		ImageView iv_main_icon = (ImageView) view.findViewById(R.id.iv_main_icon);
		TextView tv_main_name = (TextView) view.findViewById(R.id.tv_main_name);
		tv_main_name.setText(names[arg0]);
		iv_main_icon.setImageResource(icons[arg0]);
		return view;
	}

}
