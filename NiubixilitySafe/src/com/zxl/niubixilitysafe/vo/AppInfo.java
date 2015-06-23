package com.zxl.niubixilitysafe.vo;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private Drawable appicon;
	private String packname;
	private String appname;
	private String version;
	private boolean userapp;
	private boolean network;
	private boolean money;
	private boolean pri;
	
	
	public boolean isNetwork() {
		return network;
	}
	public void setNetwork(boolean network) {
		this.network = network;
	}
	public boolean isMoney() {
		return money;
	}
	public void setMoney(boolean money) {
		this.money = money;
	}
	public boolean isPri() {
		return pri;
	}
	public void setPri(boolean pri) {
		this.pri = pri;
	}
	public Drawable getAppicon() {
		return appicon;
	}
	public void setAppicon(Drawable appicon) {
		this.appicon = appicon;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isUserapp() {
		return userapp;
	}
	public void setUserapp(boolean userapp) {
		this.userapp = userapp;
	}
	
}
