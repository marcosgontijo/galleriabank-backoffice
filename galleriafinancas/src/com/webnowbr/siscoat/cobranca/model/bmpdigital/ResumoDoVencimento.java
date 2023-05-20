package com.webnowbr.siscoat.cobranca.model.bmpdigital;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ResumoDoVencimento {

	@SerializedName("CodigoVencimento")
	private String codigoVencimento;
	
	@SerializedName("ValorVencimento")
	private BigDecimal valorVencimento;
	
	@SerializedName("ValorVencimentoSpecified")
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
