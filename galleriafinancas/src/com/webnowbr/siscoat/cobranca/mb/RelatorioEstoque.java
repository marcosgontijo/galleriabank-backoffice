package com.webnowbr.siscoat.cobranca.mb;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RelatorioEstoque implements Serializable {

	private static final long serialVersionUID = 1L;
	private String numeroContratoRelatorio;
	private BigDecimal variacaoCustoRelatorio;
	private BigDecimal ltvLeilaoRelatorio;
	private BigDecimal valorEmprestimoRelatorio;
	private BigDecimal vendaForcadaRelatorio;
	private BigDecimal valorMercadoRelatorio;
	private String nomePagadorRelatorio;
	private String numeroMatriculaRelatorio;
	private String enderecoCompletoRelatorio;	
	private Date dataConsolidadoRelatorio;
	private Date dataLeilao1Relatorio;
	private Date dataLeilao2Relatorio;
	private Date dataLeilao3Relatorio; //leilão estoque
	private String leiloeiroRelatorio;
	private String statusLeilaoRelatorio;
	private String statusAtualRelatorio;
	private BigDecimal valorLeilao2Relatorio;
	private BigDecimal valorVendaRelatorio;
	private Date dataVendaRelatorio;
	private String tipoVendaRelatorio;
	private Boolean quitadoRelatorio;
	
	
	public String getNumeroContratoRelatorio() {
		return numeroContratoRelatorio;
	}
	public void setNumeroContratoRelatorio(String numeroContratoRelatorio) {
		this.numeroContratoRelatorio = numeroContratoRelatorio;
	}
	public BigDecimal getVariacaoCustoRelatorio() {
		return variacaoCustoRelatorio;
	}
	public void setVariacaoCustoRelatorio(BigDecimal variacaoCustoRelatorio) {
		this.variacaoCustoRelatorio = variacaoCustoRelatorio;
	}
	public BigDecimal getLtvLeilaoRelatorio() {
		return ltvLeilaoRelatorio;
	}
	public void setLtvLeilaoRelatorio(BigDecimal ltvLeilaoRelatorio) {
		this.ltvLeilaoRelatorio = ltvLeilaoRelatorio;
	}
	public BigDecimal getValorEmprestimoRelatorio() {
		return valorEmprestimoRelatorio;
	}
	public void setValorEmprestimoRelatorio(BigDecimal valorEmprestimoRelatorio) {
		this.valorEmprestimoRelatorio = valorEmprestimoRelatorio;
	}
	public BigDecimal getVendaForcadaRelatorio() {
		return vendaForcadaRelatorio;
	}
	public void setVendaForcadaRelatorio(BigDecimal vendaForcadaRelatorio) {
		this.vendaForcadaRelatorio = vendaForcadaRelatorio;
	}
	public BigDecimal getValorMercadoRelatorio() {
		return valorMercadoRelatorio;
	}
	public void setValorMercadoRelatorio(BigDecimal valorMercadoRelatorio) {
		this.valorMercadoRelatorio = valorMercadoRelatorio;
	}
	public String getNomePagadorRelatorio() {
		return nomePagadorRelatorio;
	}
	public void setNomePagadorRelatorio(String nomePagadorRelatorio) {
		this.nomePagadorRelatorio = nomePagadorRelatorio;
	}
	public String getNumeroMatriculaRelatorio() {
		return numeroMatriculaRelatorio;
	}
	public void setNumeroMatriculaRelatorio(String numeroMatriculaRelatorio) {
		this.numeroMatriculaRelatorio = numeroMatriculaRelatorio;
	}
	public String getEnderecoCompletoRelatorio() {
		return enderecoCompletoRelatorio;
	}
	public void setEnderecoCompletoRelatorio(String enderecoCompletoRelatorio) {
		this.enderecoCompletoRelatorio = enderecoCompletoRelatorio;
	}
	public Date getDataConsolidadoRelatorio() {
		return dataConsolidadoRelatorio;
	}
	public void setDataConsolidadoRelatorio(Date dataConsolidadoRelatorio) {
		this.dataConsolidadoRelatorio = dataConsolidadoRelatorio;
	}
	public Date getDataLeilao1Relatorio() {
		return dataLeilao1Relatorio;
	}
	public void setDataLeilao1Relatorio(Date dataLeilao1Relatorio) {
		this.dataLeilao1Relatorio = dataLeilao1Relatorio;
	}
	public Date getDataLeilao2Relatorio() {
		return dataLeilao2Relatorio;
	}
	public void setDataLeilao2Relatorio(Date dataLeilao2Relatorio) {
		this.dataLeilao2Relatorio = dataLeilao2Relatorio;
	}
	public Date getDataLeilao3Relatorio() {
		return dataLeilao3Relatorio;
	}
	public void setDataLeilao3Relatorio(Date dataLeilao3Relatorio) {
		this.dataLeilao3Relatorio = dataLeilao3Relatorio;
	}
	public String getStatusLeilaoRelatorio() {
		return statusLeilaoRelatorio;
	}
	public void setStatusLeilaoRelatorio(String statusLeilaoRelatorio) {
		this.statusLeilaoRelatorio = statusLeilaoRelatorio;
	}
	public String getStatusAtualRelatorio() {
		return statusAtualRelatorio;
	}
	public void setStatusAtualRelatorio(String statusAtualRelatorio) {
		this.statusAtualRelatorio = statusAtualRelatorio;
	}
	public BigDecimal getValorLeilao2Relatorio() {
		return valorLeilao2Relatorio;
	}
	public void setValorLeilao2Relatorio(BigDecimal valorLeilao2Relatorio) {
		this.valorLeilao2Relatorio = valorLeilao2Relatorio;
	}
	public BigDecimal getValorVendaRelatorio() {
		return valorVendaRelatorio;
	}
	public void setValorVendaRelatorio(BigDecimal valorVendaRelatorio) {
		this.valorVendaRelatorio = valorVendaRelatorio;
	}
	public Date getDataVendaRelatorio() {
		return dataVendaRelatorio;
	}
	public void setDataVendaRelatorio(Date dataVendaRelatorio) {
		this.dataVendaRelatorio = dataVendaRelatorio;
	}
	public String getTipoVendaRelatorio() {
		return tipoVendaRelatorio;
	}
	public void setTipoVendaRelatorio(String tipoVendaRelatorio) {
		this.tipoVendaRelatorio = tipoVendaRelatorio;
	}
	public Boolean getQuitadoRelatorio() {
		return quitadoRelatorio;
	}
	public void setQuitadoRelatorio(Boolean quitadoRelatorio) {
		this.quitadoRelatorio = quitadoRelatorio;
	}
	public String getLeiloeiroRelatorio() {
		return leiloeiroRelatorio;
	}
	public void setLeiloeiroRelatorio(String leiloeiroRelatorio) {
		this.leiloeiroRelatorio = leiloeiroRelatorio;
	}
	
	
	}