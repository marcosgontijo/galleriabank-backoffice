package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcessoDetalhes implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private ProcessoDetalhesDados processoDetalhesDados;	
	private List<ProcessoDetalhesPartes> processoDetalhesPartes;
	
	public ProcessoDetalhes() {
		this.processoDetalhesDados = new ProcessoDetalhesDados();
		this.processoDetalhesPartes = new ArrayList<ProcessoDetalhesPartes>();
	}
	
	public ProcessoDetalhesDados getProcessoDetalhesDados() {
		return processoDetalhesDados;
	}
	public void setProcessoDetalhesDados(ProcessoDetalhesDados processoDetalhesDados) {
		this.processoDetalhesDados = processoDetalhesDados;
	}
	public List<ProcessoDetalhesPartes> getProcessoDetalhesPartes() {
		return processoDetalhesPartes;
	}

	public void setProcessoDetalhesPartes(List<ProcessoDetalhesPartes> processoDetalhesPartes) {
		this.processoDetalhesPartes = processoDetalhesPartes;
	}
}