package com.webnowbr.siscoat.omie.response;

import java.math.BigDecimal;

public class OmieContaCorrente {

	private BigDecimal vTotal;
	private BigDecimal vLimiteCredito;
	private String cIcone;
	private String cCor;
	
	public BigDecimal getvTotal() {		
		return vTotal;
	}
	public void setvTotal(BigDecimal vTotal) {
		this.vTotal = vTotal;
	}
	public BigDecimal getvLimiteCredito() {
		return vLimiteCredito;
	}
	public void setvLimiteCredito(BigDecimal vLimiteCredito) {
		this.vLimiteCredito = vLimiteCredito;
	}
	public String getcIcone() {
		return cIcone;
	}
	public void setcIcone(String cIcone) {
		this.cIcone = cIcone;
	}
	public String getcCor() {
		return cCor;
	}
	public void setcCor(String cCor) {
		this.cCor = cCor;
	}
	
	

}
