package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;

public class ContratoCobrancaStatus implements Serializable {

	/**
	 * 
	 */
	private ContratoCobranca contrato;
	private boolean ccbPronta;
	private boolean agAssinatura;
	private boolean agRegistro;
	private boolean pajuFavoravel;
	private boolean laudoRecebido;
	private boolean analiseComercial;
	private boolean comentarioJuridicoEsteira;
	private boolean preAprovadoComite;
	private boolean documentosComite;
	private boolean aprovadoComite;
	private String contratoPreAprovado;
	
	public ContratoCobrancaStatus() {
		
	}
	
	public ContratoCobranca getContrato() {
		return contrato;
	}
	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}
	public boolean isCcbPronta() {
		return ccbPronta;
	}
	public void setCcbPronta(boolean ccbPronta) {
		this.ccbPronta = ccbPronta;
	}
	public boolean isAgAssinatura() {
		return agAssinatura;
	}
	public void setAgAssinatura(boolean agAssinatura) {
		this.agAssinatura = agAssinatura;
	}
	public boolean isAgRegistro() {
		return agRegistro;
	}
	public void setAgRegistro(boolean agRegistro) {
		this.agRegistro = agRegistro;
	}
	public boolean isPajuFavoravel() {
		return pajuFavoravel;
	}
	public void setPajuFavoravel(boolean pajuFavoravel) {
		this.pajuFavoravel = pajuFavoravel;
	}
	public boolean isLaudoRecebido() {
		return laudoRecebido;
	}
	public void setLaudoRecebido(boolean laudoRecebido) {
		this.laudoRecebido = laudoRecebido;
	}
	public boolean isPreAprovadoComite() {
		return preAprovadoComite;
	}
	public void setPreAprovadoComite(boolean preAprovadoComite) {
		this.preAprovadoComite = preAprovadoComite;
	}
	public String getContratoPreAprovado() {
		return contratoPreAprovado;
	}
	public void setContratoPreAprovado(String contratoPreAprovado) {
		this.contratoPreAprovado = contratoPreAprovado;
	}
	public boolean isDocumentosComite() {
		return documentosComite;
	}
	public void setDocumentosComite(boolean documentosComite) {
		this.documentosComite = documentosComite;
	}
	public boolean isAnaliseComercial() {
		return analiseComercial;
	}
	public void setAnaliseComercial(boolean analiseComercial) {
		this.analiseComercial = analiseComercial;
	}
	public boolean isComentarioJuridicoEsteira() {
		return comentarioJuridicoEsteira;
	}
	public void setComentarioJuridicoEsteira(boolean comentarioJuridicoEsteira) {
		this.comentarioJuridicoEsteira = comentarioJuridicoEsteira;
	}
	public boolean isAprovadoComite() {
		return aprovadoComite;
	}
	public void setAprovadoComite(boolean aprovadoComite) {
		this.aprovadoComite = aprovadoComite;
	}
}