package com.webnowbr.siscoat.cobranca.db.model;

public class ContratoCobrancaLogsAlteracao {
	
	private String nomeCampo;
	private String valorBanco;
	private String valorAlterado;
	
	public ContratoCobrancaLogsAlteracao(String nomeCampo, String valorBanco, String valorAlterado) {
		this.nomeCampo = nomeCampo;
		this.valorBanco = valorBanco;
		this.valorAlterado = valorAlterado;
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
	
	
}
