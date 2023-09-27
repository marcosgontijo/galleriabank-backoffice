package com.webnowbr.siscoat.omie.request;

import java.util.List;

public class OmieRequestBase {

	private String call;
	private String app_key;
	private String app_secret;
	private List<IOmieParam> param;
	
	
	public String getCall() {
		return call;
	}
	public void setCall(String call) {
		this.call = call;
	}
	public String getApp_key() {
		return app_key;
	}
	public void setApp_key(String app_key) {
		this.app_key = app_key;
	}
	public String getApp_secret() {
		return app_secret;
	}
	public void setApp_secret(String app_secret) {
		this.app_secret = app_secret;
	}
	public List<IOmieParam> getParam() {
		return param;
	}
	public void setParam(List<IOmieParam> param) {
		this.param = param;
	}

	
}
