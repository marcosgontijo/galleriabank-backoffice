package com.webnowbr.siscoat.cobranca.model.bmpdigital;

import java.math.BigDecimal;

public class ResumoDoClienteTraduzido {

	private String percDocumentosProcessados;
	private String dtInicioRelacionamento;
	private int qtdeInstituicoes;
	private int qtdeOperacoes;
	private int qtdeOperacoesDiscordancia;
	private BigDecimal vlrOperacoesDiscordancia;
	private int qtdeOperacoesSobJudice;
	private BigDecimal vlrOperacoesSobJudice;
	private BigDecimal carteiraVencer;
	private BigDecimal carteiraVencerAte30diasVencidosAte14dias;
	private BigDecimal carteiraVencer31a60dias;
	private BigDecimal carteiraVencer61a90dias;
	private BigDecimal carteiraVencer91a180dias;
	private BigDecimal carteiraVencer181a360dias;
	private BigDecimal carteiraVencerAcima360dias;
	private BigDecimal carteiraVencerPrazoIndeterminado;
	private BigDecimal carteiraVencido;
	private BigDecimal carteiraVencido15a30dias;
	private BigDecimal carteiraVencido31a60dias;
	private BigDecimal carteiraVencido61a90dias;
	private BigDecimal carteiraVencido91a180dias;
	private BigDecimal carteiraVencido181a360dias;
	private BigDecimal carteiraVencidoAcima360dias;
	private BigDecimal prejuizo;
	private BigDecimal prejuizoAte12meses;
	private BigDecimal prejuizoAcima12meses;
	private BigDecimal carteiradeCredito;
	private BigDecimal repasses;
	private BigDecimal coobrigacoes;
	private BigDecimal responsabilidadeTotal;
	private BigDecimal creditosaLiberar;
	private BigDecimal limitesdeCredito;
	private BigDecimal limitesdeCreditoAte360dias;
	private BigDecimal limitesdeCreditoAcima360dias;
	private BigDecimal riscoIndiretoVendor;
	private BigDecimal riscoTotal;
	
	public ResumoDoClienteTraduzido() {
	}

	public String getPercDocumentosProcessados() {
		return percDocumentosProcessados;
	}

	public void setPercDocumentosProcessados(String percDocumentosProcessados) {
		this.percDocumentosProcessados = percDocumentosProcessados;
	}

	public String getDtInicioRelacionamento() {
		return dtInicioRelacionamento;
	}

	public void setDtInicioRelacionamento(String dtInicioRelacionamento) {
		this.dtInicioRelacionamento = dtInicioRelacionamento;
	}

	public int getQtdeInstituicoes() {
		return qtdeInstituicoes;
	}

	public void setQtdeInstituicoes(int qtdeInstituicoes) {
		this.qtdeInstituicoes = qtdeInstituicoes;
	}

	public int getQtdeOperacoes() {
		return qtdeOperacoes;
	}

	public void setQtdeOperacoes(int qtdeOperacoes) {
		this.qtdeOperacoes = qtdeOperacoes;
	}

	public int getQtdeOperacoesDiscordancia() {
		return qtdeOperacoesDiscordancia;
	}

	public void setQtdeOperacoesDiscordancia(int qtdeOperacoesDiscordancia) {
		this.qtdeOperacoesDiscordancia = qtdeOperacoesDiscordancia;
	}

	public BigDecimal getVlrOperacoesDiscordancia() {
		return vlrOperacoesDiscordancia;
	}

	public void setVlrOperacoesDiscordancia(BigDecimal vlrOperacoesDiscordancia) {
		this.vlrOperacoesDiscordancia = vlrOperacoesDiscordancia;
	}

	public int getQtdeOperacoesSobJudice() {
		return qtdeOperacoesSobJudice;
	}

	public void setQtdeOperacoesSobJudice(int qtdeOperacoesSobJudice) {
		this.qtdeOperacoesSobJudice = qtdeOperacoesSobJudice;
	}

	public BigDecimal getVlrOperacoesSobJudice() {
		return vlrOperacoesSobJudice;
	}

	public void setVlrOperacoesSobJudice(BigDecimal vlrOperacoesSobJudice) {
		this.vlrOperacoesSobJudice = vlrOperacoesSobJudice;
	}

	public BigDecimal getCarteiraVencer() {
		return carteiraVencer;
	}

	public void setCarteiraVencer(BigDecimal carteiraVencer) {
		this.carteiraVencer = carteiraVencer;
	}

	public BigDecimal getCarteiraVencerAte30diasVencidosAte14dias() {
		return carteiraVencerAte30diasVencidosAte14dias;
	}

	public void setCarteiraVencerAte30diasVencidosAte14dias(BigDecimal carteiraVencerAte30diasVencidosAte14dias) {
		this.carteiraVencerAte30diasVencidosAte14dias = carteiraVencerAte30diasVencidosAte14dias;
	}

	public BigDecimal getCarteiraVencer31a60dias() {
		return carteiraVencer31a60dias;
	}

	public void setCarteiraVencer31a60dias(BigDecimal carteiraVencer31a60dias) {
		this.carteiraVencer31a60dias = carteiraVencer31a60dias;
	}

	public BigDecimal getCarteiraVencer61a90dias() {
		return carteiraVencer61a90dias;
	}

	public void setCarteiraVencer61a90dias(BigDecimal carteiraVencer61a90dias) {
		this.carteiraVencer61a90dias = carteiraVencer61a90dias;
	}

	public BigDecimal getCarteiraVencer91a180dias() {
		return carteiraVencer91a180dias;
	}

	public void setCarteiraVencer91a180dias(BigDecimal carteiraVencer91a180dias) {
		this.carteiraVencer91a180dias = carteiraVencer91a180dias;
	}

	public BigDecimal getCarteiraVencer181a360dias() {
		return carteiraVencer181a360dias;
	}

	public void setCarteiraVencer181a360dias(BigDecimal carteiraVencer181a360dias) {
		this.carteiraVencer181a360dias = carteiraVencer181a360dias;
	}

	public BigDecimal getCarteiraVencerAcima360dias() {
		return carteiraVencerAcima360dias;
	}

	public void setCarteiraVencerAcima360dias(BigDecimal carteiraVencerAcima360dias) {
		this.carteiraVencerAcima360dias = carteiraVencerAcima360dias;
	}

	public BigDecimal getCarteiraVencerPrazoIndeterminado() {
		return carteiraVencerPrazoIndeterminado;
	}

	public void setCarteiraVencerPrazoIndeterminado(BigDecimal carteiraVencerPrazoIndeterminado) {
		this.carteiraVencerPrazoIndeterminado = carteiraVencerPrazoIndeterminado;
	}

	public BigDecimal getCarteiraVencido() {
		return carteiraVencido;
	}

	public void setCarteiraVencido(BigDecimal carteiraVencido) {
		this.carteiraVencido = carteiraVencido;
	}

	public BigDecimal getCarteiraVencido15a30dias() {
		return carteiraVencido15a30dias;
	}

	public void setCarteiraVencido15a30dias(BigDecimal carteiraVencido15a30dias) {
		this.carteiraVencido15a30dias = carteiraVencido15a30dias;
	}

	public BigDecimal getCarteiraVencido31a60dias() {
		return carteiraVencido31a60dias;
	}

	public void setCarteiraVencido31a60dias(BigDecimal carteiraVencido31a60dias) {
		this.carteiraVencido31a60dias = carteiraVencido31a60dias;
	}

	public BigDecimal getCarteiraVencido61a90dias() {
		return carteiraVencido61a90dias;
	}

	public void setCarteiraVencido61a90dias(BigDecimal carteiraVencido61a90dias) {
		this.carteiraVencido61a90dias = carteiraVencido61a90dias;
	}

	public BigDecimal getCarteiraVencido91a180dias() {
		return carteiraVencido91a180dias;
	}

	public void setCarteiraVencido91a180dias(BigDecimal carteiraVencido91a180dias) {
		this.carteiraVencido91a180dias = carteiraVencido91a180dias;
	}

	public BigDecimal getCarteiraVencido181a360dias() {
		return carteiraVencido181a360dias;
	}

	public void setCarteiraVencido181a360dias(BigDecimal carteiraVencido181a360dias) {
		this.carteiraVencido181a360dias = carteiraVencido181a360dias;
	}

	public BigDecimal getCarteiraVencidoAcima360dias() {
		return carteiraVencidoAcima360dias;
	}

	public void setCarteiraVencidoAcima360dias(BigDecimal carteiraVencidoAcima360dias) {
		this.carteiraVencidoAcima360dias = carteiraVencidoAcima360dias;
	}

	public BigDecimal getPrejuizo() {
		return prejuizo;
	}

	public void setPrejuizo(BigDecimal prejuizo) {
		this.prejuizo = prejuizo;
	}

	public BigDecimal getPrejuizoAte12meses() {
		return prejuizoAte12meses;
	}

	public void setPrejuizoAte12meses(BigDecimal prejuizoAte12meses) {
		this.prejuizoAte12meses = prejuizoAte12meses;
	}

	public BigDecimal getPrejuizoAcima12meses() {
		return prejuizoAcima12meses;
	}

	public void setPrejuizoAcima12meses(BigDecimal prejuizoAcima12meses) {
		this.prejuizoAcima12meses = prejuizoAcima12meses;
	}

	public BigDecimal getCarteiradeCredito() {
		return carteiradeCredito;
	}

	public void setCarteiradeCredito(BigDecimal carteiradeCredito) {
		this.carteiradeCredito = carteiradeCredito;
	}

	public BigDecimal getRepasses() {
		return repasses;
	}

	public void setRepasses(BigDecimal repasses) {
		this.repasses = repasses;
	}

	public BigDecimal getCoobrigacoes() {
		return coobrigacoes;
	}

	public void setCoobrigacoes(BigDecimal coobrigacoes) {
		this.coobrigacoes = coobrigacoes;
	}

	public BigDecimal getResponsabilidadeTotal() {
		return responsabilidadeTotal;
	}

	public void setResponsabilidadeTotal(BigDecimal responsabilidadeTotal) {
		this.responsabilidadeTotal = responsabilidadeTotal;
	}

	public BigDecimal getCreditosaLiberar() {
		return creditosaLiberar;
	}

	public void setCreditosaLiberar(BigDecimal creditosaLiberar) {
		this.creditosaLiberar = creditosaLiberar;
	}

	public BigDecimal getLimitesdeCredito() {
		return limitesdeCredito;
	}

	public void setLimitesdeCredito(BigDecimal limitesdeCredito) {
		this.limitesdeCredito = limitesdeCredito;
	}

	public BigDecimal getLimitesdeCreditoAte360dias() {
		return limitesdeCreditoAte360dias;
	}

	public void setLimitesdeCreditoAte360dias(BigDecimal limitesdeCreditoAte360dias) {
		this.limitesdeCreditoAte360dias = limitesdeCreditoAte360dias;
	}

	public BigDecimal getLimitesdeCreditoAcima360dias() {
		return limitesdeCreditoAcima360dias;
	}

	public void setLimitesdeCreditoAcima360dias(BigDecimal limitesdeCreditoAcima360dias) {
		this.limitesdeCreditoAcima360dias = limitesdeCreditoAcima360dias;
	}

	public BigDecimal getRiscoIndiretoVendor() {
		return riscoIndiretoVendor;
	}

	public void setRiscoIndiretoVendor(BigDecimal riscoIndiretoVendor) {
		this.riscoIndiretoVendor = riscoIndiretoVendor;
	}

	public BigDecimal getRiscoTotal() {
		return riscoTotal;
	}

	public void setRiscoTotal(BigDecimal riscoTotal) {
		this.riscoTotal = riscoTotal;
	}
}
