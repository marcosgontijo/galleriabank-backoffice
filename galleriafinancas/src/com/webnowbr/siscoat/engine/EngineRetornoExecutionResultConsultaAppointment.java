package com.webnowbr.siscoat.engine;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaAppointment {
	

	@SerializedName("CNPJ")
	String  cNPJ;
	@SerializedName("CompanyName")
	String  companyName;
	@SerializedName("Nature")
	String  nature;
	@SerializedName("Amount")
    BigDecimal amount;
	@SerializedName("ContractNumber")
    String  contractNumber;
	@SerializedName("DateOccurred")
    EngineRetornoExecutionResultConsultaData dateOccurred;
	@SerializedName("ParticipantType")
    String  participantType;
	@SerializedName("ApontamentoStatus")
    String  apontamentoStatus;
	@SerializedName("DateIncluded")
    EngineRetornoExecutionResultConsultaData dateIncluded;
	@SerializedName("Address")
    EngineRetornoExecutionResultConsultaAddress address;    


	public String getcNPJ() {
		return cNPJ;
	}

	public void setcNPJ(String cNPJ) {
		this.cNPJ = cNPJ;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public EngineRetornoExecutionResultConsultaData getDateOccurred() {
		return dateOccurred;
	}

	public void setDateOccurred(EngineRetornoExecutionResultConsultaData dateOccurred) {
		this.dateOccurred = dateOccurred;
	}

	public String getParticipantType() {
		return participantType;
	}

	public void setParticipantType(String participantType) {
		this.participantType = participantType;
	}

	public String getApontamentoStatus() {
		return apontamentoStatus;
	}

	public void setApontamentoStatus(String apontamentoStatus) {
		this.apontamentoStatus = apontamentoStatus;
	}

	public EngineRetornoExecutionResultConsultaData getDateIncluded() {
		return dateIncluded;
	}

	public void setDateIncluded(EngineRetornoExecutionResultConsultaData dateIncluded) {
		this.dateIncluded = dateIncluded;
	}

	public EngineRetornoExecutionResultConsultaAddress getAddress() {
		return address;
	}

	public void setAddress(EngineRetornoExecutionResultConsultaAddress address) {
		this.address = address;
	}


}
