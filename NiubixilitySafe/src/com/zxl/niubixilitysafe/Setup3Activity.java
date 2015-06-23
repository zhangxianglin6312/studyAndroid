package com.zxl.niubixilitysafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends SetupBaseActivity {
	private EditText et_setup3_number;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup3);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		et_setup3_number = (EditText) findViewById(R.id.et_setup3_number);
		String safenumber = sp.getString("safenumber", "");
		et_setup3_number.setText(safenumber);
	}
	
	public void next(View view){
		showNext();
	}
	
	public void pre(View view){
		showPre();
	}
	
	public void selectContact(View view){
		// 打开一个新的界面,列出所有的联系人
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			String safenumber = data.getStringExtra("safenumber");
			et_setup3_number.setText(safenumber);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void showNext() {
		String safenumber = et_setup3_number.getText().toString().trim();
		if(TextUtils.isEmpty(safenumber)){
			Toast.makeText(this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}else{
			Editor editor = sp.edit();
			editor.putString("safenumber", safenumber);
			editor.commit();
			loadActivity(Setup4Activity.class);
			finish();
			overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
		}
	}

	@Override
	protected void showPre() {
		// TODO Auto-generated method stub
		loadActivity(Setup2Activity.class);
		finish();
		overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
	}
}
