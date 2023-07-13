package com.webnowbr.siscoat.cobranca.model.docket;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DocketDocumentoArquivo {

	@SerializedName("nome")
	private String nome;

	@SerializedName("tipo")
	private String tipo;

	@SerializedName("id")
	private String id;

	@SerializedName("links")
	private List<DocketDocumentoArquivoLink> links;

	@SerializedName("dataCriacao")
	private Date dataCriacao;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<DocketDocumentoArquivoLink> getLinks() {
		return links;
	}

	public void setLinks(List<DocketDocumentoArquivoLink> links) {
		this.links = links;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	

}
