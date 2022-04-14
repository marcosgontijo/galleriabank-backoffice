package com.webnowbr.siscoat.relatorioLaudoPaju;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

public class LaudoPajuVO {
	String numeroContrato;
	String nomePagador;
	BigDecimal valorTotal;
	BigDecimal valorPago;
	BigDecimal valorRestante;
	
	public String getNumeroContrato() {
		return numeroContrato;
	}
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
	public String getNomePagador() {
		return nomePagador;
	}
	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	public BigDecimal getValorPago() {
		return valorPago;
	}
	public void setValorPago(BigDecimal valorPago) {
		this.valorPago = valorPago;
	}
	public BigDecimal getValorRestante() {
		return valorRestante;
	}
	public void setValorRestante(BigDecimal valorRestante) {
		this.valorRestante = valorRestante;
	}
}