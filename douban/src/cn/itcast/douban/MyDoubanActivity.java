package cn.itcast.douban;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyDoubanActivity extends Activity {
	private ListView lv;
	private SharedPreferences sp;
	private static final String[] names = { "我读", "我听", "我看", "我评", "我的资料",
			"我的日记" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.fav);
		lv = (ListView) findViewById(R.id.melistview);
		lv.setDivider(new ColorDrawable(Color.TRANSPARENT));
		lv.setDividerHeight(5);
		lv.setAdapter(new ArrayAdapter<String>(this, R.layout.fav_item,
				R.id.fav_title, names));
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(isAuthed()){
					//进入相应的界面
					System.out.println("进入相应的界面");
					switch (position) {
					case 0:
						Intent myreadIntent = new Intent(getApplicationContext(),MyReadActivity.class);
						startActivity(myreadIntent);
						break;
						
					case 4:
						Intent meIntent = new Intent(getApplicationContext(),MeActivity.class);
						startActivity(meIntent);
						break;

					case 5:
						Intent noteIntent = new Intent(getApplicationContext(),MyNoteActivity.class);
						startActivity(noteIntent);
						break;
					}
					
					
					
				}else{
					Intent enterpwdIntent = new Intent(MyDoubanActivity.this,LoginActivity.class);
					startActivity(enterpwdIntent);
				}
			}
		});

	}

	/**
	 * 判断用户是否认证过.
	 * @return
	 */
	private boolean isAuthed() {
		String accesstoken = sp.getString("accesstoken", "");
		String tokensecret = sp.getString("tokensecret", "");
		if (TextUtils.isEmpty(accesstoken) || TextUtils.isEmpty(tokensecret)) {
			return false;
		}else{
			return true;
		}
	}

}
