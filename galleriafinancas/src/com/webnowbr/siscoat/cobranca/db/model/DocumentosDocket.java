package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DocumentosDocket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5436895386346527628L;

	private long id;	
	private String documentKitId;
	private String produtoId;
	private String documentoNome;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDocumentKitId() {
		return documentKitId;
	}
	public void setDocumentKitId(String documentKitId) {
		this.documentKitId = documentKitId;
	}
	public String getProdutoId() {
		return produtoId;
	}
	public void setProdutoId(String produtoId) {
		this.produtoId = produtoId;
	}
	public String getDocumentoNome() {
		return documentoNome;
	}
	public void setDocumentoNome(String documentoNome) {
		this.documentoNome = documentoNome;
	}
	
	
}
