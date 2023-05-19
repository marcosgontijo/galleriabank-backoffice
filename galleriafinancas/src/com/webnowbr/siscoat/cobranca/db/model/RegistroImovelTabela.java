package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RegistroImovelTabela implements Serializable {

	private static final long serialVersionUID = 1L;
	private long id;
	private Date data;
	private BigDecimal valorMin;
	private BigDecimal valorMax;
	private BigDecimal total;

	public RegistroImovelTabela() {

	}

	public RegistroImovelTabela(Date data, BigDecimal valorMin, BigDecimal valorMax, BigDecimal total) {
		super();
		this.data = data;
		this.valorMin = valorMin;
		this.valorMax = valorMax;
		this.total = total;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public BigDecimal getValorMin() {
		return valorMin;
	}

	public void setValorMin(BigDecimal valorMin) {
		this.valorMin = valorMin;
	}

	public BigDecimal getValorMax() {
		return valorMax;
	}

	public void setValorMax(BigDecimal valorMax) {
		this.valorMax = valorMax;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}