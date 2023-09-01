package com.webnowbr.siscoat.infra.db.model;

import java.util.Date;

public class TermoUsuarioVO {
private long id;
	
	private long idx;

	private long idTermo;

	private long idUsuario;

	private Date dataAceite;
	private User usuario;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getIdx() {
		return idx;
	}
	public void setIdx(long idx) {
		this.idx = idx;
	}
	public long getIdTermo() {
		return idTermo;
	}
	public void setIdTermo(long idTermo) {
		this.idTermo = idTermo;
	}
	public long getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(long idUsuario) {
		this.idUsuario = idUsuario;
	}
	public Date getDataAceite() {
		return dataAceite;
	}
	public void setDataAceite(Date dataAceite) {
		this.dataAceite = dataAceite;
	}
	public User getUsuario() {
		return usuario;
	}
	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

}
