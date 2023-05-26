package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.Date;
import java.util.List;

public class DocketWebhookRetornoDocumentoArquivo {

	public String nome;
	public String tipo;
	public String id;
	public List<DocketWebhookRetornoDocumentoArquivoLink> links;
	public Date dataCriacao;
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
	public List<DocketWebhookRetornoDocumentoArquivoLink> getLinks() {
		return links;
	}
	public void setLinks(List<DocketWebhookRetornoDocumentoArquivoLink> links) {
		this.links = links;
	}
	public Date getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	
	
}


