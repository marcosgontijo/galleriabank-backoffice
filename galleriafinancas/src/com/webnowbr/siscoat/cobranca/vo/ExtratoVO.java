package com.webnowbr.siscoat.cobranca.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.webnowbr.siscoat.common.SiscoatConstants;

public class ExtratoVO {

	private long idContratoCobranca;
	private String numeroContrato;
	private Date dataMovimento;
	private char debitoCredito;
	private BigDecimal valor;
	private String pagador;
	private char tipoLancamento;

	public String getDescricao() {
		return ((debitoCredito == 'D') ? "Aporte" : (tipoLancamento == 'Q' ? "Quitação do Contrato" : ((tipoLancamento == 'A' ? "Amortização": "Recebimento Juros")))) + "<br>"
				+ numeroContrato + " - " + ((pagador != null) ? pagador : "");
	}

	public long getIdContratoCobranca() {
		return idContratoCobranca;
	}

	public void setIdContratoCobranca(long idContratoCobranca) {
		this.idContratoCobranca = idContratoCobranca;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public Date getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public char getDebitoCredito() {
		return debitoCredito;
	}

	public void setDebitoCredito(char debitoCredito) {
		this.debitoCredito = debitoCredito;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getPagador() {
		return pagador;
	}

	public void setPagador(String pagador) {
		this.pagador = pagador;
	}

	public char getTipoLancamento() {
		return tipoLancamento;
	}

	public void setTipoLancamento(char tipoLancamento) {
		this.tipoLancamento = tipoLancamento;
	}

	

}
