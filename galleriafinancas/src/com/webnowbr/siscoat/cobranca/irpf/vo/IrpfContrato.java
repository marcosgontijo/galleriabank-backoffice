package com.webnowbr.siscoat.cobranca.irpf.vo;

import java.math.BigDecimal;
import java.util.Date;

public class IrpfContrato {

	private String tipoCalculoInvestidor;
	private BigDecimal vlrInvestidor;
	private Integer qtdeParcelasInvestidor;
	private Integer carenciaInvestidor;
	private Date dataInicioInvestidor;
	
	
	
	public IrpfContrato(String tipoCalculoInvestidor, BigDecimal vlrInvestidor, Integer qtdeParcelasInvestidor,
			Integer carenciaInvestidor, Date dataInicioInvestidor) {
		super();
		this.tipoCalculoInvestidor = tipoCalculoInvestidor;
		this.vlrInvestidor = vlrInvestidor;
		this.qtdeParcelasInvestidor = qtdeParcelasInvestidor;
		this.carenciaInvestidor = carenciaInvestidor;
		this.dataInicioInvestidor = dataInicioInvestidor;
	}
	public String getTipoCalculoInvestidor() {
		return tipoCalculoInvestidor;
	}
	public void setTipoCalculoInvestidor(String tipoCalculoInvestidor) {
		this.tipoCalculoInvestidor = tipoCalculoInvestidor;
	}
	public BigDecimal getVlrInvestidor() {
		return vlrInvestidor;
	}
	public void setVlrInvestidor(BigDecimal vlrInvestidor) {
		this.vlrInvestidor = vlrInvestidor;
	}
	public Integer getQtdeParcelasInvestidor() {
		return qtdeParcelasInvestidor;
	}
	public void setQtdeParcelasInvestidor(Integer qtdeParcelasInvestidor) {
		this.qtdeParcelasInvestidor = qtdeParcelasInvestidor;
	}
	public Integer getCarenciaInvestidor() {
		return carenciaInvestidor;
	}
	public void setCarenciaInvestidor(Integer carenciaInvestidor) {
		this.carenciaInvestidor = carenciaInvestidor;
	}
	public Date getDataInicioInvestidor() {
		return dataInicioInvestidor;
	}
	public void setDataInicioInvestidor(Date dataInicioInvestidor) {
		this.dataInicioInvestidor = dataInicioInvestidor;
	}


}
