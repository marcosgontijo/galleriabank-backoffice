package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SaldoIUGU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private PagadorRecebedor subConta;
	private BigDecimal totalSaldo;
		
	public SaldoIUGU(){
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PagadorRecebedor getSubConta() {
		return subConta;
	}

	public void setSubConta(PagadorRecebedor subConta) {
		this.subConta = subConta;
	}

	public BigDecimal getTotalSaldo() {
		return totalSaldo;
	}

	public void setTotalSaldo(BigDecimal totalSaldo) {
		this.totalSaldo = totalSaldo;
	}
}