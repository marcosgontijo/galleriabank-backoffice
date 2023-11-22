package com.webnowbr.siscoat.infra.db.model;

public enum IDmenus {
	
	relatorio((long) 22),
	Atedimento((long) 18),
	cadastros((long) 17),
	manutencao((long) 23);
	
	private Long indice;

	IDmenus(Long i) {
		this.setIndice(i);
	}

	public Long getIndice() {
		return indice;
	}

	public void setIndice(Long indice) {
		this.indice = indice;
	}

}
