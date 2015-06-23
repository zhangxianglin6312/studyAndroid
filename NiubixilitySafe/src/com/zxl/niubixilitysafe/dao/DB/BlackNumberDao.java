package com.zxl.niubixilitysafe.dao.DB;

import java.util.ArrayList;
import java.util.List;

import com.zxl.niubixilitysafe.dao.BlackNumberDBOpenHelper;
import com.zxl.niubixilitysafe.vo.BlackNumber;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;

	// 希望在构造方法里面 就完成对BlackNumberDBOpenHelper的初始化
	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * 添加一条黑名单信息到数据库
	 */
	public void save(String number, String mode) {
		if (find(number)) {
			return;
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("insert into blacknumber (number,mode) values (?,?)",
					new Object[] { number, mode });
		}
		db.close();
	}

	/**
	 * 删除一条黑名单信息
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();

		if (db.isOpen()) {
			db.execSQL("delete from blacknumber where number=?",
					new Object[] { number });
		}
		db.close();

		return !find(number);
	}

	/**
	 * 更新一条黑名单信息
	 * 
	 * @param oldnumber
	 *            旧的电话
	 * @param newnumber
	 *            新的电话 可以为空.
	 * @param mode
	 *            拦截模式
	 */
	public void update(String oldnumber, String newnumber, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		// 绝大多数的更新 只是更新黑名单拦截的模式,而不是更新的号码.
		if (TextUtils.isEmpty(newnumber)) {
			newnumber = oldnumber;
		}
		if (db.isOpen()) {
			db.execSQL("update blacknumber set number=?,mode=? where number=?",
					new Object[] { newnumber, mode, oldnumber });
		}
		db.close();

	}

	/**
	 * 查询一条黑名单信息
	 * 
	 * @param number
	 * @return
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from blacknumber where number=?",
					new String[] { number });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();
		}
		db.close();
		return result;

	}
	/**
	 * 查询一条黑名单信息的模式
	 * 
	 * @param number
	 * @return
	 */
	public String findMode(String number) {
		String result = "-1";
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select mode from blacknumber where number=?",
					new String[] { number });
			if (cursor.moveToNext()) {
				result = cursor.getString(0);
			}
			cursor.close();
		}
		db.close();
		return result;
	}
	/**
	 * 查询全部的黑名单信息
	 */
	public List<BlackNumber> findAll() {
		List<BlackNumber> blacknumbers = new ArrayList<BlackNumber>();
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select number,mode from blacknumber", null);
			while(cursor.moveToNext()){
				String number = cursor.getString(0);
				String mode = cursor.getString(1);
				BlackNumber blacknumber = new BlackNumber();
				blacknumber.setMode(mode);
				blacknumber.setNumber(number);
				blacknumbers.add(blacknumber);
				blacknumber = null;
			}
			cursor.close();
		}
		db.close();
		return blacknumbers;
	}
	/**
	 * 分页查询黑名单信息
	 */
	public List<BlackNumber> findAll(int startindex,int max) {
		List<BlackNumber> blacknumbers = new ArrayList<BlackNumber>();
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ? ", new String[]{max+"",startindex+""});
			while(cursor.moveToNext()){
				String number = cursor.getString(0);
				String mode = cursor.getString(1);
				BlackNumber blacknumber = new BlackNumber();
				blacknumber.setMode(mode);
				blacknumber.setNumber(number);
				blacknumbers.add(blacknumber);
				blacknumber = null;
			}
			cursor.close();
		}
		db.close();
		return blacknumbers;
	}
	
	/*
	 * 获取数据库总共有多少个条目
	 */
	public int getTotalCount (){
		int total= 0;
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from blacknumber", null);
			total = cursor.getCount();
			cursor.close();
			db.close();
		}
		return total;
	}
}
