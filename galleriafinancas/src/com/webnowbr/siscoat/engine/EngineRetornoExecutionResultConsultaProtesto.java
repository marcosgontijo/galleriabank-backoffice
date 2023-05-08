package com.webnowbr.siscoat.engine;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaProtesto {

	

	@SerializedName("data_consulta")
	String data_consulta;
	@SerializedName("situacao")
	String situacao;

	@SerializedName("conteudo")
	EngineRetornoExecutionResultConsultaCartorioHistory conteudo;

	public String getData_consulta() {
		return data_consulta;
	}

	public void setData_consulta(String data_consulta) {
		this.data_consulta = data_consulta;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public EngineRetornoExecutionResultConsultaCartorioHistory getConteudo() {
		return conteudo;
	}

	public void setConteudo(EngineRetornoExecutionResultConsultaCartorioHistory conteudo) {
		this.conteudo = conteudo;
	}

	

}
