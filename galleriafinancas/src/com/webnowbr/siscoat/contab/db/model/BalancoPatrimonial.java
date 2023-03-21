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
	private Long id;

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
	private BigDecimal contaCorrenteClientes;
	private BigDecimal fornecedoresConsorcio;
	private BigDecimal obrigacoesTributarias;
	private BigDecimal obrigacoesSociaisEstatutarias;
	private BigDecimal recursosDebentures;
	private BigDecimal recursosFidc;
	private BigDecimal recursosCri;
	private BigDecimal provisaoLiquidAntecipada;
	private BigDecimal valorExigivelLongoPrazo;
	private BigDecimal capitalSocial;
	private BigDecimal lucrosAcumuladosAnoAnterior;
	private BigDecimal distribuicao2Pago1;
	private BigDecimal lucroSemestreAnterior;
	private BigDecimal aumentoCapitalSocial;
	private BigDecimal distribuicao1Pago2;
	private BigDecimal lucroAnterior;

	public BigDecimal getTotalAtivos(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(saldoCaixa))
			result=result.add(saldoCaixa);
		if (!CommonsUtil.semValor(saldoBancos))
			result=result.add(saldoBancos);
		if (!CommonsUtil.semValor(saldoAplFin))
			result=result.add(saldoAplFin);
		if (!CommonsUtil.semValor(opPagasReceberFidc))
			result=result.add(opPagasReceberFidc);
		if (!CommonsUtil.semValor(apItauSoberano))
			result=result.add(apItauSoberano);
		if (!CommonsUtil.semValor(provisaoDevedoresDuvidosos))
			result=result.add(provisaoDevedoresDuvidosos);
		if (!CommonsUtil.semValor(saldoCobrancaFidc))
			result=result.add(saldoCobrancaFidc);
		if (!CommonsUtil.semValor(depositoBacenScd))
			result=result.add(depositoBacenScd);
		if (!CommonsUtil.semValor(direitosCreditorios))
			result=result.add(direitosCreditorios);
		if (!CommonsUtil.semValor(tributosCompensar))
			result=result.add(tributosCompensar);
		if (!CommonsUtil.semValor(adiantamentos))
			result=result.add(adiantamentos);
		if (!CommonsUtil.semValor(outrosCreditos))
			result=result.add(outrosCreditos);
		if (!CommonsUtil.semValor(estoque))
			result=result.add(estoque);
		if (!CommonsUtil.semValor(depositosJudiciais))
			result=result.add(depositosJudiciais);
		if (!CommonsUtil.semValor(investOperAntigas))
			result=result.add(investOperAntigas);
		if (!CommonsUtil.semValor(investimentos))
			result=result.add(investimentos);
		if (!CommonsUtil.semValor(bensImobilizados))
			result=result.add(bensImobilizados);

		return result;
	}
	//ATIVO
	public BigDecimal getTotalCaixa(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(saldoCaixa))
			result=result.add(saldoCaixa);
		if (!CommonsUtil.semValor(saldoBancos))
			result=result.add(saldoBancos);
		if (!CommonsUtil.semValor(saldoAplFin))
			result=result.add(saldoAplFin);
		if (!CommonsUtil.semValor(opPagasReceberFidc))
			result=result.add(opPagasReceberFidc);
		if (!CommonsUtil.semValor(apItauSoberano))
			result=result.add(apItauSoberano);
		if (!CommonsUtil.semValor(provisaoDevedoresDuvidosos))
			result=result.add(provisaoDevedoresDuvidosos);
		if (!CommonsUtil.semValor(saldoCobrancaFidc))
			result=result.add(saldoCobrancaFidc);
		if (!CommonsUtil.semValor(depositoBacenScd))
			result=result.add(depositoBacenScd);
		return result;
	}
	
	//ATIVO
	public BigDecimal getTotalRealizavelCurtoPrazo(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(direitosCreditorios))
			result=result.add(direitosCreditorios);
		if (!CommonsUtil.semValor(tributosCompensar))
			result=result.add(tributosCompensar);
		if (!CommonsUtil.semValor(adiantamentos))
			result=result.add(adiantamentos);
		if (!CommonsUtil.semValor(outrosCreditos))
			result=result.add(outrosCreditos);
		if (!CommonsUtil.semValor(estoque))
			result=result.add(estoque);
		return result;
	}
	
	//ATIVO
	public BigDecimal getTotalAtivoCirculante(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(saldoCaixa))
			result=result.add(saldoCaixa);
		if (!CommonsUtil.semValor(saldoBancos))
			result=result.add(saldoBancos);
		if (!CommonsUtil.semValor(saldoAplFin))
			result=result.add(saldoAplFin);
		if (!CommonsUtil.semValor(opPagasReceberFidc))
			result=result.add(opPagasReceberFidc);
		if (!CommonsUtil.semValor(apItauSoberano))
			result=result.add(apItauSoberano);
		if (!CommonsUtil.semValor(provisaoDevedoresDuvidosos))
			result=result.add(provisaoDevedoresDuvidosos);
		if (!CommonsUtil.semValor(saldoCobrancaFidc))
			result=result.add(saldoCobrancaFidc);
		if (!CommonsUtil.semValor(depositoBacenScd));
		if (!CommonsUtil.semValor(direitosCreditorios))
			result=result.add(direitosCreditorios);
		if (!CommonsUtil.semValor(tributosCompensar))
			result=result.add(tributosCompensar);
		if (!CommonsUtil.semValor(adiantamentos))
			result=result.add(adiantamentos);
		if (!CommonsUtil.semValor(outrosCreditos))
			result=result.add(outrosCreditos);
		if (!CommonsUtil.semValor(estoque))
			result=result.add(estoque);
		return result;
	}
	
	//ATIVO
	public BigDecimal getTotalRealizavelLongoPrazo(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(depositosJudiciais))
			result=result.add(depositosJudiciais);
		if (!CommonsUtil.semValor(investOperAntigas))
			result=result.add(investOperAntigas);
		return result;
	}
	
	//ATIVO
	public BigDecimal getTotalAtivoNaoCirculante(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(investimentos))
			result=result.add(investimentos);
		if (!CommonsUtil.semValor(bensImobilizados))
			result=result.add(bensImobilizados);
		return result;
	}
	
	//ATIVO
	public BigDecimal getTotalInvestimentos(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(investimentos))
			result=result.add(investimentos);
		
		return result;
	}
	
	//ATIVO
	public BigDecimal getTotalImobilizados(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(bensImobilizados))
			result=result.add(bensImobilizados);
		return result;
	}
	
	//PASSIVO
	public BigDecimal getTotalPassivo(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(contaCorrenteClientes))
			result=result.add(contaCorrenteClientes);
		if (!CommonsUtil.semValor(fornecedoresConsorcio))
			result=result.add(fornecedoresConsorcio);
		if (!CommonsUtil.semValor(obrigacoesTributarias))
			result=result.add(obrigacoesTributarias);
		if (!CommonsUtil.semValor(obrigacoesSociaisEstatutarias))
			result=result.add(obrigacoesSociaisEstatutarias);
		if (!CommonsUtil.semValor(recursosDebentures))
			result=result.add(recursosDebentures);
		if (!CommonsUtil.semValor(recursosFidc))
			result=result.add(recursosFidc);
		if (!CommonsUtil.semValor(recursosCri))
			result=result.add(recursosCri);
		if (!CommonsUtil.semValor(provisaoLiquidAntecipada))
			result=result.add(provisaoLiquidAntecipada);
		if (!CommonsUtil.semValor(valorExigivelLongoPrazo))
			result=result.add(valorExigivelLongoPrazo);
		if (!CommonsUtil.semValor(capitalSocial))
			result=result.add(capitalSocial);
		if (!CommonsUtil.semValor(lucrosAcumuladosAnoAnterior))
			result=result.add(lucrosAcumuladosAnoAnterior);
		if (!CommonsUtil.semValor(distribuicao2Pago1))
			result=result.add(distribuicao2Pago1);
		if (!CommonsUtil.semValor(lucroSemestreAnterior))
			result=result.add(lucroSemestreAnterior);
		if (!CommonsUtil.semValor(aumentoCapitalSocial))
			result=result.add(aumentoCapitalSocial);
		if (!CommonsUtil.semValor(distribuicao1Pago2))
			result=result.add(distribuicao1Pago2);
		if (!CommonsUtil.semValor(lucroAnterior))
			result=result.add(lucroAnterior);

		return result;
	}
	
	//PASSIVO
	public BigDecimal getTotalPassivoCirculante(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(contaCorrenteClientes))
			result=result.add(contaCorrenteClientes);
		if (!CommonsUtil.semValor(fornecedoresConsorcio))
			result=result.add(fornecedoresConsorcio);
		if (!CommonsUtil.semValor(obrigacoesTributarias))
			result=result.add(obrigacoesTributarias);
		if (!CommonsUtil.semValor(obrigacoesSociaisEstatutarias))
			result=result.add(obrigacoesSociaisEstatutarias);
		if (!CommonsUtil.semValor(recursosDebentures))
			result=result.add(recursosDebentures);
		if (!CommonsUtil.semValor(recursosFidc))
			result=result.add(recursosFidc);
		if (!CommonsUtil.semValor(recursosCri))
			result=result.add(recursosCri);
		if (!CommonsUtil.semValor(provisaoLiquidAntecipada))
			result=result.add(provisaoLiquidAntecipada);
		return result;
	}
	
	//PASSIVO
	public BigDecimal getTotalExigivelCurtoPrazo(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(contaCorrenteClientes))
			result=result.add(contaCorrenteClientes);
		if (!CommonsUtil.semValor(fornecedoresConsorcio))
			result=result.add(fornecedoresConsorcio);
		if (!CommonsUtil.semValor(obrigacoesTributarias))
			result=result.add(obrigacoesTributarias);
		if (!CommonsUtil.semValor(obrigacoesSociaisEstatutarias))
			result=result.add(obrigacoesSociaisEstatutarias);
		if (!CommonsUtil.semValor(recursosDebentures))
			result=result.add(recursosDebentures);
		if (!CommonsUtil.semValor(recursosFidc))
			result=result.add(recursosFidc);
		if (!CommonsUtil.semValor(recursosCri))
			result=result.add(recursosCri);
		
		return result;
	}
	
	
	//PASSIVO
	public BigDecimal getTotalExigivelLongoPrazo(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(valorExigivelLongoPrazo))
			result=result.add(valorExigivelLongoPrazo);
		
		return result;
	}
	
	//PASSIVO
	public BigDecimal getTotalPatrimonioLiquido(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(capitalSocial))
			result=result.add(capitalSocial);
		if (!CommonsUtil.semValor(lucrosAcumuladosAnoAnterior))
			result=result.add(lucrosAcumuladosAnoAnterior);
		if (!CommonsUtil.semValor(distribuicao2Pago1))
			result=result.add(distribuicao2Pago1);
		if (!CommonsUtil.semValor(lucroSemestreAnterior))
			result=result.add(lucroSemestreAnterior);
		if (!CommonsUtil.semValor(aumentoCapitalSocial))
			result=result.add(aumentoCapitalSocial);
		if (!CommonsUtil.semValor(distribuicao1Pago2))
			result=result.add(distribuicao1Pago2);
		if (!CommonsUtil.semValor(lucroAnterior))
			result=result.add(lucroAnterior);
		
		return result;
	}
	
	//PASSIVO
	public BigDecimal getTotalCapitalSocial(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(capitalSocial))
			result=result.add(capitalSocial);
		
		return result;
	}
	
	//PASSIVO
	public BigDecimal getTotalAcumuladosSemestreAnterior(){
		BigDecimal result = BigDecimal.ZERO;
		if (!CommonsUtil.semValor(lucrosAcumuladosAnoAnterior))
			result=result.add(lucrosAcumuladosAnoAnterior);
		if (!CommonsUtil.semValor(distribuicao2Pago1))
			result=result.add(distribuicao2Pago1);
		if (!CommonsUtil.semValor(lucroSemestreAnterior))
			result=result.add(lucroSemestreAnterior);
		if (!CommonsUtil.semValor(aumentoCapitalSocial))
			result=result.add(aumentoCapitalSocial);
		if (!CommonsUtil.semValor(distribuicao1Pago2))
			result=result.add(distribuicao1Pago2);
		if (!CommonsUtil.semValor(lucroAnterior))
			result=result.add(lucroAnterior);
		
		return result;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
	public BigDecimal getContaCorrenteClientes() {
		return contaCorrenteClientes;
	}
	public void setContaCorrenteClientes(BigDecimal contaCorrenteClientes) {
		this.contaCorrenteClientes = contaCorrenteClientes;
	}
	public BigDecimal getFornecedoresConsorcio() {
		return fornecedoresConsorcio;
	}
	public void setFornecedoresConsorcio(BigDecimal fornecedoresConsorcio) {
		this.fornecedoresConsorcio = fornecedoresConsorcio;
	}
	public BigDecimal getObrigacoesTributarias() {
		return obrigacoesTributarias;
	}
	public void setObrigacoesTributarias(BigDecimal obrigacoesTributarias) {
		this.obrigacoesTributarias = obrigacoesTributarias;
	}
	public BigDecimal getObrigacoesSociaisEstatutarias() {
		return obrigacoesSociaisEstatutarias;
	}
	public void setObrigacoesSociaisEstatutarias(BigDecimal obrigacoesSociaisEstatutarias) {
		this.obrigacoesSociaisEstatutarias = obrigacoesSociaisEstatutarias;
	}
	public BigDecimal getRecursosDebentures() {
		return recursosDebentures;
	}
	public void setRecursosDebentures(BigDecimal recursosDebentures) {
		this.recursosDebentures = recursosDebentures;
	}
	public BigDecimal getRecursosFidc() {
		return recursosFidc;
	}
	public void setRecursosFidc(BigDecimal recursosFidc) {
		this.recursosFidc = recursosFidc;
	}
	public BigDecimal getRecursosCri() {
		return recursosCri;
	}
	public void setRecursosCri(BigDecimal recursosCri) {
		this.recursosCri = recursosCri;
	}
	public BigDecimal getProvisaoLiquidAntecipada() {
		return provisaoLiquidAntecipada;
	}
	public void setProvisaoLiquidAntecipada(BigDecimal provisaoLiquidAntecipada) {
		this.provisaoLiquidAntecipada = provisaoLiquidAntecipada;
	}
	public BigDecimal getValorExigivelLongoPrazo() {
		return valorExigivelLongoPrazo;
	}
	public void setValorExigivelLongoPrazo(BigDecimal valorExigivelLongoPrazo) {
		this.valorExigivelLongoPrazo = valorExigivelLongoPrazo;
	}
	public BigDecimal getCapitalSocial() {
		return capitalSocial;
	}
	public void setCapitalSocial(BigDecimal capitalSocial) {
		this.capitalSocial = capitalSocial;
	}
	public BigDecimal getLucrosAcumuladosAnoAnterior() {
		return lucrosAcumuladosAnoAnterior;
	}
	public void setLucrosAcumuladosAnoAnterior(BigDecimal lucrosAcumuladosAnoAnterior) {
		this.lucrosAcumuladosAnoAnterior = lucrosAcumuladosAnoAnterior;
	}
	public BigDecimal getDistribuicao2Pago1() {
		return distribuicao2Pago1;
	}
	public void setDistribuicao2Pago1(BigDecimal distribuicao2Pago1) {
		this.distribuicao2Pago1 = distribuicao2Pago1;
	}
	public BigDecimal getLucroSemestreAnterior() {
		return lucroSemestreAnterior;
	}
	public void setLucroSemestreAnterior(BigDecimal lucroSemestreAnterior) {
		this.lucroSemestreAnterior = lucroSemestreAnterior;
	}
	public BigDecimal getAumentoCapitalSocial() {
		return aumentoCapitalSocial;
	}
	public void setAumentoCapitalSocial(BigDecimal aumentoCapitalSocial) {
		this.aumentoCapitalSocial = aumentoCapitalSocial;
	}
	public BigDecimal getDistribuicao1Pago2() {
		return distribuicao1Pago2;
	}
	public void setDistribuicao1Pago2(BigDecimal distribuicao1Pago2) {
		this.distribuicao1Pago2 = distribuicao1Pago2;
	}
	public BigDecimal getLucroAnterior() {
		return lucroAnterior;
	}
	public void setLucroAnterior(BigDecimal lucroAnterior) {
		this.lucroAnterior = lucroAnterior;
	}

}