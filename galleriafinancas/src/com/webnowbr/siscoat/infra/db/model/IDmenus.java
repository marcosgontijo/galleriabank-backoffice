package com.webnowbr.siscoat.infra.db.model;

public enum IDmenus {
	
	Relatorio(22l),
	Atedimento(18l),
	cadastros(17l),
	manutencao(23l);
	
	private long indice;

	IDmenus(long i) {
		this.setIndice(i);
	}

	public long getIndice() {
		return indice;
	}

	public void setIndice(long indice) {
		this.indice = indice;
	}

}
