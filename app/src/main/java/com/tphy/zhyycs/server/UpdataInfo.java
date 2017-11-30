package com.tphy.zhyycs.server;

public class UpdataInfo {

	private String version;//版本信息
	private String url;//下载地址
	private String description;//更新简介
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
