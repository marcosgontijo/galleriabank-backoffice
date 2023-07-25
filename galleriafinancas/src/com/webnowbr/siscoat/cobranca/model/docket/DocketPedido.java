package com.webnowbr.siscoat.cobranca.model.docket;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DocketPedido {

	@SerializedName("lead")
	private String lead;

	@SerializedName("urlWebHookEntregaDocumento")
	private String urlWebHookEntregaDocumento;

	@SerializedName("id")
	private String id;

	@SerializedName("status")
	private String status;

	@SerializedName("idExibicao")
	private String idExibicao;

	@SerializedName("dataCriacao")
	private Date dataCriacao;

	@SerializedName("usuarioCriacaoId")
	private String usuarioCriacaoId;

	@SerializedName("usuarioCriacaoNome")
	private String usuarioCriacaoNome;

	@SerializedName("documentos")
	private List<DocketDocumento> documentos;

	public String getLead() {
		return lead;
	}

	public void setLead(String lead) {
		this.lead = lead;
	}

	public String getUrlWebHookEntregaDocumento() {
		return urlWebHookEntregaDocumento;
	}

	public void setUrlWebHookEntregaDocumento(String urlWebHookEntregaDocumento) {
		this.urlWebHookEntregaDocumento = urlWebHookEntregaDocumento;
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

	public String getIdExibicao() {
		return idExibicao;
	}

	public void setIdExibicao(String idExibicao) {
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

	public List<DocketDocumento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocketDocumento> documentos) {
		this.documentos = documentos;
	}
	
	

}
