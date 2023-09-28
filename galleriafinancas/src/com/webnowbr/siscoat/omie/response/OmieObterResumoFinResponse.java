package com.webnowbr.siscoat.omie.response;

import java.util.List;

public class OmieObterResumoFinResponse {
	
	private String cDia;
	
	OmieContaCorrente contaCorrente;
	OmieContaPagar contaPagar;
	OmieContaReceber contaReceber;
	List<OmieContaPagarCategoria> contaPagarCategoria;
	List<OmieContaReceberCategoria> contaReceberCategoria;
	List<OmieContaPagarAtraso> contaPagarAtraso;
	List<OmieContaReceberAtraso> contaReceberAtraso;
	List<OmieFluxoCaixa> fluxoCaixa;
	
	public String getcDia() {
		return cDia;
	}
	public void setcDia(String cDia) {
		this.cDia = cDia;
	}
	public OmieContaCorrente getContaCorrente() {
		return contaCorrente;
	}
	public void setContaCorrente(OmieContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}
	public OmieContaPagar getContaPagar() {
		return contaPagar;
	}
	public void setContaPagar(OmieContaPagar contaPagar) {
		this.contaPagar = contaPagar;
	}
	public OmieContaReceber getContaReceber() {
		return contaReceber;
	}
	public void setContaReceber(OmieContaReceber contaReceber) {
		this.contaReceber = contaReceber;
	}
	public List<OmieContaPagarCategoria> getContaPagarCategoria() {
		return contaPagarCategoria;
	}
	public void setContaPagarCategoria(List<OmieContaPagarCategoria> contaPagarCategoria) {
		this.contaPagarCategoria = contaPagarCategoria;
	}
	public List<OmieContaReceberCategoria> getContaReceberCategoria() {
		return contaReceberCategoria;
	}
	public void setContaReceberCategoria(List<OmieContaReceberCategoria> contaReceberCategoria) {
		this.contaReceberCategoria = contaReceberCategoria;
	}
	public List<OmieContaPagarAtraso> getContaPagarAtraso() {
		return contaPagarAtraso;
	}
	public void setContaPagarAtraso(List<OmieContaPagarAtraso> contaPagarAtraso) {
		this.contaPagarAtraso = contaPagarAtraso;
	}
	public List<OmieContaReceberAtraso> getContaReceberAtraso() {
		return contaReceberAtraso;
	}
	public void setContaReceberAtraso(List<OmieContaReceberAtraso> contaReceberAtraso) {
		this.contaReceberAtraso = contaReceberAtraso;
	}
	public List<OmieFluxoCaixa> getFluxoCaixa() {
		return fluxoCaixa;
	}
	public void setFluxoCaixa(List<OmieFluxoCaixa> fluxoCaixa) {
		this.fluxoCaixa = fluxoCaixa;
	}
	
	
	
}
