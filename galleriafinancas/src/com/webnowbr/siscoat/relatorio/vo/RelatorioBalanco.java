package com.webnowbr.siscoat.relatorio.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RelatorioBalanco implements Serializable {

	private static final long serialVersionUID = 1L;
	private String numeroContratoRelatorio;
	private String nomePagadorRelatorio;
	private String tipoPagadorRelatorio;
	private String empresaContratoRelatorio;
	private Date dataVencimentoRelatorio;
	private BigDecimal taxaContratoRelatorio;
	private BigDecimal taxaInvestidor;
	private BigDecimal valorContratoRelatorio;
	private BigDecimal valorAmortizacao;
	private BigDecimal valorCapitalizacao;
	private String indiceContratoRelatorio;
	private String indiceNovoContratoRelatorio;
	
	
	public RelatorioBalanco() {
	}


	public String getNumeroContratoRelatorio() {
		return numeroContratoRelatorio;
	}

	public void setNumeroContratoRelatorio(String numeroContratoRelatorio) {
		this.numeroContratoRelatorio = numeroContratoRelatorio;
	}

	public String getNomePagadorRelatorio() {
		return nomePagadorRelatorio;
	}

	public void setNomePagadorRelatorio(String nomePagadorRelatorio) {
		this.nomePagadorRelatorio = nomePagadorRelatorio;
	}

	public String getTipoPagadorRelatorio() {
		return tipoPagadorRelatorio;
	}

	public void setTipoPagadorRelatorio(String tipoPagadorRelatorio) {
		this.tipoPagadorRelatorio = tipoPagadorRelatorio;
	}

	public String getEmpresaContratoRelatorio() {
		return empresaContratoRelatorio;
	}

	public void setEmpresaContratoRelatorio(String empresaContratoRelatorio) {
		this.empresaContratoRelatorio = empresaContratoRelatorio;
	}

	public Date getDataVencimentoRelatorio() {
		return dataVencimentoRelatorio;
	}

	public void setDataVencimentoRelatorio(Date dataVencimentoRelatorio) {
		this.dataVencimentoRelatorio = dataVencimentoRelatorio;
	}

	public BigDecimal getTaxaContratoRelatorio() {
		return taxaContratoRelatorio;
	}

	public void setTaxaContratoRelatorio(BigDecimal taxaContratoRelatorio) {
		this.taxaContratoRelatorio = taxaContratoRelatorio;
	}

	public BigDecimal getValorContratoRelatorio() {
		return valorContratoRelatorio;
	}

	public void setValorContratoRelatorio(BigDecimal valorContratoRelatorio) {
		this.valorContratoRelatorio = valorContratoRelatorio;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getIndiceContratoRelatorio() {
		return indiceContratoRelatorio;
	}
	
	public void setIndiceContratoRelatorio(String indiceContratoRelatorio) {
		this.indiceContratoRelatorio = indiceContratoRelatorio;
	}

	public BigDecimal getTaxaInvestidor() {
		return taxaInvestidor;
	}

	public void setTaxaInvestidor(BigDecimal taxaInvestidor) {
		this.taxaInvestidor = taxaInvestidor;
	}

	public BigDecimal getValorAmortizacao() {
		return valorAmortizacao;
	}

	public void setValorAmortizacao(BigDecimal valorAmortizacao) {
		this.valorAmortizacao = valorAmortizacao;
	}

	public BigDecimal getValorCapitalizacao() {
		return valorCapitalizacao;
	}
	
	public void setValorCapitalizacao(BigDecimal valorCapitalizacao) {
		this.valorCapitalizacao = valorCapitalizacao;
	}

	public String getIndiceNovoContratoRelatorio() {
		return indiceNovoContratoRelatorio;
	}
	
	public void setIndiceNovoContratoRelatorio(String indiceNovoContratoRelatorio) {
		this.indiceNovoContratoRelatorio = indiceNovoContratoRelatorio;
	}
	
}