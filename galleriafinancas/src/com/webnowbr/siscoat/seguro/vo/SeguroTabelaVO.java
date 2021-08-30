package com.webnowbr.siscoat.seguro.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

public class SeguroTabelaVO {
	String numeroContratoSeguro = "1";
	String codigoSegurado = "1";
	BigInteger  parcelasOriginais = BigInteger.ONE;
	BigInteger parcelasFaltantes = BigInteger.ONE;
	BigDecimal avaliacao = BigDecimal.ONE;
	String CPFPrincipal = "1";
	String nomePrincipal = "1";
	BigDecimal porcentagemPrincipal = BigDecimal.ONE;
	
	
	
	
	
	public String getNumeroContratoSeguro() {
		return numeroContratoSeguro;
	}
	public void setNumeroContratoSeguro(String numeroContratoSeguro) {
		this.numeroContratoSeguro = numeroContratoSeguro;
	}
	public String getCodigoSegurado() {
		return codigoSegurado;
	}
	public void setCodigoSegurado(String codigoSegurado) {
		this.codigoSegurado = codigoSegurado;
	}
	public BigInteger getParcelasOriginais() {
		return parcelasOriginais;
	}
	public void setParcelasOriginais(BigInteger parcelasOriginais) {
		this.parcelasOriginais = parcelasOriginais;
	}
	public BigInteger getParcelasFaltantes() {
		return parcelasFaltantes;
	}
	public void setParcelasFaltantes(BigInteger parcelasFaltantes) {
		this.parcelasFaltantes = parcelasFaltantes;
	}
	public BigDecimal getAvaliacao() {
		return avaliacao;
	}
	public void setAvaliacao(BigDecimal avaliacao) {
		this.avaliacao = avaliacao;
	}
	public String getCPFPrincipal() {
		return CPFPrincipal;
	}
	public void setCPFPrincipal(String cPFPrincipal) {
		CPFPrincipal = cPFPrincipal;
	}
	public String getNomePrincipal() {
		return nomePrincipal;
	}
	public void setNomePrincipal(String nomePrincipal) {
		this.nomePrincipal = nomePrincipal;
	}
	public BigDecimal getPorcentagemPrincipal() {
		return porcentagemPrincipal;
	}
	public void setPorcentagemPrincipal(BigDecimal porcentagemPrincipal) {
		this.porcentagemPrincipal = porcentagemPrincipal;
	}	
}