package cn.itcast.douban;

import com.google.gdata.client.douban.DoubanService;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

public abstract class BaseActivity extends Activity {
	public SharedPreferences sp;
	public RelativeLayout loading;
	public DoubanService myService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		String apiKey = "0c8417cf07c862dd258c476d17592897";
		String secret = "010f9e7472bd0abf";

		myService = new DoubanService("国培豆瓣", apiKey, secret);
		String accesstoken = sp.getString("accesstoken", "");
		String tokensecret = sp.getString("tokensecret", "");
		myService.setAccessToken(accesstoken, tokensecret);
	
		findView();
		loading = (RelativeLayout) findViewById(R.id.loading);
		setupView();
	}
	
	
	public abstract void findView();
	
	public abstract void setupView();
	
	public void showLoading(){
		loading.setVisibility(View.VISIBLE);
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(200);
		loading.startAnimation(aa);
	}
	public void hideLoading(){
		loading.setVisibility(View.INVISIBLE);
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
		aa.setDuration(200);
		loading.startAnimation(aa);
	}
}
