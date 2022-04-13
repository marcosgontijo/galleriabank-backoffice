package com.webnowbr.siscoat.visaoGeral;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class VisaoGeralGrupoDetalhe {

	long idDetalhes;
	long idContratoCobranca;
	String numeroContrato;
	String nome;
	BigDecimal valor;

	public long getIdDetalhes() {
		return idDetalhes;
	}
	public void setIdDetalhes(long idDetalhes) {
		this.idDetalhes = idDetalhes;
	}
	public long getIdContratoCobranca() {
		return idContratoCobranca;
	}
	public void setIdContratoCobranca(long idContratoCobranca) {
		this.idContratoCobranca = idContratoCobranca;
	}
	public String getNumeroContrato() {
		return numeroContrato;
	}
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

}
