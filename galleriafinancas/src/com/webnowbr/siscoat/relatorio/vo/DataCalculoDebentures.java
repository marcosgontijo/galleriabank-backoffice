package com.webnowbr.siscoat.relatorio.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.powerbi.PowerBiDetalhes;

public class DataCalculoDebentures implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date dataCalculo;
	private BigDecimal valor;
	
	public DataCalculoDebentures() {
		this.valor = BigDecimal.ZERO;
	}
	
	public DataCalculoDebentures(Date dataCalculo) {
		super();
		this.dataCalculo = dataCalculo;
		this.valor = BigDecimal.ZERO;
	}
	
	public DataCalculoDebentures(DataCalculoDebentures dataCD) {
		super();
		this.dataCalculo = dataCD.getDataCalculo();
		this.valor = dataCD.getValor();
	}

	public DataCalculoDebentures(Date dataCalculo, BigDecimal valor) {
		super();
		this.dataCalculo = dataCalculo;
		this.valor = valor;
	}

	public Date getDataCalculo() {
		return dataCalculo;
	}

	public void setDataCalculo(Date dataCalculo) {
		this.dataCalculo = dataCalculo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	
}