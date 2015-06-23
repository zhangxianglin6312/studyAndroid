package com.zxl.niubixilitysafe.dao.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberDao {
	/**
	 * 查询电话号码的归属地信息
	 * 
	 * @param number
	 * @return
	 */
	public static String getAddress(Context context, String number) {
		String address = number; // 如果没有查询出来归属地 就显示原来的号码
		String path = context.getFilesDir() + "/address.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (db.isOpen()) {
			if (number.matches("^1[3458]\\d{9}$")) {// 手机号码
			Cursor cursor = db.rawQuery(
					"select city from address_tb inner join numinfo on address_tb.[_id] = numinfo.outkey where numinfo.mobileprefix = ?",
					new String[] { number.substring(0, 7) });
				if(cursor.moveToFirst()){
					address = cursor.getString(0);
				}
				cursor.close();
			}else{//固定电话
				Cursor cursor ;
				switch (number.length()) {
				case 4:
					address = "模拟器";
					break;
				case 7:
					address ="本地号码";
					break;
				case 8:
					address ="本地号码";
					break;
				case 10://3位区号 + 7 位号码
					cursor = db.rawQuery("select city from address_tb where area =? limit 1", new String[]{number.substring(0, 3)});
					if(cursor.moveToFirst()){
						address = cursor.getString(0);
					}
					cursor.close();
					break;
				case 11://4位区号 + 7 位号码  | 3位区号 + 8 位号码
					cursor = db.rawQuery("select city from address_tb where area =? limit 1", new String[]{number.substring(0, 3)});
					if(cursor.moveToFirst()){
						address = cursor.getString(0);
					}
					cursor.close();
					cursor = db.rawQuery("select city from address_tb where area =? limit 1", new String[]{number.substring(0, 4)});
					if(cursor.moveToFirst()){
						address = cursor.getString(0);
					}
					cursor.close();
					break;
				case 12://4位区号 + 8 位号码
					cursor = db.rawQuery("select city from address_tb where area =? limit 1", new String[]{number.substring(0, 4)});
					if(cursor.moveToFirst()){
						address = cursor.getString(0);
					}
					cursor.close();
					break;
				}
			}
			db.close();
		}
		return address;
	}
}
