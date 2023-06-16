package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;

public class ContratoTipoTemplateCampo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1651831367097660421L;

	private long id;

	private String tag;

	private String expressao;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getExpressao() {
		return expressao;
	}

	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}

}
