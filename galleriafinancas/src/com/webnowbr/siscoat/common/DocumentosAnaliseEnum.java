package com.webnowbr.siscoat.common;

public enum DocumentosAnaliseEnum {
	REA("Rea"), RELATO("Relato"), CREDNET("Crednet");

	private String nome;

	private DocumentosAnaliseEnum(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return this.nome;
	}
	
}
