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
	private boolean preAprovadoComite;
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
}