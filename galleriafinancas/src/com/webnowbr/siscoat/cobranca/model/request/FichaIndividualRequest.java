package com.webnowbr.siscoat.cobranca.model.request;

import com.webnowbr.siscoat.common.CommonsUtil;

public class FichaIndividualRequest {
	String origemChamada;
	String tipoPessoaIsFisica;
	String nome;
	String documento;

	
	
	public FichaIndividualRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FichaIndividualRequest(String origemChamada, boolean tipoPessoaIsFisica, String nome, String documento) {
		super();
		this.origemChamada = origemChamada;
		this.tipoPessoaIsFisica = CommonsUtil.stringValue(tipoPessoaIsFisica);
		this.nome = nome;
		this.documento = documento;
	}

	public String getOrigemChamada() {
		return origemChamada;
	}

	public void setOrigemChamada(String origemChamada) {
		this.origemChamada = origemChamada;
	}

	public String isTipoPessoaIsFisica() {
		return tipoPessoaIsFisica;
	}

	public void setTipoPessoaIsFisica(String tipoPessoaIsFisica) {
		this.tipoPessoaIsFisica = tipoPessoaIsFisica;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

}
