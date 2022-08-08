package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CcbProcessosJudiciais implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	
	private BigDecimal valor = BigDecimal.ZERO;
	private String numero = "";
	private boolean processoInseridoContrato;
	
	public CcbProcessosJudiciais(BigDecimal valor, String numero) {
		this.valor = valor;
		this.numero = numero;
	}
	
	public CcbProcessosJudiciais() {
		
	}
	
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public boolean isProcessoInseridoContrato() {
		return processoInseridoContrato;
	}

	public void setProcessoInseridoContrato(boolean processoInseridoContrato) {
		this.processoInseridoContrato = processoInseridoContrato;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	
}
