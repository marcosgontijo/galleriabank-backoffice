package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalculosDetalhes implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private Date dataVencimento;
	private Date dataPagamento;
	private BigDecimal vlrParcela;
	private BigDecimal txJuros;
	private BigDecimal multa;
	private BigDecimal total;
	private BigDecimal honorarios;
	private int numeroParcela;
	
	private BigDecimal vlrTxJuros;
	private BigDecimal vlrMulta;
	private BigDecimal vlrHonorarios;
	
	private String observacao;

	public CalculosDetalhes(){

	}
	
	public CalculosDetalhes(long id, Date dataVencimento, BigDecimal txJuros, BigDecimal multa){
		this.id = id;
		this.dataVencimento = dataVencimento;
		this.txJuros = txJuros;
		this.multa = multa;
	}
	
	public CalculosDetalhes(Date dataVencimento, BigDecimal txJuros, BigDecimal multa){
		this.dataVencimento = dataVencimento;
		this.txJuros = txJuros;
		this.multa = multa;
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
	 * @return the txJuros
	 */
	public BigDecimal getTxJuros() {
		return txJuros;
	}

	/**
	 * @param txJuros the txJuros to set
	 */
	public void setTxJuros(BigDecimal txJuros) {
		this.txJuros = txJuros;
	}

	/**
	 * @return the multa
	 */
	public BigDecimal getMulta() {
		return multa;
	}

	/**
	 * @param multa the multa to set
	 */
	public void setMulta(BigDecimal multa) {
		this.multa = multa;
	}

	/**
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
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

	public int getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(int numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	/**
	 * @return the honorarios
	 */
	public BigDecimal getHonorarios() {
		return honorarios;
	}

	/**
	 * @param honorarios the honorarios to set
	 */
	public void setHonorarios(BigDecimal honorarios) {
		this.honorarios = honorarios;
	}

	/**
	 * @return the vlrTxJuros
	 */
	public BigDecimal getVlrTxJuros() {
		return vlrTxJuros;
	}

	/**
	 * @param vlrTxJuros the vlrTxJuros to set
	 */
	public void setVlrTxJuros(BigDecimal vlrTxJuros) {
		this.vlrTxJuros = vlrTxJuros;
	}

	/**
	 * @return the vlrMulta
	 */
	public BigDecimal getVlrMulta() {
		return vlrMulta;
	}

	/**
	 * @param vlrMulta the vlrMulta to set
	 */
	public void setVlrMulta(BigDecimal vlrMulta) {
		this.vlrMulta = vlrMulta;
	}

	/**
	 * @return the vlrHonorarios
	 */
	public BigDecimal getVlrHonorarios() {
		return vlrHonorarios;
	}

	/**
	 * @param vlrHonorarios the vlrHonorarios to set
	 */
	public void setVlrHonorarios(BigDecimal vlrHonorarios) {
		this.vlrHonorarios = vlrHonorarios;
	}

	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}