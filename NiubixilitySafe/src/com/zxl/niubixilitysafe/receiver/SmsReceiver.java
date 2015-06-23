package com.zxl.niubixilitysafe.receiver;

import com.zxl.niubixilitysafe.R;
import com.zxl.niubixilitysafe.dao.DB.BlackNumberDao;
import com.zxl.niubixilitysafe.engine.GPSInfoProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		BlackNumberDao dao = new BlackNumberDao(context);
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for(Object pdu :pdus){
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
			String body = sms.getMessageBody();
			String sender = sms.getOriginatingAddress();			
			//判断 发短信的人 是否在黑名单列表里面 如果在就中断广播
			//1 短信 2 全部拦截.
			String result = dao.findMode(sender);
			if("1".equals(result)||"2".equals(result)){
				Log.i(TAG,"发现黑名单短信");
				abortBroadcast();
			}else if(body.contains("fapiao")){
				Log.i(TAG,"发票垃圾短信");
				abortBroadcast();
			}
			
			if ("#*location*#".equals(body)) {
				String locationInfo = GPSInfoProvider.getInstance(context).getLastLoation();
				SmsManager smg = SmsManager.getDefault();
				if(!TextUtils.isEmpty(locationInfo)){
					smg.sendTextMessage(sender, null, locationInfo, null, null);
				}
				abortBroadcast();
			} else if ("#*alarm*#".equals(body)) {
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			} else if ("#*wipedata*#".equals(body)) {
				abortBroadcast();
			} else if ("#*lockscreen*#".equals(body)) {

				abortBroadcast();
			}
		}
	}

}
