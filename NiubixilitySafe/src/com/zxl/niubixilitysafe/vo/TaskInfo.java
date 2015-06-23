package com.zxl.niubixilitysafe.vo;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private String packname;
	private long memsize;//内存占用情况 单位 byte
	private Drawable icon;
	private String appname;
	private boolean ischecked;
	private boolean isusertask;
	
	public boolean isIsusertask() {
		return isusertask;
	}
	public void setIsusertask(boolean isusertask) {
		this.isusertask = isusertask;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public long getMemsize() {
		return memsize;
	}
	public void setMemsize(long memsize) {
		this.memsize = memsize;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public boolean isIschecked() {
		return ischecked;
	}
	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}
	
	
}
