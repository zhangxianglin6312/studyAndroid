package com.zxl.niubixilitysafe.vo;


public class BlackNumber {

	private String number;
	private String mode;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		if ("1".equals(mode) || "2".equals(mode) || "0".equals(mode)) {
			this.mode = mode;
		} else {
			this.mode = "0";
		}
	}

	@Override
	public String toString() {
		return "BlackNumber [number=" + number + ", mode=" + mode + "]";
	}

}
