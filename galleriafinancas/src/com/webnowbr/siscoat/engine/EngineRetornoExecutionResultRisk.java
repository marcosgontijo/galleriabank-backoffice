package com.webnowbr.siscoat.engine;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultRisk {

	@SerializedName("type")
	String type;
	@SerializedName("text")
	String text;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
