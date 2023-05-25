package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;

public class DocumentoAnaliseResumo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5727133605918684738L;

	private String descricao;

	private String valor;
	private int numero;

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

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public Object setValor(int score) {
		// TODO Auto-generated method stub
		return this.valor = valor;
	}



}
