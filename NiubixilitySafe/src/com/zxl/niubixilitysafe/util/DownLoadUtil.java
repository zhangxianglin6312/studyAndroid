package com.zxl.niubixilitysafe.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;

public class DownLoadUtil {

	/**
	 * 下载文件的工具类
	 * 
	 * @param path
	 *            文件服务器的路径
	 * @param savedpath
	 *            文件保存的路径
	 * @return
	 */
	public static File download(ProgressDialog pd, String path, String savedpath) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(path);
			HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			
			int max = (int) response.getEntity().getContentLength();
			pd.setMax(max);
			
			File file = new File(savedpath);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			int process = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				process += len;
				pd.setProgress(process);
			}
			fos.flush();
			fos.close();

			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
