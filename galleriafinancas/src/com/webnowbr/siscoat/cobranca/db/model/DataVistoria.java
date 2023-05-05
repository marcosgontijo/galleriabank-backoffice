package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DataVistoria implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4327959574378948956L;
	private long id;
	private Date dataVistoria;
	private ContratoCobranca contratoCobranca;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataVistoria() {
		return dataVistoria;
	}

	public void setDataVistoria(Date dataVistoria) {
		this.dataVistoria = dataVistoria;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

}
