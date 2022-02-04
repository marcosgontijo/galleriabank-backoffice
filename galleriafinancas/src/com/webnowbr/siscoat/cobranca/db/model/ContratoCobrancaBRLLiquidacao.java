package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContratoCobrancaBRLLiquidacao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private ContratoCobranca contrato;
	
	private String numeroParcela;
	private Date dataVencimento;	
	private Date dataPagamento;
	private BigDecimal vlrParcela;
	private BigDecimal vlrRecebido;
	
	private BigDecimal vlrJurosParcela;
	private BigDecimal vlrAmortizacaoParcela;
	
	private BigDecimal vlrJurosSemIPCA;
	private BigDecimal vlrAmortizacaoSemIPCA;
	
	public ContratoCobrancaBRLLiquidacao(){
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the numeroParcela
	 */
	public String getNumeroParcela() {
		return numeroParcela;
	}

	/**
	 * @param numeroParcela the numeroParcela to set
	 */
	public void setNumeroParcela(String numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	/**
	 * @return the dataVencimento
	 */
	public Date getDataVencimento() {
		return dataVencimento;
	}

	/**
	 * @param dataVencimento the dataVencimento to set
	 */
	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	/**
	 * @return the dataPagamento
	 */
	public Date getDataPagamento() {
		return dataPagamento;
	}

	/**
	 * @param dataPagamento the dataPagamento to set
	 */
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	/**
	 * @return the vlrParcela
	 */
	public BigDecimal getVlrParcela() {
		return vlrParcela;
	}

	/**
	 * @param vlrParcela the vlrParcela to set
	 */
	public void setVlrParcela(BigDecimal vlrParcela) {
		this.vlrParcela = vlrParcela;
	}

	/**
	 * @return the vlrRecebido
	 */
	public BigDecimal getVlrRecebido() {
		return vlrRecebido;
	}

	/**
	 * @param vlrRecebido the vlrRecebido to set
	 */
	public void setVlrRecebido(BigDecimal vlrRecebido) {
		this.vlrRecebido = vlrRecebido;
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
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

	public BigDecimal getVlrJurosSemIPCA() {
		return vlrJurosSemIPCA;
	}

	public void setVlrJurosSemIPCA(BigDecimal vlrJurosSemIPCA) {
		this.vlrJurosSemIPCA = vlrJurosSemIPCA;
	}

	public BigDecimal getVlrAmortizacaoSemIPCA() {
		return vlrAmortizacaoSemIPCA;
	}

	public void setVlrAmortizacaoSemIPCA(BigDecimal vlrAmortizacaoSemIPCA) {
		this.vlrAmortizacaoSemIPCA = vlrAmortizacaoSemIPCA;
	}
}