package com.webnowbr.siscoat.cobranca.auxiliar;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;

public class RelatorioContabilidadeInvestidor {

	private PagadorRecebedor investidor;	
	private BigDecimal saldoInvestidoresAberto;
	
	private Date dataParcela;
	private BigDecimal valorParcela;
	
	private BigDecimal valorJuros;
	
	private List<ContratoCobrancaParcelasInvestidor> lisContratoCobrancaParcelasInvestidor;
	
	public RelatorioContabilidadeInvestidor() {
		this.investidor = new PagadorRecebedor("RelatorioContabilidadeInvestidor");
	}

	public PagadorRecebedor getInvestidor() {
		return investidor;
	}

	public void setInvestidor(PagadorRecebedor investidor) {
		this.investidor = investidor;
	}

	public BigDecimal getSaldoInvestidoresAberto() {
		return saldoInvestidoresAberto;
	}

	public void setSaldoInvestidoresAberto(BigDecimal saldoInvestidoresAberto) {
		this.saldoInvestidoresAberto = saldoInvestidoresAberto;
	}

	public Date getDataParcela() {
		return dataParcela;
	}

	public void setDataParcela(Date dataParcela) {
		this.dataParcela = dataParcela;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public BigDecimal getValorJuros() {
		return valorJuros;
	}

	public void setValorJuros(BigDecimal valorJuros) {
		this.valorJuros = valorJuros;
	}

	public List<ContratoCobrancaParcelasInvestidor> getLisContratoCobrancaParcelasInvestidor() {
		return lisContratoCobrancaParcelasInvestidor;
	}

	public void setLisContratoCobrancaParcelasInvestidor(
			List<ContratoCobrancaParcelasInvestidor> lisContratoCobrancaParcelasInvestidor) {
		this.lisContratoCobrancaParcelasInvestidor = lisContratoCobrancaParcelasInvestidor;
	}
}
