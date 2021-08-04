package com.webnowbr.siscoat.cobranca.auxiliar;

import java.math.BigDecimal;
import java.util.Date;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;

public class RelatorioContabilidadeInvestidor {

	private PagadorRecebedor investidor;	
	private BigDecimal saldoInvestidoresAberto;
	
	private Date dataParcela;
	private BigDecimal valorParcela;
	
	private BigDecimal valorJuros;
	
	public RelatorioContabilidadeInvestidor() {
		this.investidor = new PagadorRecebedor();
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
}
