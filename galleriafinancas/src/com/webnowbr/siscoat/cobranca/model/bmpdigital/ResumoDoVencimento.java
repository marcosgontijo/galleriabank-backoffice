package com.webnowbr.siscoat.cobranca.model.bmpdigital;

import java.math.BigDecimal;

public class ResumoDoVencimento {

	private String codigoVencimento;
	private BigDecimal valorVencimento;
	private boolean valorVencimentoSpecified;
	
	public ResumoDoVencimento() {
	}

	public String getCodigoVencimento() {
		return codigoVencimento;
	}

	public void setCodigoVencimento(String codigoVencimento) {
		this.codigoVencimento = codigoVencimento;
	}

	public BigDecimal getValorVencimento() {
		return valorVencimento;
	}

	public void setValorVencimento(BigDecimal valorVencimento) {
		this.valorVencimento = valorVencimento;
	}

	public boolean isValorVencimentoSpecified() {
		return valorVencimentoSpecified;
	}

	public void setValorVencimentoSpecified(boolean valorVencimentoSpecified) {
		this.valorVencimentoSpecified = valorVencimentoSpecified;
	}
}
