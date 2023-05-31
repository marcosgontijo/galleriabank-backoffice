package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DocketRetornoPedido implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2816983335653056299L;
	
	private String id;
	private String idExibicao;
	private String lead;
	private String urlWebHookEntregaDocumento;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdExibicao() {
		return idExibicao;
	}

	public void setIdExibicao(String idExibicao) {
		this.idExibicao = idExibicao;
	}

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

}