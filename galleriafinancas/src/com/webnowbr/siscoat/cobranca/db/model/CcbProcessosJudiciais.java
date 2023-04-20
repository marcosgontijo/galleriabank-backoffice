package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CcbProcessosJudiciais implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	
	private BigDecimal valor = BigDecimal.ZERO;
	private String numero = "";
	private ContasPagar contaPagar;
	private ContratoCobranca contrato;
	private String quitar = "";
	
	public CcbProcessosJudiciais(BigDecimal valor, String numero) {
		contaPagar = new ContasPagar();
		this.valor = valor;
		this.numero = numero;
	}
	
	public CcbProcessosJudiciais() {
		contaPagar = new ContasPagar();
	}
	
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public ContasPagar getContaPagar() {
		return contaPagar;
	}

	public void setContaPagar(ContasPagar contaPagar) {
		this.contaPagar = contaPagar;
	}

	public String getQuitar() {
		return quitar;
	}

	public void setQuitar(String quitar) {
		this.quitar = quitar;
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}
	
}
