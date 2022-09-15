package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContratoCobrancaDetalhesParcial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String numeroParcela;
	private Date dataVencimento;
	private Date dataVencimentoAtual;
	private Date dataPagamento;
	private BigDecimal vlrParcela;

	private BigDecimal vlrRecebido;
	
	private PagadorRecebedor recebedor;
	
	private String observacaoRecebedor;
	
	private BigDecimal vlrParcelaAtualizado;
	private BigDecimal saldoAPagar;
	
	private BigDecimal vlrRecebidoGalleria;
	private Date dataPagamentoGalleria;
	private boolean baixaGalleria;
	
	private boolean baixaCustosDiversos;
	
	public ContratoCobrancaDetalhesParcial(){
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

	/**
	 * @return the dataVencimentoAtual
	 */
	public Date getDataVencimentoAtual() {
		return dataVencimentoAtual;
	}

	/**
	 * @param dataVencimentoAtual the dataVencimentoAtual to set
	 */
	public void setDataVencimentoAtual(Date dataVencimentoAtual) {
		this.dataVencimentoAtual = dataVencimentoAtual;
	}

	/**
	 * @return the recebedor
	 */
	public PagadorRecebedor getRecebedor() {
		return recebedor;
	}

	/**
	 * @param recebedor the recebedor to set
	 */
	public void setRecebedor(PagadorRecebedor recebedor) {
		this.recebedor = recebedor;
	}

	/**
	 * @return the observacaoRecebedor
	 */
	public String getObservacaoRecebedor() {
		return observacaoRecebedor;
	}

	/**
	 * @param observacaoRecebedor the observacaoRecebedor to set
	 */
	public void setObservacaoRecebedor(String observacaoRecebedor) {
		this.observacaoRecebedor = observacaoRecebedor;
	}

	/**
	 * @return the vlrParcelaAtualizado
	 */
	public BigDecimal getVlrParcelaAtualizado() {
		return vlrParcelaAtualizado;
	}

	/**
	 * @param vlrParcelaAtualizado the vlrParcelaAtualizado to set
	 */
	public void setVlrParcelaAtualizado(BigDecimal vlrParcelaAtualizado) {
		this.vlrParcelaAtualizado = vlrParcelaAtualizado;
	}

	/**
	 * @return the saldoAPagar
	 */
	public BigDecimal getSaldoAPagar() {
		return saldoAPagar;
	}

	/**
	 * @param saldoAPagar the saldoAPagar to set
	 */
	public void setSaldoAPagar(BigDecimal saldoAPagar) {
		this.saldoAPagar = saldoAPagar;
	}

	public BigDecimal getVlrRecebidoGalleria() {
		return vlrRecebidoGalleria;
	}

	public void setVlrRecebidoGalleria(BigDecimal vlrRecebidoGalleria) {
		this.vlrRecebidoGalleria = vlrRecebidoGalleria;
	}

	public Date getDataPagamentoGalleria() {
		return dataPagamentoGalleria;
	}

	public void setDataPagamentoGalleria(Date dataPagamentoGalleria) {
		this.dataPagamentoGalleria = dataPagamentoGalleria;
	}

	public boolean isBaixaGalleria() {
		return baixaGalleria;
	}

	public void setBaixaGalleria(boolean baixaGalleria) {
		this.baixaGalleria = baixaGalleria;
	}

	public boolean isBaixaCustosDiversos() {
		return baixaCustosDiversos;
	}

	public void setBaixaCustosDiversos(boolean baixaCustosDiversos) {
		this.baixaCustosDiversos = baixaCustosDiversos;
	}
}