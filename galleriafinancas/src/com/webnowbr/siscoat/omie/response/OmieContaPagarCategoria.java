package com.webnowbr.siscoat.omie.response;

import java.math.BigDecimal;

public class OmieContaPagarCategoria {

	private BigDecimal vTotal;
	private BigDecimal nTotal;
	private String cCodCateg;
	private String cDescCateg;
	private String cIcone;
	private String cCor;
	
	public BigDecimal getvTotal() {
		return vTotal;
	}
	public void setvTotal(BigDecimal vTotal) {
		this.vTotal = vTotal;
	}
	public BigDecimal getnTotal() {
		return nTotal;
	}
	public void setnTotal(BigDecimal nTotal) {
		this.nTotal = nTotal;
	}
	public String getcCodCateg() {
		return cCodCateg;
	}
	public void setcCodCateg(String cCodCateg) {
		this.cCodCateg = cCodCateg;
	}
	public String getcDescCateg() {
		return cDescCateg;
	}
	public void setcDescCateg(String cDescCateg) {
		this.cDescCateg = cDescCateg;
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
