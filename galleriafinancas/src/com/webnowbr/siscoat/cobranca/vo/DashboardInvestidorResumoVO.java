package com.webnowbr.siscoat.cobranca.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DashboardInvestidorResumoVO {

	private String situacaoContrato;
	private BigInteger qtdeContratos;
	private BigInteger parcelasAbertas;
	private BigDecimal valorReceber;
	private BigDecimal valorRecebido;
	private BigDecimal valorInvestido;

	public void adicionaQtdeContratos() {
		qtdeContratos = qtdeContratos.add(BigInteger.ONE);
	}

	public void addValorInvestido(BigDecimal valor) {
		valorInvestido = valorInvestido.add(valor);
	}

	public void addValorReceber(BigDecimal valor) {
		valorReceber = valorReceber.add(valor);
	}

	public void addValorRecebido(BigDecimal valor) {
		valorRecebido = valorRecebido.add(valor);
	}

	public void addParcelasAbertas(BigInteger valor) {
		parcelasAbertas = parcelasAbertas.add( valor);
	}

	public String getSituacaoContrato() {
		return situacaoContrato;
	}

	public void setSituacaoContrato(String situacaoContrato) {
		this.situacaoContrato = situacaoContrato;
	}

	public BigInteger getQtdeContratos() {
		return qtdeContratos;
	}

	public void setQtdeContratos(BigInteger qtdeContratos) {
		this.qtdeContratos = qtdeContratos;
	}

	public BigInteger getParcelasAbertas() {
		return parcelasAbertas;
	}

	public void setParcelasAbertas(BigInteger parcelasAbertas) {
		this.parcelasAbertas = parcelasAbertas;
	}

	public BigDecimal getValorReceber() {
		return valorReceber;
	}

	public void setValorReceber(BigDecimal valorReceber) {
		this.valorReceber = valorReceber;
	}

	public BigDecimal getValorRecebido() {
		return valorRecebido;
	}

	public void setValorRecebido(BigDecimal valorRecebido) {
		this.valorRecebido = valorRecebido;
	}

	public BigDecimal getValorInvestido() {
		return valorInvestido;
	}

	public void setValorInvestido(BigDecimal valorInvestido) {
		this.valorInvestido = valorInvestido;
	}

}
