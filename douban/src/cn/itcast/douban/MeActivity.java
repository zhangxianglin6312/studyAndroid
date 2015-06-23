package cn.itcast.douban;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.itcast.douban.utils.LoadImageAsyncTask;
import cn.itcast.douban.utils.NetUtil;

import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.util.ServiceException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MeActivity extends BaseActivity {
	protected static final int LOAD_FINISH = 20;
	protected static final int LOAD_ERROR = 21;
	private ImageView imgUser;
	private TextView txtUserName;
	private TextView txtUserAddress;
	private TextView txtUserDescription;
	private RelativeLayout loading;
	private String location;
	private String content;
	private String title;
	private Bitmap bitmap;
	private String iconpath;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			hideLoading();
			switch (msg.what) {
			case LOAD_FINISH:
				txtUserName.setText(title);
				txtUserAddress.setText(location);
				txtUserDescription.setText(content);
				// imgUser.setImageBitmap(bitmap);
				// 判断是否有缓存的头像.
				File file = new File(getCacheDir(), "icon.jpg");
				if (file.exists()) {
					imgUser.setImageURI(Uri.fromFile(file));
					System.out.println("使用头像的缓存");
				} else {
					LoadImageAsyncTask task = new LoadImageAsyncTask(
							new LoadImageAsyncTask.LoadImageAsynTaskCallBack() {

								public void onImageLoaded(Bitmap bitmap) {
									if (bitmap != null) {
										imgUser.setImageBitmap(bitmap);
										// 缓存获取到的图片
										try {
											File file = new File(getCacheDir(),
													"icon.jpg");
											FileOutputStream fos = new FileOutputStream(
													file);
											bitmap.compress(
													CompressFormat.JPEG, 100,
													fos);
										} catch (Exception e) {
											e.printStackTrace();
											
										}

									} else {
										Toast.makeText(getApplicationContext(),
												"下载图片失败", 0).show();
									}

								}

								public void beforeImageLoad() {
									imgUser.setImageResource(R.drawable.ic_launcher);

								}
							});
					task.execute(iconpath);
					System.out.println("下载新的图片");
				}

				break;

			case LOAD_ERROR:
				Toast.makeText(getApplicationContext(), "加载数据失败,请检查网络", 0)
						.show();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.me);
		super.onCreate(savedInstanceState);
		/*
		 * Button button = new Button(getApplicationContext());
		 * button.setOnClickListener(new OnClickListener() {
		 * 
		 * public void onClick(View v) {
		 * 
		 * 
		 * } });
		 */
	}

	@Override
	public void findView() {
		imgUser = (ImageView) findViewById(R.id.imgUser);
		txtUserName = (TextView) findViewById(R.id.txtUserName);
		txtUserAddress = (TextView) findViewById(R.id.txtUserAddress);
		txtUserDescription = (TextView) findViewById(R.id.txtUserDescription);
		loading = (RelativeLayout) findViewById(R.id.loading);
	}

	@Override
	public void setupView() {
		showLoading();
		new Thread() {
			public void run() {
				try {
					String uid = myService.getAuthorizedUser().getUid();
					UserEntry ue = myService.getUser(uid);
					location = ue.getLocation();
					content = ((TextContent) ue.getContent()).getContent()
							.getPlainText();
					title = ue.getTitle().getPlainText();
					iconpath = ue.getLink("icon", null).getHref();
					// bitmap = NetUtil.getBitmap(iconpath);

					Message msg = Message.obtain();
					msg.what = LOAD_FINISH;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = LOAD_ERROR;
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

}
