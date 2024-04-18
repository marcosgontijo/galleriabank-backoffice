package com.webnowbr.siscoat.cobranca.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;

public class PlanilhaRestituicaoDetalhesVO  implements Serializable  {


	private String descricao;
	private BigDecimal valor;
	private boolean contaPaga;
	private Date dataPagamento;

	private Date dataVencimento;
	private BigDecimal valorPagamento;
	private String numeroDocumento;
	
	
	
	public PlanilhaRestituicaoDetalhesVO() {
		super();
	}
	
	public PlanilhaRestituicaoDetalhesVO(String descricao, BigDecimal valor,
			boolean contaPaga, Date dataPagamento, Date dataVencimento, BigDecimal valorPagamento,
			String numeroDocumento) {
		super();
		this.descricao = descricao;
		this.valor = valor;
		this.contaPaga = contaPaga;
		this.dataPagamento = dataPagamento;
		this.dataVencimento = dataVencimento;
		this.valorPagamento = valorPagamento;
		this.numeroDocumento = numeroDocumento;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public boolean isContaPaga() {
		return contaPaga;
	}
	public void setContaPaga(boolean contaPaga) {
		this.contaPaga = contaPaga;
	}
	public Date getDataPagamento() {
		return dataPagamento;
	}
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	public Date getDataVencimento() {
		return dataVencimento;
	}
	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}
	public BigDecimal getValorPagamento() {
		return valorPagamento;
	}
	public void setValorPagamento(BigDecimal valorPagamento) {
		this.valorPagamento = valorPagamento;
	}
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	
	
	
	
}
