package com.webnowbr.siscoat.cobranca.model.bmpdigital;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ResumoDaOperacao {

	@SerializedName("ListaDeVencimentos")
	private List<ResumoDoVencimento> listaDeVencimentos;
	
	@SerializedName("Modalidade")
	private String modalidade;
	
	@SerializedName("VariacaoCambial")
	private String variacaoCambial;
	
	public ResumoDaOperacao() {
	}

	public List<ResumoDoVencimento> getListaDeVencimentos() {
		return listaDeVencimentos;
	}

	public void setListaDeVencimentos(List<ResumoDoVencimento> listaDeVencimentos) {
		this.listaDeVencimentos = listaDeVencimentos;
	}

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public String getVariacaoCambial() {
		return variacaoCambial;
	}

	public void setVariacaoCambial(String variacaoCambial) {
		this.variacaoCambial = variacaoCambial;
	}
}
