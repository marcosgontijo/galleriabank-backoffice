package com.webnowbr.siscoat.boletosPagos.vo;

import java.math.BigDecimal;
import java.sql.Date;

public class BoletosPagosVO {
	Date dataBoletoPago;
	String numeroContrato;
	String valorBoleto;
	String valorContrato;
	
	
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
	public String getValorBoleto() {
		return valorBoleto;
	}
	public void setValorBoleto(String valorBoleto) {
		this.valorBoleto = valorBoleto;
	}
	public String getValorContrato() {
		return valorContrato;
	}
	public void setValorContrato(String valorContrato) {
		this.valorContrato = valorContrato;
	}
	
	
}
