package com.zxl.niubixilitysafe;

import com.zxl.niubixilitysafe.dao.DB.NumberDao;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberQueryActivity extends Activity {
	private EditText et_number_query;
	private Button bt_number_query;
	private TextView tv_number_query_address;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.number_query);
		et_number_query = (EditText) findViewById(R.id.et_number_query);
		bt_number_query = (Button) findViewById(R.id.bt_number_query);
		tv_number_query_address = (TextView) findViewById(R.id.tv_number_query_address);
		
		bt_number_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String number = et_number_query.getText().toString().trim();
				//查询数据库 .
				if(TextUtils.isEmpty(number)){
					Toast.makeText(getApplicationContext(), "电话号码不能为空", 0).show();
					Animation animation = AnimationUtils.loadAnimation(NumberQueryActivity.this, R.anim.shake);
					et_number_query.startAnimation(animation);
				}else{
					String address = NumberDao.getAddress(NumberQueryActivity.this, number);
					tv_number_query_address.setText(address);
				}
			}
		});
		
		et_number_query.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				int len = arg0.length();
				if(len>=7){
					String address = NumberDao.getAddress(NumberQueryActivity.this, arg0.toString());
					tv_number_query_address.setText(address);
				}
			}
		});
	}
}
