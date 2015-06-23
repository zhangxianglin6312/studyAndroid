package com.zxl.niubixilitysafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "手机重启了");
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		 boolean protecting = sp.getBoolean("protecting", false);
		 if(protecting){
			 //判断sim是否发生变化
			 TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			 String realsim = tm.getSimSerialNumber() == null?"49629649271":tm.getSimSerialNumber();
			 String savesim = sp.getString("sim", "");
			 if(!realsim.equals(savesim)){
				 String safenumber = sp.getString("safenumber", "");
				 SmsManager sms = SmsManager.getDefault();
				 sms.sendTextMessage(safenumber, null, "sim changed !", null, null);
			 }
		 }
	}

}
