package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.Date;
import java.util.List;

public class ReaWebhookRetornoArquivo {
	public String nome;
	public String tipo;
	public String id;
	public Date dataCriacao;
	public List<ReaWebhookRetornoLink> links; /*
							 * :[ { "href":
							 * "{{url}}/api/v1/{{cliente}}/rea/matriculas/7fad1750-4142-48a7-8f4f-60dd189a0d40/download/1fa7cebe-2bec-45ee-b42e-71e3d92f3876",
							 * "nome":"Matrícula de Imóvel.pdf" }
							 */

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

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public List<ReaWebhookRetornoLink> getLinks() {
		return links;
	}

	public void setLinks(List<ReaWebhookRetornoLink> links) {
		this.links = links;
	}

	

}
