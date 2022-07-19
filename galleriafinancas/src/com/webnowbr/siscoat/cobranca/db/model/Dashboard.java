package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Dashboard implements Serializable {

	private static final long serialVersionUID = 1L;
	private int refinanciamentoDeImovel;
	private int emprestimoParaNegativados;
	private int emprestimoOnline;
	private int emprestimoHomeEquity;
	private int emprestimoComTerrenoEmGarantia;
	private int emprestimoOnlineYT;
	private int simuladorOnline;
	private int outrasOrigens;
	
	private int creditocasa;
	private int emprestimoimobiliario;
	private int creditoimobiliario;
	private int garantiadeimovel;
	private int homeequity;
	private int emprestimocomgarantiadeimovel;
	
	private int novoLead;
	private int leadEmTratamento;
	
	private Responsavel responsavel;
	private String nomeResponsavel;
	private String gerenteResponsavel;
	private int totalNovosContratos;
	
	
	private int contratosCadastrados;
	private BigDecimal valorContratosCadastrados;
	private List<ContratoCobranca> listaCadastrados;
	
	private int contratosPreAprovados;
	private BigDecimal valorContratosPreAprovados;
	private List<ContratoCobranca> listaPreAprovados;
	
	private int contratosBoletosPagos;
	private BigDecimal valorBoletosPagos;
	private List<ContratoCobranca> listaBoletosPagos;
	
	private int contratosCcbsEmitidas;
	private BigDecimal valorCcbsEmitidas;
	private List<ContratoCobranca> listaCcbsEmitidas;
	
	private int contratosRegistrados;
	private BigDecimal valorContratosRegistrados;
	private List<ContratoCobranca> listaRegistrados;
	
	private int contratosComite;
	private BigDecimal valorContratosComite;
	private List<ContratoCobranca> listaComite;
	
	
	private int totalAprovados;
	private int totalReprovados;
	private int totalaEmAnalise;
	private int totalpago;
	private BigDecimal totalCCB;
	
	public Dashboard() {
	}

	public int getRefinanciamentoDeImovel() {
		return refinanciamentoDeImovel;
	}

	public void setRefinanciamentoDeImovel(int refinanciamentoDeImovel) {
		this.refinanciamentoDeImovel = refinanciamentoDeImovel;
	}

	public int getEmprestimoParaNegativados() {
		return emprestimoParaNegativados;
	}

	public void setEmprestimoParaNegativados(int emprestimoParaNegativados) {
		this.emprestimoParaNegativados = emprestimoParaNegativados;
	}

	public int getEmprestimoOnline() {
		return emprestimoOnline;
	}

	public void setEmprestimoOnline(int emprestimoOnline) {
		this.emprestimoOnline = emprestimoOnline;
	}

	public int getEmprestimoHomeEquity() {
		return emprestimoHomeEquity;
	}

	public void setEmprestimoHomeEquity(int emprestimoHomeEquity) {
		this.emprestimoHomeEquity = emprestimoHomeEquity;
	}

	public int getEmprestimoComTerrenoEmGarantia() {
		return emprestimoComTerrenoEmGarantia;
	}

	public void setEmprestimoComTerrenoEmGarantia(int emprestimoComTerrenoEmGarantia) {
		this.emprestimoComTerrenoEmGarantia = emprestimoComTerrenoEmGarantia;
	}

	public int getEmprestimoOnlineYT() {
		return emprestimoOnlineYT;
	}

	public void setEmprestimoOnlineYT(int emprestimoOnlineYT) {
		this.emprestimoOnlineYT = emprestimoOnlineYT;
	}

	public int getSimuladorOnline() {
		return simuladorOnline;
	}

	public void setSimuladorOnline(int simuladorOnline) {
		this.simuladorOnline = simuladorOnline;
	}

	public int getOutrasOrigens() {
		return outrasOrigens;
	}

	public void setOutrasOrigens(int outrasOrigens) {
		this.outrasOrigens = outrasOrigens;
	}

	public int getNovoLead() {
		return novoLead;
	}

	public void setNovoLead(int novoLead) {
		this.novoLead = novoLead;
	}

	public int getLeadEmTratamento() {
		return leadEmTratamento;
	}

	public void setLeadEmTratamento(int leadEmTratamento) {
		this.leadEmTratamento = leadEmTratamento;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
	
	public int getTotalNovosContratos() {
		return totalNovosContratos;
	}

	public void setTotalNovosContratos(int totalNovosContratos) {
		this.totalNovosContratos = totalNovosContratos;
	}

	public int getTotalAprovados() {
		return totalAprovados;
	}

	public void setTotalAprovados(int totalAprovados) {
		this.totalAprovados = totalAprovados;
	}

	public int getTotalReprovados() {
		return totalReprovados;
	}

	public void setTotalReprovados(int totalReprovados) {
		this.totalReprovados = totalReprovados;
	}

	public int getTotalaEmAnalise() {
		return totalaEmAnalise;
	}

	public void setTotalaEmAnalise(int totalaEmAnalise) {
		this.totalaEmAnalise = totalaEmAnalise;
	}

	public int getTotalpago() {
		return totalpago;
	}

	public void setTotalpago(int totalpago) {
		this.totalpago = totalpago;
	}

	public String getGerenteResponsavel() {
		return gerenteResponsavel;
	}

	public void setGerenteResponsavel(String gerenteResponsavel) {
		this.gerenteResponsavel = gerenteResponsavel;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public BigDecimal getTotalCCB() {
		return totalCCB;
	}

	public void setTotalCCB(BigDecimal totalCCB) {
		this.totalCCB = totalCCB;
	}

	public int getContratosCadastrados() {
		return contratosCadastrados;
	}

	public void setContratosCadastrados(int contratosCadastrados) {
		this.contratosCadastrados = contratosCadastrados;
	}

	public int getContratosPreAprovados() {
		return contratosPreAprovados;
	}

	public void setContratosPreAprovados(int contratosPreAprovados) {
		this.contratosPreAprovados = contratosPreAprovados;
	}

	public int getContratosBoletosPagos() {
		return contratosBoletosPagos;
	}

	public void setContratosBoletosPagos(int contratosBoletosPagos) {
		this.contratosBoletosPagos = contratosBoletosPagos;
	}

	public int getContratosCcbsEmitidas() {
		return contratosCcbsEmitidas;
	}

	public void setContratosCcbsEmitidas(int contratosCcbsEmitidas) {
		this.contratosCcbsEmitidas = contratosCcbsEmitidas;
	}

	public int getContratosRegistrados() {
		return contratosRegistrados;
	}

	public void setContratosRegistrados(int contratosRegistrados) {
		this.contratosRegistrados = contratosRegistrados;
	}

	public BigDecimal getValorContratosCadastrados() {
		return valorContratosCadastrados;
	}

	public void setValorContratosCadastrados(BigDecimal valorContratosCadastrados) {
		this.valorContratosCadastrados = valorContratosCadastrados;
	}

	public BigDecimal getValorContratosPreAprovados() {
		return valorContratosPreAprovados;
	}

	public void setValorContratosPreAprovados(BigDecimal valorContratosPreAprovados) {
		this.valorContratosPreAprovados = valorContratosPreAprovados;
	}

	public BigDecimal getValorBoletosPagos() {
		return valorBoletosPagos;
	}

	public void setValorBoletosPagos(BigDecimal valorBoletosPagos) {
		this.valorBoletosPagos = valorBoletosPagos;
	}

	public BigDecimal getValorCcbsEmitidas() {
		return valorCcbsEmitidas;
	}

	public void setValorCcbsEmitidas(BigDecimal valorCcbsEmitidas) {
		this.valorCcbsEmitidas = valorCcbsEmitidas;
	}

	public BigDecimal getValorContratosRegistrados() {
		return valorContratosRegistrados;
	}

	public void setValorContratosRegistrados(BigDecimal valorContratosRegistrados) {
		this.valorContratosRegistrados = valorContratosRegistrados;
	}

	public List<ContratoCobranca> getListaCadastrados() {
		return listaCadastrados;
	}

	public void setListaCadastrados(List<ContratoCobranca> listaCadastrados) {
		this.listaCadastrados = listaCadastrados;
	}

	public List<ContratoCobranca> getListaPreAprovados() {
		return listaPreAprovados;
	}

	public void setListaPreAprovados(List<ContratoCobranca> listaPreAprovados) {
		this.listaPreAprovados = listaPreAprovados;
	}

	public List<ContratoCobranca> getListaBoletosPagos() {
		return listaBoletosPagos;
	}

	public void setListaBoletosPagos(List<ContratoCobranca> listaBoletosPagos) {
		this.listaBoletosPagos = listaBoletosPagos;
	}

	public List<ContratoCobranca> getListaCcbsEmitidas() {
		return listaCcbsEmitidas;
	}

	public void setListaCcbsEmitidas(List<ContratoCobranca> listaCcbsEmitidas) {
		this.listaCcbsEmitidas = listaCcbsEmitidas;
	}

	public List<ContratoCobranca> getListaRegistrados() {
		return listaRegistrados;
	}

	public void setListaRegistrados(List<ContratoCobranca> listaRegistrados) {
		this.listaRegistrados = listaRegistrados;
	}

	public int getContratosComite() {
		return contratosComite;
	}

	public void setContratosComite(int contratosComite) {
		this.contratosComite = contratosComite;
	}

	public BigDecimal getValorContratosComite() {
		return valorContratosComite;
	}

	public void setValorContratosComite(BigDecimal valorContratosComite) {
		this.valorContratosComite = valorContratosComite;
	}

	public List<ContratoCobranca> getListaComite() {
		return listaComite;
	}

	public void setListaComite(List<ContratoCobranca> listaComite) {
		this.listaComite = listaComite;
	}

	public int getCreditocasa() {
		return creditocasa;
	}

	public void setCreditocasa(int creditocasa) {
		this.creditocasa = creditocasa;
	}

	public int getEmprestimoimobiliario() {
		return emprestimoimobiliario;
	}

	public void setEmprestimoimobiliario(int emprestimoimobiliario) {
		this.emprestimoimobiliario = emprestimoimobiliario;
	}

	public int getCreditoimobiliario() {
		return creditoimobiliario;
	}

	public void setCreditoimobiliario(int creditoimobiliario) {
		this.creditoimobiliario = creditoimobiliario;
	}

	public int getGarantiadeimovel() {
		return garantiadeimovel;
	}

	public void setGarantiadeimovel(int garantiadeimovel) {
		this.garantiadeimovel = garantiadeimovel;
	}

	public int getHomeequity() {
		return homeequity;
	}

	public void setHomeequity(int homeequity) {
		this.homeequity = homeequity;
	}

	public int getEmprestimocomgarantiadeimovel() {
		return emprestimocomgarantiadeimovel;
	}

	public void setEmprestimocomgarantiadeimovel(int emprestimocomgarantiadeimovel) {
		this.emprestimocomgarantiadeimovel = emprestimocomgarantiadeimovel;
	}	
}