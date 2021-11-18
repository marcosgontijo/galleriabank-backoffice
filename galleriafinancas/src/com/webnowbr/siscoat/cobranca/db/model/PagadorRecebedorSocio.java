package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;


public class PagadorRecebedorSocio implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6223346147907743099L;
	private PagadorRecebedor pessoa;
	private long id;
	private ContratoCobranca contratoCobranca;
	
	
	
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

}


