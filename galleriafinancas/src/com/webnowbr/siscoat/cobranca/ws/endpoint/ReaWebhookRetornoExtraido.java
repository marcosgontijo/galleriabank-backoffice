package com.webnowbr.siscoat.cobranca.ws.endpoint;

public class ReaWebhookRetornoExtraido {

	public ReaWebhookRetornoProprietarios proprietarios;

	public ReaWebhookRetornoImovel imovel;


	public ReaWebhookRetornoProprietarios getProprietarios() {
		return proprietarios;
	}

	public void setProprietarios(ReaWebhookRetornoProprietarios proprietarios) {
		this.proprietarios = proprietarios;
	}

	public ReaWebhookRetornoImovel getImovel() {
		return imovel;
	}

	public void setImovel(ReaWebhookRetornoImovel imovel) {
		this.imovel = imovel;
	}

}
