package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

public class DataEngine implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private PagadorRecebedor pagador; //titulares pra enviar pedido
	private String idCallManager;
	private String pdfBase64;
	private String usuario;
	private Date data;
	
	
	public DataEngine(){

	}

	public DataEngine(PagadorRecebedor pagador, String idEngine,
			String usuario, Date data) {
		super();
		this.pagador = pagador;
		this.idCallManager = idEngine;
		this.usuario = usuario;
		this.data = data;
	}
	
	public DataEngine(PagadorRecebedor pagador) {
		super();
		this.pagador = pagador;
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}

	
	public PagadorRecebedor getPagador() {
		return pagador;
	}


	public void setPagador(PagadorRecebedor pagador) {
		this.pagador = pagador;
	}

	public String getIdCallManager() {
		return idCallManager;
	}

	public void setIdCallManager(String idCallManager) {
		this.idCallManager = idCallManager;
	}

	public String getPdfBase64() {
		return pdfBase64;
	}


	public void setPdfBase64(String pdfBase64) {
		this.pdfBase64 = pdfBase64;
	}


	public String getUsuario() {
		return usuario;
	}


	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}


	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}
	
}
