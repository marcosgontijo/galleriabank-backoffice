package com.webnowbr.siscoat.omie.response;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class OmieListarExtratoResponse { 
	private BigInteger nCodCC;
	private String cCodIntCC;
	private String nCodAgencia;
	private String nCodBanco;
	private String nNumConta;
	private String cDescricao;
	private String cCodTipo;
	private String cDesTipo;
	private String cFluxoCaixa;
	private String cResumoExecutivo;
	private String dPeriodoInicial;
	private String dPeriodoFinal;
	private BigDecimal nSaldoAnterior;	
	private BigDecimal nSaldoAtual;	
	private BigDecimal nSaldoConciliado;	
	private BigDecimal nSaldoProvisorio;	
	private BigDecimal nLimiteCreditoTotal;	
	private BigDecimal nSaldoDisponivel;
	
	private List<OmieListaMovimentos> listaMovimentos;

	public BigInteger getnCodCC() {
		return nCodCC;
	}

	public void setnCodCC(BigInteger nCodCC) {
		this.nCodCC = nCodCC;
	}

	public String getcCodIntCC() {
		return cCodIntCC;
	}

	public void setcCodIntCC(String cCodIntCC) {
		this.cCodIntCC = cCodIntCC;
	}

	public String getnCodAgencia() {
		return nCodAgencia;
	}

	public void setnCodAgencia(String nCodAgencia) {
		this.nCodAgencia = nCodAgencia;
	}

	public String getnCodBanco() {
		return nCodBanco;
	}

	public void setnCodBanco(String nCodBanco) {
		this.nCodBanco = nCodBanco;
	}

	public String getnNumConta() {
		return nNumConta;
	}

	public void setnNumConta(String nNumConta) {
		this.nNumConta = nNumConta;
	}

	public String getcDescricao() {
		return cDescricao;
	}

	public void setcDescricao(String cDescricao) {
		this.cDescricao = cDescricao;
	}

	public String getcCodTipo() {
		return cCodTipo;
	}

	public void setcCodTipo(String cCodTipo) {
		this.cCodTipo = cCodTipo;
	}

	public String getcDesTipo() {
		return cDesTipo;
	}

	public void setcDesTipo(String cDesTipo) {
		this.cDesTipo = cDesTipo;
	}

	public String getcFluxoCaixa() {
		return cFluxoCaixa;
	}

	public void setcFluxoCaixa(String cFluxoCaixa) {
		this.cFluxoCaixa = cFluxoCaixa;
	}

	public String getcResumoExecutivo() {
		return cResumoExecutivo;
	}

	public void setcResumoExecutivo(String cResumoExecutivo) {
		this.cResumoExecutivo = cResumoExecutivo;
	}

	public String getdPeriodoInicial() {
		return dPeriodoInicial;
	}

	public void setdPeriodoInicial(String dPeriodoInicial) {
		this.dPeriodoInicial = dPeriodoInicial;
	}

	public String getdPeriodoFinal() {
		return dPeriodoFinal;
	}

	public void setdPeriodoFinal(String dPeriodoFinal) {
		this.dPeriodoFinal = dPeriodoFinal;
	}

	public BigDecimal getnSaldoAnterior() {
		return nSaldoAnterior;
	}

	public void setnSaldoAnterior(BigDecimal nSaldoAnterior) {
		this.nSaldoAnterior = nSaldoAnterior;
	}

	public BigDecimal getnSaldoAtual() {
		return nSaldoAtual;
	}

	public void setnSaldoAtual(BigDecimal nSaldoAtual) {
		this.nSaldoAtual = nSaldoAtual;
	}

	public BigDecimal getnSaldoConciliado() {
		return nSaldoConciliado;
	}

	public void setnSaldoConciliado(BigDecimal nSaldoConciliado) {
		this.nSaldoConciliado = nSaldoConciliado;
	}

	public BigDecimal getnSaldoProvisorio() {
		return nSaldoProvisorio;
	}

	public void setnSaldoProvisorio(BigDecimal nSaldoProvisorio) {
		this.nSaldoProvisorio = nSaldoProvisorio;
	}

	public BigDecimal getnLimiteCreditoTotal() {
		return nLimiteCreditoTotal;
	}

	public void setnLimiteCreditoTotal(BigDecimal nLimiteCreditoTotal) {
		this.nLimiteCreditoTotal = nLimiteCreditoTotal;
	}

	public BigDecimal getnSaldoDisponivel() {
		return nSaldoDisponivel;
	}

	public void setnSaldoDisponivel(BigDecimal nSaldoDisponivel) {
		this.nSaldoDisponivel = nSaldoDisponivel;
	}

	public List<OmieListaMovimentos> getListaMovimentos() {
		return listaMovimentos;
	}

	public void setListaMovimentos(List<OmieListaMovimentos> listaMovimentos) {
		this.listaMovimentos = listaMovimentos;
	}

	
}
