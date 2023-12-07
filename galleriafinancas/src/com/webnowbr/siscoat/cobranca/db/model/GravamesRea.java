package com.webnowbr.siscoat.cobranca.db.model;

import com.webnowbr.siscoat.cobranca.ws.endpoint.ReaWebhookRetornoBloco;

public class GravamesRea {

	private long id;
	private String statusFechamentoGravame;
	private String texto;
	private String nomeClassificacao;
	private int numeroSequencia = 0;
	private DocumentoAnalise documentoAnalise;
	
		
	public GravamesRea() {
		super();
	}

	public GravamesRea(DocumentoAnalise documentoAnalise, ReaWebhookRetornoBloco gravameAberto) {
		super();
		this.documentoAnalise = documentoAnalise;
		this.statusFechamentoGravame = gravameAberto.statusFechamentoGravame;
		this.nomeClassificacao = gravameAberto.nomeClassificacao;
		this.numeroSequencia = gravameAberto.numeroSequencia;
		this.texto = gravameAberto.getConteudo().texto;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatusFechamentoGravame() {
		return statusFechamentoGravame;
	}

	public void setStatusFechamentoGravame(String statusFechamentoGravame) {
		this.statusFechamentoGravame = statusFechamentoGravame;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getNomeClassificacao() {
		return nomeClassificacao;
	}

	public void setNomeClassificacao(String nomeClassificacao) {
		this.nomeClassificacao = nomeClassificacao;
	}

	public DocumentoAnalise getDocumentoAnalise() {
		return documentoAnalise;
	}

	public void setDocumentoAnalise(DocumentoAnalise documentoAnalise) {
		this.documentoAnalise = documentoAnalise;
	}

	public int getNumeroSequencia() {
		return numeroSequencia;
	}

	public void setNumeroSequencia(int numeroSequencia) {
		this.numeroSequencia = numeroSequencia;
	}
}
