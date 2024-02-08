package com.webnowbr.siscoat.credlocaliza.response;

public class CredlocalizaVeiculo {

	private CredlocalizaCabecalho cabecalho;
	private CredlocalizaProdutosConsultados produtos_consultados;
	private CredlocalizaFrota frota;
	private String informacoes_adcionais;
	
	public CredlocalizaCabecalho getCabecalho() {
		return cabecalho;
	}
	public void setCabecalho(CredlocalizaCabecalho cabecalho) {
		this.cabecalho = cabecalho;
	}
	public CredlocalizaProdutosConsultados getProdutos_consultados() {
		return produtos_consultados;
	}
	public void setProdutos_consultados(CredlocalizaProdutosConsultados produtos_consultados) {
		this.produtos_consultados = produtos_consultados;
	}
	public CredlocalizaFrota getFrota() {
		return frota;
	}
	public void setFrota(CredlocalizaFrota frota) {
		this.frota = frota;
	}
	public String getInformacoes_adcionais() {
		return informacoes_adcionais;
	}
	public void setInformacoes_adcionais(String informacoes_adcionais) {
		this.informacoes_adcionais = informacoes_adcionais;
	}
}
