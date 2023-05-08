package com.webnowbr.siscoat.engine;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaPhoneHistory {
	
	@SerializedName( "PhoneNumber")
	List<EngineRetornoExecutionResultConsultaPhone> phoneNumber;

	public List<EngineRetornoExecutionResultConsultaPhone> getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(List<EngineRetornoExecutionResultConsultaPhone> phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	

}
