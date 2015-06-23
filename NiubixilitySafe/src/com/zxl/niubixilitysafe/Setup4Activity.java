package com.zxl.niubixilitysafe;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends SetupBaseActivity {
	private CheckBox cb_setup4_status;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup4);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_setup4_status = (CheckBox) findViewById(R.id.cb_setup4_status);
		boolean protecting = sp.getBoolean("protecting", false);
		cb_setup4_status.setChecked(protecting);
		if(protecting){
			cb_setup4_status.setText("防盗保护已经开启");
		}else{
			cb_setup4_status.setText("防盗保护没有开启");
		}
		
		cb_setup4_status.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				Editor editor = sp.edit();
				editor.putBoolean("protecting", arg1);
				editor.commit();
				
				if(arg1){
					cb_setup4_status.setText("防盗保护已经开启");
				}else{
					cb_setup4_status.setText("防盗保护没有开启");
				}
				
			}
		});
	}
	
	public void next(View view){
		showNext();
	}
	
	public void pre(View view){
		showPre();
	}
	
	@Override
	protected void showNext() {
		if(cb_setup4_status.isChecked()){
			Editor editor = sp.edit();
			editor.putBoolean("isSetup", true);
			editor.commit();
			loadActivity(LostFindActivity.class);
			finish();
			overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
		}else{
			//再次提示用户开启防盗保护
			showAlertDialog();
		}
	}

	@Override
	protected void showPre() {
		loadActivity(Setup3Activity.class);
		finish();
		overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
	}
	
	private void showAlertDialog(){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("友情提示");
		builder.setMessage("手机防盗极大的保护你的手机安全,赶紧开启吧!");
		builder.setPositiveButton("立刻开启", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				cb_setup4_status.setChecked(true);
			}
		});
		builder.setNegativeButton("以后开启", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Editor editor = sp.edit();
				editor.putBoolean("isSetup", true);
				editor.commit();
				Intent intent = new Intent(Setup4Activity.this, LostFindActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		builder.create().show();
	}
}
