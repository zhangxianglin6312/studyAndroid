package cn.itcast.douban;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.itcast.douban.utils.NetUtil;

public class LoginActivity extends Activity {
	protected static final int LOGIN_SUCCESS = 10;
	protected static final int LOGIN_ERROR = 11;
	protected static final int GET_CAPTCHA = 12;
	protected static final int GET_CAPTCHA_ERROR = 13;
	private EditText EditTextEmail;
	private EditText EditTextPassword;
	
	private EditText EditTextCaptchaValue;
	
	private LinearLayout ll_Captcha;
	private ImageView ImageViewCaptcha;
	private ProgressDialog pd;
	private SharedPreferences sp;
	private String captchaId;// 验证码的id
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOGIN_ERROR:
				Toast.makeText(getApplicationContext(), "登陆失败", 0).show();
				break;

			case LOGIN_SUCCESS:
				finish();
				break;
			case GET_CAPTCHA:
				ll_Captcha.setVisibility(View.VISIBLE);
				ImageViewCaptcha.setImageBitmap((Bitmap) msg.obj);
				break;
			case GET_CAPTCHA_ERROR:
				Toast.makeText(getApplicationContext(), "获取验证码失败", 0).show();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		EditTextEmail = (EditText) findViewById(R.id.EditTextEmail);
		EditTextPassword = (EditText) findViewById(R.id.EditTextPassword);
		EditTextCaptchaValue =(EditText)findViewById(R.id.EditTextCaptchaValue);
		pd = new ProgressDialog(this);
		ll_Captcha = (LinearLayout) findViewById(R.id.Captcha);
		ImageViewCaptcha =(ImageView)findViewById(R.id.ImageViewCaptcha);
		ImageViewCaptcha.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				checkCaptcha();
			}
		});
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		checkCaptcha();
		
		
		
	}

	/**
	 * 检查是否需要输入验证码
	 */
	private void checkCaptcha() {
		// 连接服务器 判断是否需要输入验证码
		pd.setMessage("正在检查是否需要验证码");
		pd.show();
		new Thread(){
			public void run() {
				captchaId = NetUtil.getCaptchaId();
				if(captchaId!=null){
					//需要验证码
					 String captchapath ="http://www.douban.com/misc/captcha?id="+captchaId+"&amp;size=s"; 
					 System.out.println(captchapath);
					 //访问服务器 下载这个图片
					Bitmap bitmap = NetUtil.getBitmap(captchapath);
					if(bitmap!=null){
						//获取到了验证码
						Message msg = new Message();
						msg.what = GET_CAPTCHA;
						msg.obj = bitmap;
						handler.sendMessage(msg);
					}else{
						//获取验证码失败
						Message msg = new Message();
						msg.what = GET_CAPTCHA_ERROR;
						handler.sendMessage(msg);
					}
				}
				pd.dismiss();
			};
		}.start();
	}

	public void btnUserLogin(View view) {
		final String email = EditTextEmail.getText().toString().trim();
		final String password = EditTextPassword.getText().toString().trim();
		final String captchasolution = EditTextCaptchaValue.getText().toString().trim();
		if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
			Toast.makeText(this, "用户名或密码不能为空", 0).show();
			return;
		} else {
			// 提交数据到服务器 换取 后门的钥匙.
			if(captchaId!=null){
				if(TextUtils.isEmpty(captchasolution)){
					Toast.makeText(this, "验证码不能为空", 0).show();
					return;
				}
			}
			
			
			pd.setMessage("正在登陆");
			pd.setCancelable(false);
			pd.show();
			new Thread() {
				public void run() {
					List<String> result = NetUtil.login(email, password, captchasolution, captchaId);
					if (result != null) {
						Message msg = Message.obtain();
						msg.what = LOGIN_SUCCESS;
						handler.sendMessage(msg);
						Editor editor = sp.edit();
						editor.putString("accesstoken", result.get(0));
						editor.putString("tokensecret", result.get(1));
						editor.commit();
					} else {
						Message msg = Message.obtain();
						msg.what = LOGIN_ERROR;
						handler.sendMessage(msg);
					}
					pd.dismiss();
				};
			}.start();
		}

	}

	public void btnUserExit(View view) {
		finish();
	}
}
