package com.webnowbr.siscoat.cobranca.model.bmpdigital;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ResumoModalidade {

	@SerializedName("tipo")
	private String tipo;

	@SerializedName("modalidade")
	private String modalidade;

	@SerializedName("dominio")
	private String dominio;

	@SerializedName("subdominio")
	private String subdominio;

	@SerializedName("valorVencimento")
	private BigDecimal valorVencimento;
	
	public ResumoModalidade() {
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public String getDominio() {
		return dominio;
	}

	public void setDominio(String dominio) {
		this.dominio = dominio;
	}

	public String getSubdominio() {
		return subdominio;
	}

	public void setSubdominio(String subdominio) {
		this.subdominio = subdominio;
	}

	public BigDecimal getValorVencimento() {
		return valorVencimento;
	}

	public void setValorVencimento(BigDecimal valorVencimento) {
		this.valorVencimento = valorVencimento;
	}
}
