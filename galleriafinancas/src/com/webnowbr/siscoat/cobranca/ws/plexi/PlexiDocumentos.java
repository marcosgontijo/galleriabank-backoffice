package com.webnowbr.siscoat.cobranca.ws.plexi;

public class PlexiDocumentos {
	private long id;	
	private String url;
	private String nome;
	private boolean pf;
	private boolean pj;
	private String estados;
	private String etapa;
	private String obs;
	
	@Override
	public String toString() {
		return "PlexiDocumentos [id=" + id + ", url=" + url + ", nome=" + nome + "]";
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public boolean isPf() {
		return pf;
	}
	public void setPf(boolean pf) {
		this.pf = pf;
	}
	public boolean isPj() {
		return pj;
	}
	public void setPj(boolean pj) {
		this.pj = pj;
	}
	public String getEstados() {
		return estados;
	}
	public void setEstados(String estados) {
		this.estados = estados;
	}

	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
}
