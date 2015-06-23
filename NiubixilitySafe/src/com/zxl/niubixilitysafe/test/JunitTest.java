package com.zxl.niubixilitysafe.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zxl.niubixilitysafe.vo.ContactInfoVO;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.test.AndroidTestCase;
import android.util.Log;

public class JunitTest extends AndroidTestCase {

	public void testGetContacts(){
		List<ContactInfoVO> conInfoVOs = new ArrayList<ContactInfoVO>();
		// 获取用来操作数据的类的对象，对联系人的基本操作都是使用这个对象  
		ContentResolver cr = getContext().getContentResolver();
		Cursor contactCursor = cr.query(Phone.CONTENT_URI,new String[]{Phone.DISPLAY_NAME,Phone.NUMBER}, null, null, Phone.SORT_KEY_PRIMARY);
		if(contactCursor.getCount()>0){
			while(contactCursor.moveToNext()){
				ContactInfoVO contactInfo = new ContactInfoVO();
				String name = contactCursor.getString(0);
				String phone = contactCursor.getString(1);
				contactInfo.setName(name);
				contactInfo.setPhone(phone);
				if(name.equals("陈滔")){
					conInfoVOs.add(contactInfo);
				}
			}
			contactCursor.close();
		}
		List<ContactInfoVO> sss = conInfoVOs;
	}
	
	public void testSendSms(){
		Log.i("BootReceiver", "手机重启了");
		SharedPreferences sp = getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
		 boolean protecting = sp.getBoolean("protecting", false);
		 if(protecting){
			 //判断sim是否发生变化
			 TelephonyManager tm = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
			 String realsim = tm.getSimSerialNumber() == null?"49629649271":tm.getSimSerialNumber();
			 String savesim = sp.getString("sim", "");
			 if(!realsim.equals(savesim)){
				 String safenumber = sp.getString("safenumber", "");
				 SmsManager sms = SmsManager.getDefault();
				 sms.sendTextMessage(safenumber, null, "贱人", null, null);
			 }
		 }
	}
	
	public void testDB(){
		String number = "13418560387"; // 如果没有查询出来归属地 就显示原来的号码
		String path = getContext().getFilesDir() + "/address.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select city from address_tb inner join numinfo on address_tb.[_id] = numinfo.outkey where numinfo.mobileprefix = ?",
					new String[] { number.substring(0, 7) });
				if(cursor.moveToFirst()){
					String sss = cursor.getString(0);
				}
				cursor.close();
		}
	}
	
	public void deleteFile(){
		String path = getContext().getFilesDir() + "/commonnum.db";
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
	}

}
