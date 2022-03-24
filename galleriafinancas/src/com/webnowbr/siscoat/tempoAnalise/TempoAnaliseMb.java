package com.webnowbr.siscoat.tempoAnalise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.powerbi.PowerBiDetalhes;

/** ManagedBean. */
@ManagedBean(name = "tempoAnaliseMB")
@SessionScoped
public class TempoAnaliseMb {
	
	public List<Analise> analises = new ArrayList<Analise>();
	public Date dataInicio;
	public Date dataFim;
	public Collection<ContratosAnalise> contratosConsulta;
	
	public String clearTempoAnalise() {
		
		this.dataInicio = null;
		this.dataFim = null;
		analises = new ArrayList<Analise>();
				
		return "/Relatorios/Analise/TempoAnallise.xhtml";
	}
	
	public void consultarAnalise() {
		TempoAnaliseDao tempoAnaliseDao  = new TempoAnaliseDao();
		analises = tempoAnaliseDao.listaAnalises(dataInicio, dataFim);
	}

	public List<Analise> getAnalises() {
		return analises;
	}

	public void setAnalises(List<Analise> analises) {
		this.analises = analises;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Collection<ContratosAnalise> getContratosConsulta() {
		return contratosConsulta;
	}

	public void setContratosConsulta(Collection<ContratosAnalise> contratosConsulta) {
		this.contratosConsulta = contratosConsulta;
	}
	
}

