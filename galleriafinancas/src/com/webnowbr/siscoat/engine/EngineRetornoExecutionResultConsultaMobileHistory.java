package com.webnowbr.siscoat.engine;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaMobileHistory {
	
	
	@SerializedName( "MobilePhoneNumber")
	List<EngineRetornoExecutionResultConsultaPhone> mobilePhoneNumber;

	public List<EngineRetornoExecutionResultConsultaPhone> getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(List<EngineRetornoExecutionResultConsultaPhone> mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

	
	

}
