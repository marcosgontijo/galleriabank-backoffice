package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContratoCobrancaFinanceiroDiaConsultaDetalhesParcialVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String numeroParcela;
	private Date dataVencimento;
	private Date dataVencimentoAtual;
	private Date dataPagamento;
	private BigDecimal vlrRecebido;
	private BigDecimal vlrParcelaAtualizado;
	private BigDecimal vlrRecebidoGalleria;
	private Date dataPagamentoGalleria;
	private boolean baixaGalleria;

	
	
	public ContratoCobrancaFinanceiroDiaConsultaDetalhesParcialVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ContratoCobrancaFinanceiroDiaConsultaDetalhesParcialVO(long id, String numeroParcela, Date dataVencimento,
			Date dataVencimentoAtual, Date dataPagamento, BigDecimal vlrRecebido, BigDecimal vlrParcelaAtualizado,
			BigDecimal vlrRecebidoGalleria, Date dataPagamentoGalleria, boolean baixaGalleria) {
		super();
		this.id = id;
		this.numeroParcela = numeroParcela;
		this.dataVencimento = dataVencimento;
		this.dataVencimentoAtual = dataVencimentoAtual;
		this.dataPagamento = dataPagamento;
		this.vlrRecebido = vlrRecebido;
		this.vlrParcelaAtualizado = vlrParcelaAtualizado;
		this.vlrRecebidoGalleria = vlrRecebidoGalleria;
		this.dataPagamentoGalleria = dataPagamentoGalleria;
		this.baixaGalleria = baixaGalleria;
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

	public BigDecimal getVlrRecebido() {
		return vlrRecebido;
	}

	public void setVlrRecebido(BigDecimal vlrRecebido) {
		this.vlrRecebido = vlrRecebido;
	}

	public BigDecimal getVlrParcelaAtualizado() {
		return vlrParcelaAtualizado;
	}

	public void setVlrParcelaAtualizado(BigDecimal vlrParcelaAtualizado) {
		this.vlrParcelaAtualizado = vlrParcelaAtualizado;
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
}