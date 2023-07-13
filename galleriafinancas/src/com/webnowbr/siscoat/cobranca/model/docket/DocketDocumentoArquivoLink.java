package com.webnowbr.siscoat.cobranca.model.docket;

import com.google.gson.annotations.SerializedName;

public class DocketDocumentoArquivoLink {

	@SerializedName("href")
	private String href;

	@SerializedName("nome")
	private String nome;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
}
