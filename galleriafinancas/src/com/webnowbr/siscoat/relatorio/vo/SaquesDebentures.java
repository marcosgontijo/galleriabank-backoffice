package com.webnowbr.siscoat.relatorio.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaquesDebentures implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date dataSaque;
	private BigDecimal valorSaque;
	
	private List<DataCalculoDebentures> calculos;
	
	public SaquesDebentures() {
		this.calculos = new ArrayList<DataCalculoDebentures>();
		this.valorSaque = BigDecimal.ZERO;
	}

	public Date getDataSaque() {
		return dataSaque;
	}

	public void setDataSaque(Date dataSaque) {
		this.dataSaque = dataSaque;
	}

	public BigDecimal getValorSaque() {
		return valorSaque;
	}

	public void setValorSaque(BigDecimal valorSaque) {
		this.valorSaque = valorSaque;
	}

	public List<DataCalculoDebentures> getCalculos() {
		return calculos;
	}

	public void setCalculos(List<DataCalculoDebentures> calculos) {
		this.calculos = calculos;
	}
	
	
}