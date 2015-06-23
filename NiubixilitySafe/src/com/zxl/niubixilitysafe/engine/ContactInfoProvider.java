package com.zxl.niubixilitysafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.zxl.niubixilitysafe.vo.ContactInfoVO;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactInfoProvider{
	public static List<ContactInfoVO> getContactInfos(Context context){
		List<ContactInfoVO> conInfoVOs = null;
		// 获取用来操作数据的类的对象，对联系人的基本操作都是使用这个对象  
		ContentResolver cr = context.getContentResolver();
		Cursor contactCursor = cr.query(Phone.CONTENT_URI,new String[]{Phone.DISPLAY_NAME,Phone.NUMBER}, null, null, Phone.SORT_KEY_PRIMARY);
		if(contactCursor.getCount()>0){
			conInfoVOs = new ArrayList<ContactInfoVO>();
			while(contactCursor.moveToNext()){
				ContactInfoVO contactInfo = new ContactInfoVO();
				String name = contactCursor.getString(0);
				String phone = contactCursor.getString(1);
				contactInfo.setName(name);
				contactInfo.setPhone(phone);
				conInfoVOs.add(contactInfo);
			}
			contactCursor.close();
		}
		return conInfoVOs;
	}
}
