package com.webnowbr.siscoat.cobranca.ws.endpoint;

public class ReaWebhookRetornoLink {
	public String href;
	public String nome;
	/*
	 * "href":
	 * "{{url}}/api/v1/{{cliente}}/rea/matriculas/7fad1750-4142-48a7-8f4f-60dd189a0d40/download/1fa7cebe-2bec-45ee-b42e-71e3d92f3876",
	 * "nome":"Matrícula de Imóvel.pdf"
	 */
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
