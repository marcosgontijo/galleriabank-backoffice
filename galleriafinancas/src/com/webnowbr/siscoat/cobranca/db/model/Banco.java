package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;

public class Banco implements Serializable  {

	private long id;
	
	/** Código do banco */
	private Integer codigoBanco;

	/** nome do banco, ex: Bradesco, HSBC, etc */
	private String nomeReduzido;

	/** Nome completo do bancoo */
	private String nomeCompleto;

	/** Path Arquivo Licença DLL Cobre bem */
	private String nomeArquivoImpressaoBoleto;

	/** Base do CNPJ para migrar as agencias */
	private String cnpjBase;

	/** Endereço URL para acesso na conta cobrança */
	private String urlCobranca;

	/** Endereço URL para acesso na conta cobrança */
	private Boolean flagInativo;

	/** Dias uteis para envio de instrucao de protesto */
	private Integer bancNuDiasUteisEnvioProtesto;

	/** Controle de domicilio bancario de cedentes */
	private Boolean flagControlaDomicilio;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/** @see #codigoBanco */
	public Integer getCodigoBanco() {
		return codigoBanco;
	}

	/** @see #codigoBanco */
	public void setCodigoBanco(Integer codigoBanco) {
		this.codigoBanco = codigoBanco;
	}

	/** @see #nomeReduzido */
	public String getNomeReduzido() {
		return nomeReduzido;
	}

	/** @see #nomeReduzido */
	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}

	/** @see #nomeCompleto */
	public String getNomeCompleto() {
		return nomeCompleto;
	}

	/** @see #nomeCompleto */
	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	/** @see #nomeArquivoImpressaoBoleto */
	public String getNomeArquivoImpressaoBoleto() {
		return nomeArquivoImpressaoBoleto;
	}

	/** @see #nomeArquivoImpressaoBoleto */
	public void setNomeArquivoImpressaoBoleto(String nomeArquivoImpressaoBoleto) {
		this.nomeArquivoImpressaoBoleto = nomeArquivoImpressaoBoleto;
	}

	/** @see #cnpjBase */
	public String getCnpjBase() {
		return cnpjBase;
	}

	/** @see #cnpjBase */
	public void setCnpjBase(String cnpjBase) {
		this.cnpjBase = cnpjBase;
	}

	/** @see #urlCobranca */
	public String getUrlCobranca() {
		return urlCobranca;
	}

	/** @see #urlCobranca */
	public void setUrlCobranca(String urlCobranca) {
		this.urlCobranca = urlCobranca;
	}

	/** @return the flagInativo */
	public Boolean getFlagInativo() {
		return flagInativo;
	}

	/**
	 * @param flagInativo the flagInativo to set
	 */
	public void setFlagInativo(Boolean flagInativo) {
		this.flagInativo = flagInativo;
	}

	public Integer getBancNuDiasUteisEnvioProtesto() {
		return bancNuDiasUteisEnvioProtesto;
	}

	public void setBancNuDiasUteisEnvioProtesto(Integer bancNuDiasUteisEnvioProtesto) {
		this.bancNuDiasUteisEnvioProtesto = bancNuDiasUteisEnvioProtesto;
	}

	public Boolean getFlagControlaDomicilio() {
		return flagControlaDomicilio;
	}

	public void setFlagControlaDomicilio(Boolean flagControlaDomicilio) {
		this.flagControlaDomicilio = flagControlaDomicilio;
	}

}
