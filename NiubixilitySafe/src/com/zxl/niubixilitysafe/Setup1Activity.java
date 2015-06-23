package com.zxl.niubixilitysafe;

import android.os.Bundle;
import android.view.View;

public class Setup1Activity extends SetupBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup1);
	}
	
	public void next(View view){
		showNext();
	}

	@Override
	protected void showNext() {
		// TODO Auto-generated method stub
		loadActivity(Setup2Activity.class);
		finish();
		overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}

	@Override
	protected void showPre() {
		// TODO Auto-generated method stub
		
	}
	
}
