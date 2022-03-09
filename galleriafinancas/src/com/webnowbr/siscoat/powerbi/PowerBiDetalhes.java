package com.webnowbr.siscoat.powerbi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;

public class PowerBiDetalhes {
	
	private String nome;
	private int qtdContratos;
	private Collection<ContratoCobranca> contratos;
	
	public PowerBiDetalhes() {
		super();
		this.contratos = new ArrayList<ContratoCobranca>();
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

	public Collection<ContratoCobranca> getContratos() {
		return contratos;
	}

	public void setContratos(Collection<ContratoCobranca> contratos) {
		this.contratos = contratos;
	}
}
