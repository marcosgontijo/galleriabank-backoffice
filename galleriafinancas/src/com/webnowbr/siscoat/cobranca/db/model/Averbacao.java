package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class Averbacao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3364649308267521304L;

	private long id;
	
	private BigDecimal valor;
	private String descricao;
	private ContratoCobranca contratoCobranca;
	
	public Averbacao() {
		
	}

	public Averbacao(BigDecimal valor) {
		super();
		this.valor = valor;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}	
}
