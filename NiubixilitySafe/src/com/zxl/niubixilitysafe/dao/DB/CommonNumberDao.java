package com.zxl.niubixilitysafe.dao.DB;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {
	public static final String path = "/data/data/com.zxl.niubixilitysafe/files/commonnum.db";
	

	/**
	 * 查询有多个分组
	 */

	public static int getGroupCount() {
		int count = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		Cursor cursor = db.rawQuery("select * from classlist", null);
		count = cursor.getCount();

		cursor.close();
		db.close();
		return count;
	}
	
	/**
	 * 查询所有的分组信息
	 */

	public static List<String> getGroupItems() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		Cursor cursor = db.rawQuery("select name from classlist", null);
		List<String> classnames = new ArrayList<String>();
		while(cursor.moveToNext()){
			String name = cursor.getString(0);
			classnames.add(name);
			name = null;
		}
		cursor.close();
		db.close();
		return classnames;
	}

	/**
	 * 查询每一个分组里面有多个条目
	 */
	public static int getChildCountByGroupId(int groupPosition) {
		int newposition = groupPosition + 1;
		int count = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		Cursor cursor = db.rawQuery("select * from table" + newposition, null);
		count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 查询某一个分组里面有所有的孩子信息
	 */
	public static List<String> getChildItemsByGroupId(int groupPosition) {
		int newposition = groupPosition + 1;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		Cursor cursor = db.rawQuery("select name,number from table" + newposition, null);
		List<String> children = new ArrayList<String>();
		while(cursor.moveToNext()){
		
			String name = cursor.getString(0);
			String number = cursor.getString(1);
		   String result = name + "\n" + number;
		   
		  children.add(result);
		  result  =null;
		}
		cursor.close();
		db.close();
		return children;
	}
	
	
	/**
	 * 获取每一个分组的名称
	 * 
	 * @param groupPosition
	 * @return
	 */
	public static String getGroupName(int groupPosition) {
		String name = null;
		int newposition = groupPosition + 1;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select name from classlist where idx=?",
				new String[] { newposition + "" });
		if (cursor.moveToFirst()) {
			name = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return name;
	}

	/**
	 * 获取每一个分组里面某一个孩子的内容
	 * 
	 * @param groupPosition
	 * @return
	 */
	public static String getChildName(int groupPosition, int childPosition) {
		String result = null;
		int newposition = groupPosition + 1;
		int newChildposition = childPosition + 1;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select name,number from table"
				+ newposition + " where _id=?", new String[] { newChildposition
				+ "" });
		if (cursor.moveToFirst()) {
			String name = cursor.getString(0);
			String number = cursor.getString(1);
			result = name + "\n" + number;
		}
		cursor.close();
		db.close();
		return result;
	}
}
