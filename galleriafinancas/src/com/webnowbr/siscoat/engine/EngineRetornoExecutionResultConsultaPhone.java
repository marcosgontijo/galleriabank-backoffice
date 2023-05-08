package com.webnowbr.siscoat.engine;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaPhone {
	
	@SerializedName( "MobilePhoneNumber")
	String phoneNumber;
	
	@SerializedName( "DateLastSeen")
	EngineRetornoExecutionResultConsultaData dateLastSeen;
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public EngineRetornoExecutionResultConsultaData getDateLastSeen() {
		return dateLastSeen;
	}
	public void setDateLastSeen(EngineRetornoExecutionResultConsultaData dateLastSeen) {
		this.dateLastSeen = dateLastSeen;
	}
	
	

}
