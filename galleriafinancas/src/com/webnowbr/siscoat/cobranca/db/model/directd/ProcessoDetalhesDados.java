package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProcessoDetalhesDados implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String numero;		
	private String classe;		
	private String assunto;		
	private String localFisico;		
	private String outrosAssuntos;		
	private String distribuicao;		
	private String controle;		
	private String juiz;		
	private String outrosNumeros;		
	private String valorAcao;
	
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getClasse() {
		return classe;
	}
	public void setClasse(String classe) {
		this.classe = classe;
	}
	public String getAssunto() {
		return assunto;
	}
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	public String getLocalFisico() {
		return localFisico;
	}
	public void setLocalFisico(String localFisico) {
		this.localFisico = localFisico;
	}
	public String getOutrosAssuntos() {
		return outrosAssuntos;
	}
	public void setOutrosAssuntos(String outrosAssuntos) {
		this.outrosAssuntos = outrosAssuntos;
	}
	public String getDistribuicao() {
		return distribuicao;
	}
	public void setDistribuicao(String distribuicao) {
		this.distribuicao = distribuicao;
	}
	public String getControle() {
		return controle;
	}
	public void setControle(String controle) {
		this.controle = controle;
	}
	public String getJuiz() {
		return juiz;
	}
	public void setJuiz(String juiz) {
		this.juiz = juiz;
	}
	public String getOutrosNumeros() {
		return outrosNumeros;
	}
	public void setOutrosNumeros(String outrosNumeros) {
		this.outrosNumeros = outrosNumeros;
	}
	public String getValorAcao() {
		return valorAcao;
	}
	public void setValorAcao(String valorAcao) {
		this.valorAcao = valorAcao;
	}
}