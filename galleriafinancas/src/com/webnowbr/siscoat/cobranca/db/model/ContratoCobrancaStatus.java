package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ContratoCobrancaStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4858733789805448599L;
	/**\
	 * 
	 */
	private ContratoCobranca contrato;
	private boolean ccbPronta;
	private boolean agAssinatura;
	private boolean agRegistro;
	private boolean pedidoLaudo;
	private boolean pajuFavoravel;
	private boolean laudoRecebido;
	private boolean analiseComercial;
	private boolean comentarioJuridicoEsteira;
	private boolean preAprovadoComite;
	private boolean documentosComite;
	private boolean aprovadoComite;
	private boolean documentosCompletos;
	private boolean reanalisePronta;
	private boolean pajuAtualizado;
	private boolean contratoConferido;
	private String contratoPreAprovado;
	private BigDecimal valorPreLaudo;
	private boolean notaFiscalEmitida;
	private boolean notaFiscalPaga;
	
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
	public BigDecimal getValorPreLaudo() {
		return valorPreLaudo;
	}
	public void setValorPreLaudo(BigDecimal valorPreLaudo) {
		this.valorPreLaudo = valorPreLaudo;
	}
	public boolean isPedidoLaudo() {
		return pedidoLaudo;
	}
	public void setPedidoLaudo(boolean pedidoLaudo) {
		this.pedidoLaudo = pedidoLaudo;
	}
	public boolean isDocumentosCompletos() {
		return documentosCompletos;
	}
	public void setDocumentosCompletos(boolean documentosCompletos) {
		this.documentosCompletos = documentosCompletos;
	}

	public boolean isContratoConferido() {
		return contratoConferido;
	}

	public void setContratoConferido(boolean contratoConferido) {
		this.contratoConferido = contratoConferido;
	}

	public boolean isReanalisePronta() {
		return reanalisePronta;
	}

	public void setReanalisePronta(boolean reanalisePronta) {
		this.reanalisePronta = reanalisePronta;
	}

	public boolean isPajuAtualizado() {
		return pajuAtualizado;
	}

	public void setPajuAtualizado(boolean pajuAtualizado) {
		this.pajuAtualizado = pajuAtualizado;
	}
	
	public boolean isNotaFiscalEmitida() {
		return this.notaFiscalEmitida;
	}
	
	public void setNotaFiscalEmitida( boolean notaFiscalEmitida ) {
		this.notaFiscalEmitida = notaFiscalEmitida;
	}
	
	public boolean isNotaFiscalPaga() {
		return this.notaFiscalPaga;
	}
	
	public void setNotaFiscalPaga( boolean notaFiscalPaga ) {
		this.notaFiscalPaga = notaFiscalPaga;
	}
	
}