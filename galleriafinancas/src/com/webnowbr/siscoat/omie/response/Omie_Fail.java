package com.webnowbr.siscoat.omie.response;

public class Omie_Fail {

	private Integer code;
	private String description;
	private String referer;
	private boolean fatal;
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	public boolean isFatal() {
		return fatal;
	}
	public void setFatal(boolean fatal) {
		this.fatal = fatal;
	}
	
	
}
