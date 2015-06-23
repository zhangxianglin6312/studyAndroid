package com.zxl.niubixilitysafe;

import com.zxl.niubixilitysafe.adapter.MainTabAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainTabActivity extends Activity {
	private GridView gv_main;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintab);
		gv_main = (GridView) findViewById(R.id.gv_main);
		gv_main.setAdapter(new MainTabAdapter(this));
		gv_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					Intent lostFindIntent = new Intent(getApplicationContext(), LostFindActivity.class);
					startActivity(lostFindIntent);
					break;
				case 1:
					Intent callSmsSafeIntent = new Intent(getApplicationContext(), CallSmsSafeActivity.class);
					startActivity(callSmsSafeIntent);
					break;
				case 2:
					Intent appmanagerIntent = new Intent(MainTabActivity.this,
							AppManagerActivity.class);
					startActivity(appmanagerIntent);
					break;
				case 3:
					Intent taskmanagerIntent = new Intent(MainTabActivity.this,
							TaskManagerActivity.class);
					startActivity(taskmanagerIntent);
					break;
				case 7:
					Intent atoolsIntent = new Intent(MainTabActivity.this, AtoolsActivity.class);
					startActivity(atoolsIntent);
					break;
				case 8:
					Intent settingIntent = new Intent(getApplicationContext(), SettingActivity.class);
					startActivity(settingIntent);
					break;

				default:
					break;
				}
			}
			
		});
	}

}
