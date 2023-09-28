package com.webnowbr.siscoat.omie.request;

public class ObterListaFinancasRequest implements IOmieParam {
	private String dDia;
	private String cCodCateg;
	private String cTipo;

	public String getdDia() {
		return dDia;
	}

	public void setdDia(String dDia) {
		this.dDia = dDia;
	}

	public String getcCodCateg() {
		return cCodCateg;
	}

	public void setcCodCateg(String cCodCateg) {
		this.cCodCateg = cCodCateg;
	}

	public String getcTipo() {
		return cTipo;
	}

	public void setcTipo(String cTipo) {
		this.cTipo = cTipo;
	}
}
