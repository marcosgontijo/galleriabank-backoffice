package com.webnowbr.siscoat.boletosPagos.vo;

import java.math.BigDecimal;
import java.sql.Date;

public class BoletosPagosVO {
	Date dataBoletoPago;
	String numeroContrato;
	BigDecimal valorBoleto;
	BigDecimal valorContrato;
	
	
	public Date getDataBoletoPago() {
		return dataBoletoPago;
	}
	public void setDataBoletoPago(Date dataBoletoPago) {
		this.dataBoletoPago = dataBoletoPago;
	}
	public String getNumeroContrato() {
		return numeroContrato;
	}
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
	public BigDecimal getValorBoleto() {
		return valorBoleto;
	}
	public void setValorBoleto(BigDecimal valorBoleto) {
		this.valorBoleto = valorBoleto;
	}
	public BigDecimal getValorContrato() {
		return valorContrato;
	}
	public void setValorContrato(BigDecimal valorContrato) {
		this.valorContrato = valorContrato;
	}
	
	
	
}
