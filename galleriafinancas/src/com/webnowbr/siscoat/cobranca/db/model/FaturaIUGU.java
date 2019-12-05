package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FaturaIUGU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private Date due_date;
	private String due_date_str;
	private String secure_url;
	private String total;
	
	private String status;
	
	private String sacado;
	private String email;
	private String cedente;
	
	private String nomeContaIUGU;
	private String observacaoSaque;
	
	/****
	 * uso do extrato financeiro
	 */
	private String tipoTransacao;
	private String descricaoTransacao;
	private String valor;
	private String saldo;
	private Date dataTransacao;
	
	private Date paid_at;
	
	private BigDecimal totalMovimentacoes;
	private BigDecimal totalSaldo;
		
	private String idTransacao;
	
	private String numeroContrato;
	private BigDecimal valorParcela;
	
	private BigDecimal valorNum;
	private BigDecimal saldoNum;
	
	public FaturaIUGU(){
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the due_date
	 */
	public Date getDue_date() {
		return due_date;
	}

	/**
	 * @param due_date the due_date to set
	 */
	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

	/**
	 * @return the secure_url
	 */
	public String getSecure_url() {
		return secure_url;
	}

	/**
	 * @param secure_url the secure_url to set
	 */
	public void setSecure_url(String secure_url) {
		this.secure_url = secure_url;
	}

	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(String total) {
		this.total = total;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the sacado
	 */
	public String getSacado() {
		return sacado;
	}

	/**
	 * @param sacado the sacado to set
	 */
	public void setSacado(String sacado) {
		this.sacado = sacado;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the due_date_str
	 */
	public String getDue_date_str() {
		return due_date_str;
	}

	/**
	 * @param due_date_str the due_date_str to set
	 */
	public void setDue_date_str(String due_date_str) {
		this.due_date_str = due_date_str;
	}

	/**
	 * @return the cedente
	 */
	public String getCedente() {
		return cedente;
	}

	/**
	 * @param cedente the cedente to set
	 */
	public void setCedente(String cedente) {
		this.cedente = cedente;
	}

	/**
	 * @return the tipoTransacao
	 */
	public String getTipoTransacao() {
		return tipoTransacao;
	}

	/**
	 * @param tipoTransacao the tipoTransacao to set
	 */
	public void setTipoTransacao(String tipoTransacao) {
		this.tipoTransacao = tipoTransacao;
	}

	/**
	 * @return the descricaoTransacao
	 */
	public String getDescricaoTransacao() {
		return descricaoTransacao;
	}

	/**
	 * @param descricaoTransacao the descricaoTransacao to set
	 */
	public void setDescricaoTransacao(String descricaoTransacao) {
		this.descricaoTransacao = descricaoTransacao;
	}

	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * @return the saldo
	 */
	public String getSaldo() {
		return saldo;
	}

	/**
	 * @param saldo the saldo to set
	 */
	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	/**
	 * @return the dataTransacao
	 */
	public Date getDataTransacao() {
		return dataTransacao;
	}

	/**
	 * @param dataTransacao the dataTransacao to set
	 */
	public void setDataTransacao(Date dataTransacao) {
		this.dataTransacao = dataTransacao;
	}

	public Date getPaid_at() {
		return paid_at;
	}

	public void setPaid_at(Date paid_at) {
		this.paid_at = paid_at;
	}

	public String getNomeContaIUGU() {
		return nomeContaIUGU;
	}

	public void setNomeContaIUGU(String nomeContaIUGU) {
		this.nomeContaIUGU = nomeContaIUGU;
	}

	public BigDecimal getTotalMovimentacoes() {
		return totalMovimentacoes;
	}

	public void setTotalMovimentacoes(BigDecimal totalMovimentacoes) {
		this.totalMovimentacoes = totalMovimentacoes;
	}

	public BigDecimal getTotalSaldo() {
		return totalSaldo;
	}

	public void setTotalSaldo(BigDecimal totalSaldo) {
		this.totalSaldo = totalSaldo;
	}

	public String getObservacaoSaque() {
		return observacaoSaque;
	}

	public void setObservacaoSaque(String observacaoSaque) {
		this.observacaoSaque = observacaoSaque;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public BigDecimal getValorNum() {
		return valorNum;
	}

	public void setValorNum(BigDecimal valorNum) {
		this.valorNum = valorNum;
	}

	public BigDecimal getSaldoNum() {
		return saldoNum;
	}

	public void setSaldoNum(BigDecimal saldoNum) {
		this.saldoNum = saldoNum;
	}

	public String getIdTransacao() {
		return idTransacao;
	}

	public void setIdTransacao(String idTransacao) {
		this.idTransacao = idTransacao;
	}
}