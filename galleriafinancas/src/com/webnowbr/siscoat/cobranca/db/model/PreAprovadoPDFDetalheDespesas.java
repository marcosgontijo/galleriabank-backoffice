package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.webnowbr.siscoat.common.CommonsUtil;

public class PreAprovadoPDFDetalheDespesas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String descricao;
	private String valor;
	
	
	
	public PreAprovadoPDFDetalheDespesas() {
		super();
	}
	public PreAprovadoPDFDetalheDespesas(String descricao, String valor) {
		super();
		this.descricao = descricao;
		this.valor = valor;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
