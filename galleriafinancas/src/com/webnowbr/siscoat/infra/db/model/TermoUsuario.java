package com.webnowbr.siscoat.infra.db.model;

import java.util.Date;

public class TermoUsuario {
	
	private long id;
	
//	private long idx;

	private long idTermo;

	private long idUsuario;

	private Date dataCiencia;
	
	private Date dataAdiado;
	
	private Date dataAceite;
	
	public TermoUsuario() {
		
	}
	
	public TermoUsuario(long idTermo, long idUsuario, Date dataCiencia, Date dataAdiado, Date dataAceite) {
		this.idTermo = idTermo;
		this.idUsuario = idUsuario;
		this.dataCiencia = dataCiencia;	
		this.dataAdiado = dataAdiado;
		this.dataAceite = dataAceite;		
	}

	public long getIdTermo() {
		return idTermo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

//	public long getIdx() {
//		return idx;
//	}
//
//	public void setIdx(long idx) {
//		this.idx = idx;
//	}

	public void setIdTermo(long idTermo) {
		this.idTermo = idTermo;
	}

	public long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Date getDataAdiado() {
		return dataAdiado;
	}

	public void setDataAdiado(Date dataAdiado) {
		this.dataAdiado = dataAdiado;
	}

	public Date getDataAceite() {
		return dataAceite;
	}

	public void setDataAceite(Date dataAceite) {
		this.dataAceite = dataAceite;
	}

	public Date getDataCiencia() {
		return dataCiencia;
	}

	public void setDataCiencia(Date dataCiencia) {
		this.dataCiencia = dataCiencia;
	}
	
	

}
