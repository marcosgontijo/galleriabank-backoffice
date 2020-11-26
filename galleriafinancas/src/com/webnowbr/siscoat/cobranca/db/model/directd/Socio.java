package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Socio implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String nomeNomeEmpresarial;
	private String qualificacao;
	private String qualifRepLegal;
	private String nomeRepresLegal;	
	private boolean representanteLegal;
	private String docSocio;	
	
	public Socio() {
		
	}

	public String getNomeNomeEmpresarial() {
		return nomeNomeEmpresarial;
	}

	public void setNomeNomeEmpresarial(String nomeNomeEmpresarial) {
		this.nomeNomeEmpresarial = nomeNomeEmpresarial;
	}

	public String getQualificacao() {
		return qualificacao;
	}

	public void setQualificacao(String qualificacao) {
		this.qualificacao = qualificacao;
	}

	public String getQualifRepLegal() {
		return qualifRepLegal;
	}

	public void setQualifRepLegal(String qualifRepLegal) {
		this.qualifRepLegal = qualifRepLegal;
	}

	public String getNomeRepresLegal() {
		return nomeRepresLegal;
	}

	public void setNomeRepresLegal(String nomeRepresLegal) {
		this.nomeRepresLegal = nomeRepresLegal;
	}

	public boolean isRepresentanteLegal() {
		return representanteLegal;
	}

	public void setRepresentanteLegal(boolean representanteLegal) {
		this.representanteLegal = representanteLegal;
	}

	public String getDocSocio() {
		return docSocio;
	}

	public void setDocSocio(String docSocio) {
		this.docSocio = docSocio;
	}
}