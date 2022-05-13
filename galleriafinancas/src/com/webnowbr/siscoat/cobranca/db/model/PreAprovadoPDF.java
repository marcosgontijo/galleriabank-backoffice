package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PreAprovadoPDF implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nome;
	private Date data;
	private String numeroOp;
	private String cpf;
	private BigDecimal taxa;
	private String observacao;
	private String cidade;
	private String matricula;
	private String uf;
	private String prazo;
	
	public PreAprovadoPDF(String nome, Date data, String numeroOp, String cpf, BigDecimal taxa, String observacao,
			String cidade, String matricula, String uf, String prazo) {
		super();
		this.nome = nome;
		this.data = data;
		this.numeroOp = numeroOp;
		this.cpf = cpf;
		this.taxa = taxa;
		this.observacao = observacao;
		this.cidade = cidade;
		this.matricula = matricula;
		this.uf = uf;
		this.prazo = prazo;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getNumeroOp() {
		return numeroOp;
	}
	public void setNumeroOp(String numeroOp) {
		this.numeroOp = numeroOp;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public BigDecimal getTaxa() {
		return taxa;
	}
	public void setTaxa(BigDecimal taxa) {
		this.taxa = taxa;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getPrazo() {
		return prazo;
	}
	public void setPrazo(String prazo) {
		this.prazo = prazo;
	}
	
}
