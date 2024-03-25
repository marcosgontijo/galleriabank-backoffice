package com.webnowbr.siscoat.cobranca.db.model;

import java.util.Set;

public class ContratoCobrancaLogsAlteracaoDetalhe {

	private long id;
	private String nomeCampo;
	private String nomeClasse;
	private String valorBanco;
	private String valorAlterado;
	private ContratoCobrancaLogsAlteracao logsalteracao;
	//nao colocar no hbm somente transiente
	private long ordem;
	
	public ContratoCobrancaLogsAlteracaoDetalhe(String nomeCampo, String nomeClasse, String valorBanco,
			String valorAlterado, ContratoCobrancaLogsAlteracao alteracao,long ordem) {
		this.nomeCampo = nomeCampo;
		this.setNomeClasse(nomeClasse);
		this.valorBanco = valorBanco;
		this.valorAlterado = valorAlterado;
		this.logsalteracao = alteracao;
		this.ordem= ordem;
	}
	
	public ContratoCobrancaLogsAlteracaoDetalhe() {
		
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

	

	public ContratoCobrancaLogsAlteracao getLogsalteracao() {
		return logsalteracao;
	}

	public void setLogsalteracao(ContratoCobrancaLogsAlteracao logsalteracao) {
		this.logsalteracao = logsalteracao;
	}

	public String getNomeClasse() {
		return nomeClasse;
	}

	public void setNomeClasse(String nomeClasse) {
		this.nomeClasse = nomeClasse;
	}

	public long getOrdem() {
		return ordem;
	}

	public void setOrdem(long ordem) {
		this.ordem = ordem;
	}
	
}
