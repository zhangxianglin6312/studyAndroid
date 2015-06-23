package com.zxl.niubixilitysafe.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.ProgressDialog;
import android.content.Context;

public class CopyAssetUtil {
	/**
	 * 完成资产文件的拷贝
	 * @param assetName 资产文件的名称
	 * @param desFile 目标文件
	 * @return
	 */
	public static File copyFile(InputStream is,File desFile,ProgressDialog pd){
		try {
			 FileOutputStream fos = new FileOutputStream(desFile);
			 byte[] buffer = new byte[1024];
			 int len = 0 ;
			 int progress = 0;
			 while((len = is.read(buffer))!=-1){
				 progress += len;
				 fos.write(buffer, 0, len);
				 pd.setProgress(progress);
			 }
			 fos.flush();
			 fos.close();
			 return desFile;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
