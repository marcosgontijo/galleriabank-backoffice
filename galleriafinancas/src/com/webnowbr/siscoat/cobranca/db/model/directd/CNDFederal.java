package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CNDFederal implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * EXEMPLO DE MENSAGEM
		{
		    "ConsultaUid": "46bx9as1hygnj6fjfdzuacu33",
		    "IdTipo": 1,
		    "Tipo": "Sucesso",
		    "Mensagem": "Sucesso. ",
		    "TempoExecucaoMs": 9653,
		    "CustoTotalEmCreditos": 1,
		    "SaldoEmCreditos": 2035,
		    "ApiVersion": "v1",
		    "Retorno": {
		        "CNPJ": "91.981.505/0001-15",
		        "Nome": "ENTREGAS EXPRESSAS ME",
		        "Portaria": "Certidão emitida gratuitamente com base na Portaria Conjunta RFB/PGFN nº 1.751, de 2/10/2014.",
		        "EmitidaAs": "2018-08-23T01:42:54.0000000",
		        "ValidaAte": "2019-02-19T00:00:00.0000000",
		        "Status": "NADA CONSTA",
		        "PossuiDividas": false,
		        "ListaDividas": [],
		        "CodigoControleCertidao": "A1B2.C3D4.E5F6.G7H8"
		    }
		}
	*****
	*****/
	
	private InfoServico infoServico;
	private String titulo;	
	private String cnpj;
	private String nome;
	private String portaria;
	private Date emitidaAs;
	private Date validaAte;
	private String status;
	private boolean possuiDividas;
	private List<String> listaDividas;
	private String codigoControleCertidao;
	
	public CNDFederal() {
		this.infoServico = new InfoServico();
	}
	
	public InfoServico getInfoServico() {
		return infoServico;
	}
	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getPortaria() {
		return portaria;
	}
	public void setPortaria(String portaria) {
		this.portaria = portaria;
	}
	public Date getEmitidaAs() {
		return emitidaAs;
	}
	public void setEmitidaAs(Date emitidaAs) {
		this.emitidaAs = emitidaAs;
	}
	public Date getValidaAte() {
		return validaAte;
	}
	public void setValidaAte(Date validaAte) {
		this.validaAte = validaAte;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isPossuiDividas() {
		return possuiDividas;
	}
	public void setPossuiDividas(boolean possuiDividas) {
		this.possuiDividas = possuiDividas;
	}
	public List<String> getListaDividas() {
		return listaDividas;
	}
	public void setListaDividas(List<String> listaDividas) {
		this.listaDividas = listaDividas;
	}
	public String getCodigoControleCertidao() {
		return codigoControleCertidao;
	}
	public void setCodigoControleCertidao(String codigoControleCertidao) {
		this.codigoControleCertidao = codigoControleCertidao;
	}
}