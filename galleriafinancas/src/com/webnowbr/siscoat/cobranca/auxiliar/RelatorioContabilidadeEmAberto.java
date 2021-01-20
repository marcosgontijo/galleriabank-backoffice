package com.webnowbr.siscoat.cobranca.auxiliar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;

public class RelatorioContabilidadeEmAberto {
	
	public String numeroContrato;
	public Date dataContrato;
	
	public ContratoCobranca contrato;
	
	private PagadorRecebedor pagador;
	private Responsavel responsavel;
	
	private List<RelatorioContabilidadeInvestidor> listInvestidores;
	
	private List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhesQuitadas;
	private List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhesEmAberto;
	
	private BigDecimal saldoParcelasAberto;
	private BigDecimal saldoInvestidoresAberto;
	
	public RelatorioContabilidadeEmAberto() {
		this.listContratoCobrancaDetalhesEmAberto = new ArrayList<ContratoCobrancaDetalhes>();
		this.listContratoCobrancaDetalhesQuitadas = new ArrayList<ContratoCobrancaDetalhes>();
		this.listInvestidores = new ArrayList<RelatorioContabilidadeInvestidor>();
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public Date getDataContrato() {
		return dataContrato;
	}

	public void setDataContrato(Date dataContrato) {
		this.dataContrato = dataContrato;
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public PagadorRecebedor getPagador() {
		return pagador;
	}

	public void setPagador(PagadorRecebedor pagador) {
		this.pagador = pagador;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public List<ContratoCobrancaDetalhes> getListContratoCobrancaDetalhesQuitadas() {
		return listContratoCobrancaDetalhesQuitadas;
	}

	public void setListContratoCobrancaDetalhesQuitadas(
			List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhesQuitadas) {
		this.listContratoCobrancaDetalhesQuitadas = listContratoCobrancaDetalhesQuitadas;
	}

	public List<ContratoCobrancaDetalhes> getListContratoCobrancaDetalhesEmAberto() {
		return listContratoCobrancaDetalhesEmAberto;
	}

	public void setListContratoCobrancaDetalhesEmAberto(
			List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhesEmAberto) {
		this.listContratoCobrancaDetalhesEmAberto = listContratoCobrancaDetalhesEmAberto;
	}

	public BigDecimal getSaldoParcelasAberto() {
		return saldoParcelasAberto;
	}

	public void setSaldoParcelasAberto(BigDecimal saldoParcelasAberto) {
		this.saldoParcelasAberto = saldoParcelasAberto;
	}

	public List<RelatorioContabilidadeInvestidor> getListInvestidores() {
		return listInvestidores;
	}

	public void setListInvestidores(List<RelatorioContabilidadeInvestidor> listInvestidores) {
		this.listInvestidores = listInvestidores;
	}

	public BigDecimal getSaldoInvestidoresAberto() {
		return saldoInvestidoresAberto;
	}

	public void setSaldoInvestidoresAberto(BigDecimal saldoInvestidoresAberto) {
		this.saldoInvestidoresAberto = saldoInvestidoresAberto;
	}
}
