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
	
	private int novoLead;
	private int leadEmTratamento;
	
	private String nomeResponsavel;
	private int totalcontratos;
	private int totalaprovados;
	private int totalreprovados;
	private int totalagpagamento;
	private int totalpago;

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

	public int getTotalcontratos() {
		return totalcontratos;
	}

	public void setTotalcontratos(int totalcontratos) {
		this.totalcontratos = totalcontratos;
	}

	public int getTotalaprovados() {
		return totalaprovados;
	}

	public void setTotalaprovados(int totalaprovados) {
		this.totalaprovados = totalaprovados;
	}

	public int getTotalreprovados() {
		return totalreprovados;
	}

	public void setTotalreprovados(int totalreprovados) {
		this.totalreprovados = totalreprovados;
	}

	public int getTotalagpagamento() {
		return totalagpagamento;
	}

	public void setTotalagpagamento(int totalagpagamento) {
		this.totalagpagamento = totalagpagamento;
	}

	public int getTotalpago() {
		return totalpago;
	}

	public void setTotalpago(int totalpago) {
		this.totalpago = totalpago;
	}
}