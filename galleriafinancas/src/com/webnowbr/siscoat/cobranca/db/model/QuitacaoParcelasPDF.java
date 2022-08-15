package com.webnowbr.siscoat.cobranca.db.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class QuitacaoParcelasPDF {
	private String numeroParcela;
	private Date dataVencimento;
	private BigDecimal valorParcela;
	private BigDecimal valorDesconto;
	private BigDecimal valorPresenteParcela;

	public QuitacaoParcelasPDF() {
		super();
	}
	
	public QuitacaoParcelasPDF(String numeroParcela, Date dataVencimento, BigDecimal valorParcela,
			BigDecimal valorDesconto, BigDecimal valorPresenteParcela) {
		super();
		this.numeroParcela = numeroParcela;
		this.dataVencimento = dataVencimento;
		this.valorParcela = valorParcela;
		this.valorDesconto = valorDesconto;
		this.valorPresenteParcela = valorPresenteParcela;
	}


	public String getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(String numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}
	
	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public BigDecimal getValorPresenteParcela() {
		return valorPresenteParcela;
	}
	
	public void setValorPresenteParcela(BigDecimal valorPresenteParcela) {
		this.valorPresenteParcela = valorPresenteParcela;
	}
}
