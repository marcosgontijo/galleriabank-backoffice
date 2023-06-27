package com.webnowbr.siscoat.contab.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.service.OmieService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.omie.request.IOmieParam;
import com.webnowbr.siscoat.omie.request.ListarExtratoRequest;
import com.webnowbr.siscoat.omie.request.OmieRequestBase;
import com.webnowbr.siscoat.omie.response.OmieListarExtratoResponse;
import com.webnowbr.siscoat.relatorio.vo.RelatorioBalanco;

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
	private BigDecimal saldoCaixaNaoConciliado;
	private BigDecimal saldoBancos;
	private BigDecimal saldoAplFin;
	private BigDecimal saldoAplFinNaoConciliado;
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
	private BigDecimal cdi;
	private BigDecimal ipca;
	private BigDecimal taxaFidc;
	private BigDecimal taxaCri1;
	private BigDecimal taxaCri2;
	private BigDecimal taxaCri3;
	
	private BigDecimal custoPonderado;
	
	private List<OmieListarExtratoResponse> extratoResponse;
	private BigDecimal saldoCaixaOmie;
	
	public void calcularCustoPonderado(List<RelatorioBalanco> relatorioBalancoPagar) {
		BigDecimal somaParcela = BigDecimal.ZERO;
		BigDecimal somaFatorX = BigDecimal.ZERO;
		
		for(RelatorioBalanco pagarDebenture : relatorioBalancoPagar) {

			BigDecimal valorParcela = pagarDebenture.getValorContratoRelatorio();
			BigDecimal taxaInvestidor = pagarDebenture.getTaxaInvestidor();
			BigDecimal fatorX = valorParcela.multiply(taxaInvestidor);
	
			somaParcela = somaParcela.add(valorParcela);
			somaFatorX = somaFatorX.add(fatorX);
			
		}
		custoPonderado = (somaFatorX.divide(somaParcela,MathContext.DECIMAL128));
		System.out.println("Custo ponderado: " +custoPonderado);
		calcularDebenturistaPagar(relatorioBalancoPagar);
	}
	public void calcularDebenturistaPagar (List<RelatorioBalanco> relatorioBalancoPagar) {
		this.setRecursosDebentures(BigDecimal.ZERO);
		
		BigDecimal quantidadeMeses = BigDecimal.ONE;
		for(RelatorioBalanco pagarDebenture : relatorioBalancoPagar) {
			
			BigDecimal vlrParcela = pagarDebenture.getValorContratoRelatorio(); //vlrParcela = valor parcela
			
			
			quantidadeMeses = BigDecimal.valueOf(DateUtil.Days360(pagarDebenture.getDataVencimentoRelatorio(), this.getAaaaMM())); //quantidade de dias entre dataParcela e dataReferencia
			quantidadeMeses = quantidadeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128); //divide a quantidade acima por 30
			Double quantidadeMesesDouble = CommonsUtil.doubleValue(quantidadeMeses); //armazena resultado na variável
			
			double potencia = Math.pow(CommonsUtil.doubleValue(custoPonderado), quantidadeMesesDouble); //potencia = custoponderado elevado a quantidade de meses
			
			
			
			BigDecimal pagarDebenturista; //declaração de variável
			pagarDebenturista = (vlrParcela).multiply(CommonsUtil.bigDecimalValue(potencia) , MathContext.DECIMAL128); //valor da parcela * potencia (acima)
			pagarDebenturista = pagarDebenturista.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			if (recursosDebentures == null) {
				recursosDebentures = BigDecimal.ZERO;
			}
			this.setRecursosDebentures(this.getRecursosDebentures().add(pagarDebenturista));
			}			
		
		
	}
	
	public void calcularVariaveisReceber ( List<RelatorioBalanco> relatorioBalancoReceber) {
		BigDecimal quantidadeMeses = BigDecimal.ONE;
		BigDecimal valorFace = BigDecimal.ONE; //valor calculado
		BigDecimal ipcaMeses = BigDecimal.ONE; // ipca ^ meses a ser calculado
		
		for (RelatorioBalanco receberParcela : relatorioBalancoReceber) {
			BigDecimal vlrParcela = receberParcela.getValorContratoRelatorio(); // vlrParcela = valor parcela

			String indice = receberParcela.getEmpresaContratoRelatorio();
			quantidadeMeses = BigDecimal
					.valueOf(DateUtil.Days360(receberParcela.getDataVencimentoRelatorio(), this.getAaaaMM())); // quantidade de dias entre dataParcela e dataReferencia
			quantidadeMeses = quantidadeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128); // divide a quantidade acima por 30
			Double quantidadeMesesDouble = CommonsUtil.doubleValue(quantidadeMeses); // armazena resultado na variável

			// valor face

			if (quantidadeMeses.compareTo(BigDecimal.ZERO) >= 0) {
				valorFace = vlrParcela;
			} else if (indice == "Não") {
				valorFace = vlrParcela;
			} else {
//				this.setIpca(); // ipca soma 1
				ipcaMeses = CommonsUtil
						.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(this.getIpca().add(BigDecimal.ONE)), quantidadeMesesDouble * -1)); // (ipca + 1)^quantidade meses -1

				valorFace = vlrParcela.multiply(ipcaMeses);// parcela * ipcaMeses
			}
			calculaDireitosCreditorios(quantidadeMeses, valorFace, receberParcela);
		}			
	}
	
	public void calculaDireitosCreditorios (BigDecimal quantidadeDeMeses, BigDecimal valorFace,RelatorioBalanco receberParcela ) {
		this.setRecursosDebentures(BigDecimal.ZERO);	
		BigDecimal saldoAtualizado = BigDecimal.ONE;
		
		
		
//		for(RelatorioBalanco receberParcela : relatorioBalancoReceber) {
			BigDecimal jurosFidc = BigDecimal.ONE;
			BigDecimal jurosCri1 = BigDecimal.ONE;
			BigDecimal jurosCri2 = BigDecimal.ONE;
			BigDecimal jurosCri3 = BigDecimal.ONE;
			BigDecimal jurosPonderado = BigDecimal.ONE;
			BigDecimal juros = new BigDecimal(1.01); //juros 1%
			BigDecimal multa = new BigDecimal(1.1);  //multa 10%
			
		
			BigDecimal vlrParcela = receberParcela.getValorContratoRelatorio(); //vlrParcela = valor parcela
			String empresa = receberParcela.getEmpresaContratoRelatorio(); //vlrParcela = valor parcela
			
			
			//juros = 1% ^ quantidadeMeses
			juros = CommonsUtil.bigDecimalValue (Math.pow(CommonsUtil.doubleValue(juros), CommonsUtil.doubleValue(quantidadeDeMeses)));
			
			//(1 + CDI + TAXA FIDC)
			jurosFidc = jurosFidc.add(cdi);
			jurosFidc = jurosFidc.add(taxaFidc);
			jurosFidc = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosFidc), CommonsUtil.doubleValue(quantidadeDeMeses)));
			jurosFidc = valorFace.multiply(jurosFidc);
					
			//(1 + IPCA + TAXA CRI1)
			jurosCri1 = jurosCri1.add(ipca);
			jurosCri1 = jurosCri1.add(taxaCri1);
			jurosCri1 = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosCri1), CommonsUtil.doubleValue(quantidadeDeMeses)));
			jurosCri1 = valorFace.multiply(jurosCri1);
			
			//(1 + IPCA + TAXA CRI2)
			jurosCri2 = jurosCri2.add(ipca);
			jurosCri2 = jurosCri2.add(taxaCri2);
			jurosCri2 = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosCri2), CommonsUtil.doubleValue(quantidadeDeMeses)));
			jurosCri2 = valorFace.multiply(jurosCri2);
			
			//(1 + IPCA + TAXA CRI3)
			jurosCri3 = jurosCri3.add(ipca);
			jurosCri3 = jurosCri3.add(taxaCri3);
			jurosCri3 = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosCri3), CommonsUtil.doubleValue(quantidadeDeMeses)));
			jurosCri3 = valorFace.multiply(jurosCri3);
			
			//(1 + CUSTO PONDERADO)
			jurosPonderado = BigDecimal.ONE.add(custoPonderado);
			jurosPonderado = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosPonderado), CommonsUtil.doubleValue(quantidadeDeMeses)));
			jurosPonderado = valorFace.multiply(jurosPonderado);
			
			if (CommonsUtil.mesmoValor(empresa, "FIDC GALLERIA")) {
				if (quantidadeDeMeses.compareTo(BigDecimal.ZERO)>=0 ) {
					saldoAtualizado = vlrParcela.multiply(jurosFidc); //parcela * juros
					saldoAtualizado = saldoAtualizado.multiply(multa); // parcela * juros * multa
				}
				else {
					saldoAtualizado = jurosFidc;			
				}	
			}
			else if (CommonsUtil.mesmoValor(empresa, "CRI 1")) {
				if (quantidadeDeMeses.compareTo(BigDecimal.ZERO)>=0 ) {
					saldoAtualizado = vlrParcela.multiply(jurosCri1); //parcela * juros
					saldoAtualizado = saldoAtualizado.multiply(multa); // parcela * juros * multa
				}
				else {
					saldoAtualizado = jurosCri1;			
				}	
			}
			else if (CommonsUtil.mesmoValor(empresa, "CRI 2")) {
				if (quantidadeDeMeses.compareTo(BigDecimal.ZERO)>=0 ) {
					saldoAtualizado = vlrParcela.multiply(jurosCri2); //parcela * juros
					saldoAtualizado = saldoAtualizado.multiply(multa); // parcela * juros * multa
				}
				else {
					saldoAtualizado = jurosCri2;			
				}	
			}
			else if (CommonsUtil.mesmoValor(empresa, "CRI 3")) {
				if (quantidadeDeMeses.compareTo(BigDecimal.ZERO)>=0 ) {
					saldoAtualizado = vlrParcela.multiply(jurosCri3); //parcela * juros
					saldoAtualizado = saldoAtualizado.multiply(multa); // parcela * juros * multa
				}
				else {
					saldoAtualizado = jurosCri3;			
				}	
			}
			else {
				if (quantidadeDeMeses.compareTo(BigDecimal.ZERO)>=0 ) {
					saldoAtualizado = vlrParcela.multiply(juros).multiply(multa); //parcela * juros * multa

					}
					else {
						saldoAtualizado = jurosPonderado;			
					}	
				}
			
			if (direitosCreditorios == null) {
				direitosCreditorios = BigDecimal.ZERO;
			}
			this.setDireitosCreditorios(this.getDireitosCreditorios().add(saldoAtualizado));
//		}
	}
	public void saldoCaixaOmie() {
		
		long[] contas = new long[7];
		
		contas[0] = 3297923118l; //BB Sec
		contas[1] = 3303125728l; //Inter Sec
		contas[2] = 3303126311l; //Bradesco Sec
		contas[3] = 3303154498l; //Itaú Sec

		contas[4] = 3303977357l; //Caixinha Sorocaba
		contas[5] = 3303977483l; //Caixinha Campinas
		
		contas[6] = 3361295394l; // Aplicação Sec
	  //contas[7] = 3308481402l; // Crédito Bradesco Sec
		
		OmieRequestBase omieRequestBase = new OmieRequestBase();
		omieRequestBase.setApp_key("2935241731422");
		omieRequestBase.setApp_secret("88961e398f6eaa1df414837312d5bd71");
		omieRequestBase.setCall("ListarExtrato");
		List<IOmieParam> params = new ArrayList<>();
		this.saldoBancos = BigDecimal.ZERO;
		this.saldoCaixa = BigDecimal.ZERO;
		this.saldoCaixaNaoConciliado = BigDecimal.ZERO;
		this.saldoAplFin = BigDecimal.ZERO;
		this.saldoAplFinNaoConciliado = BigDecimal.ZERO;
	
		for (int i=0; i< contas.length; i++) {
		
		ListarExtratoRequest listarExtratoRequest = new ListarExtratoRequest();
		listarExtratoRequest.setcCodIntCC("");
		listarExtratoRequest.setnCodCC(contas[i]);
		listarExtratoRequest.setdPeriodoInicial(CommonsUtil.formataData(this.getAaaaMM(), "dd/MM/yyyy"));
		listarExtratoRequest.setdPeriodoFinal(CommonsUtil.formataData(this.getAaaaMM(), "dd/MM/yyyy"));
		listarExtratoRequest.setcExibirApenasSaldo("S");
		
		params = new ArrayList<>();
		params.add(listarExtratoRequest);		
		
		omieRequestBase.setParam(params);

		OmieService omieService = new OmieService();
		OmieListarExtratoResponse omieListarExtratoResponse = omieService.listarExtratoResponse(omieRequestBase);
		
		if ( i <= 3) {
		this.saldoBancos = this.saldoBancos.add(omieListarExtratoResponse.getnSaldoConciliado());
		this.setSaldoBancos(saldoBancos);
		}
		else if (i <= 5){
			this.saldoCaixa = this.saldoCaixa.add(omieListarExtratoResponse.getnSaldoConciliado());
			this.saldoCaixaNaoConciliado = this.saldoCaixaNaoConciliado.add(omieListarExtratoResponse.getnSaldoAtual().subtract((omieListarExtratoResponse.getnSaldoConciliado())));
			this.setSaldoCaixa(saldoCaixa);
		}
		else {
			this.saldoAplFin = this.saldoAplFin.add(omieListarExtratoResponse.getnSaldoConciliado());
			this.saldoAplFinNaoConciliado = this.saldoAplFinNaoConciliado.add(omieListarExtratoResponse.getnSaldoAtual().subtract((omieListarExtratoResponse.getnSaldoConciliado())));
			this.setSaldoAplFin(saldoAplFin);
		}
		
}
	} 
	
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
		if (!CommonsUtil.semValor(depositoBacenScd))
			result=result.add(depositoBacenScd);;
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
			result=result.subtract(distribuicao2Pago1);
		if (!CommonsUtil.semValor(lucroSemestreAnterior))
			result=result.add(lucroSemestreAnterior);
		if (!CommonsUtil.semValor(aumentoCapitalSocial))
			result=result.subtract(aumentoCapitalSocial);
		if (!CommonsUtil.semValor(distribuicao1Pago2))
			result=result.subtract(distribuicao1Pago2);
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

	public BigDecimal getSaldoCaixaNaoConciliado() {
		return saldoCaixaNaoConciliado;
	}
	public void setSaldoCaixaNaoConciliado(BigDecimal saldoCaixaNaoConciliado) {
		this.saldoCaixaNaoConciliado = saldoCaixaNaoConciliado;
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

	public BigDecimal getSaldoAplFinNaoConciliado() {
		return saldoAplFinNaoConciliado;
	}
	public void setSaldoAplFinNaoConciliado(BigDecimal saldoAplFinNaoConciliado) {
		this.saldoAplFinNaoConciliado = saldoAplFinNaoConciliado;
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
	public BigDecimal getCustoPonderado() {
		return custoPonderado;
	}
	public void setCustoPonderado(BigDecimal custoPonderado) {
		this.custoPonderado = custoPonderado;
	}
	public BigDecimal getCdi() {
		return cdi;
	}
	public void setCdi(BigDecimal cdi) {
		this.cdi = cdi;
	}
	public BigDecimal getIpca() {
		return ipca;
	}
	public void setIpca(BigDecimal ipca) {
		this.ipca = ipca;
	}
	public BigDecimal getTaxaFidc() {
		return taxaFidc;
	}
	public void setTaxaFidc(BigDecimal taxaFidc) {
		this.taxaFidc = taxaFidc;
	}
	public BigDecimal getTaxaCri1() {
		return taxaCri1;
	}
	public void setTaxaCri1(BigDecimal taxaCri1) {
		this.taxaCri1 = taxaCri1;
	}
	public BigDecimal getTaxaCri2() {
		return taxaCri2;
	}
	public void setTaxaCri2(BigDecimal taxaCri2) {
		this.taxaCri2 = taxaCri2;
	}
	public List<OmieListarExtratoResponse> getExtratoResponse() {
		return extratoResponse;
	}
	public void setExtratoResponse(List<OmieListarExtratoResponse> extratoResponse) {
		this.extratoResponse = extratoResponse;
	}
	public BigDecimal getSaldoCaixaOmie() {
		return saldoCaixaOmie;
	}
	public void setSaldoCaixaOmie(BigDecimal saldoCaixaOmie) {
		this.saldoCaixaOmie = saldoCaixaOmie;
	}
	public BigDecimal getTaxaCri3() {
		return taxaCri3;
	}
	public void setTaxaCri3(BigDecimal taxaCri3) {
		this.taxaCri3 = taxaCri3;
	}

}