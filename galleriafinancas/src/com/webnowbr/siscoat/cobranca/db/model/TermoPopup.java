package com.webnowbr.siscoat.cobranca.db.model;

import java.util.Date;

public class TermoPopup {
	private String usuario;
	private Date data;
	
	
	public TermoPopup(String usuario,Date data) {
		this.data = data;
		this.usuario = usuario;
		
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}
