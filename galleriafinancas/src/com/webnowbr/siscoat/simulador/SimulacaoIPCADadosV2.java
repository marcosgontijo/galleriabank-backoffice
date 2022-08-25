package com.webnowbr.siscoat.simulador;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class SimulacaoIPCADadosV2 {
	private BigInteger numeroParcela;
	private BigDecimal amortizacao;
	private BigDecimal juros;
	private BigDecimal seguroMIP;
	private BigDecimal seguroDFI;
	private BigDecimal valorParcela;
	private BigDecimal ipca;
	private BigDecimal saldoDevedorInicial;
	private BigDecimal saldoDevedorFinal;
	private BigDecimal taxaIPCA;
	
	private BigDecimal taxaADM;
	
	private Date dataReferencia;

	public SimulacaoIPCADadosV2() {
		super();
	}

	public BigInteger getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(BigInteger numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public BigDecimal getAmortizacao() {
		return amortizacao;
	}

	public void setAmortizacao(BigDecimal amortizacao) {
		this.amortizacao = amortizacao;
	}

	public BigDecimal getJuros() {
		return juros;
	}

	public void setJuros(BigDecimal juros) {
		this.juros = juros;
	}

	public BigDecimal getSeguroMIP() {
		return seguroMIP;
	}

	public void setSeguroMIP(BigDecimal seguroMIP) {
		this.seguroMIP = seguroMIP;
	}

	public BigDecimal getSeguroDFI() {
		return seguroDFI;
	}

	public void setSeguroDFI(BigDecimal seguroDFI) {
		this.seguroDFI = seguroDFI;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public BigDecimal getSaldoDevedorInicial() {
		return saldoDevedorInicial;
	}

	public void setSaldoDevedorInicial(BigDecimal saldoDevedorInicial) {
		this.saldoDevedorInicial = saldoDevedorInicial;
	}

	public BigDecimal getSaldoDevedorFinal() {
		return saldoDevedorFinal;
	}

	public void setSaldoDevedorFinal(BigDecimal saldoDevedorFinal) {
		this.saldoDevedorFinal = saldoDevedorFinal;
	}

	public BigDecimal getIpca() {
		return ipca;
	}

	public void setIpca(BigDecimal ipca) {
		this.ipca = ipca;
	}

	public Date getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(Date dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

	public BigDecimal getTaxaIPCA() {
		return taxaIPCA;
	}

	public void setTaxaIPCA(BigDecimal taxaIPCA) {
		this.taxaIPCA = taxaIPCA;
	}

	public BigDecimal getTaxaADM() {
		return taxaADM;
	}

	public void setTaxaADM(BigDecimal taxaADM) {
		this.taxaADM = taxaADM;
	}
}
