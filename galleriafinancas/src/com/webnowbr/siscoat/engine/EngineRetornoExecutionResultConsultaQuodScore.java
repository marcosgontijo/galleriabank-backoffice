package com.webnowbr.siscoat.engine;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaQuodScore {
	
	@SerializedName("Score")
	int score;
	@SerializedName("Message")
	String message;
	@SerializedName("CreditRisk")
	String creditRisk;
	@SerializedName("ProbabilityOfPayment")
	int probabilityOfPayment;
    
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCreditRisk() {
		return creditRisk;
	}
	public void setCreditRisk(String creditRisk) {
		this.creditRisk = creditRisk;
	}
	public int getProbabilityOfPayment() {
		return probabilityOfPayment;
	}
	public void setProbabilityOfPayment(int probabilityOfPayment) {
		this.probabilityOfPayment = probabilityOfPayment;
	}
    
    

}
