package com.webnowbr.siscoat.engine;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultIndicator {
	
	@SerializedName("Risk")
	EngineRetornoExecutionResultRisk risk;
	@SerializedName("VariableName")
	String variableName;
	@SerializedName("Description")
	String description;
	@SerializedName("Value")
	String value;
	@SerializedName("Error")
	String error;

	public EngineRetornoExecutionResultRisk getRisk() {
		return risk;
	}

	public void setRisk(EngineRetornoExecutionResultRisk risk) {
		this.risk = risk;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
