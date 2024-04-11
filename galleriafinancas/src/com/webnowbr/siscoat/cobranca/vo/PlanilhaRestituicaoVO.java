package com.webnowbr.siscoat.cobranca.vo;

import java.math.BigDecimal;
import java.util.Set;

import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;

public class PlanilhaRestituicaoVO {

	private String nome;
	private String cpfCnpj;
	private String numeroCcb;

	private BigDecimal valorCartaSplitGalleria;
	private BigDecimal somaValorPago;
	private BigDecimal contaPagarValorTotal;

	private Set<ContasPagar> listContasPagar;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getNumeroCcb() {
		return numeroCcb;
	}

	public void setNumeroCcb(String numeroCcb) {
		this.numeroCcb = numeroCcb;
	}

	public BigDecimal getValorCartaSplitGalleria() {
		return valorCartaSplitGalleria;
	}

	public void setValorCartaSplitGalleria(BigDecimal valorCartaSplitGalleria) {
		this.valorCartaSplitGalleria = valorCartaSplitGalleria;
	}

	public BigDecimal getSomaValorPago() {
		return somaValorPago;
	}

	public void setSomaValorPago(BigDecimal somaValorPago) {
		this.somaValorPago = somaValorPago;
	}

	public BigDecimal getContaPagarValorTotal() {
		return contaPagarValorTotal;
	}

	public void setContaPagarValorTotal(BigDecimal contaPagarValorTotal) {
		this.contaPagarValorTotal = contaPagarValorTotal;
	}

	public Set<ContasPagar> getListContasPagar() {
		return listContasPagar;
	}

	public void setListContasPagar(Set<ContasPagar> listContasPagar) {
		this.listContasPagar = listContasPagar;
	}

}
