package com.webnowbr.siscoat.engine;

import java.math.BigDecimal;

public class EngineRetornoExecutionResult {
	boolean available;
	int classification;
	String field;
	String observation;
	BigDecimal similarity;
	boolean valid;
	String validationSource;
	
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public int getClassification() {
		return classification;
	}
	public void setClassification(int classification) {
		this.classification = classification;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getObservation() {
		return observation;
	}
	public void setObservation(String observation) {
		this.observation = observation;
	}
	public BigDecimal getSimilarity() {
		return similarity;
	}
	public void setSimilarity(BigDecimal similarity) {
		this.similarity = similarity;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getValidationSource() {
		return validationSource;
	}
	public void setValidationSource(String validationSource) {
		this.validationSource = validationSource;
	}
	
	
}
