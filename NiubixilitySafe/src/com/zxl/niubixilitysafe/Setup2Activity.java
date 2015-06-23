package com.zxl.niubixilitysafe;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Setup2Activity extends SetupBaseActivity {
	private ImageView iv_setup2_bind_status;
	private RelativeLayout rl_setup2_bind;
	private SharedPreferences sp;
	private TelephonyManager tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup2);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		rl_setup2_bind = (RelativeLayout) findViewById(R.id.rl_setup2_bind);
		iv_setup2_bind_status = (ImageView) findViewById(R.id.iv_setup2_bind_status);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		if(isBind()){
			iv_setup2_bind_status.setImageResource(R.drawable.btn_check_buttonless_on);
		}else{
			iv_setup2_bind_status.setImageResource(R.drawable.btn_close_normal);
		}
		
		rl_setup2_bind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Editor edit = sp.edit();
				if(isBind()){
					iv_setup2_bind_status.setImageResource(R.drawable.btn_close_normal);
					edit.putString("sim", "");
				}else{
					iv_setup2_bind_status.setImageResource(R.drawable.btn_check_buttonless_on);
					edit.putString("sim", tm.getSimSerialNumber()==null?"12234":tm.getSimSerialNumber());
				}
				edit.commit();
			}
		});
		
	}
	
	private boolean isBind() {
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			return false;
		}else{
			return true;
		}
	}

	public void next(View view){
		showNext();
	}
	
	public void pre(View view){
		showPre();
	}

	@Override
	protected void showNext() {
		// TODO Auto-generated method stub
		loadActivity(Setup3Activity.class);
		finish();
		overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}

	@Override
	protected void showPre() {
		// TODO Auto-generated method stub
		loadActivity(Setup1Activity.class);
		finish();
		overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}
}
