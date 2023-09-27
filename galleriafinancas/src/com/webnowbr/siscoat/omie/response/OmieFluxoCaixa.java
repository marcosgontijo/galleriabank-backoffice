package com.webnowbr.siscoat.omie.response;

import java.math.BigDecimal;

public class OmieFluxoCaixa {

	private String dDia;
	private BigDecimal vPagar;
	private BigDecimal vReceber;
	private BigDecimal vSaldo;
	
	public String getdDia() {
		return dDia;
	}
	public void setdDia(String dDia) {
		this.dDia = dDia;
	}
	public BigDecimal getvPagar() {
		return vPagar;
	}
	public void setvPagar(BigDecimal vPagar) {
		this.vPagar = vPagar;
	}
	public BigDecimal getvReceber() {
		return vReceber;
	}
	public void setvReceber(BigDecimal vReceber) {
		this.vReceber = vReceber;
	}
	public BigDecimal getvSaldo() {
		return vSaldo;
	}
	public void setvSaldo(BigDecimal vSaldo) {
		this.vSaldo = vSaldo;
	}

	
}
