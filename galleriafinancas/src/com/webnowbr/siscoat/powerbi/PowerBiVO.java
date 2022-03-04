package com.webnowbr.siscoat.powerbi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.poi.ss.formula.functions.FinanceLib;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.common.DateUtil;


public class PowerBiVO {
	
	public BigInteger numeroOperacoesCadastradas;
	public BigInteger numeroNovosLeadsCadastrados;
	public BigInteger numeroLeadsCompletos;
	public BigInteger numeroOperacoesInicioAnalise;
	public BigInteger numeroOperacoesAnalisadas;
	public BigInteger numeroOperacoesAssinadas;
	public BigInteger numeroOperacoesRegistradas;
	
	public BigDecimal valorOperacoesCadastradas;
	public BigDecimal valorNovosLeadsCadastrados;
	public BigDecimal valorLeadsCompletos;
	public BigDecimal valorOperacoesInicioAnalise;
	public BigDecimal valorOperacoesAnalisadas;
	public BigDecimal valorOperacoesAssinadas;
	public BigDecimal valorOperacoesRegistradas;
	
	private Date dataConsulta;
	
	private DadosContratosVO fidc;
	private DadosContratosVO securitizadora;	
	
	private List<PowerBiDetalhes> analises = new ArrayList<PowerBiDetalhes>();
	private List<PowerBiDetalhes> preAprovacoes = new ArrayList<PowerBiDetalhes>();
	private List<PowerBiDetalhes> assinaturas = new ArrayList<PowerBiDetalhes>();
	private List<PowerBiDetalhes> registros = new ArrayList<PowerBiDetalhes>();
	
	public PowerBiVO() {
		super();
		this.fidc = new DadosContratosVO();
		this.securitizadora = new DadosContratosVO();
	}
	
	public BigInteger getNumeroOperacoesCadastradas() {
		return numeroOperacoesCadastradas;
	}

	public void setNumeroOperacoesCadastradas(BigInteger numeroOperacoesCadastradas) {
		this.numeroOperacoesCadastradas = numeroOperacoesCadastradas;
	}

	public BigInteger getNumeroOperacoesAssinadas() {
		return numeroOperacoesAssinadas;
	}

	public void setNumeroOperacoesAssinadas(BigInteger numeroOperacoesAssinadas) {
		this.numeroOperacoesAssinadas = numeroOperacoesAssinadas;
	}

	public BigInteger getNumeroOperacoesRegistradas() {
		return numeroOperacoesRegistradas;
	}

	public void setNumeroOperacoesRegistradas(BigInteger numeroOperacoesRegistradas) {
		this.numeroOperacoesRegistradas = numeroOperacoesRegistradas;
	}

	public BigDecimal getValorOperacoesCadastradas() {
		return valorOperacoesCadastradas;
	}

	public void setValorOperacoesCadastradas(BigDecimal valorOperacoesCadastradas) {
		this.valorOperacoesCadastradas = valorOperacoesCadastradas;
	}

	public BigDecimal getValorOperacoesAssinadas() {
		return valorOperacoesAssinadas;
	}

	public void setValorOperacoesAssinadas(BigDecimal valorOperacoesAssinadas) {
		this.valorOperacoesAssinadas = valorOperacoesAssinadas;
	}

	public BigDecimal getValorOperacoesRegistradas() {
		return valorOperacoesRegistradas;
	}

	public void setValorOperacoesRegistradas(BigDecimal valorOperacoesRegistradas) {
		this.valorOperacoesRegistradas = valorOperacoesRegistradas;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public DadosContratosVO getFidc() {
		return fidc;
	}

	public void setFidc(DadosContratosVO fidc) {
		this.fidc = fidc;
	}

	public DadosContratosVO getSecuritizadora() {
		return securitizadora;
	}

	public void setSecuritizadora(DadosContratosVO securitizadora) {
		this.securitizadora = securitizadora;
	}

	public BigInteger getNumeroOperacoesAnalisadas() {
		return numeroOperacoesAnalisadas;
	}

	public void setNumeroOperacoesAnalisadas(BigInteger numeroOperacoesAnalisadas) {
		this.numeroOperacoesAnalisadas = numeroOperacoesAnalisadas;
	}

	public BigDecimal getValorOperacoesAnalisadas() {
		return valorOperacoesAnalisadas;
	}

	public void setValorOperacoesAnalisadas(BigDecimal valorOperacoesAnalisadas) {
		this.valorOperacoesAnalisadas = valorOperacoesAnalisadas;
	}

	public BigInteger getNumeroOperacoesInicioAnalise() {
		return numeroOperacoesInicioAnalise;
	}

	public void setNumeroOperacoesInicioAnalise(BigInteger numeroOperacoesInicioAnalise) {
		this.numeroOperacoesInicioAnalise = numeroOperacoesInicioAnalise;
	}

	public BigDecimal getValorOperacoesInicioAnalise() {
		return valorOperacoesInicioAnalise;
	}

	public void setValorOperacoesInicioAnalise(BigDecimal valorOperacoesInicioAnalise) {
		this.valorOperacoesInicioAnalise = valorOperacoesInicioAnalise;
	}

	public List<PowerBiDetalhes> getAnalises() {
		return analises;
	}

	public void setAnalises(List<PowerBiDetalhes> analises) {
		this.analises = analises;
	}

	public List<PowerBiDetalhes> getPreAprovacoes() {
		return preAprovacoes;
	}

	public void setPreAprovacoes(List<PowerBiDetalhes> preAprovacoes) {
		this.preAprovacoes = preAprovacoes;
	}

	public List<PowerBiDetalhes> getAssinaturas() {
		return assinaturas;
	}

	public void setAssinaturas(List<PowerBiDetalhes> assinaturas) {
		this.assinaturas = assinaturas;
	}

	public List<PowerBiDetalhes> getRegistros() {
		return registros;
	}

	public void setRegistros(List<PowerBiDetalhes> registros) {
		this.registros = registros;
	}

	public BigInteger getNumeroNovosLeadsCadastrados() {
		return numeroNovosLeadsCadastrados;
	}

	public void setNumeroNovosLeadsCadastrados(BigInteger numeroNovosLeadsCadastrados) {
		this.numeroNovosLeadsCadastrados = numeroNovosLeadsCadastrados;
	}

	public BigDecimal getValorNovosLeadsCadastrados() {
		return valorNovosLeadsCadastrados;
	}

	public void setValorNovosLeadsCadastrados(BigDecimal valorNovosLeadsCadastrados) {
		this.valorNovosLeadsCadastrados = valorNovosLeadsCadastrados;
	}

	public BigInteger getNumeroLeadsCompletos() {
		return numeroLeadsCompletos;
	}

	public void setNumeroLeadsCompletos(BigInteger numeroLeadsCompletos) {
		this.numeroLeadsCompletos = numeroLeadsCompletos;
	}

	public BigDecimal getValorLeadsCompletos() {
		return valorLeadsCompletos;
	}

	public void setValorLeadsCompletos(BigDecimal valorLeadsCompletos) {
		this.valorLeadsCompletos = valorLeadsCompletos;
	}
	
	
}
