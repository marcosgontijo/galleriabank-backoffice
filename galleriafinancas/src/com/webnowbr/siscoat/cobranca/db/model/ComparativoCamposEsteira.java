package com.webnowbr.siscoat.cobranca.db.model;

public class ComparativoCamposEsteira {

	private Long id;
	private String id_form;
	private String nome_propiedade;
	private String descricao;
	private String validarClasses;
	private boolean validar;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public void setNome_propiedade(String nome_propiedadde) {
		this.nome_propiedade = nome_propiedadde;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getValidarClasses() {
		return validarClasses;
	}

	public void setValidarClasses(String validarClasses) {
		this.validarClasses = validarClasses;
	}

	public boolean isValidar() {
		return validar;
	}

	public void setValidar(boolean validar) {
		this.validar = validar;
	}
}
