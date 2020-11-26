package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProtestosIEPTB implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private boolean constamProtestos;
	private String documentoConsultado;
	private Date dataConsulta;
	private int TotalNumProtestos;
	
	private InfoServico infoServico;
	private List<Protestos> protestos;
	
	public ProtestosIEPTB() {
		this.infoServico = new InfoServico();
	}

	public boolean isConstamProtestos() {
		return constamProtestos;
	}

	public void setConstamProtestos(boolean constamProtestos) {
		this.constamProtestos = constamProtestos;
	}

	public String getDocumentoConsultado() {
		return documentoConsultado;
	}

	public void setDocumentoConsultado(String documentoConsultado) {
		this.documentoConsultado = documentoConsultado;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public int getTotalNumProtestos() {
		return TotalNumProtestos;
	}

	public void setTotalNumProtestos(int totalNumProtestos) {
		TotalNumProtestos = totalNumProtestos;
	}

	public List<Protestos> getProtestos() {
		return protestos;
	}

	public void setProtestos(List<Protestos> protestos) {
		this.protestos = protestos;
	}

	public InfoServico getInfoServico() {
		return infoServico;
	}

	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}
}