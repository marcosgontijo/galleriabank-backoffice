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
	private boolean pf;
	private boolean pj;
	private String estados;
	private String etapa;
	private String obs;
	
	@Override
	public String toString() {
		return "DocumentosDocket [id=" + id + ", documentoNome=" + documentoNome + "]";
	}
	
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
	public boolean isPf() {
		return pf;
	}
	public void setPf(boolean pf) {
		this.pf = pf;
	}
	public boolean isPj() {
		return pj;
	}
	public void setPj(boolean pj) {
		this.pj = pj;
	}
	public String getEstados() {
		return estados;
	}
	public void setEstados(String estados) {
		this.estados = estados;
	}
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}	
}
