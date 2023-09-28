package com.webnowbr.siscoat.omie.response;

import java.math.BigDecimal;

public class OmieContaReceber {
	private BigDecimal nTotal;
	private BigDecimal vTotal;
	private BigDecimal vAtraso;
	private String cIcone;
	private String cCor;
	
	
	public BigDecimal getnTotal() {
		return nTotal;
	}
	public void setnTotal(BigDecimal nTotal) {
		this.nTotal = nTotal;
	}
	public BigDecimal getvTotal() {
		return vTotal;
	}
	public void setvTotal(BigDecimal vTotal) {
		this.vTotal = vTotal;
	}
	public BigDecimal getvAtraso() {
		return vAtraso;
	}
	public void setvAtraso(BigDecimal vAtraso) {
		this.vAtraso = vAtraso;
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
