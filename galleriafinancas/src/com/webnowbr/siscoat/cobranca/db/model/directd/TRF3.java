package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TRF3 implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * EXEMPLO DE MENSAGEM
	        "ConsultaUid": "77yfoi8ky5dsor8es3brgxezr",
		    "IdTipo": 1,
		    "Tipo": "Sucesso",
		    "Mensagem": "Sucesso. ",
		    "TempoExecucaoMs": 44267,
		    "CustoTotalEmCreditos": 1,
		    "SaldoEmCreditos": 80,
		    "ApiVersion": "v1",
		    "Retorno": {
		        "Titulo": "PODER JUDICIÁRIO JUSTIÇA FEDERAL DA 3ª REGIÃO - CERTIDÃO REGIONAL PARA FINS GERAIS CÍVEL E CRIMINAL",
		        "CNPJ": "91.981.505/0001-15",
		        "Nome": "FRANCISCO LEANDRO ROCHA",
		        "EmitidaAs": "2019-08-27T13:38:10.1420086-03:00",
		        "ValidaAte": "2019-08-27T13:38:10.1420086-03:00",
		        "Status": "NADA CONSTA",
		        "PossuiDividas": false,
		        "ListaDividas": [],
		        "CodigoControleCertidao": " 1234567"
	*****
	*****/
	
	private InfoServico infoServico;
	private String titulo;
	private String cNPJ;
	private String cpf;
	private String nome;
	private Date emitidaAs;
	private Date validaAte;
	private String status;
	private boolean possuiDividas;
	private String codigoControleCertidao;
	private List<String> listaDividas;
	
	public TRF3() {
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

	public String getcNPJ() {
		return cNPJ;
	}

	public void setcNPJ(String cNPJ) {
		this.cNPJ = cNPJ;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public String getCodigoControleCertidao() {
		return codigoControleCertidao;
	}

	public void setCodigoControleCertidao(String codigoControleCertidao) {
		this.codigoControleCertidao = codigoControleCertidao;
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
}