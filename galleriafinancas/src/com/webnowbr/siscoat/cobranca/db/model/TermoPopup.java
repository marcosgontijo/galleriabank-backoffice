package com.webnowbr.siscoat.cobranca.db.model;

import java.util.Date;

public class TermoPopup {
	private String usuario;
	private String data;
	private String data;
	
	
	public TermoPopup(String usuario,String data) {
		this.data = data;
		this.usuario = usuario;
		
	}
	public String getData() {
	public String getData() {
		return data;
	}
	public void setData(String data) {
	public void setData(String data) {
		this.data = data;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}
