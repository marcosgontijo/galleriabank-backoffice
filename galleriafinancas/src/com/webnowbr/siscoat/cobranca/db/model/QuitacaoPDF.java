package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuitacaoPDF implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nome;
	private Date data;
	private String numeroOp;
	private String cpf;
	private BigDecimal valorQuitacao;
	
	private List<QuitacaoParcelasPDF> parcelas = new ArrayList<QuitacaoParcelasPDF>();
	
	public QuitacaoPDF(String nome, Date data, String numeroOp, String cpf) {
		super();
		this.nome = nome;
		this.data = data;
		this.numeroOp = numeroOp;
		this.cpf = cpf;
	}
	
	public QuitacaoPDF() {
		super();
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

	public BigDecimal getValorQuitacao() {
		return valorQuitacao;
	}

	public void setValorQuitacao(BigDecimal valorQuitacao) {
		this.valorQuitacao = valorQuitacao;
	}

	public List<QuitacaoParcelasPDF> getParcelas() {
		return parcelas;
	}

	public void setParcelas(List<QuitacaoParcelasPDF> parcelas) {
		this.parcelas = parcelas;
	}
}
