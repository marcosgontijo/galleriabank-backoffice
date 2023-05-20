package com.webnowbr.siscoat.cobranca.model.bmpdigital;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.google.gson.annotations.SerializedName;
import com.webnowbr.siscoat.common.CommonsUtil;

public class ResumoDoCliente {

	@SerializedName("CnpjDaIfSolicitante")
	private String cnpjDaIfSolicitante;
	
	@SerializedName("CodigoDoCliente")
	private String codigoDoCliente;
	
	@SerializedName("CoobrigacaoAssumida")
	private BigDecimal coobrigacaoAssumida;
	
	@SerializedName("CoobrigacaoAssumidaSpecified")
	private boolean coobrigacaoAssumidaSpecified;
	
	@SerializedName("CoobrigacaoRecebida")
	private BigDecimal coobrigacaoRecebida;
	
	@SerializedName("CoobrigacaoRecebidaSpecified")
	private boolean coobrigacaoRecebidaSpecified;
	
	@SerializedName("DataBaseConsultada")
	private String dataBaseConsultada;
	
	@SerializedName("DataInicioRelacionamento")
	private String dataInicioRelacionamento;
	
	@SerializedName("ListaDeMensagensDeValidacao")
	private List<BcMsgRetorno> listaDeMensagensDeValidacao;
	
	@SerializedName("ListaDeResumoDasOperacoes")
	private List<ResumoDaOperacao> listaDeResumoDasOperacoes;
	
	@SerializedName("PercentualDocumentosProcessados")
	private String percentualDocumentosProcessados;
	
	@SerializedName("PercentualVolumeProcessado")
	private String percentualVolumeProcessado;
	
	@SerializedName("QuantidadeDeInstituicoes")
	private int quantidadeDeInstituicoes;
	
	@SerializedName("QuantidadeDeOperacoes")
	private int quantidadeDeOperacoes;
	
//	@SerializedName("QuantidadeOperacoesSubJudice")
//	private int quantidadeOperacoesDiscordancia;
	
	@SerializedName("QuantidadeOperacoesSubJudice")
	private int quantidadeOperacoesSubJudice;
	
//	@SerializedName("CoobrigacaoRecebida")
//	private BigDecimal responsabilidadeTotalDiscordancia;
	
	@SerializedName("ResponsabilidadeTotalDiscordanciaSpecified")
	private boolean responsabilidadeTotalDiscordanciaSpecified;
	
	@SerializedName("ResponsabilidadeTotalSubJudice")
	private BigDecimal responsabilidadeTotalSubJudice;
	
	@SerializedName("ResponsabilidadeTotalSubJudiceSpecified")
	private boolean responsabilidadeTotalSubJudiceSpecified;
	
	@SerializedName("RiscoIndiretoVendor")
	private BigDecimal riscoIndiretoVendor;
	
	@SerializedName("RiscoIndiretoVendorSpecified")
	private boolean riscoIndiretoVendorSpecified;
	
	@SerializedName("TipoDoCliente")
	private String tipoDoCliente;
	
	public ResumoDoCliente() {
	}

	public String getCnpjDaIfSolicitante() {
		return cnpjDaIfSolicitante;
	}

	public void setCnpjDaIfSolicitante(String cnpjDaIfSolicitante) {
		this.cnpjDaIfSolicitante = cnpjDaIfSolicitante;
	}

	public String getCodigoDoCliente() {
		return codigoDoCliente;
	}

	public void setCodigoDoCliente(String codigoDoCliente) {
		this.codigoDoCliente = codigoDoCliente;
	}

	public BigDecimal getCoobrigacaoAssumida() {
		return coobrigacaoAssumida;
	}

	public void setCoobrigacaoAssumida(BigDecimal coobrigacaoAssumida) {
		this.coobrigacaoAssumida = coobrigacaoAssumida;
	}

	public boolean isCoobrigacaoAssumidaSpecified() {
		return coobrigacaoAssumidaSpecified;
	}

	public void setCoobrigacaoAssumidaSpecified(boolean coobrigacaoAssumidaSpecified) {
		this.coobrigacaoAssumidaSpecified = coobrigacaoAssumidaSpecified;
	}

	public BigDecimal getCoobrigacaoRecebida() {
		return coobrigacaoRecebida;
	}

	public void setCoobrigacaoRecebida(BigDecimal coobrigacaoRecebida) {
		this.coobrigacaoRecebida = coobrigacaoRecebida;
	}

	public boolean isCoobrigacaoRecebidaSpecified() {
		return coobrigacaoRecebidaSpecified;
	}

	public void setCoobrigacaoRecebidaSpecified(boolean coobrigacaoRecebidaSpecified) {
		this.coobrigacaoRecebidaSpecified = coobrigacaoRecebidaSpecified;
	}

	public String getDataBaseConsultada() {
		return dataBaseConsultada;
	}

	public void setDataBaseConsultada(String dataBaseConsultada) {
		this.dataBaseConsultada = dataBaseConsultada;
	}

	public String getDataInicioRelacionamento() {
		return dataInicioRelacionamento;
	}

	public void setDataInicioRelacionamento(String dataInicioRelacionamento) {
		this.dataInicioRelacionamento = dataInicioRelacionamento;
	}

	public List<BcMsgRetorno> getListaDeMensagensDeValidacao() {
		return listaDeMensagensDeValidacao;
	}

	public void setListaDeMensagensDeValidacao(List<BcMsgRetorno> listaDeMensagensDeValidacao) {
		this.listaDeMensagensDeValidacao = listaDeMensagensDeValidacao;
	}

	public List<ResumoDaOperacao> getListaDeResumoDasOperacoes() {
		return listaDeResumoDasOperacoes;
	}

	public void setListaDeResumoDasOperacoes(List<ResumoDaOperacao> listaDeResumoDasOperacoes) {
		this.listaDeResumoDasOperacoes = listaDeResumoDasOperacoes;
	}

	public String getPercentualDocumentosProcessados() {
		return percentualDocumentosProcessados;
	}

	public void setPercentualDocumentosProcessados(String percentualDocumentosProcessados) {
		this.percentualDocumentosProcessados = percentualDocumentosProcessados;
	}

	public String getPercentualVolumeProcessado() {
		return percentualVolumeProcessado;
	}

	public void setPercentualVolumeProcessado(String percentualVolumeProcessado) {
		this.percentualVolumeProcessado = percentualVolumeProcessado;
	}

	public int getQuantidadeDeInstituicoes() {
		return quantidadeDeInstituicoes;
	}

	public void setQuantidadeDeInstituicoes(int quantidadeDeInstituicoes) {
		this.quantidadeDeInstituicoes = quantidadeDeInstituicoes;
	}

	public int getQuantidadeDeOperacoes() {
		return quantidadeDeOperacoes;
	}

	public void setQuantidadeDeOperacoes(int quantidadeDeOperacoes) {
		this.quantidadeDeOperacoes = quantidadeDeOperacoes;
	}
//
//	public int getQuantidadeOperacoesDiscordancia() {
//		if (CommonsUtil.semValor(quantidadeOperacoesDiscordancia)) {
//			return quantidadeOperacoesSubJudice;
//		}
//		return quantidadeOperacoesDiscordancia;
//	}
//
//	public void setQuantidadeOperacoesDiscordancia(int quantidadeOperacoesDiscordancia) {
//		this.quantidadeOperacoesDiscordancia = quantidadeOperacoesDiscordancia;
//	}

	public int getQuantidadeOperacoesSubJudice() {
		return quantidadeOperacoesSubJudice;
	}

	public void setQuantidadeOperacoesSubJudice(int quantidadeOperacoesSubJudice) {
		this.quantidadeOperacoesSubJudice = quantidadeOperacoesSubJudice;
	}

//	public BigDecimal getResponsabilidadeTotalDiscordancia() {
//		return responsabilidadeTotalDiscordancia;
//	}
//
//	public void setResponsabilidadeTotalDiscordancia(BigDecimal responsabilidadeTotalDiscordancia) {
//		this.responsabilidadeTotalDiscordancia = responsabilidadeTotalDiscordancia;
//	}

	public boolean isResponsabilidadeTotalDiscordanciaSpecified() {
		return responsabilidadeTotalDiscordanciaSpecified;
	}

	public void setResponsabilidadeTotalDiscordanciaSpecified(boolean responsabilidadeTotalDiscordanciaSpecified) {
		this.responsabilidadeTotalDiscordanciaSpecified = responsabilidadeTotalDiscordanciaSpecified;
	}

	public BigDecimal getResponsabilidadeTotalSubJudice() {
		return responsabilidadeTotalSubJudice;
	}

	public void setResponsabilidadeTotalSubJudice(BigDecimal responsabilidadeTotalSubJudice) {
		this.responsabilidadeTotalSubJudice = responsabilidadeTotalSubJudice;
	}

	public boolean isResponsabilidadeTotalSubJudiceSpecified() {
		return responsabilidadeTotalSubJudiceSpecified;
	}

	public void setResponsabilidadeTotalSubJudiceSpecified(boolean responsabilidadeTotalSubJudiceSpecified) {
		this.responsabilidadeTotalSubJudiceSpecified = responsabilidadeTotalSubJudiceSpecified;
	}

	public BigDecimal getRiscoIndiretoVendor() {
		return riscoIndiretoVendor;
	}

	public void setRiscoIndiretoVendor(BigDecimal riscoIndiretoVendor) {
		this.riscoIndiretoVendor = riscoIndiretoVendor;
	}

	public boolean isRiscoIndiretoVendorSpecified() {
		return riscoIndiretoVendorSpecified;
	}

	public void setRiscoIndiretoVendorSpecified(boolean riscoIndiretoVendorSpecified) {
		this.riscoIndiretoVendorSpecified = riscoIndiretoVendorSpecified;
	}

	public String getTipoDoCliente() {
		return tipoDoCliente;
	}

	public void setTipoDoCliente(String tipoDoCliente) {
		this.tipoDoCliente = tipoDoCliente;
	}
}
