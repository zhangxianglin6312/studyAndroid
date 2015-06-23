package com.zxl.niubixilitysafe.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.zxl.niubixilitysafe.vo.UpdateInfoVO;

import android.util.Xml;

public class UpdateInfoParser {

	/**
	 * 返回服务器更新的配置信息
	 * 
	 * @param is
	 * @return
	 */
	public static UpdateInfoVO getUpdateInfo(InputStream is) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, "utf-8");
			int type = parser.getEventType();
			UpdateInfoVO info = new UpdateInfoVO();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("version".equals(parser.getName())) {
						info.setVersion(parser.nextText());
					} else if ("description".equals(parser.getName())) {
						info.setDescription(parser.nextText());
					} else if ("apkUrl".equals(parser.getName())) {
						info.setApkurl(parser.nextText());
					}
					break;

				}
				type = parser.next();
			}
			return info;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
