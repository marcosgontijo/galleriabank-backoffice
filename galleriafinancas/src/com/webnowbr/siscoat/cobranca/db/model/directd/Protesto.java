package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Protesto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Date dataProtesto;
	private Float valorprotestado;
	
	public Protesto() {
		
	}

	public Date getDataProtesto() {
		return dataProtesto;
	}

	public void setDataProtesto(Date dataProtesto) {
		this.dataProtesto = dataProtesto;
	}

	public Float getValorprotestado() {
		return valorprotestado;
	}

	public void setValorprotestado(Float valorprotestado) {
		this.valorprotestado = valorprotestado;
	}	
}