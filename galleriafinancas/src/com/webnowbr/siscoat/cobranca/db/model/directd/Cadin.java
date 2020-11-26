package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cadin implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * EXEMPLO DE MENSAGEM
		"Retorno": {
	        "CodigoDeclaracao": "ABCD1234.1234ABCD.A1B2C3D4.D4C3B2A1",
	        "TemPendencia": true,
	        "Data": "2018-10-03T12:48:17.0000000",
	        "Pendencias": [
	            {
	                "Entidade": "Procuradoria Geral do Estado",
	                "DataInclusaoCadin": "2018-09-13T00:00:00.0000000",
	                "QuantidadePendencias": 1,
	                "LocalRegularizacao": "R ÓBIDOS, 674, SÃO JOSÉ DOS CAMPOS."
	            }
	        ]
	    }
	*****
	*****/
	
	private InfoServico infoServico;
	private String codigoDeclaracao;
	private boolean temPendencia;
	private Date data;
	private List<Pendencias> pendencias;
	
	public Cadin() {
		this.infoServico = new InfoServico();
		this.pendencias = new ArrayList<Pendencias>();
	}

	public String getCodigoDeclaracao() {
		return codigoDeclaracao;
	}

	public void setCodigoDeclaracao(String codigoDeclaracao) {
		this.codigoDeclaracao = codigoDeclaracao;
	}

	public boolean isTemPendencia() {
		return temPendencia;
	}

	public void setTemPendencia(boolean temPendencia) {
		this.temPendencia = temPendencia;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public List<Pendencias> getPendencias() {
		return pendencias;
	}

	public void setPendencias(List<Pendencias> pendencias) {
		this.pendencias = pendencias;
	}

	public InfoServico getInfoServico() {
		return infoServico;
	}

	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}
}