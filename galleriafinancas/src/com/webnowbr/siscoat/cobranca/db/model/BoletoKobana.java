package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BoletoKobana implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5436895386346527628L;

	private long id;	
	private Date expireAt;
	private Date paidAt;
	private Date createdAt;
	private String status;
	private String customerPersonName;
	private String customerPersonCNPJCPF;
	private String customerEmail;
	private BigDecimal paidAmount;
	private String urlBoleto;
	private String beneficiaryName;
	private String documentNumber;
	private String description;
	
	private ContratoCobranca contrato;
	private ContratoCobrancaDetalhes parcela;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getExpireAt() {
		return expireAt;
	}
	public void setExpireAt(Date expireAt) {
		this.expireAt = expireAt;
	}
	public Date getPaidAt() {
		return paidAt;
	}
	public void setPaidAt(Date paidAt) {
		this.paidAt = paidAt;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCustomerPersonName() {
		return customerPersonName;
	}
	public void setCustomerPersonName(String customerPersonName) {
		this.customerPersonName = customerPersonName;
	}
	public String getCustomerPersonCNPJCPF() {
		return customerPersonCNPJCPF;
	}
	public void setCustomerPersonCNPJCPF(String customerPersonCNPJCPF) {
		this.customerPersonCNPJCPF = customerPersonCNPJCPF;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}
	public String getUrlBoleto() {
		return urlBoleto;
	}
	public void setUrlBoleto(String urlBoleto) {
		this.urlBoleto = urlBoleto;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ContratoCobranca getContrato() {
		return contrato;
	}
	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}
	public ContratoCobrancaDetalhes getParcela() {
		return parcela;
	}
	public void setParcela(ContratoCobrancaDetalhes parcela) {
		this.parcela = parcela;
	}
}
