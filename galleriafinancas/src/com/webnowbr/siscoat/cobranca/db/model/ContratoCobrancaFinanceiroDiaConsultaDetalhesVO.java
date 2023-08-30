package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;

public class ContratoCobrancaFinanceiroDiaConsultaDetalhesVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9037801433784636356L;

	private long id;
	private long contratoCobranca;
	private boolean parcelaPaga;
	private boolean parcelaVencida;
	private boolean parcelaVencendo;
	private String numeroParcela;
	private Date dataVencimento;
	private Date dataVencimentoAtual;
	private Date dataPagamento;
	private Date dataUltimoPagamento;
	private BigDecimal valorTotalPagamento;
	private BigDecimal vlrParcela;
	private BigDecimal vlrParcelaOriginal;
	private BigDecimal vlrJurosParcela;
	private BigDecimal vlrAmortizacaoParcela;
	private BigDecimal vlrSeguroDFI;
	private BigDecimal vlrSeguroMIP;
	private BigDecimal vlrTaxaADM;
	private BigDecimal vlrSaldoParcela;
	private BigDecimal vlrJuros;
	private BigDecimal txMulta;
	private BigDecimal vlrParcelaAtualizada;


	private List<ContratoCobrancaFinanceiroDiaConsultaDetalhesParcialVO> listContratoCobrancaDetalhesParcial;
	
	
	public ContratoCobrancaFinanceiroDiaConsultaDetalhesVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ContratoCobrancaFinanceiroDiaConsultaDetalhesVO(long id, long contratoCobranca, String numeroParcela,
			boolean parcelaPaga, Date dataVencimento, Date dataPagamento, BigDecimal valorTotalPagamento,
			BigDecimal vlrParcela, BigDecimal vlrParcelaOriginal, BigDecimal vlrJurosParcela,
			BigDecimal vlrAmortizacaoParcela, BigDecimal vlrSeguroDFI,BigDecimal vlrSeguroMIP,
			BigDecimal vlrTaxaADM, BigDecimal vlrSaldoParcela,
			BigDecimal vlrJuros,
			BigDecimal txMulta, List<ContratoCobrancaFinanceiroDiaConsultaDetalhesParcialVO> listContratoCobrancaDetalhesParcial) {
		super();
		this.id = id;
		this.contratoCobranca = contratoCobranca;
		this.numeroParcela = numeroParcela;
		this.parcelaPaga = parcelaPaga;
		this.dataVencimento = dataVencimento;
		this.dataPagamento = dataPagamento;
		this.valorTotalPagamento = valorTotalPagamento;
		this.vlrParcela = vlrParcela;
		this.vlrParcelaOriginal = vlrParcelaOriginal;
		this.vlrJurosParcela = vlrJurosParcela;
		this.vlrAmortizacaoParcela = vlrAmortizacaoParcela;
		this.vlrSeguroDFI = vlrSeguroDFI;
		this.vlrSeguroMIP = vlrSeguroMIP;
		this.vlrTaxaADM = vlrTaxaADM;
		this.vlrSaldoParcela = vlrSaldoParcela;
		this.vlrJuros = vlrJuros;
		this.txMulta = txMulta;
		this.listContratoCobrancaDetalhesParcial = listContratoCobrancaDetalhesParcial;
	}

	public boolean isAmortizacao() {
		return CommonsUtil.mesmoValor("Amortização", this.getNumeroParcela());
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(long contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public boolean isParcelaPaga() {
		return parcelaPaga;
	}

	public void setParcelaPaga(boolean parcelaPaga) {
		this.parcelaPaga = parcelaPaga;
	}

	public boolean isParcelaVencida() {
		return parcelaVencida;
	}

	public void setParcelaVencida(boolean parcelaVencida) {
		this.parcelaVencida = parcelaVencida;
	}

	public boolean isParcelaVencendo() {
		return parcelaVencendo;
	}

	public void setParcelaVencendo(boolean parcelaVencendo) {
		this.parcelaVencendo = parcelaVencendo;
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

	public Date getDataVencimentoAtual() {
		return dataVencimentoAtual;
	}

	public void setDataVencimentoAtual(Date dataVencimentoAtual) {
		this.dataVencimentoAtual = dataVencimentoAtual;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public Date getDataUltimoPagamento() {
		return dataUltimoPagamento;
	}

	public void setDataUltimoPagamento(Date dataUltimoPagamento) {
		this.dataUltimoPagamento = dataUltimoPagamento;
	}

	public BigDecimal getValorTotalPagamento() {
		return valorTotalPagamento;
	}

	public void setValorTotalPagamento(BigDecimal valorTotalPagamento) {
		this.valorTotalPagamento = valorTotalPagamento;
	}

	public BigDecimal getVlrParcela() {
		return vlrParcela;
	}

	public void setVlrParcela(BigDecimal vlrParcela) {
		this.vlrParcela = vlrParcela;
	}

	public BigDecimal getVlrParcelaOriginal() {
		return vlrParcelaOriginal;
	}

	public void setVlrParcelaOriginal(BigDecimal vlrParcelaOriginal) {
		this.vlrParcelaOriginal = vlrParcelaOriginal;
	}

	public BigDecimal getVlrJurosParcela() {
		return vlrJurosParcela;
	}

	public void setVlrJurosParcela(BigDecimal vlrJurosParcela) {
		this.vlrJurosParcela = vlrJurosParcela;
	}

	public BigDecimal getVlrAmortizacaoParcela() {
		return vlrAmortizacaoParcela;
	}

	public void setVlrAmortizacaoParcela(BigDecimal vlrAmortizacaoParcela) {
		this.vlrAmortizacaoParcela = vlrAmortizacaoParcela;
	}

	public BigDecimal getVlrSaldoParcela() {
		return vlrSaldoParcela;
	}

	public void setVlrSaldoParcela(BigDecimal vlrSaldoParcela) {
		this.vlrSaldoParcela = vlrSaldoParcela;
	}

	public BigDecimal getVlrJuros() {
		return vlrJuros;
	}

	public void setVlrJuros(BigDecimal vlrJuros) {
		this.vlrJuros = vlrJuros;
	}

	public BigDecimal getTxMulta() {
		return txMulta;
	}

	public void setTxMulta(BigDecimal txMulta) {
		this.txMulta = txMulta;
	}

	public BigDecimal getVlrParcelaAtualizada() {
		return vlrParcelaAtualizada;
	}

	public void setVlrParcelaAtualizada(BigDecimal vlrParcelaAtualizada) {
		this.vlrParcelaAtualizada = vlrParcelaAtualizada;
	}

	public List<ContratoCobrancaFinanceiroDiaConsultaDetalhesParcialVO> getListContratoCobrancaDetalhesParcial() {
		return listContratoCobrancaDetalhesParcial;
	}

	public void setListContratoCobrancaDetalhesParcial(
			List<ContratoCobrancaFinanceiroDiaConsultaDetalhesParcialVO> listContratoCobrancaDetalhesParcial) {
		this.listContratoCobrancaDetalhesParcial = listContratoCobrancaDetalhesParcial;
	}

	public BigDecimal getVlrSeguroDFI() {
		return vlrSeguroDFI;
	}

	public void setVlrSeguroDFI(BigDecimal vlrSeguroDFI) {
		this.vlrSeguroDFI = vlrSeguroDFI;
	}

	public BigDecimal getVlrSeguroMIP() {
		return vlrSeguroMIP;
	}

	public void setVlrSeguroMIP(BigDecimal vlrSeguroMIP) {
		this.vlrSeguroMIP = vlrSeguroMIP;
	}

	public BigDecimal getVlrTaxaADM() {
		return vlrTaxaADM;
	}

	public void setVlrTaxaADM(BigDecimal vlrTaxaADM) {
		this.vlrTaxaADM = vlrTaxaADM;
	}
}
