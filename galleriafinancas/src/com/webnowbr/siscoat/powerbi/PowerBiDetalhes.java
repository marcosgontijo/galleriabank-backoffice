package com.webnowbr.siscoat.powerbi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;

public class PowerBiDetalhes {
	
	private String nome;
	private int qtdContratos;
	private List<ContratoCobranca> contratos;
	
	public PowerBiDetalhes() {
		super();
		this.contratos = new ArrayList<ContratoCobranca>();
	}
	
	public PowerBiDetalhes(PowerBiDetalhes pbD) {
		super();
		this.contratos = new ArrayList<ContratoCobranca>();
		for(ContratoCobranca contrato : pbD.getContratos()) {
			this.contratos.add(contrato);
		}
		this.nome = pbD.getNome();
		this.qtdContratos = pbD.getQtdContratos();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getQtdContratos() {
		return qtdContratos;
	}

	public void setQtdContratos(int qtdContratos) {
		this.qtdContratos = qtdContratos;
	}

	public List<ContratoCobranca> getContratos() {
		return contratos;
	}

	public void setContratos(List<ContratoCobranca> contratos) {
		this.contratos = contratos;
	}
}
