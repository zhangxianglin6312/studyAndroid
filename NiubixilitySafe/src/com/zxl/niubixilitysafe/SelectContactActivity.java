package com.zxl.niubixilitysafe;

import java.util.List;

import com.zxl.niubixilitysafe.engine.ContactInfoProvider;
import com.zxl.niubixilitysafe.vo.ContactInfoVO;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SelectContactActivity extends Activity {
	private ListView lv_select_contact;
	private LinearLayout ll_select_loading;
	private List<ContactInfoVO> contacts;
	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(android.os.Message msg) {
			ll_select_loading.setVisibility(View.INVISIBLE);
			lv_select_contact.setAdapter( new contactAdapter());
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_contact);
		lv_select_contact = (ListView) findViewById(R.id.lv_select_contact);
		ll_select_loading = (LinearLayout) findViewById(R.id.ll_select_loading);
		ll_select_loading.setVisibility(View.VISIBLE);
		
		lv_select_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ContactInfoVO contact = (ContactInfoVO) lv_select_contact.getItemAtPosition(arg2);
				Intent data = new Intent();
				data.putExtra("safenumber", contact.getPhone());
				setResult(0, data);
				finish();
			}
		});
		
		new Thread(){
			public void run() {
				contacts = ContactInfoProvider.getContactInfos(getApplicationContext());
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
	
	private class contactAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contacts.size();
		}

		@Override
		public ContactInfoVO getItem(int arg0) {
			// TODO Auto-generated method stub
			return contacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = View.inflate(getApplicationContext(), R.layout.contact_item, null);
			TextView tv_contact_name = (TextView) view.findViewById(R.id.tv_contact_name);
			TextView tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);
			
			ContactInfoVO contact =  getItem(arg0);
			tv_contact_name.setText(contact.getName());
			tv_contact_phone.setText(contact.getPhone());
			
			return view;
		}
		
	}
	
}
