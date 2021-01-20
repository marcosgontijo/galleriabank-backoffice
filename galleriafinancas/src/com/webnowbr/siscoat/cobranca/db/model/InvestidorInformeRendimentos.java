package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvestidorInformeRendimentos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int indice;
	private BigDecimal saldoAnoAnterior;
	private BigDecimal saldoAnoAtual;
	private BigDecimal irRetido;
	private BigDecimal juros;
	
	private String numeroContrato;
	
	private String empresa;
	private String cnpj;
	
	public InvestidorInformeRendimentos(){

	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public BigDecimal getSaldoAnoAnterior() {
		return saldoAnoAnterior;
	}

	public void setSaldoAnoAnterior(BigDecimal saldoAnoAnterior) {
		this.saldoAnoAnterior = saldoAnoAnterior;
	}

	public BigDecimal getSaldoAnoAtual() {
		return saldoAnoAtual;
	}

	public void setSaldoAnoAtual(BigDecimal saldoAnoAtual) {
		this.saldoAnoAtual = saldoAnoAtual;
	}

	public BigDecimal getIrRetido() {
		return irRetido;
	}

	public void setIrRetido(BigDecimal irRetido) {
		this.irRetido = irRetido;
	}

	public BigDecimal getJuros() {
		return juros;
	}

	public void setJuros(BigDecimal juros) {
		this.juros = juros;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
}