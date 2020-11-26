package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Telefone implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * EXEMPLO DE MENSAGEM
	    "Telefones": [
	      {
	        "TelefoneComDDD": "(19) 89898989",
	        "TelemarketingBloqueado": null,
	        "TelemarketingUltBloqDesb": null,
	        "Operadora": null,
	        "UltimaAtualizacao": null
	      },
	*****
	*****/
	
	private String telefoneComDDD;
	private boolean telemarketingBloqueado;
	private boolean telemarketingUltBloqDesb;
	private String operadora;
	private Date ultimaAtualizacao;
	
	public Telefone() {
		
	}

	public String getTelefoneComDDD() {
		return telefoneComDDD;
	}

	public void setTelefoneComDDD(String telefoneComDDD) {
		this.telefoneComDDD = telefoneComDDD;
	}

	public boolean isTelemarketingBloqueado() {
		return telemarketingBloqueado;
	}

	public void setTelemarketingBloqueado(boolean telemarketingBloqueado) {
		this.telemarketingBloqueado = telemarketingBloqueado;
	}

	public boolean isTelemarketingUltBloqDesb() {
		return telemarketingUltBloqDesb;
	}

	public void setTelemarketingUltBloqDesb(boolean telemarketingUltBloqDesb) {
		this.telemarketingUltBloqDesb = telemarketingUltBloqDesb;
	}

	public String getOperadora() {
		return operadora;
	}

	public void setOperadora(String operadora) {
		this.operadora = operadora;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}
}