package com.webnowbr.siscoat.cobranca.db.model;

public class PesquisaObservacoes {

	private ContratoCobrancaObservacoes contratoCobrancaObservacoes;
	private long idContratoCobranca;
	private String numContrato;
	
	public PesquisaObservacoes(ContratoCobrancaObservacoes contratoCobrancaObservacoes, long idContratoCobranca, String numContrato) {
		this.contratoCobrancaObservacoes = contratoCobrancaObservacoes;
		this.idContratoCobranca = idContratoCobranca;
		this.numContrato = numContrato;
	}

	public ContratoCobrancaObservacoes getContratoCobrancaObservacoes() {
		return contratoCobrancaObservacoes;
	}

	public void setContratoCobrancaObservacoes(ContratoCobrancaObservacoes contratoCobrancaObservacoes) {
		this.contratoCobrancaObservacoes = contratoCobrancaObservacoes;
	}

	public long getIdContratoCobranca() {
		return idContratoCobranca;
	}

	public void setIdContratoCobranca(long idContratoCobranca) {
		this.idContratoCobranca = idContratoCobranca;
	}

	public String getNumContrato() {
		return numContrato;
	}

	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}
}
