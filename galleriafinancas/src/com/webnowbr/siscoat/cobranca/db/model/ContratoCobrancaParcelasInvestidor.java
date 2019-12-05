package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContratoCobrancaParcelasInvestidor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String numeroParcela;
	private Date dataVencimento;
	private BigDecimal parcelaMensal;
	private BigDecimal juros;
	private BigDecimal amortizacao;
	private BigDecimal saldoCredor;
	private BigDecimal saldoCredorAtualizado;	
	private BigDecimal irRetido;
	private BigDecimal valorLiquido;
	
	private boolean baixado;
	private Date dataBaixa;
	private BigDecimal valorBaixado;
	
	private PagadorRecebedor investidor;
	
	// atributos temporarios, sem persistir
	private boolean parcelaVencendo;
	private boolean parcelaVencida;
	
	public ContratoCobrancaParcelasInvestidor(){

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(String numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public BigDecimal getParcelaMensal() {
		return parcelaMensal;
	}

	public void setParcelaMensal(BigDecimal parcelaMensal) {
		this.parcelaMensal = parcelaMensal;
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

	public BigDecimal getSaldoCredor() {
		return saldoCredor;
	}

	public void setSaldoCredor(BigDecimal saldoCredor) {
		this.saldoCredor = saldoCredor;
	}

	public BigDecimal getIrRetido() {
		return irRetido;
	}

	public void setIrRetido(BigDecimal irRetido) {
		this.irRetido = irRetido;
	}

	public BigDecimal getValorLiquido() {
		return valorLiquido;
	}

	public void setValorLiquido(BigDecimal valorLiquido) {
		this.valorLiquido = valorLiquido;
	}

	public Date getDataBaixa() {
		return dataBaixa;
	}

	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}

	public BigDecimal getValorBaixado() {
		return valorBaixado;
	}

	public void setValorBaixado(BigDecimal valorBaixado) {
		this.valorBaixado = valorBaixado;
	}

	public boolean isBaixado() {
		return baixado;
	}

	public void setBaixado(boolean baixado) {
		this.baixado = baixado;
	}

	public BigDecimal getSaldoCredorAtualizado() {
		return saldoCredorAtualizado;
	}

	public void setSaldoCredorAtualizado(BigDecimal saldoCredorAtualizado) {
		this.saldoCredorAtualizado = saldoCredorAtualizado;
	}

	public PagadorRecebedor getInvestidor() {
		return investidor;
	}

	public void setInvestidor(PagadorRecebedor investidor) {
		this.investidor = investidor;
	}

	public boolean isParcelaVencendo() {
		return parcelaVencendo;
	}

	public void setParcelaVencendo(boolean parcelaVencendo) {
		this.parcelaVencendo = parcelaVencendo;
	}

	public boolean isParcelaVencida() {
		return parcelaVencida;
	}

	public void setParcelaVencida(boolean parcelaVencida) {
		this.parcelaVencida = parcelaVencida;
	}
}