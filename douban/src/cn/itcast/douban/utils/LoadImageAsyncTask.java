package cn.itcast.douban.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.itcast.douban.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * 下载图片的异步任务,接受三个参数. 第一个参数 是String 下载图片的路径 二 integer 下载图片的进度 三 Bitmap
 * 下载的图片所对应的bitmap
 * 
 */
public class LoadImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
	private LoadImageAsynTaskCallBack callback;

	public LoadImageAsyncTask(LoadImageAsynTaskCallBack callback) {
		this.callback = callback;
	}

	public interface LoadImageAsynTaskCallBack {
		public void beforeImageLoad();

		public void onImageLoaded(Bitmap bitmap);
	}

	@Override
	protected void onPreExecute() {

		callback.beforeImageLoad();

		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		callback.onImageLoaded(result);
		super.onPostExecute(result);

	}

	@Override
	protected Bitmap doInBackground(String... params) {
		try {
			String path = params[0];
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			int max = conn.getContentLength();
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
				total += len;
				publishProgress(total, max);
				Thread.sleep(90);
			}
			byte[] result = baos.toByteArray();
			return BitmapFactory.decodeByteArray(result, 0, result.length);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		int total = values[0];
		int max = values[1];
		super.onProgressUpdate(values);
	}
}
