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
	
	private Responsavel responsavel;
	private String nomeResponsavel;
	private String gerenteResponsavel;
	private int totalNovosContratos;
	private int totalAprovados;
	private int totalReprovados;
	private int totalaEmAnalise;
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
}