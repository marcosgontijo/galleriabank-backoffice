package com.webnowbr.siscoat.simulador;

import java.math.BigDecimal;

import java.math.BigInteger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
/** ManagedBean. */
@ManagedBean(name = "simulacaoIPCAv2MB")
@SessionScoped
public class SimulacaoIPCAv2MB {
	private BigInteger numeroParcela;
	private BigDecimal amortizacao;
	private BigDecimal juros;
	private BigDecimal seguroMIP;
	private BigDecimal seguroDFI;
	private BigDecimal valorParcela;
	private BigDecimal saldoDevedorInicial;
	private BigDecimal saldoDevedorFinal;

	private BigDecimal valorIOF;
	
	private BigDecimal txAdm;
	

	public SimulacaoIPCAv2MB() {
		super();
	}

	public SimulacaoIPCAv2MB(BigDecimal saldoDevedorInicial) {
		super();
		this.numeroParcela = BigInteger.ZERO;
		this.valorParcela = BigDecimal.ZERO;
		this.juros = BigDecimal.ZERO;
		this.amortizacao = BigDecimal.ZERO;
		this.valorParcela = BigDecimal.ZERO;
		this.seguroDFI = BigDecimal.ZERO;
		this.seguroMIP = BigDecimal.ZERO;
		this.txAdm = BigDecimal.ZERO;
		this.saldoDevedorInicial = saldoDevedorInicial;
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

	public BigDecimal getValorIOF() {
		return valorIOF;
	}

	public void setValorIOF(BigDecimal valorIOF) {
		this.valorIOF = valorIOF;
	}

	public BigDecimal getTxAdm() {
		return txAdm;
	}

	public void setTxAdm(BigDecimal txAdm) {
		this.txAdm = txAdm;
	}

//	public BigDecimal getValorIOFAdicional() {
//		return valorIOFAdicional;
//	}
//
//	public void setValorIOFAdicional(BigDecimal valorIOFAdicional) {
//		this.valorIOFAdicional = valorIOFAdicional;
//	}

	
}