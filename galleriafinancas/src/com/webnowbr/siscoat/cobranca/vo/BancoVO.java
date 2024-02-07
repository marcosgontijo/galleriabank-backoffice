package com.webnowbr.siscoat.cobranca.vo;

public class BancoVO {
	/** Código do banco */
	private Long id;
	
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

	/** Controle de domicilio bancario de cedentes */
	private Boolean flagControlaDomicilio;
	
	/** @see #codigoBanco */
	public Integer getCodigoBanco() {
		return codigoBanco;
	}

	/** @see #codigoBanco */
	public void setCodigoBanco(Integer codigoBanco) {
		this.codigoBanco = codigoBanco;
	}

	/** @see #nomeReduzido */
	public String getNomeReduzidoComCodigo() {
		return nomeReduzido + " - " + codigoBanco;
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

	public Boolean getFlagControlaDomicilio() {
		return flagControlaDomicilio;
	}

	public void setFlagControlaDomicilio(Boolean flagControlaDomicilio) {
		this.flagControlaDomicilio = flagControlaDomicilio;
	}



}
