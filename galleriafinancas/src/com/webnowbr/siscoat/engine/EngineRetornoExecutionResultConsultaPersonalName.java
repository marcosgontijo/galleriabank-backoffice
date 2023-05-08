package com.webnowbr.siscoat.engine;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaPersonalName {

	@SerializedName("Name")
	EngineRetornoExecutionResultConsultaPersonalNameFull name;

	public EngineRetornoExecutionResultConsultaPersonalNameFull getName() {
		return name;
	}

	public void setName(EngineRetornoExecutionResultConsultaPersonalNameFull name) {
		this.name = name;
	}

}
