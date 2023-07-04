package com.webnowbr.siscoat.cobranca.auxiliar;

import java.math.BigDecimal;
import java.math.BigInteger;

public class RelatorioFinanceiroCobrancaResumo {
	
	public BigInteger qtdeNumeroContrato;
	
	public BigDecimal valorParcela;
	private BigDecimal vlrTotalPago;
	
	
	
	public RelatorioFinanceiroCobrancaResumo() {
		super();
	}


	public RelatorioFinanceiroCobrancaResumo(BigInteger qtdeNumeroContrato, BigDecimal valorParcela,
			BigDecimal vlrTotalPago) {
		super();
		this.qtdeNumeroContrato = qtdeNumeroContrato;
		this.valorParcela = valorParcela;
		this.vlrTotalPago = vlrTotalPago;
	}


	public BigInteger getQtdeNumeroContrato() {
		return qtdeNumeroContrato;
	}


	public void setQtdeNumeroContrato(BigInteger qtdeNumeroContrato) {
		this.qtdeNumeroContrato = qtdeNumeroContrato;
	}


	public BigDecimal getValorParcela() {
		return valorParcela;
	}


	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}


	public BigDecimal getVlrTotalPago() {
		return vlrTotalPago;
	}


	public void setVlrTotalPago(BigDecimal vlrTotalPago) {
		this.vlrTotalPago = vlrTotalPago;
	}
	
	

	
		
}
