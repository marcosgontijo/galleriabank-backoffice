package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class InfoServico implements Serializable {
	
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
		    "ApiVersion": "v1"

	*****
	*****/
	
	private String consultaUid;
	private long idTipo;
	private String tipo;
	private String mensagem;
	private long TempoExecucaoMs;
	private long CustoTotalEmCreditos;
	private long SaldoEmCreditos;
	private String ApiVersion;
	
	public InfoServico() {
		
	}

	public String getConsultaUid() {
		return consultaUid;
	}

	public void setConsultaUid(String consultaUid) {
		this.consultaUid = consultaUid;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getApiVersion() {
		return ApiVersion;
	}

	public void setApiVersion(String apiVersion) {
		ApiVersion = apiVersion;
	}

	public long getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(long idTipo) {
		this.idTipo = idTipo;
	}

	public long getTempoExecucaoMs() {
		return TempoExecucaoMs;
	}

	public void setTempoExecucaoMs(long tempoExecucaoMs) {
		TempoExecucaoMs = tempoExecucaoMs;
	}

	public long getCustoTotalEmCreditos() {
		return CustoTotalEmCreditos;
	}

	public void setCustoTotalEmCreditos(long custoTotalEmCreditos) {
		CustoTotalEmCreditos = custoTotalEmCreditos;
	}

	public long getSaldoEmCreditos() {
		return SaldoEmCreditos;
	}

	public void setSaldoEmCreditos(long saldoEmCreditos) {
		SaldoEmCreditos = saldoEmCreditos;
	}
}