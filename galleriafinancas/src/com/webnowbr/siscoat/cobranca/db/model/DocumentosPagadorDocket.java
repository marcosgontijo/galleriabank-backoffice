package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DocumentosPagadorDocket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5436895386346527628L;

	private long id;	
	private DocumentosDocket documentoDocket;
	private String estado;
	private String estadoId;
	private String cidade;
	private String cidadeId;
	
	public DocumentosPagadorDocket() {
		
	}
	
	public DocumentosPagadorDocket(DocumentosDocket doc) {
		this.documentoDocket = doc;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public DocumentosDocket getDocumentoDocket() {
		return documentoDocket;
	}
	public void setDocumentoDocket(DocumentosDocket documentoDocket) {
		this.documentoDocket = documentoDocket;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getEstadoId() {
		return estadoId;
	}
	public void setEstadoId(String estadoId) {
		this.estadoId = estadoId;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getCidadeId() {
		return cidadeId;
	}
	public void setCidadeId(String cidadeId) {
		this.cidadeId = cidadeId;
	}	
}
