package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

public class ErrosProcessamentoEmLote implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private Date dataProcessamento;
	private String descricaoProcessamento;
	private String numeroContrato;
	
	public ErrosProcessamentoEmLote(){

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataProcessamento() {
		return dataProcessamento;
	}

	public void setDataProcessamento(Date dataProcessamento) {
		this.dataProcessamento = dataProcessamento;
	}

	public String getDescricaoProcessamento() {
		return descricaoProcessamento;
	}

	public void setDescricaoProcessamento(String descricaoProcessamento) {
		this.descricaoProcessamento = descricaoProcessamento;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
}