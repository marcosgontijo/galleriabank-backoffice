package com.webnowbr.siscoat.relatorio.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

public class RelatorioVendaOperacaoVO {
	private BigInteger contratoCobranca;
	private String numeroContrato;
	private Date ultimaParcela;
	private String sistema;
	private String pagador;
	private BigDecimal valorParcela;
	private BigDecimal valorVenda;
	private BigDecimal faltaVender;
	private BigDecimal percVendido;
	private Boolean situacao;
	private BigDecimal valorAvaliacao;

	public RelatorioVendaOperacaoVO() {
		super();
	}

	public RelatorioVendaOperacaoVO(Long contratoCobranca, String numeroContrato, Date ultimaParcela, String sistema,
			String pagador, BigDecimal valorParcela, BigDecimal valorVenda, BigDecimal faltaVender, Boolean situacao) {
		super();
		this.contratoCobranca = BigInteger.valueOf(contratoCobranca);
		this.numeroContrato = numeroContrato;
		this.ultimaParcela = ultimaParcela;
		this.sistema = sistema;
		this.pagador = pagador;
		this.valorParcela = valorParcela == null ? BigDecimal.valueOf(0) : valorParcela;
		this.valorVenda = valorVenda == null ? BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_HALF_UP) : valorVenda;
		this.faltaVender = faltaVender == null ? BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_HALF_UP)
				: faltaVender;
		this.situacao = situacao;
		if (valorVenda != null && valorVenda.doubleValue() > 0) {
			this.percVendido = BigDecimal.valueOf(1)
					.add((faltaVender.divide(valorVenda, 4, BigDecimal.ROUND_HALF_UP)).negate());

			this.percVendido = this.percVendido.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			this.percVendido = BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
	}

	public BigInteger getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(BigInteger contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public Date getUltimaParcela() {
		return ultimaParcela;
	}

	public void setUltimaParcela(Date ultimaParcela) {
		this.ultimaParcela = ultimaParcela;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getPagador() {
		return pagador;
	}

	public void setPagador(String pagador) {
		this.pagador = pagador;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public BigDecimal getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(BigDecimal valorVenda) {
		this.valorVenda = valorVenda;
	}

	public BigDecimal getFaltaVender() {
		return faltaVender;
	}

	public void setFaltaVender(BigDecimal faltaVender) {
		this.faltaVender = faltaVender;
	}

	public BigDecimal getPercVendido() {
		return percVendido;
	}

	public void setPercVendido(BigDecimal percVendido) {
		this.percVendido = percVendido;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public BigDecimal getValorAvaliacao() {
		return valorAvaliacao;
	}

	public void setValorAvaliacao(BigDecimal valorAvaliacao) {
		this.valorAvaliacao = valorAvaliacao;
	}

}
