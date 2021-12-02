package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;


public class PagadorRecebedorAdicionais implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PagadorRecebedor pessoa;
	private long id;
	private ContratoCobranca contratoCobranca;
	private String relacaoComTomador;
	
	
	
	public PagadorRecebedor getPessoa() {
		return pessoa;
	}

	public void setPessoa(PagadorRecebedor pessoa) {
		this.pessoa = pessoa;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public String getRelacaoComTomador() {
		return relacaoComTomador;
	}

	public void setRelacaoComTomador(String relacaoComTomador) {
		this.relacaoComTomador = relacaoComTomador;
	}

}


