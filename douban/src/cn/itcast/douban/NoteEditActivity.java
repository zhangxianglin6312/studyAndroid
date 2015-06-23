package cn.itcast.douban;

import java.io.IOException;
import java.net.MalformedURLException;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.NoteEntry;
import com.google.gdata.util.ServiceException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class NoteEditActivity extends BaseActivity {
	private EditText EditTextTitle;
	private EditText EditTextContent;
	private RadioGroup rg_privacy;
	private RadioButton rb_all;
	private RadioButton rb_friend;
	private RadioButton rb_self;
	private CheckBox cb_can_not_replay;
	private boolean isupdate;
	private NoteEntry ne;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		if ("add".equals(type)) {
			Toast.makeText(this, "添加", 1).show();
			isupdate = false;
		} else if ("update".equals(type)) {
			Toast.makeText(this, "更新", 1).show();
			isupdate = true;
		}
		
		super.onCreate(savedInstanceState);



	}

	@Override
	public void findView() {
		setContentView(R.layout.note_edit);
		EditTextTitle = (EditText) findViewById(R.id.EditTextTitle);
		EditTextContent = (EditText) findViewById(R.id.EditTextContent);
		rg_privacy = (RadioGroup) findViewById(R.id.rg_privacy);
		rb_all = (RadioButton) findViewById(R.id.rb_all);
		rb_friend = (RadioButton) findViewById(R.id.rb_friend);
		rb_self = (RadioButton) findViewById(R.id.rb_self);
		cb_can_not_replay = (CheckBox) findViewById(R.id.cb_can_not_replay);

	}

	@Override
	public void setupView() {
		if (isupdate) {// 更新的操作
			// 初始化相应的数据.
			MyApplication myapp = (MyApplication) getApplication();
			ne = myapp.ne;
			String title = ne.getTitle().getPlainText();
			EditTextTitle.setText(title);
			if (ne.getSummary() != null)
				EditTextContent.setText(ne.getSummary().getPlainText());
			for (Attribute attr : ne.getAttributes()) {
				if ("privacy".equals(attr.getName())) {
					String privacy = attr.getContent();
					// public|private|friend）
					if ("public".equals(privacy)) {
						rb_all.setChecked(true);
					} else if ("private".equals(privacy)) {
						rb_self.setChecked(true);
					}
					if ("friend".equals(privacy)) {
						rb_friend.setChecked(true);
					}
				}
				if ("can_reply".equals(attr.getName())) {
					String can_reply = attr.getContent();
					if ("no".equals(can_reply)) {
						cb_can_not_replay.setChecked(true);
					} else {
						cb_can_not_replay.setChecked(false);
					}
				}
			}

		}
	}

	public void btnSave(View view) {
		final String title = EditTextTitle.getText().toString().trim();
		if (TextUtils.isEmpty(title)) {
			Toast.makeText(this, "标题不能为空", 0).show();
			return;
		}
		final String content = EditTextContent.getText().toString().trim();
		int privacyid = rg_privacy.getCheckedRadioButtonId();
		final String privacy;
		switch (privacyid) {
		case R.id.rb_all:
			privacy = "public";
			break;

		case R.id.rb_friend:
			privacy = "friend";
			break;
		case R.id.rb_self:
			privacy = "private";
			break;
		default:
			privacy = "public";
		}
		final String can_reply;
		if (cb_can_not_replay.isChecked()) {
			can_reply = "no";
		} else {
			can_reply = "yes";
		}

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				showLoading();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Boolean result) {
				hideLoading();
				if (result) {
					// 添加或者更新成功.
					Intent data = new Intent();
					data.putExtra("result", true);
					setResult(0, data);
					finish();// 重新回到上一个日记列表的界面(通知上一个界面更新数据)
				} else {
					if (isupdate) {
						Toast.makeText(getApplicationContext(), "更新失败,请稍后再试", 1)
								.show();
					} else {
						Toast.makeText(getApplicationContext(), "发表失败,请稍后再试", 1)
								.show();
					}
				}

				super.onPostExecute(result);
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					if (isupdate) {
						myService.updateNote(ne, new PlainTextConstruct(title),
								new PlainTextConstruct(content), privacy,
								can_reply);
					} else {
						myService.createNote(new PlainTextConstruct(title),
								new PlainTextConstruct(content), privacy,
								can_reply);
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			}

		}.execute();
	}

	public void btnCancle(View view) {
		finish();
	}
}
