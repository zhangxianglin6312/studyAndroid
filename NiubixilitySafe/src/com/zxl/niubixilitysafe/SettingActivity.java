package com.zxl.niubixilitysafe;

import com.zxl.niubixilitysafe.service.CallFireWallService;
import com.zxl.niubixilitysafe.service.ShowAddressService;
import com.zxl.niubixilitysafe.ui.SettingView;
import com.zxl.niubixilitysafe.util.ServiceStatusUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends Activity implements OnClickListener{
	private CheckBox cb_setting_autoupdate;
	private TextView tv_setting_autoupdate_stauts;
	
	private CheckBox cb_setting_showaddress;
	private RelativeLayout rl_setting_showaddress;
	private TextView tv_setting_showaddress_stauts;
	
	private Intent shawAddressServerIntent;
	
	private SettingView sv_setting_blacknumber;
	private Intent blacknumberintent ;
	
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_center);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tv_setting_autoupdate_stauts = (TextView) findViewById(R.id.tv_setting_autoupdate_stauts);
		cb_setting_autoupdate = (CheckBox) findViewById(R.id.cb_setting_autoupdate);
		
		cb_setting_showaddress = (CheckBox) findViewById(R.id.cb_setting_showaddress);
		rl_setting_showaddress =  (RelativeLayout) findViewById(R.id.rl_setting_showaddress);
		tv_setting_showaddress_stauts =  (TextView) findViewById(R.id.tv_setting_showaddress_stauts);
		rl_setting_showaddress.setOnClickListener(this);
		shawAddressServerIntent = new Intent(this, ShowAddressService.class);
		blacknumberintent = new Intent(this,CallFireWallService.class);
		//初始化空间状态
		boolean autoUpdate = sp.getBoolean("autoUpdate", true);
		if(autoUpdate){
			tv_setting_autoupdate_stauts.setText("自动更新已开启");
			cb_setting_autoupdate.setChecked(autoUpdate);
		}else{
			tv_setting_autoupdate_stauts.setText("自动更新已关闭");
			cb_setting_autoupdate.setChecked(autoUpdate);
		}
		cb_setting_autoupdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Editor edit = sp.edit();
				edit.putBoolean("autoUpdate", isChecked);
				edit.commit();
				if(isChecked){
					tv_setting_autoupdate_stauts.setText("自动更新已开启");
				}else{
					tv_setting_autoupdate_stauts.setText("自动更新已关闭");
				}
			}
		});
		
		sv_setting_blacknumber= (SettingView) findViewById(R.id.sv_setting_blacknumber);
		
		sv_setting_blacknumber.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_setting_showaddress:
			if(cb_setting_showaddress.isChecked()){
				cb_setting_showaddress.setChecked(false);
				tv_setting_showaddress_stauts.setText("归属地显示已关闭");
				stopService(shawAddressServerIntent);
			}else{
				cb_setting_showaddress.setChecked(true);
				tv_setting_showaddress_stauts.setText("归属地显示已开启");
				startService(shawAddressServerIntent);
			}
			break;
		case R.id.sv_setting_blacknumber:
			if(sv_setting_blacknumber.isChecked()){
				sv_setting_blacknumber.setContent("来电黑名单服务已经关闭");
				sv_setting_blacknumber.setChecked(false);
				
				stopService(blacknumberintent);
				
			}else{
				sv_setting_blacknumber.setContent("来电黑名单服务已经开启");
				sv_setting_blacknumber.setChecked(true);
				startService(blacknumberintent);
			}
			
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//当界面重新过去到焦点的时候 执行的方法
		// 初始化服务的状态
		if (ServiceStatusUtil.isServiceRunning(this,
				"com.zxl.niubixilitysafe.service.ShowAddressService")) {
			tv_setting_showaddress_stauts.setText("归属地服务已经开启");
			cb_setting_showaddress.setChecked(true);
		} else {
			tv_setting_showaddress_stauts.setText("归属地服务没有开启");
			cb_setting_showaddress.setChecked(false);
		}

		if (ServiceStatusUtil.isServiceRunning(this,
				"com.zxl.niubixilitysafe.service.CallFireWallService")) {

			sv_setting_blacknumber.setChecked(true);
			sv_setting_blacknumber.setContent("来电黑名单服务已经开启");
		} else {
			sv_setting_blacknumber.setChecked(false);
			sv_setting_blacknumber.setContent("来电黑名单服务没有开启");
		}
	}
	
	
}
