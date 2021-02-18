package com.webnowbr.siscoat.cobranca.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.webnowbr.siscoat.common.SiscoatConstants;

public class ContratoCobrancaResumoVO {
	private long id;
	private String numeroContrato;
	private Date dataContrato;
	private String pagador;
	private String situacao;
	private BigInteger parcelasAbertas;
	private BigDecimal valorReceber;
	private BigDecimal valorRecebido;
	private BigDecimal valorInvestido;
	private BigDecimal valorInvestidor;

	public void adicionaParcelaAberta() {
		parcelasAbertas = parcelasAbertas.add(BigInteger.ONE);
	}

	public void addValorReceber(BigDecimal valor) {
		valorReceber = valorReceber.add(valor);
	}

	public void addValorRecebido(BigDecimal valor) {
		valorRecebido = valorRecebido.add(valor);
	}

	public void acertaValorRecebido() {
		if (situacao.equals(SiscoatConstants.CONTRATO_QUITADO))
			valorRecebido = valorRecebido.subtract(valorInvestido);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public Date getDataContrato() {
		return dataContrato;
	}

	public void setDataContrato(Date dataContrato) {
		this.dataContrato = dataContrato;
	}

	public String getPagador() {
		if (pagador.length() > 30) {
			return pagador.substring(0,30).concat("...");
		} else {
			return pagador;
		}
	}

	public void setPagador(String pagador) {
		this.pagador = pagador;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public BigInteger getParcelasAbertas() {
		return parcelasAbertas;
	}

	public void setParcelasAbertas(BigInteger parcelasAbertas) {
		this.parcelasAbertas = parcelasAbertas;
	}

	public BigDecimal getValorReceber() {
		return valorReceber;
	}

	public void setValorReceber(BigDecimal valorReceber) {
		this.valorReceber = valorReceber;
	}

	public BigDecimal getValorRecebido() {
		return valorRecebido;
	}

	public void setValorRecebido(BigDecimal valorRecebido) {
		this.valorRecebido = valorRecebido;
	}

	public BigDecimal getValorInvestido() {
		return valorInvestido;
	}

	public void setValorInvestido(BigDecimal valorInvestido) {
		this.valorInvestido = valorInvestido;
	}

	public BigDecimal getValorInvestidor() {
		return valorInvestidor;
	}

	public void setValorInvestidor(BigDecimal valorInvestidor) {
		this.valorInvestidor = valorInvestidor;
	}

}
