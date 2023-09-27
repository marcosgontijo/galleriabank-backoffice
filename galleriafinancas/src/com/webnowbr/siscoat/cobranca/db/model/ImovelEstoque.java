package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.webnowbr.siscoat.common.CommonsUtil;

public class ImovelEstoque implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private long parcelaParouPagar;
	private BigDecimal variacaoCusto;
	private BigDecimal ltvLeilao;
	private BigDecimal valorEmprestimo;
	private BigDecimal vendaForcada;
	private BigDecimal valorMercado;
	private Date dataConsolidado;
	private Date dataLeilao1;
	private Date dataLeilao2;
	private Date dataLeilao3; //leilão estoque
	private String leiloeiro;
	private String statusLeilao;
	private String statusAtual;
	private BigDecimal valorLeilao2;
	private BigDecimal valorVenda;
	private Date dataVenda;
	private String tipoVenda;
	private Boolean quitado;
	private ContratoCobranca objetoContratoCobranca;
	private ImovelCobranca objetoImovelCobranca;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getParcelaParouPagar() {
		return parcelaParouPagar;
	}
	public void setParcelaParouPagar(long parcelaParouPagar) {
		this.parcelaParouPagar = parcelaParouPagar;
	}
	public BigDecimal getVariacaoCusto() {
		return variacaoCusto;
	}
	public void setVariacaoCusto(BigDecimal variacaoCusto) {
		this.variacaoCusto = variacaoCusto;
	}
	public BigDecimal getLtvLeilao() {
		return ltvLeilao;
	}
	public void setLtvLeilao(BigDecimal ltvLeilao) {
		this.ltvLeilao = ltvLeilao;
	}
	public BigDecimal getValorEmprestimo() {
		return valorEmprestimo;
	}
	public void setValorEmprestimo(BigDecimal valorEmprestimo) {
		this.valorEmprestimo = valorEmprestimo;
	}
	public BigDecimal getVendaForcada() {
		return vendaForcada;
	}
	public void setVendaForcada(BigDecimal vendaForcada) {
		this.vendaForcada = vendaForcada;
	}
	public BigDecimal getValorMercado() {
		return valorMercado;
	}
	public void setValorMercado(BigDecimal valorMercado) {
		this.valorMercado = valorMercado;
	}
	public Date getDataConsolidado() {
		return dataConsolidado;
	}
	public void setDataConsolidado(Date dataConsolidado) {
		this.dataConsolidado = dataConsolidado;
	}
	public Date getDataLeilao1() {
		return dataLeilao1;
	}
	public void setDataLeilao1(Date dataLeilao1) {
		this.dataLeilao1 = dataLeilao1;
	}
	public Date getDataLeilao2() {
		return dataLeilao2;
	}
	public void setDataLeilao2(Date dataLeilao2) {
		this.dataLeilao2 = dataLeilao2;
	}
	public Date getDataLeilao3() {
		return dataLeilao3;
	}
	public void setDataLeilao3(Date dataLeilao3) {
		this.dataLeilao3 = dataLeilao3;
	}
	public String getStatusLeilao() {
		return statusLeilao;
	}
	public void setStatusLeilao(String statusLeilao) {
		this.statusLeilao = statusLeilao;
	}
	public String getStatusAtual() {
		return statusAtual;
	}
	public void setStatusAtual(String statusAtual) {
		this.statusAtual = statusAtual;
	}
	public BigDecimal getValorLeilao2() {
		return valorLeilao2;
	}
	public void setValorLeilao2(BigDecimal valorLeilao2) {
		this.valorLeilao2 = valorLeilao2;
	}
	public BigDecimal getValorVenda() {
		return valorVenda;
	}
	public void setValorVenda(BigDecimal valorVenda) {
		this.valorVenda = valorVenda;
	}
	public Date getDataVenda() {
		return dataVenda;
	}
	public void setDataVenda(Date dataVenda) {
		this.dataVenda = dataVenda;
	}
	public String getTipoVenda() {
		return tipoVenda;
	}
	public void setTipoVenda(String tipoVenda) {
		this.tipoVenda = tipoVenda;
	}
	public Boolean getQuitado() {
		return quitado;
	}
	public void setQuitado(Boolean quitado) {
		this.quitado = quitado;
	}
	public String getLeiloeiro() {
		return leiloeiro;
	}
	public void setLeiloeiro(String leiloeiro) {
		this.leiloeiro = leiloeiro;
	}
	
	
}