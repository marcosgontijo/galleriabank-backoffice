package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DocketCidades implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String idDocket;
	private String nome;
	private String url;
	
	public DocketCidades(){

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdDocket() {
		return idDocket;
	}

	public void setIdDocket(String idDocket) {
		this.idDocket = idDocket;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}