package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.webnowbr.siscoat.common.CommonsUtil;

public class ContratoCobrancaParcelasInvestidor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String numeroParcela;
	private String empresa;
	private Date dataVencimento;
	private BigDecimal parcelaMensal;
	
	private BigDecimal juros;
	private BigDecimal amortizacao;
	private BigDecimal saldoCredor;
	private BigDecimal saldoCredorAtualizado;
	private BigDecimal irRetido;
	private BigDecimal valorLiquido;
	
	///
	
	private BigDecimal capitalizacao;
	private BigDecimal valorLiquidoBaixa;
	private BigDecimal parcelaMensalBaixa;
	private BigDecimal jurosBaixa;
	
	private boolean baixado;
	private Date dataBaixa;
	private BigDecimal valorBaixado;

	private PagadorRecebedor investidor;
	private PagadorRecebedor pagador;

	private String numeroContrato;
	private long idContrato;

	// atributos temporarios, sem persistir
	private boolean parcelaVencendo;
	private boolean parcelaVencida;
	private boolean envelope;
	private boolean investidorGarantido;

	private boolean parcelaContratoVencida;

	public ContratoCobrancaParcelasInvestidor() {

	}
	
	public void setParcelaMensalBaixa(BigDecimal parcelaMensalBaixa) {		
		if (BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(capitalizacao)) == -1) {
			this.parcelaMensalBaixa = parcelaMensalBaixa;
		} else {
			this.parcelaMensal = parcelaMensalBaixa;
		}
	}


	public void setValorLiquidoBaixa(BigDecimal valorLiquidoBaixa) {
		if (BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(capitalizacao)) == -1) {
			this.valorLiquidoBaixa = valorLiquidoBaixa;
		} else {
			this.valorLiquido = valorLiquidoBaixa;
		}
	}


	public BigDecimal getParcelaMensalBaixa() {		
		if (BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(capitalizacao)) == -1) {
			this.parcelaMensalBaixa = this.capitalizacao.add(this.amortizacao);
		} else {
			this.parcelaMensalBaixa = parcelaMensal;
		}
		return parcelaMensalBaixa;
	}

	public void setJurosBaixa(BigDecimal jurosBaixa) {
		if (BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(capitalizacao)) == -1) {
			this.capitalizacao = jurosBaixa;
		} else {
			this.juros = jurosBaixa;
		}
	}


	public BigDecimal getJurosBaixa() {
		if (BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(capitalizacao)) == -1) {
			this.jurosBaixa = this.capitalizacao;
		} else {
			this.jurosBaixa = this.juros;
		}
		return jurosBaixa;
	}
	
	public BigDecimal getValorLiquidoBaixa() {		
		if (BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(capitalizacao)) == -1) {
			this.valorLiquidoBaixa = this.capitalizacao.add(this.amortizacao).subtract(irRetido);
		} else {
			this.valorLiquidoBaixa = this.valorLiquido;
		}
		return this.valorLiquidoBaixa;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public BigDecimal getParcelaMensal() {
		return parcelaMensal;
	}

	public void setParcelaMensal(BigDecimal parcelaMensal) {
		this.parcelaMensal = parcelaMensal;
	}

	public BigDecimal getJuros() {
		return juros;
	}

	public void setJuros(BigDecimal juros) {
		this.juros = juros;
	}

	public BigDecimal getCapitalizacao() {
		return capitalizacao;
	}

	public void setCapitalizacao(BigDecimal capitalizacao) {
		this.capitalizacao = capitalizacao;
	}

	public BigDecimal getAmortizacao() {
		return amortizacao;
	}

	public void setAmortizacao(BigDecimal amortizacao) {
		this.amortizacao = amortizacao;
	}

	public BigDecimal getSaldoCredor() {
		return saldoCredor;
	}

	public void setSaldoCredor(BigDecimal saldoCredor) {
		this.saldoCredor = saldoCredor;
	}

	public BigDecimal getIrRetido() {
		return irRetido;
	}

	public void setIrRetido(BigDecimal irRetido) {
		this.irRetido = irRetido;
	}

	public BigDecimal getValorLiquido() {
		return valorLiquido;
	}

	public void setValorLiquido(BigDecimal valorLiquido) {
		this.valorLiquido = valorLiquido;
	}

	public Date getDataBaixa() {
		return dataBaixa;
	}

	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}

	public BigDecimal getValorBaixado() {
		return valorBaixado;
	}

	public void setValorBaixado(BigDecimal valorBaixado) {
		this.valorBaixado = valorBaixado;
	}

	public boolean isBaixado() {
		return baixado;
	}

	public void setBaixado(boolean baixado) {
		this.baixado = baixado;
	}

	public BigDecimal getSaldoCredorAtualizado() {
		return saldoCredorAtualizado;
	}

	public void setSaldoCredorAtualizado(BigDecimal saldoCredorAtualizado) {
		this.saldoCredorAtualizado = saldoCredorAtualizado;
	}

	public PagadorRecebedor getInvestidor() {
		return investidor;
	}

	public void setInvestidor(PagadorRecebedor investidor) {
		this.investidor = investidor;
	}

	public boolean isParcelaVencendo() {
		return parcelaVencendo;
	}

	public void setParcelaVencendo(boolean parcelaVencendo) {
		this.parcelaVencendo = parcelaVencendo;
	}

	public boolean isParcelaVencida() {
		return parcelaVencida;
	}

	public void setParcelaVencida(boolean parcelaVencida) {
		this.parcelaVencida = parcelaVencida;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public PagadorRecebedor getPagador() {
		return pagador;
	}

	public void setPagador(PagadorRecebedor pagador) {
		this.pagador = pagador;
	}

	public boolean isEnvelope() {
		return envelope;
	}

	public void setEnvelope(boolean envelope) {
		this.envelope = envelope;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public boolean isInvestidorGarantido() {
		return investidorGarantido;
	}

	public void setInvestidorGarantido(boolean investidorGarantido) {
		this.investidorGarantido = investidorGarantido;
	}

	public boolean isParcelaContratoVencida() {
		return parcelaContratoVencida;
	}

	public void setParcelaContratoVencida(boolean parcelaContratoVencida) {
		this.parcelaContratoVencida = parcelaContratoVencida;
	}

	public long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(long idContrato) {
		this.idContrato = idContrato;
	}
}