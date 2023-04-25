package com.webnowbr.siscoat.cobranca.ws.endpoint;

public class ReaWebhookRetornoBloco {
	public ReaWebhookRetornoBlocoConteudo conteudo;
	public String tipo;
	public String nomeClassificacao;
	public Integer numeroPagina;
	public Integer numeroSequencia;
	public boolean relacionadoAoProprietarioAtual;

	public ReaWebhookRetornoBlocoConteudo getConteudo() {
		return conteudo;
	}

	public void setConteudo(ReaWebhookRetornoBlocoConteudo conteudo) {
		this.conteudo = conteudo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNomeClassificacao() {
		return nomeClassificacao;
	}

	public void setNomeClassificacao(String nomeClassificacao) {
		this.nomeClassificacao = nomeClassificacao;
	}

	public Integer getNumeroPagina() {
		return numeroPagina;
	}

	public void setNumeroPagina(Integer numeroPagina) {
		this.numeroPagina = numeroPagina;
	}

	public Integer getNumeroSequencia() {
		return numeroSequencia;
	}

	public void setNumeroSequencia(Integer numeroSequencia) {
		this.numeroSequencia = numeroSequencia;
	}

	public boolean isRelacionadoAoProprietarioAtual() {
		return relacionadoAoProprietarioAtual;
	}

	public void setRelacionadoAoProprietarioAtual(boolean relacionadoAoProprietarioAtual) {
		this.relacionadoAoProprietarioAtual = relacionadoAoProprietarioAtual;
	}

}
