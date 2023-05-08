package com.webnowbr.siscoat.engine;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaNegative {
	
	@SerializedName("DateLastApontamento")
	EngineRetornoExecutionResultConsultaData dateLastApontamento;
	@SerializedName("PendenciesControlCred")
	BigDecimal pendenciesControlCred;
	@SerializedName("Apontamentos")
	List<EngineRetornoExecutionResultConsultaAppointment> apontamentos;
	@SerializedName("Protests")
	EngineRetornoExecutionResultConsultaProtesto protests;
	
	@SerializedName("TotalApontamentos")
	int totalApontamentos;
	@SerializedName("TotalValorApontamentos")
	BigDecimal totalValorApontamentos;
	@SerializedName("TotalProtests")
	int totalProtests;
	@SerializedName("TotalValorProtests")
	BigDecimal totalValorProtests;
	public EngineRetornoExecutionResultConsultaData getDateLastApontamento() {
		return dateLastApontamento;
	}
	public void setDateLastApontamento(EngineRetornoExecutionResultConsultaData dateLastApontamento) {
		this.dateLastApontamento = dateLastApontamento;
	}
	public BigDecimal getPendenciesControlCred() {
		return pendenciesControlCred;
	}
	public void setPendenciesControlCred(BigDecimal pendenciesControlCred) {
		this.pendenciesControlCred = pendenciesControlCred;
	}
	public List<EngineRetornoExecutionResultConsultaAppointment> getApontamentos() {
		return apontamentos;
	}
	public void setApontamentos(List<EngineRetornoExecutionResultConsultaAppointment> apontamentos) {
		this.apontamentos = apontamentos;
	}
	public EngineRetornoExecutionResultConsultaProtesto getProtests() {
		return protests;
	}
	public void setProtests(EngineRetornoExecutionResultConsultaProtesto protests) {
		this.protests = protests;
	}
	public int getTotalApontamentos() {
		return totalApontamentos;
	}
	public void setTotalApontamentos(int totalApontamentos) {
		this.totalApontamentos = totalApontamentos;
	}
	public BigDecimal getTotalValorApontamentos() {
		return totalValorApontamentos;
	}
	public void setTotalValorApontamentos(BigDecimal totalValorApontamentos) {
		this.totalValorApontamentos = totalValorApontamentos;
	}
	public int getTotalProtests() {
		return totalProtests;
	}
	public void setTotalProtests(int totalProtests) {
		this.totalProtests = totalProtests;
	}
	public BigDecimal getTotalValorProtests() {
		return totalValorProtests;
	}
	public void setTotalValorProtests(BigDecimal totalValorProtests) {
		this.totalValorProtests = totalValorProtests;
	}


}
