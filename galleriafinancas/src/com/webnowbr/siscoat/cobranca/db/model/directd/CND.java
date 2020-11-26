package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CND implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * EXEMPLO DE MENSAGEM
		{
		    "ConsultaUid": "1y5d5opmvnfagyi8ct66fl5io",
		    "IdTipo": "1",
		    "Tipo": "Sucesso",
		    "Mensagem": "Sucesso. ",
		    "TempoExecucaoMs": 244,
		    "CustoTotalEmCreditos": 0,
		    "SaldoEmCreditos": 2032,
		    "ApiVersion": "v1",
		    "Retorno": {
		        "NumCertidao": "123456/2018",
		        "Contribuinte": "ENTREGAS EXPRESSAS ME",
		        "CCE": "12.345.678-9",
		        "ConstaDivida": false,
		        "DataEmissao": "2018-10-03T12:23:18.0000000",
		        "CNPJ": "91.981.505/0001-15",
		        "Titulo": "DISTRITO FEDERAL - CERTIDÃO NEGATIVA DE DÉBITOS",
		        "ValidaAte": "2019-09-09T00:00:00.0000000",
		        "Status": "Não consta qualquer débito inscrito em nome do interessado acima identificado, até a presente data.",
		        "ListaDividas": []
		    }
		}
	*****
	*****/
	
	private InfoServico infoServico;
	private String numCertidao;
	private String contribuinte;
	private String CCE;
	private boolean ConstaDivida;
	private Date dataEmissao;
	private String cnpj;
	private String cpf;
	private String ie;
	private String titulo;
	private Date validoAte;
	private String status;
	private List<String> listaDividas;
	
	public CND() {
		this.infoServico = new InfoServico();
	}

	public InfoServico getInfoServico() {
		return infoServico;
	}

	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}

	public String getNumCertidao() {
		return numCertidao;
	}

	public void setNumCertidao(String numCertidao) {
		this.numCertidao = numCertidao;
	}

	public String getContribuinte() {
		return contribuinte;
	}

	public void setContribuinte(String contribuinte) {
		this.contribuinte = contribuinte;
	}

	public String getCCE() {
		return CCE;
	}

	public void setCCE(String cCE) {
		CCE = cCE;
	}

	public boolean isConstaDivida() {
		return ConstaDivida;
	}

	public void setConstaDivida(boolean constaDivida) {
		ConstaDivida = constaDivida;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Date getValidoAte() {
		return validoAte;
	}

	public void setValidoAte(Date validoAte) {
		this.validoAte = validoAte;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getListaDividas() {
		return listaDividas;
	}

	public void setListaDividas(List<String> listaDividas) {
		this.listaDividas = listaDividas;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getIe() {
		return ie;
	}

	public void setIe(String ie) {
		this.ie = ie;
	}
}