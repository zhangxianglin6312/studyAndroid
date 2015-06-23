package com.zxl.niubixilitysafe;

import com.zxl.niubixilitysafe.util.Md5Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LostFindActivity extends Activity implements OnClickListener{
	private SharedPreferences sp;
	private EditText et_first_pwd;
	private EditText et_first_pwd_confirm;
	private Button bt_first_ok;
	private Button bt_first_cancle;
	
	private EditText et_normal_pwd;
	private Button bt_normal_ok;
	private Button bt_normal_cancle;
	
	private TextView tv_lostfind_number;
	private ImageView iv_lostfind_status;
	private AlertDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		if(isSetupPWD()){
			//设置过密码，进入输入密码界面
			showNormalEntryDialog();
		}else{
			// 显示设置密码对话框
			showFirstEntryDialog();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_first_ok:
			String firstPwd = et_first_pwd.getText().toString().trim();
			String firstConfirmPwd = et_first_pwd_confirm.getText().toString().trim();
			if(TextUtils.isEmpty(firstPwd)||TextUtils.isEmpty(firstConfirmPwd)){
				Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			}else if(!firstPwd.equals(firstConfirmPwd)){
				Toast.makeText(this, "密码不相同", Toast.LENGTH_SHORT).show();
			}else{
				Editor edit = sp.edit();
				edit.putString("password", Md5Util.getMD5Str(firstPwd));
				edit.commit();
				dialog.dismiss();
				finish();
			}
			break;
		case R.id.bt_first_cancle:
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			finish();
			break;
		case R.id.bt_normal_ok:
			String normalPwd = et_normal_pwd.getText().toString().trim();
			if(TextUtils.isEmpty(normalPwd)){
				Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			}else{
				String savePwd = sp.getString("password", "");
				if(savePwd.equals(Md5Util.getMD5Str(normalPwd))){
					dialog.dismiss();
					//判断 用户是否进行过设置向导.
					if(isSetup()){
						setContentView(R.layout.lostfind);
						tv_lostfind_number = (TextView) findViewById(R.id.tv_lostfind_number);
						iv_lostfind_status = (ImageView) findViewById(R.id.iv_lostfind_status);
						boolean protecting = sp.getBoolean("protecting", false);
						String safenumber = sp.getString("safenumber", "");
						tv_lostfind_number.setText(safenumber);
						if(protecting){
							iv_lostfind_status.setImageResource(R.drawable.btn_check_buttonless_on);
						}else{
							iv_lostfind_status.setImageResource(R.drawable.btn_close_normal);
						}
					}else{
						Intent setUpIntent = new Intent(getApplicationContext(), Setup1Activity.class);
						startActivity(setUpIntent);
						finish();
					}
				}else{
					Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.bt_normal_cancle:
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			finish();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 重新设置向导
	 * @param view
	 */
	public void reEntrySetup(View view){
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * 判断用户是否设置过密码
	 * 
	 * @return
	 */
	private boolean isSetupPWD() {
		String pwd = sp.getString("password", "");
		if(TextUtils.isEmpty(pwd)){
			//没设置过密码
			return false;
		}else{
			return true;
		}
	}
	
	private void showNormalEntryDialog() {		
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.normal_entry_dialog, null);
		et_normal_pwd = (EditText) view.findViewById(R.id.et_normal_pwd);
		bt_normal_ok = (Button) view.findViewById(R.id.bt_normal_ok);
		bt_normal_cancle = (Button) view.findViewById(R.id.bt_normal_cancle);
		bt_normal_ok.setOnClickListener(this);
		bt_normal_cancle.setOnClickListener(this);
		builder.setView(view);
		//用户点击手机的后退键的时候 会自动关闭掉对话框 关闭他的activity
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		dialog = builder.create();
		dialog.show();
		
	}

	private void showFirstEntryDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.first_entry_dialog, null);
		et_first_pwd = (EditText) view.findViewById(R.id.et_first_pwd);
		et_first_pwd_confirm = (EditText) view.findViewById(R.id.et_first_pwd_confirm);
		bt_first_ok = (Button) view.findViewById(R.id.bt_first_ok);
		bt_first_cancle = (Button) view.findViewById(R.id.bt_first_cancle);
		bt_first_ok.setOnClickListener(this);
		bt_first_cancle.setOnClickListener(this);
		builder.setView(view);
		//用户点击手机的后退键的时候 会自动关闭掉对话框 关闭他的activity
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		dialog = builder.create();
		dialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_settings:
	        setName();
	        return true;
	    }
		return false;
	}

	private void setName() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("更改名称");
		final EditText et = new EditText(this);
		et.setHint("请输入要更改的名称");
		builder.setView(et);
		builder.setPositiveButton("确定", new  DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				String newname = et.getText().toString().trim();
				Editor editor = sp.edit();
				editor.putString("newname", newname);
				editor.commit();
			}
		});
		builder.create().show();
	}
	
	private Boolean isSetup(){
		return sp.getBoolean("isSetup", false);
	}
	
	
}
