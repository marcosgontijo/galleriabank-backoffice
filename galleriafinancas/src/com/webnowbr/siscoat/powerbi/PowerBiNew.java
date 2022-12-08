package com.webnowbr.siscoat.powerbi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.poi.ss.formula.functions.FinanceLib;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.common.DateUtil;


public class PowerBiNew {
	
	public String tipo;
	public int numeroOperacoes;
	public BigDecimal valorOperacoes;
		
	private List<PowerBiDetalhes> detalhes = new ArrayList<PowerBiDetalhes>();
	private List<ContratoCobranca> contratos  = new ArrayList<ContratoCobranca>();
	
	public PowerBiNew() {
		super();
		valorOperacoes = BigDecimal.ZERO;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getNumeroOperacoes() {
		return numeroOperacoes;
	}

	public void setNumeroOperacoes(int numeroOperacoes) {
		this.numeroOperacoes = numeroOperacoes;
	}

	public BigDecimal getValorOperacoes() {
		return valorOperacoes;
	}

	public void setValorOperacoes(BigDecimal valorOperacoes) {
		this.valorOperacoes = valorOperacoes;
	}

	public List<PowerBiDetalhes> getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(List<PowerBiDetalhes> detalhes) {
		this.detalhes = detalhes;
	}

	public List<ContratoCobranca> getContratos() {
		return contratos;
	}

	public void setContratos(List<ContratoCobranca> contratos) {
		this.contratos = contratos;
	}
	
}
