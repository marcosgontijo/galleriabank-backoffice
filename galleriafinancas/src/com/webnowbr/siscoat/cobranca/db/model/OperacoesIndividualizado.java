package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OperacoesIndividualizado implements Serializable {

	private static final long serialVersionUID = 1L;
	private ContratoCobranca contrato;
	private String cedente;
	private long prazoMedio;
	private BigDecimal valorBruto;
	private BigDecimal desagio;
	private BigDecimal valorLiquido;
	
	public OperacoesIndividualizado(){
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public String getCedente() {
		return cedente;
	}

	public void setCedente(String cedente) {
		this.cedente = cedente;
	}

	public long getPrazoMedio() {
		return prazoMedio;
	}

	public void setPrazoMedio(long prazoMedio) {
		this.prazoMedio = prazoMedio;
	}

	public BigDecimal getValorBruto() {
		return valorBruto;
	}

	public void setValorBruto(BigDecimal valorBruto) {
		this.valorBruto = valorBruto;
	}

	public BigDecimal getDesagio() {
		return desagio;
	}

	public void setDesagio(BigDecimal desagio) {
		this.desagio = desagio;
	}

	public BigDecimal getValorLiquido() {
		return valorLiquido;
	}

	public void setValorLiquido(BigDecimal valorLiquido) {
		this.valorLiquido = valorLiquido;
	}
}