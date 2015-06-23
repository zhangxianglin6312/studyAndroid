package cn.itcast.douban;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends Activity {
	private TextView tv_version;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        tv_version = (TextView) findViewById(R.id.versionNumber);
        tv_version.setText("版本号:"+getVersion());
        
 
        
    }
    
    /**
     * 在当前界面 变成用户可见的时候 调用的方法
     */
    @Override
    protected void onStart() {
        //判断当前客户端所在手机的网络状态.
        if(isNetWorkAvailable()){
        	//进入主界面
        	
        	new Thread(){
        		public void run() {
        			try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			loadMainUI();
        		};
        	}.start();
        	
        	
        }else{
        	showSetNetWorkDialog();
        }
        
    	super.onStart();
    }
    
    
    
    private void loadMainUI(){
    	Intent intent = new Intent(this,MainTabActivity.class);
    	startActivity(intent);
    	finish();
    }
    
    
    private void showSetNetWorkDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("设置网络");
		builder.setMessage("网络连接错误,请检查网络设置");
		builder.setPositiveButton("设置网络", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				//cmp=com.android.settings/.WirelessSettings
				Intent intent = new Intent();
				intent.setClassName("com.android.settings","com.android.settings.WirelessSettings");
				startActivity(intent);
				
			}
		});
		builder.setNegativeButton("取消",  new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.create().show();
	}


	/**
     * 判断手机网络是否可用
     * @return
     */
    private boolean isNetWorkAvailable(){
    	boolean result = false;
    	ConnectivityManager cm  = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo netinfo = cm.getActiveNetworkInfo();
    	if(netinfo!=null){
    		result =	netinfo.isConnected();
    	}
    	return result;
    }
    
    
    
    private String getVersion(){
    	try {
    		PackageInfo info =	getPackageManager().getPackageInfo(getPackageName(), 0);
    		return info.versionName;
    	} catch (NameNotFoundException e) {
			// can't reach
			e.printStackTrace();
			return "";
		}
    }
}