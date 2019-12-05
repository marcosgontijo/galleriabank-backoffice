package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

public class SaqueIUGU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String idSaque;
	private String idAccountIUGU;
	private String status;
	private Date created_at;
	private String amount;
		
	public SaqueIUGU(){
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getIdSaque() {
		return idSaque;
	}

	public void setIdSaque(String idSaque) {
		this.idSaque = idSaque;
	}

	public String getIdAccountIUGU() {
		return idAccountIUGU;
	}

	public void setIdAccountIUGU(String idAccountIUGU) {
		this.idAccountIUGU = idAccountIUGU;
	}	
}