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
	
	public CcbProcessosJudiciais(BigDecimal valor) {
		this.valor = valor;
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
}
