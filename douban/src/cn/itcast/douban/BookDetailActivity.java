package cn.itcast.douban;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import net.htmlparser.jericho.Source;

import cn.itcast.douban.domain.Book;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BookDetailActivity extends Activity {
	ProgressBar pb ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		pb = new ProgressBar(this);
		LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		pb.setLayoutParams(params);
		ll.addView(pb);
		TextView tv = new TextView(this);
		tv.setTextSize(12);
		tv.setText("正在加载");
		tv.setLayoutParams(params);
		ll.addView(tv);
		setContentView(ll);
		
		MyApplication myapp = (MyApplication) getApplication();
		Book book = myapp.book;
		String isbn = book.getIsbn();
		
		new AsyncTask<String, Void, String>(){

			@Override
			protected void onPreExecute() {
			
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				if(result!=null){
				ScrollView sv = new ScrollView(getApplicationContext());
				TextView tv = new TextView(getApplicationContext());
				tv.setTextSize(20);
				tv.setText(result);
				sv.addView(tv);
				setContentView(sv);
				}else{
					 Toast.makeText(getApplicationContext(), "获取摘要信息失败", 0).show();
				}
				super.onPostExecute(result);
			}

			@Override
			protected String doInBackground(String... params) {
				try {
					String path = "http://api.douban.com/book/subject/isbn/"+params[0]+"?alt=json";
					URL url = new URL(path);
					URLConnection conn =  url.openConnection();
					Source source = new Source(conn);
					String result = source.toString();
					JSONObject jsonobj = new JSONObject(result);
					JSONObject summaryobj = (JSONObject) jsonobj.get("summary");
					String summary = (String) summaryobj.get("$t");
					return summary;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				return null;
			}}.execute(isbn);
	}
}
