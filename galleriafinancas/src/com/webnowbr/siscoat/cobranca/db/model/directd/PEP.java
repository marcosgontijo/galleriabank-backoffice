package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class PEP implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * EXEMPLO DE MENSAGEM
		{
		    "ConsultaUid": "f2s1ncrizy9gihwd883a9htbl",
		    "IdTipo": 1,
		    "Tipo": "Sucesso",
		    "Mensagem": "Sucesso. ",
		    "TempoExecucaoMs": 1285,
		    "CustoTotalEmCreditos": 0,
		    "SaldoEmCreditos": 10,
		    "ApiVersion": "v1",
		    "Retorno": {
		        "Cpf": "642.373.775-95",
		        "Nome": "FRANCISCO LEANDRO ROCHA",
		        "IsPEP": false,
		        "DataConsulta": "2019-04-04T13:55:37.9343969-03:00"
		    }
		}
	*****
	*****/
	
	private InfoServico infoServico;
	private String cpf;
	private String nome;
	private boolean pep;
	private Date dataConsulta;

	public PEP() {
		this.infoServico = new InfoServico();
	}

	public InfoServico getInfoServico() {
		return infoServico;
	}

	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public boolean isPep() {
		return pep;
	}

	public void setPep(boolean pep) {
		this.pep = pep;
	}
}