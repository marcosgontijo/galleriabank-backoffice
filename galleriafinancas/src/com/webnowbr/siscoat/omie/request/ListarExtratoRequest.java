package com.webnowbr.siscoat.omie.request;

public class ListarExtratoRequest implements IOmieParam {
	private Long nCodCC;
	private String cCodIntCC;
	private String dPeriodoInicial;
	private String dPeriodoFinal;
	private String cExibirApenasSaldo;
	
	
	
	public Long getnCodCC() {
		return nCodCC;
	}
	public void setnCodCC(Long nCodCC) {
		this.nCodCC = nCodCC;
	}
	public String getcCodIntCC() {
		return cCodIntCC;
	}
	public void setcCodIntCC(String cCodIntCC) {
		this.cCodIntCC = cCodIntCC;
	}
	public String getdPeriodoInicial() {
		return dPeriodoInicial;
	}
	public void setdPeriodoInicial(String dPeriodoInicial) {
		this.dPeriodoInicial = dPeriodoInicial;
	}
	public String getdPeriodoFinal() {
		return dPeriodoFinal;
	}
	public void setdPeriodoFinal(String dPeriodoFinal) {
		this.dPeriodoFinal = dPeriodoFinal;
	}
	public String getcExibirApenasSaldo() {
		return cExibirApenasSaldo;
	}
	public void setcExibirApenasSaldo(String cExibirApenasSaldo) {
		this.cExibirApenasSaldo = cExibirApenasSaldo;
	}

	
}
