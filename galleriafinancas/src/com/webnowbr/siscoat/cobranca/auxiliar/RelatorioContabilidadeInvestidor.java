package com.webnowbr.siscoat.cobranca.auxiliar;

import java.math.BigDecimal;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;

public class RelatorioContabilidadeInvestidor {

	private PagadorRecebedor investidor;	
	private BigDecimal saldoInvestidoresAberto;
	
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
}
