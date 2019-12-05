package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

public class TransferenciasObservacoesIUGU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String idTransferencia;
	private String observacao;
	
	public TransferenciasObservacoesIUGU(){
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}



	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * @return the idTransferencia
	 */
	public String getIdTransferencia() {
		return idTransferencia;
	}

	/**
	 * @param idTransferencia the idTransferencia to set
	 */
	public void setIdTransferencia(String idTransferencia) {
		this.idTransferencia = idTransferencia;
	}
}