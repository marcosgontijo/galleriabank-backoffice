package com.webnowbr.siscoat.tempoAnalise;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;

public class Analise {
	
	private String nome;
	private int qtdAnalises;
	private List<ContratosAnalise> contratos;
	private Time tempoMedio;
	
	public Analise() {
		super();
		this.contratos = new ArrayList<ContratosAnalise>();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getQtdAnalises() {
		return qtdAnalises;
	}

	public void setQtdAnalises(int qtdAnalises) {
		this.qtdAnalises = qtdAnalises;
	}

	public List<ContratosAnalise> getContratos() {
		return contratos;
	}

	public void setContratos(List<ContratosAnalise> contratos) {
		this.contratos = contratos;
	}

	public Time getTempoMedio() {
		return tempoMedio;
	}

	public void setTempoMedio(Time tempoMedio) {
		this.tempoMedio = tempoMedio;
	}
}
