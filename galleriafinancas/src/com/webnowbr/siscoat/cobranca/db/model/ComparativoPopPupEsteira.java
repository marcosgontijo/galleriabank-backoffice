package com.webnowbr.siscoat.cobranca.db.model;

import java.util.Date;

public class ComparativoPopPupEsteira {
	
	private long id;
	private String id_form;
	private String nome_propiedade;
	private String nome_alterador;
	private Date data_modificacao;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getId_form() {
		return id_form;
	}

	public void setId_form(String id_form) {
		this.id_form = id_form;
	}

	public String getNome_propiedade() {
		return nome_propiedade;
	}

	public void setNome_propiedade(String nome_propiedade) {
		this.nome_propiedade = nome_propiedade;
	}

	public String getNome_alterador() {
		return nome_alterador;
	}

	public void setNome_alterador(String nome_alterador) {
		this.nome_alterador = nome_alterador;
	}

	public Date getData_modificacao() {
		return data_modificacao;
	}

	public void setData_modificacao(Date data_modificacao) {
		this.data_modificacao = data_modificacao;
	}
}
