package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;

public class Cidade implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String nome;
	private String estado;
	private int rankingNacional;
	private int rankingEstadual;
	private int populacao;
	private boolean praia;
	private boolean pintarLinha;
	
	public Cidade(){

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getRankingNacional() {
		return rankingNacional;
	}

	public void setRankingNacional(int rankingNacional) {
		this.rankingNacional = rankingNacional;
	}

	public int getRankingEstadual() {
		return rankingEstadual;
	}

	public void setRankingEstadual(int rankingEstadual) {
		this.rankingEstadual = rankingEstadual;
	}

	public int getPopulacao() {
		return populacao;
	}

	public void setPopulacao(int populacao) {
		this.populacao = populacao;
	}

	public boolean isPraia() {
		return praia;
	}

	public void setPraia(boolean praia) {
		this.praia = praia;
	}

	public boolean isPintarLinha() {
		return pintarLinha;
	}

	public void setPintarLinha(boolean pintarLinha) {
		this.pintarLinha = pintarLinha;
	}

	
}