package com.webnowbr.siscoat.cobranca.model.bmpdigital;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ScrResult {
	
	@SerializedName("Erro")
	private boolean erro;
	
	@SerializedName("MensagemOperador")
	private String mensagemOperador;
	
	@SerializedName("Periodo")
	private String periodo;
	
	@SerializedName("ResumoDoCliente")
	private ResumoDoCliente resumoDoCliente;
	
	@SerializedName("ResumoDoClienteTraduzido")
	private ResumoDoClienteTraduzido resumoDoClienteTraduzido;
	
	@SerializedName("ResumoModalidade")
	private List<ResumoModalidade> resumoModalidade;
	
	public ScrResult() {
	}

	public boolean isErro() {
		return erro;
	}
	public void setErro(boolean erro) {
		this.erro = erro;
	}
	public String getMensagemOperador() {
		return mensagemOperador;
	}
	public void setMensagemOperador(String mensagemOperador) {
		this.mensagemOperador = mensagemOperador;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public ResumoDoCliente getResumoDoCliente() {
		return resumoDoCliente;
	}
	public void setResumoDoCliente(ResumoDoCliente resumoDoCliente) {
		this.resumoDoCliente = resumoDoCliente;
	}
	public ResumoDoClienteTraduzido getResumoDoClienteTraduzido() {
		return resumoDoClienteTraduzido;
	}
	public void setResumoDoClienteTraduzido(ResumoDoClienteTraduzido resumoDoClienteTraduzido) {
		this.resumoDoClienteTraduzido = resumoDoClienteTraduzido;
	}
	public List<ResumoModalidade> getResumoModalidade() {
		return resumoModalidade;
	}
	public void setResumoModalidade(List<ResumoModalidade> resumoModalidade) {
		this.resumoModalidade = resumoModalidade;
	}
}
