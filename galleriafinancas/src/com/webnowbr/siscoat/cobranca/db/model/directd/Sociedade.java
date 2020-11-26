package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Sociedade implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String documento;
	private String nome;
	private String percentualParticipacao;
	private Date dataEntrada;	
	private String cargo;
	
	public Sociedade() {
		
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPercentualParticipacao() {
		return percentualParticipacao;
	}

	public void setPercentualParticipacao(String percentualParticipacao) {
		this.percentualParticipacao = percentualParticipacao;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
}