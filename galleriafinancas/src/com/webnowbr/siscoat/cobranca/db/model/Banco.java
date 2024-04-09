package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;

public class Banco implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	
	/** Código do banco */
	private int codigoBanco;

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
	private boolean flagInativo;

	/** Dias uteis para envio de instrucao de protesto */
	private int bancNuDiasUteisEnvioProtesto;

	/** Controle de domicilio bancario de cedentes */
	private boolean flagControlaDomicilio;
	
	private String isbp;

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCodigoBanco() {
		return codigoBanco;
	}

	public void setCodigoBanco(int codigoBanco) {
		this.codigoBanco = codigoBanco;
	}

	public String getNomeReduzido() {
		return nomeReduzido;
	}

	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public String getNomeArquivoImpressaoBoleto() {
		return nomeArquivoImpressaoBoleto;
	}

	public void setNomeArquivoImpressaoBoleto(String nomeArquivoImpressaoBoleto) {
		this.nomeArquivoImpressaoBoleto = nomeArquivoImpressaoBoleto;
	}

	public String getCnpjBase() {
		return cnpjBase;
	}

	public void setCnpjBase(String cnpjBase) {
		this.cnpjBase = cnpjBase;
	}

	public String getUrlCobranca() {
		return urlCobranca;
	}

	public void setUrlCobranca(String urlCobranca) {
		this.urlCobranca = urlCobranca;
	}

	public boolean isFlagInativo() {
		return flagInativo;
	}

	public void setFlagInativo(boolean flagInativo) {
		this.flagInativo = flagInativo;
	}

	public int getBancNuDiasUteisEnvioProtesto() {
		return bancNuDiasUteisEnvioProtesto;
	}

	public void setBancNuDiasUteisEnvioProtesto(int bancNuDiasUteisEnvioProtesto) {
		this.bancNuDiasUteisEnvioProtesto = bancNuDiasUteisEnvioProtesto;
	}

	public boolean isFlagControlaDomicilio() {
		return flagControlaDomicilio;
	}

	public void setFlagControlaDomicilio(boolean flagControlaDomicilio) {
		this.flagControlaDomicilio = flagControlaDomicilio;
	}

	public String getIsbp() {
		return isbp;
	}

	public void setIsbp(String isbp) {
		this.isbp = isbp;
	}
}
