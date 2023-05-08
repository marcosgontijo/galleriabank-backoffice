package com.webnowbr.siscoat.engine;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaEmailHistory {

	@SerializedName( "Email")
	List<EngineRetornoExecutionResultConsultaEmail> email;

	public List<EngineRetornoExecutionResultConsultaEmail> getEmail() {
		return email;
	}

	public void setEmail(List<EngineRetornoExecutionResultConsultaEmail> email) {
		this.email = email;
	}
	
	
}
