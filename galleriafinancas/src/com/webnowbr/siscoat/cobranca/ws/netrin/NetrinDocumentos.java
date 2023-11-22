package com.webnowbr.siscoat.cobranca.ws.netrin;

import com.webnowbr.siscoat.common.CommonsUtil;

public class NetrinDocumentos {
	private long id;
	private String urlService;
	private String nome;
	private boolean pf;
	private boolean pj;
	private String estados;
	private String etapa;
	private String obs;

	private String nomePaju;
	private boolean mostrarPaju;

	@Override
	public String toString() {
		return "NetrinDocumentos [id=" + id + ", url=" + urlService + ", nome=" + nome + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrlService() {
		return urlService;
	}

	public void setUrlService(String urlService) {
		this.urlService = urlService;
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

	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getNomePaju() {
		return (!CommonsUtil.semValor(nomePaju)) ? this.nomePaju : this.nome;
	}

	public void setNomePaju(String nomePaju) {
		this.nomePaju = nomePaju;
	}

	public boolean isMostrarPaju() {
		return mostrarPaju;
	}

	public void setMostrarPaju(boolean mostrarPaju) {
		this.mostrarPaju = mostrarPaju;
	}
}
