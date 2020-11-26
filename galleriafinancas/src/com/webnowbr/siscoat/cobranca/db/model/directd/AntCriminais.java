package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AntCriminais implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * EXEMPLO DE MENSAGEM
			{
			    "ConsultaUid": "9vlsdyxx18k8cjrkyrux568n4",
			    "IdTipo": 1,
			    "Tipo": "Sucesso",
			    "Mensagem": "Sucesso. ",
			    "TempoExecucaoMs": 265,
			    "CustoTotalEmCreditos": 1,
			    "SaldoEmCreditos": 10,
			    "ApiVersion": "v1",
			    "Retorno": {
			        "PossuiAntecedCriminais": false,
			        "NumeroCertidao": 12345678912,
			        "DataHoraEmissao": "2018-10-09T11:53:00.0000000",
			        "Observacoes": null,
			        "DataConsulta": "2018-10-09T11:53:07.3461786",
			        "CertidaoAntecedentesPDF": "...."
			    }
			}
	*****
	*****/
	
	private InfoServico infoServico;
	private String nome;
	private String CPF;
	private boolean possuiAntecedCriminais;
	private String numeroCertidao;
	private Date dataHoraEmissao;
	private String observacoes;
	private Date dataConsulta;
	private String certidaoAntecedentesPDF;
	
	public AntCriminais() {
		this.infoServico = new InfoServico();
	}

	public InfoServico getInfoServico() {
		return infoServico;
	}

	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCPF() {
		return CPF;
	}

	public void setCPF(String cPF) {
		CPF = cPF;
	}

	public boolean isPossuiAntecedCriminais() {
		return possuiAntecedCriminais;
	}

	public void setPossuiAntecedCriminais(boolean possuiAntecedCriminais) {
		this.possuiAntecedCriminais = possuiAntecedCriminais;
	}

	public String getNumeroCertidao() {
		return numeroCertidao;
	}

	public void setNumeroCertidao(String numeroCertidao) {
		this.numeroCertidao = numeroCertidao;
	}

	public Date getDataHoraEmissao() {
		return dataHoraEmissao;
	}

	public void setDataHoraEmissao(Date dataHoraEmissao) {
		this.dataHoraEmissao = dataHoraEmissao;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getCertidaoAntecedentesPDF() {
		return certidaoAntecedentesPDF;
	}

	public void setCertidaoAntecedentesPDF(String certidaoAntecedentesPDF) {
		this.certidaoAntecedentesPDF = certidaoAntecedentesPDF;
	}
}