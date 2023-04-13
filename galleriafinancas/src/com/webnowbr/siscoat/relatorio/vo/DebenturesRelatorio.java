package com.webnowbr.siscoat.relatorio.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DebenturesRelatorio implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nome;
	private String cpfCnpj;
	private String agenciaConta;
	private String numerocontrato;
	private String garantido;
	private Date dataVencimento;
	private BigDecimal taxaMensal;
	private String recebeMensal;
	private BigDecimal valorBruto;
	private BigDecimal valorLiquido;
	private BigDecimal taxaDiaria;
	private BigDecimal valorFace;
	private Date dataInicio;

	private long idContrato;
	private long idInvestidor;
	private long idParcela;
	private int numeroInvestidor;

	private List<SaquesDebentures> saques;
	private List<DataCalculoDebentures> calculos;
	private List<DataCalculoDebentures> total;

	public DebenturesRelatorio() {
		saques = new ArrayList<SaquesDebentures>();
		calculos = new ArrayList<DataCalculoDebentures>();
		total = new ArrayList<DataCalculoDebentures>();
	}

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

	public String getAgenciaConta() {
		return agenciaConta;
	}

	public void setAgenciaConta(String agenciaConta) {
		this.agenciaConta = agenciaConta;
	}

	public String getNumerocontrato() {
		return numerocontrato;
	}

	public void setNumerocontrato(String numerocontrato) {
		this.numerocontrato = numerocontrato;
	}

	public String getGarantido() {
		return garantido;
	}

	public void setGarantido(String garantido) {
		this.garantido = garantido;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public BigDecimal getTaxaMensal() {
		return taxaMensal;
	}

	public void setTaxaMensal(BigDecimal taxaMensal) {
		this.taxaMensal = taxaMensal;
	}

	public String getRecebeMensal() {
		return recebeMensal;
	}

	public void setRecebeMensal(String recebeMensal) {
		this.recebeMensal = recebeMensal;
	}

	public BigDecimal getValorBruto() {
		return valorBruto;
	}

	public void setValorBruto(BigDecimal valorBruto) {
		this.valorBruto = valorBruto;
	}

	public BigDecimal getValorLiquido() {
		return valorLiquido;
	}

	public void setValorLiquido(BigDecimal valorLiquido) {
		this.valorLiquido = valorLiquido;
	}

	public BigDecimal getTaxaDiaria() {
		return taxaDiaria;
	}

	public void setTaxaDiaria(BigDecimal taxaDiaria) {
		this.taxaDiaria = taxaDiaria;
	}

	public BigDecimal getValorFace() {
		return valorFace;
	}

	public void setValorFace(BigDecimal valorFace) {
		this.valorFace = valorFace;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public List<SaquesDebentures> getSaques() {
		return saques;
	}

	public void setSaques(List<SaquesDebentures> saques) {
		this.saques = saques;
	}

	public List<DataCalculoDebentures> getCalculos() {
		return calculos;
	}

	public void setCalculos(List<DataCalculoDebentures> calculos) {
		this.calculos = calculos;
	}

	public List<DataCalculoDebentures> getTotal() {
		return total;
	}

	public void setTotal(List<DataCalculoDebentures> total) {
		this.total = total;
	}

	public long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(long idContrato) {
		this.idContrato = idContrato;
	}

	public long getIdInvestidor() {
		return idInvestidor;
	}

	public void setIdInvestidor(long idInvestidor) {
		this.idInvestidor = idInvestidor;
	}

	public long getIdParcela() {
		return idParcela;
	}

	public void setIdParcela(long idParcela) {
		this.idParcela = idParcela;
	}

	public int getNumeroInvestidor() {
		return numeroInvestidor;
	}

	public void setNumeroInvestidor(int numeroInvestidor) {
		this.numeroInvestidor = numeroInvestidor;
	}

}