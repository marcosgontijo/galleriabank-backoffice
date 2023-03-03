package com.webnowbr.siscoat.contab.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.webnowbr.siscoat.common.CommonsUtil;

public class BalancoPatrimonial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 634225993537962423L;
	/** Chave primaria. */
	private long id;

	private BigDecimal saldoTotalApi;
	private Date aaaaMM;
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
	private BigDecimal depositosJudiciais;
	private BigDecimal investOperAntigas;
	private BigDecimal investimentos;
	private BigDecimal bensImobilizados;

	public BigDecimal getTotalAtivos() {
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(saldoCaixa))
			result.add(saldoCaixa);
		if (!CommonsUtil.semValor(saldoBancos))
		result.add(saldoBancos);
		if (!CommonsUtil.semValor(saldoAplFin))
			result.add(saldoAplFin);
		if (!CommonsUtil.semValor(opPagasReceberFidc))
			result.add(opPagasReceberFidc);
		if (!CommonsUtil.semValor(apItauSoberano))
			result.add(apItauSoberano);
		if (!CommonsUtil.semValor(provisaoDevedoresDuvidosos))
			result.add(provisaoDevedoresDuvidosos);
		if (!CommonsUtil.semValor(saldoCobrancaFidc))
			result.add(saldoCobrancaFidc);
		if (!CommonsUtil.semValor(depositoBacenScd))
			result.add(depositoBacenScd);
		if (!CommonsUtil.semValor(direitosCreditorios))
			result.add(direitosCreditorios);
		if (!CommonsUtil.semValor(tributosCompensar))
			result.add(tributosCompensar);
		if (!CommonsUtil.semValor(adiantamentos))
			result.add(adiantamentos);
		if (!CommonsUtil.semValor(outrosCreditos))
			result.add(outrosCreditos);
		if (!CommonsUtil.semValor(estoque))
			result.add(estoque);
		if (!CommonsUtil.semValor(depositosJudiciais))
			result.add(depositosJudiciais);
		if (!CommonsUtil.semValor(investOperAntigas))
			result.add(investOperAntigas);
		if (!CommonsUtil.semValor(investimentos))
			result.add(investimentos);
		if (!CommonsUtil.semValor(bensImobilizados))
			result.add(bensImobilizados);

		return result;
	};

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getSaldo_total_api() {
		return saldoTotalApi;
	}

	public void setSaldo_total_api(BigDecimal saldo_total_api) {
		this.saldoTotalApi = saldo_total_api;
	}

	public Date getAaaaMM() {
		return aaaaMM;
	}

	public void setAaaamm(Date aaaaMM) {
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
		return depositosJudiciais;
	}

	public void setDepositosjudiciais(BigDecimal depositosjudiciais) {
		this.depositosJudiciais = depositosjudiciais;
	}

	public BigDecimal getInvestOperantigas() {
		return investOperAntigas;
	}

	public void setInvestOperantigas(BigDecimal investOperantigas) {
		this.investOperAntigas = investOperantigas;
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

	public BigDecimal getSaldoTotalApi() {
		return saldoTotalApi;
	}

	public void setSaldoTotalApi(BigDecimal saldoTotalApi) {
		this.saldoTotalApi = saldoTotalApi;
	}

	public BigDecimal getDepositosJudiciais() {
		return depositosJudiciais;
	}

	public void setDepositosJudiciais(BigDecimal depositosJudiciais) {
		this.depositosJudiciais = depositosJudiciais;
	}

	public BigDecimal getInvestOperAntigas() {
		return investOperAntigas;
	}

	public void setInvestOperAntigas(BigDecimal investOperAntigas) {
		this.investOperAntigas = investOperAntigas;
	}

	public void setAaaaMM(Date aaaaMM) {
		this.aaaaMM = aaaaMM;
	}

}