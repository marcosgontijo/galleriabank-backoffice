package com.webnowbr.siscoat.cobranca.db.model;

import java.util.Date;

import com.webnowbr.siscoat.infra.db.model.User;

public class Cartorio {
	private Long id;
	private ContratoCobranca idContrato;
	private String status;
	private Date dataStatus;
	private String nomeUsuario;
	public Date getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(Date dataStatus) {
		this.dataStatus = dataStatus;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNomeUsuario() {
		return nomeUsuario;
	}
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
	public ContratoCobranca getIdContrato() {
		return idContrato;
	}
	public void setIdContrato(ContratoCobranca idContrato) {
		this.idContrato = idContrato;
	}

}
