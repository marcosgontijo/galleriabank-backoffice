package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.Date;
import java.util.List;

public class DocketWebhookRetornoPedido {

	public String  lead;
	public String id;
	public String status;
	public Long idExibicao;
	public Date dataCriacao;
	public String usuarioCriacaoId;
    public String usuarioCriacaoNome;
    
    public List<DocketWebhookRetornoDocumento> documentos;

	public String getLead() {
		return lead;
	}

	public void setLead(String lead) {
		this.lead = lead;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getIdExibicao() {
		return idExibicao;
	}

	public void setIdExibicao(Long idExibicao) {
		this.idExibicao = idExibicao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getUsuarioCriacaoId() {
		return usuarioCriacaoId;
	}

	public void setUsuarioCriacaoId(String usuarioCriacaoId) {
		this.usuarioCriacaoId = usuarioCriacaoId;
	}

	public String getUsuarioCriacaoNome() {
		return usuarioCriacaoNome;
	}

	public void setUsuarioCriacaoNome(String usuarioCriacaoNome) {
		this.usuarioCriacaoNome = usuarioCriacaoNome;
	}

	public List<DocketWebhookRetornoDocumento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocketWebhookRetornoDocumento> documentos) {
		this.documentos = documentos;
	}

}
