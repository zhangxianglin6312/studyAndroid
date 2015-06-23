package com.zxl.niubixilitysafe.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	public BlackNumberDBOpenHelper(Context context) {
		super(context, "black.db", null, 1);
	}

	/**
	 * 当数据库 被第一次创建的时候 调用的方法.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//mode 是黑名单号码的拦截模式  0 电话拦截 1短信拦截 2全部拦截
		db.execSQL("create table blacknumber (_id integer primary key autoincrement, number varchar(20), mode varchar(3))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
