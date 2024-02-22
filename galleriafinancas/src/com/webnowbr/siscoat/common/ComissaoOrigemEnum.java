package com.webnowbr.siscoat.common;

public enum ComissaoOrigemEnum {
	GERAL("geral"), INDIVIDUAL("Individual");

	private String nome;

	private ComissaoOrigemEnum(String nome) {
		this.nome = nome;
	}

	public static ComissaoOrigemEnum getByNome(String nome) {
		for (ComissaoOrigemEnum origem : ComissaoOrigemEnum.values()) {
			if (CommonsUtil.mesmoValor(nome.trim(), origem.nome.toLowerCase().trim())) {
				return origem;
			}
		}
		return null;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
