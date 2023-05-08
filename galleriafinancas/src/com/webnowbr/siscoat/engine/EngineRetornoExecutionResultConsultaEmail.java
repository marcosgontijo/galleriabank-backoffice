package com.webnowbr.siscoat.engine;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaEmail {

	@SerializedName( "Email")
	String email;
	
	@SerializedName( "DateLastSeen")
	EngineRetornoExecutionResultConsultaData dateLastSeen;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public EngineRetornoExecutionResultConsultaData getDateLastSeen() {
		return dateLastSeen;
	}
	public void setDateLastSeen(EngineRetornoExecutionResultConsultaData dateLastSeen) {
		this.dateLastSeen = dateLastSeen;
	}

}
