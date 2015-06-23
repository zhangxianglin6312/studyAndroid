package com.zxl.niubixilitysafe.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsUtil {
	
	
	/**
	 * 备份短信到sd卡
	 * @param context
	 * @param file 备份到神马文件
	 * 
	 */
	public static void backupSms(Context context,File file,ProgressDialog pd) throws Exception{
		Uri uri = Uri.parse("content://sms/");
		FileOutputStream fos = new FileOutputStream(file);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "UTF-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"_id","address","type","body","date"}, null, null, null);
		int max = cursor.getCount();
		pd.setMax(max);
		serializer.attribute(null, "count", max+"");
		int count = 0;
		while(cursor.moveToNext()){
			serializer.startTag(null, "sms");
			String id = cursor.getString(0);
			serializer.attribute(null, "id", id);
			serializer.startTag(null, "address");
			String address = cursor.getString(1);
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "type");
			String type = cursor.getString(2);
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "body");
			String body = cursor.getString(3);
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.startTag(null, "date");
			String date = cursor.getString(4);
			serializer.text(date);
			serializer.endTag(null, "date");
			
			//写到sd卡的文件上.
			serializer.endTag(null, "sms");
			fos.flush();
		
			count ++;
			pd.setProgress(count);
		}
		cursor.close();
		
		
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.flush();
		fos.close();
	}
	
	private class SmsBean{
		public String type;
		public String date;
		public String body;
		public String id;
		public String address;
	}
	
	/**
	 * 还原短信到系统
	 * @param file
	 */
	public static void restoreSms(Context context, File file,ProgressDialog pd) throws Exception{
		//context.getContentResolver().insert(uri, values);
		//读取xml文件 获取短信的信息 ,插入到系统.
		XmlPullParser parser = Xml.newPullParser();
		FileInputStream fis = new FileInputStream(file);
		parser.setInput(fis, "UTF-8");
		Uri uri = Uri.parse("content://sms/");
		int type = parser.getEventType();
		SmsBean smsBean = null;
		int process = 0;
		while(type!=XmlPullParser.END_DOCUMENT){
			switch (type) {
			case XmlPullParser.START_TAG:
				if("smss".equals(parser.getName())){
					String count = parser.getAttributeValue(0);
					pd.setMax(Integer.parseInt(count));
				}else if("sms".equals(parser.getName())){
					smsBean = new SmsUtil().new SmsBean();
					smsBean.id = parser.getAttributeValue(0);
				}else if("type".equals(parser.getName())){
					smsBean.type = parser.nextText();
				}else if("date".equals(parser.getName())){
					smsBean.date = parser.nextText();
				}else if("body".equals(parser.getName())){
					smsBean.body = parser.nextText();
				}else if("address".equals(parser.getName())){
					smsBean.address = parser.nextText();
				}
				
				break;

			case XmlPullParser.END_TAG:
				if("sms".equals(parser.getName())){
					//插入到系统的内容提供者里面
					ContentValues values = new ContentValues();
					values.put("address", smsBean.address);
					values.put("body", smsBean.body);
					values.put("date", smsBean.date);
					values.put("type", smsBean.type);
					context.getContentResolver().insert(uri, values);
					smsBean = null; //防止数据 加载错误的信息
					process ++;
					pd.setProgress(process);
				}
				break;
			}
			
			
			
			
			
			type = parser.next();
		}

	}
	
}
