package com.webnowbr.siscoat.infra.db.model;

import java.util.Date;

public class Termo {

	private long id;

	private String identificacao;

	private String descricao;
	
	private String instrucao;

	private Date inicioValidade;

	private Date fimValidade;

	private String arquivo;

	private String path;

	private UserPerfil userPerfil;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdentificacao() {
		return identificacao;
	}
		
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getInstrucao() {
		return instrucao;
	}

	public void setInstrucao(String instrucao) {
		this.instrucao = instrucao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}

	public Date getInicioValidade() {
		return inicioValidade;
	}

	public void setInicioValidade(Date inicioValidade) {
		this.inicioValidade = inicioValidade;
	}

	public Date getFimValidade() {
		return fimValidade;
	}

	public void setFimValidade(Date fimValidade) {
		this.fimValidade = fimValidade;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public UserPerfil getUserPerfil() {
		return userPerfil;
	}

	public void setUserPerfil(UserPerfil userPerfil) {
		this.userPerfil = userPerfil;
	}

}
