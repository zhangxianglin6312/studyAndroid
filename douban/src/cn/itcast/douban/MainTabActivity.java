package cn.itcast.douban;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class MainTabActivity extends TabActivity {
	private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);
		mTabHost = getTabHost();
		TabSpec tabMyDouban = mTabHost.newTabSpec("我的豆瓣");
		tabMyDouban
				.setIndicator(getTabView(R.drawable.tab_main_nav_me, "我的豆瓣"));
		Intent myDoubanIntent = new Intent(this, MyDoubanActivity.class);
		tabMyDouban.setContent(myDoubanIntent);
		mTabHost.addTab(tabMyDouban);

		TabSpec tabNewBook = mTabHost.newTabSpec("豆瓣新书");
		tabNewBook
				.setIndicator(getTabView(R.drawable.tab_main_nav_book, "豆瓣新书"));
		Intent newBookIntent = new Intent(this, NewBookActivity.class);
		tabNewBook.setContent(newBookIntent);
		mTabHost.addTab(tabNewBook);

	}

	/**
	 * 返回某一个条目的indicator
	 * 
	 * @param icon
	 * @param text
	 * @return
	 */
	private View getTabView(int icon, String text) {
		View view = View.inflate(this, R.layout.tab_main_nav, null);
		ImageView iv_icon = (ImageView) view.findViewById(R.id.ivIcon);
		TextView tv_tilte = (TextView) view.findViewById(R.id.tvTitle);
		iv_icon.setImageResource(icon);
		tv_tilte.setText(text);
		return view;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("提示");
			builder.setMessage("确定退出豆瓣客户端吗?");
			builder.setPositiveButton("确定", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			
			builder.create().show();
			return true;
		}

		return super.dispatchKeyEvent(event);
	}
}
