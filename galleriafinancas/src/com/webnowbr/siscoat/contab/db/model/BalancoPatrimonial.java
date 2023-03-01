package com.webnowbr.siscoat.contab.db.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class BalancoPatrimonial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 634225993537962423L;

	private BigDecimal saldoTotalApi;
	private BigDecimal aaaaMM;
	private BigDecimal saldoCaixa;
	private BigDecimal saldoBancos;
	private BigDecimal saldoAplFin;
	private BigDecimal opPagasReceberFidc;
	private BigDecimal apItauSoberano;
	private BigDecimal provisaoDevedoresDuvidosos;
	private BigDecimal saldoCobrancaFidc;
	private BigDecimal depositoBacenScd;
	private BigDecimal direitosCreditorios;
	private BigDecimal tributosCompensar;
	private BigDecimal adiantamentos;
	private BigDecimal outrosCreditos;
	private BigDecimal estoque;
	private BigDecimal depositosjudiciais;
	private BigDecimal investOperantigas;
	private BigDecimal investimentos;
	private BigDecimal bensImobilizados;

	private
	
	public BigDecimal getSaldo_total_api() {
		return saldoTotalApi;
	}

	public void setSaldo_total_api(BigDecimal saldo_total_api) {
		this.saldoTotalApi = saldo_total_api;
	}

	public BigDecimal getAaaaMM() {
		return aaaaMM;
	}

	public void setAaaamm(BigDecimal aaaaMM) {
		this.aaaaMM = aaaaMM;
	}

	public BigDecimal getSaldoCaixa() {
		return saldoCaixa;
	}

	public void setSaldoCaixa(BigDecimal saldoCaixa) {
		this.saldoCaixa = saldoCaixa;
	}

	public BigDecimal getSaldoBancos() {
		return saldoBancos;
	}

	public void setSaldoBancos(BigDecimal saldoBancos) {
		this.saldoBancos = saldoBancos;
	}

	public BigDecimal getSaldoAplFin() {
		return saldoAplFin;
	}

	public void setSaldoAplFin(BigDecimal saldoAplFin) {
		this.saldoAplFin = saldoAplFin;
	}

	public BigDecimal getOpPagasReceberFidc() {
		return opPagasReceberFidc;
	}

	public void setOpPagasReceberFidc(BigDecimal opPagasReceberFidc) {
		this.opPagasReceberFidc = opPagasReceberFidc;
	}

	public BigDecimal getApItauSoberano() {
		return apItauSoberano;
	}

	public void setApItauSoberano(BigDecimal apItauSoberano) {
		this.apItauSoberano = apItauSoberano;
	}

	public BigDecimal getProvisaoDevedoresDuvidosos() {
		return provisaoDevedoresDuvidosos;
	}

	public void setProvisaoDevedoresDuvidosos(BigDecimal provisaoDevedoresDuvidosos) {
		this.provisaoDevedoresDuvidosos = provisaoDevedoresDuvidosos;
	}

	public BigDecimal getSaldoCobrancaFidc() {
		return saldoCobrancaFidc;
	}

	public void setSaldoCobrancaFidc(BigDecimal saldoCobrancaFidc) {
		this.saldoCobrancaFidc = saldoCobrancaFidc;
	}

	public BigDecimal getDepositoBacenScd() {
		return depositoBacenScd;
	}

	public void setDepositoBacenScd(BigDecimal depositoBacenScd) {
		this.depositoBacenScd = depositoBacenScd;
	}

	public BigDecimal getDireitosCreditorios() {
		return direitosCreditorios;
	}

	public void setDireitosCreditorios(BigDecimal direitosCreditorios) {
		this.direitosCreditorios = direitosCreditorios;
	}

	public BigDecimal getTributosCompensar() {
		return tributosCompensar;
	}

	public void setTributosCompensar(BigDecimal tributosCompensar) {
		this.tributosCompensar = tributosCompensar;
	}

	public BigDecimal getAdiantamentos() {
		return adiantamentos;
	}

	public void setAdiantamentos(BigDecimal adiantamentos) {
		this.adiantamentos = adiantamentos;
	}

	public BigDecimal getOutrosCreditos() {
		return outrosCreditos;
	}

	public void setOutrosCreditos(BigDecimal outrosCreditos) {
		this.outrosCreditos = outrosCreditos;
	}

	public BigDecimal getEstoque() {
		return estoque;
	}

	public void setEstoque(BigDecimal estoque) {
		this.estoque = estoque;
	}

	public BigDecimal getDepositosjudiciais() {
		return depositosjudiciais;
	}

	public void setDepositosjudiciais(BigDecimal depositosjudiciais) {
		this.depositosjudiciais = depositosjudiciais;
	}

	public BigDecimal getInvestOperantigas() {
		return investOperantigas;
	}

	public void setInvestOperantigas(BigDecimal investOperantigas) {
		this.investOperantigas = investOperantigas;
	}

	public BigDecimal getInvestimentos() {
		return investimentos;
	}

	public void setInvestimentos(BigDecimal investimentos) {
		this.investimentos = investimentos;
	}

	public BigDecimal getBensImobilizados() {
		return bensImobilizados;
	}

	public void setBensImobilizados(BigDecimal bensImobilizados) {
		this.bensImobilizados = bensImobilizados;
	}
}