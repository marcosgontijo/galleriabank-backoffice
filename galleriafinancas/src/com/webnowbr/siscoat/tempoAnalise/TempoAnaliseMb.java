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
@ManagedBean(name = "tempoAnaliseMb")
@SessionScoped
public class TempoAnaliseMb {
	
	public List<Analise> analises = new ArrayList<Analise>();
	public Date dataInicio;
	public Date dataFim;
	
	public String clearPowerBi() {
		
		this.dataInicio = null;
		this.dataFim = null;
		analises = new ArrayList<Analise>();
				
		return "/Atendimento/Cobranca/PowerBi/PowerBi.xhtml";
	}
	
	
	
	
	
			
}

