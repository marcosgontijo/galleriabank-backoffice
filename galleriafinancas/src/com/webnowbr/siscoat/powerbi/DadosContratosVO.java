package com.webnowbr.siscoat.powerbi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;

public class DadosContratosVO {
	
	private BigDecimal vlrParcelasAtraso;
	private BigDecimal vlrParcelasPagas;
	private BigDecimal vlrParcelasQuitadas;
	
	private BigDecimal vlrContratosQuitados;
	private BigDecimal vlrContratosPagos;
	private BigDecimal vlrContratosAtraso;
	
	private int numeroContratosAtraso;
	private int numeroContratosPagas;
	private int numeroContratosQuitados;
	
	private Collection<ContratoCobranca> contratosAtraso;
	private Collection<ContratoCobranca> contratosPagos;
	private Collection<ContratoCobranca> contratosQuitados;
	
	public DadosContratosVO() {
		super();
		this.contratosAtraso = new ArrayList<ContratoCobranca>();
		this.contratosPagos = new ArrayList<ContratoCobranca>();
		this.contratosQuitados = new ArrayList<ContratoCobranca>();
	}
	
	public BigDecimal getVlrParcelasAtraso() {
		return vlrParcelasAtraso;
	}
	public void setVlrParcelasAtraso(BigDecimal vlrParcelasAtraso) {
		this.vlrParcelasAtraso = vlrParcelasAtraso;
	}
	public BigDecimal getVlrParcelasPagas() {
		return vlrParcelasPagas;
	}
	public void setVlrParcelasPagas(BigDecimal vlrParcelasPagas) {
		this.vlrParcelasPagas = vlrParcelasPagas;
	}
	public BigDecimal getVlrContratosQuitados() {
		return vlrContratosQuitados;
	}
	public void setVlrContratosQuitados(BigDecimal vlrContratosQuitados) {
		this.vlrContratosQuitados = vlrContratosQuitados;
	}
	public int getNumeroContratosAtraso() {
		return numeroContratosAtraso;
	}
	public void setNumeroContratosAtraso(int numeroContratosAtraso) {
		this.numeroContratosAtraso = numeroContratosAtraso;
	}
	public int getNumeroContratosPagas() {
		return numeroContratosPagas;
	}
	public void setNumeroContratosPagas(int numeroContratosPagas) {
		this.numeroContratosPagas = numeroContratosPagas;
	}
	public int getNumeroContratosQuitados() {
		return numeroContratosQuitados;
	}
	public void setNumeroContratosQuitados(int numeroContratosQuitados) {
		this.numeroContratosQuitados = numeroContratosQuitados;
	}
	public BigDecimal getVlrParcelasQuitadas() {
		return vlrParcelasQuitadas;
	}
	public void setVlrParcelasQuitadas(BigDecimal vlrParcelasQuitadas) {
		this.vlrParcelasQuitadas = vlrParcelasQuitadas;
	}
	public BigDecimal getVlrContratosPagos() {
		return vlrContratosPagos;
	}
	public void setVlrContratosPagos(BigDecimal vlrContratosPagos) {
		this.vlrContratosPagos = vlrContratosPagos;
	}
	public BigDecimal getVlrContratosAtraso() {
		return vlrContratosAtraso;
	}
	public void setVlrContratosAtraso(BigDecimal vlrContratosAtraso) {
		this.vlrContratosAtraso = vlrContratosAtraso;
	}
	public Collection<ContratoCobranca> getContratosAtraso() {
		return contratosAtraso;
	}
	public void setContratosAtraso(Collection<ContratoCobranca> contratosAtraso) {
		this.contratosAtraso = contratosAtraso;
	}
	public Collection<ContratoCobranca> getContratosPagos() {
		return contratosPagos;
	}
	public void setContratosPagos(Collection<ContratoCobranca> contratosPagos) {
		this.contratosPagos = contratosPagos;
	}
	public Collection<ContratoCobranca> getContratosQuitados() {
		return contratosQuitados;
	}
	public void setContratosQuitados(Collection<ContratoCobranca> contratosQuitados) {
		this.contratosQuitados = contratosQuitados;
	}
}
