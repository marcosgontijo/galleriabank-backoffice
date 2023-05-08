package com.webnowbr.siscoat.engine;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaAddressHistory {

	@SerializedName( "Address")
	List<EngineRetornoExecutionResultConsultaAddress> addressHistory;

	public List<EngineRetornoExecutionResultConsultaAddress> getAddressHistory() {
		return addressHistory;
	}

	public void setAddressHistory(List<EngineRetornoExecutionResultConsultaAddress> addressHistory) {
		this.addressHistory = addressHistory;
	}
	
	}
