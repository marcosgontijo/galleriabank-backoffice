package com.webnowbr.siscoat.cobranca.db.model;

public class ContratoCobrancaLogsAlteracaoDetalhe {
	
	private long id;
	private String nomeCampo;
	private String valorBanco;
	private String valorAlterado;
	private ContratoCobrancaLogsAlteracao alteracao;
	
	public ContratoCobrancaLogsAlteracaoDetalhe(String nomeCampo, String valorBanco, String valorAlterado) {
		this.nomeCampo = nomeCampo;
		this.valorBanco = valorBanco;
		this.valorAlterado = valorAlterado;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNomeCampo() {
		return nomeCampo;
	}

	public void setNomeCampo(String nomeCampo) {
		this.nomeCampo = nomeCampo;
	}

	public String getValorBanco() {
		return valorBanco;
	}

	public void setValorBanco(String valorBanco) {
		this.valorBanco = valorBanco;
	}

	public String getValorAlterado() {
		return valorAlterado;
	}

	public void setValorAlterado(String valorAlterado) {
		this.valorAlterado = valorAlterado;
	}

	public ContratoCobrancaLogsAlteracao getAlteracao() {
		return alteracao;
	}

	public void setAlteracao(ContratoCobrancaLogsAlteracao alteracao) {
		this.alteracao = alteracao;
	}
}
