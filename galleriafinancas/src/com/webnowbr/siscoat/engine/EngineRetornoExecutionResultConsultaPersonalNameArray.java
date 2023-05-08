package com.webnowbr.siscoat.engine;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaPersonalNameArray {


	@SerializedName( "Name")
	List<EngineRetornoExecutionResultConsultaPersonalName> name;

	public List<EngineRetornoExecutionResultConsultaPersonalName> getName() {
		return name;
	}

	public void setName(List<EngineRetornoExecutionResultConsultaPersonalName> name) {
		this.name = name;
	}


	
	
}
