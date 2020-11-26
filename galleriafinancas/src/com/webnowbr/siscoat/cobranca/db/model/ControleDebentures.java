package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ControleDebentures implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private long numeroDebentures;
	
	public ControleDebentures(){

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getNumeroDebentures() {
		return numeroDebentures;
	}

	public void setNumeroDebentures(long numeroDebentures) {
		this.numeroDebentures = numeroDebentures;
	}
}