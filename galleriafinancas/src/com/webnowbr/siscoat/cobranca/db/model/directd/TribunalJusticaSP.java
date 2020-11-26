package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TribunalJusticaSP implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private InfoServico infoServico;
	private Date dataConsulta;
	private List<Processo1Grau> processo1Grau;
	private List<Processo2Grau> processo2Grau;
	
	public TribunalJusticaSP() {
		this.infoServico = new InfoServico();
		this.processo1Grau = new ArrayList<Processo1Grau>();
		this.processo2Grau = new ArrayList<Processo2Grau>();
	}
	
	public Date getDataConsulta() {
		return dataConsulta;
	}
	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}
	public InfoServico getInfoServico() {
		return infoServico;
	}
	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}
	public List<Processo1Grau> getProcesso1Grau() {
		return processo1Grau;
	}
	public void setProcesso1Grau(List<Processo1Grau> processo1Grau) {
		this.processo1Grau = processo1Grau;
	}
	public List<Processo2Grau> getProcesso2Grau() {
		return processo2Grau;
	}
	public void setProcesso2Grau(List<Processo2Grau> processo2Grau) {
		this.processo2Grau = processo2Grau;
	}
}