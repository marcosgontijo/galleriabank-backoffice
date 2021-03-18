package com.webnowbr.siscoat.cobranca.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class DemonstrativoResultadosGrupoDetalhe {

	long idDetalhes;
	long idContratoCobranca;
	String numeroContrato;
	String nome;
	Integer numeroParcela;
	Date dataVencimento;
	BigDecimal valor;
	BigDecimal juros;
	BigDecimal amortizacao;
	long idContaContabil;
	String nomeContaContabil;
	long idContaContabilPai;
	String nomeContaContabilPai;
	
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
	public Integer getNumeroParcela() {
		return numeroParcela;
	}
	public void setNumeroParcela(Integer numeroParcela) {
		this.numeroParcela = numeroParcela;
	}
	public Date getDataVencimento() {
		return dataVencimento;
	}
	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public BigDecimal getJuros() {
		return juros;
	}
	public void setJuros(BigDecimal juros) {
		this.juros = juros;
	}
	public BigDecimal getAmortizacao() {
		return amortizacao;
	}
	public void setAmortizacao(BigDecimal amortizacao) {
		this.amortizacao = amortizacao;
	}
	public long getIdContaContabil() {
		return idContaContabil;
	}
	public void setIdContaContabil(long idContaContabil) {
		this.idContaContabil = idContaContabil;
	}
	public String getNomeContaContabil() {
		return nomeContaContabil;
	}
	public void setNomeContaContabil(String nomeContaContabil) {
		this.nomeContaContabil = nomeContaContabil;
	}
	public long getIdContaContabilPai() {
		return idContaContabilPai;
	}
	public void setIdContaContabilPai(long idContaContabilPai) {
		this.idContaContabilPai = idContaContabilPai;
	}
	public String getNomeContaContabilPai() {
		return nomeContaContabilPai;
	}
	public void setNomeContaContabilPai(String nomeContaContabilPai) {
		this.nomeContaContabilPai = nomeContaContabilPai;
	}

	

}
