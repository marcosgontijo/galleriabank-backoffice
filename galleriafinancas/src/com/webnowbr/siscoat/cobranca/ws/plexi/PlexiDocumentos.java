package com.webnowbr.siscoat.cobranca.ws.plexi;

public class PlexiDocumentos {
	private long id;	
	private String url;
	private String nome;
	private boolean pf;
	private boolean pj;
	private String obs;
	
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
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
}
