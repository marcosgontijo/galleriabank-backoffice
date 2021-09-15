package com.webnowbr.siscoat.cobranca.model.bmpdigital;

import java.math.BigDecimal;

public class ResumoModalidade {

	private String tipo;
	private String modalidade;
	private String dominio;
	private String subdominio;
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
