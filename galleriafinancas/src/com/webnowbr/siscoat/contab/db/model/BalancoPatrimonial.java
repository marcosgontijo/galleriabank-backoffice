package com.webnowbr.siscoat.contab.db.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.formula.functions.FinanceLib;

import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.cobranca.mb.RelatorioEstoque;
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
	private BigDecimal taxaCri4;
	private BigDecimal taxaCri5;
	private BigDecimal taxaCri6;
	private BigDecimal kpmg;
	
	private BigDecimal custoPonderado;
	
	private List<OmieListarExtratoResponse> extratoResponse;
	private BigDecimal saldoCaixaOmie;
	
	public void calcularCustoPonderado(List<RelatorioBalanco> relatorioBalancoPagar) {
		BigDecimal somaParcela = BigDecimal.ZERO;
		BigDecimal somaFatorX = BigDecimal.ZERO;
		int quantidadeItens = 0;
		
		for(RelatorioBalanco pagarDebenture : relatorioBalancoPagar) {

			BigDecimal valorParcela = pagarDebenture.getValorContratoRelatorio();
			BigDecimal taxaInvestidor = pagarDebenture.getTaxaInvestidor().divide(new BigDecimal (100),MathContext.DECIMAL128);
			BigDecimal fatorX = valorParcela.multiply(taxaInvestidor);
	
			somaParcela = somaParcela.add(valorParcela);
			somaFatorX = somaFatorX.add(fatorX);
			quantidadeItens = quantidadeItens+1;
		}
		
		System.out.println("Quantidade de itens Pagar: " + quantidadeItens);
		System.out.println("somaParcela: " +somaParcela);
		System.out.println("somaFatorX: " +somaFatorX);
		custoPonderado = (somaFatorX.divide(somaParcela,MathContext.DECIMAL128));
		System.out.println("Custo ponderado: " +custoPonderado);
		calcularDebenturistaPagar(relatorioBalancoPagar);
	}
	public void calcularDebenturistaPagar (List<RelatorioBalanco> relatorioBalancoPagar) {
		this.setRecursosDebentures(BigDecimal.ZERO);
		BigDecimal pagarSec = BigDecimal.ZERO;
		BigDecimal pagarCoban = BigDecimal.ZERO;
		
		
		BigDecimal quantidadeMeses = BigDecimal.ONE;
		for(RelatorioBalanco pagarDebenture : relatorioBalancoPagar) {
			
			BigDecimal vlrParcela = pagarDebenture.getValorContratoRelatorio(); //vlrParcela = valor parcela
			String recebedor = pagarDebenture.getNomePagadorRelatorio(); //empresa = empresa 
			
			quantidadeMeses = BigDecimal.valueOf(DateUtil.Days360(pagarDebenture.getDataVencimentoRelatorio(), this.getAaaaMM())); //quantidade de dias entre dataParcela e dataReferencia
			quantidadeMeses = quantidadeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128); //divide a quantidade acima por 30
			Double quantidadeMesesDouble = CommonsUtil.doubleValue(quantidadeMeses); //armazena resultado na variável
			
			double potencia = Math.pow(CommonsUtil.doubleValue(custoPonderado.add(BigDecimal.ONE)), quantidadeMesesDouble); //potencia = (custoponderado +1) elevado a quantidade de meses

			BigDecimal pagarDebenturista; //declaração de variável
			pagarDebenturista = (vlrParcela).multiply(CommonsUtil.bigDecimalValue(potencia) , MathContext.DECIMAL128); //valor da parcela * potencia (acima)
			pagarDebenturista = pagarDebenturista.setScale(2, BigDecimal.ROUND_HALF_UP);

			if (CommonsUtil.mesmoValor(recebedor,"Galleria Finanças Securitizadora S.A.")) {
				pagarSec = pagarSec.add(pagarDebenturista);
			}
			else if (CommonsUtil.mesmoValor(recebedor, "Galleria Correspondente Bancario Eireli")) {
				pagarCoban = pagarCoban.add(pagarDebenturista);
			}
			else {
				this.setRecursosDebentures(this.getRecursosDebentures().add(pagarDebenturista));
				}
			}
		System.out.println("pagarSec: " +pagarSec);
		System.out.println("pagarCoban: " +pagarCoban);
		
	}
	
	public void calcularVariaveisReceber ( List<RelatorioBalanco> relatorioBalancoReceber,int inicio) {
		BigDecimal quantidadeMeses = BigDecimal.ONE;
		BigDecimal valorFace = BigDecimal.ONE; //valor calculado
		BigDecimal ipcaMeses = BigDecimal.ONE; // ipca ^ meses a ser calculado
		
		long icount = 0l;
		
		for (RelatorioBalanco receberParcela : relatorioBalancoReceber) {
			
			BigDecimal vlrParcela = receberParcela.getValorContratoRelatorio(); // vlrParcela = valor parcela
			
			String indice = receberParcela.getIndiceContratoRelatorio();
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
						.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(this.getIpca().divide(new BigDecimal(100)).add(BigDecimal.ONE)), quantidadeMesesDouble * -1)); // (ipca + 1)^quantidade meses -1

				valorFace = vlrParcela.multiply(ipcaMeses);// parcela * ipcaMeses
			}
			calculaDireitosCreditorios(quantidadeMeses, valorFace, receberParcela,inicio);
		}			
	}
	
	
	private void calculaDireitosCreditorios (BigDecimal quantidadeDeMeses, BigDecimal valorFace,RelatorioBalanco receberParcela,int inicio ) {
			
		BigDecimal saldoAtualizado = BigDecimal.ONE;
		
//		for(RelatorioBalanco receberParcela : relatorioBalancoReceber) {
			BigDecimal jurosFidc = BigDecimal.ONE;
			BigDecimal jurosPonderado = BigDecimal.ONE;
			BigDecimal juros = new BigDecimal(0.01); //juros 1%
			BigDecimal multa = new BigDecimal(0.1);  //multa 10%
			
		
			BigDecimal vlrParcela = receberParcela.getValorContratoRelatorio(); //vlrParcela = valor parcela
			String empresa = receberParcela.getEmpresaContratoRelatorio(); //vlrParcela = valor parcela
			
			
			//juros = 1% ^ quantidadeMeses
			juros = juros.add(new BigDecimal (1));
			juros = CommonsUtil.bigDecimalValue (Math.pow(CommonsUtil.doubleValue(juros), CommonsUtil.doubleValue(quantidadeDeMeses)));
			if (quantidadeDeMeses.compareTo(BigDecimal.ZERO) == 0) {
				saldoAtualizado= vlrParcela;
			}else {
			
				if (CommonsUtil.mesmoValor(empresa, "FIDC GALLERIA")) {
					if (quantidadeDeMeses.compareTo(BigDecimal.ZERO) > 0) {
						saldoAtualizado = vlrParcela.multiply(juros); // parcela * juros
						saldoAtualizado = saldoAtualizado.multiply(multa.add(new BigDecimal(1))); // parcela * juros * multa
					} else {
						// (1 + CDI + TAXA FIDC)
						BigDecimal cdiCalculo = cdi.divide(new BigDecimal(100));
						BigDecimal taxaFidcCalculo = taxaFidc.divide(new BigDecimal(100));
						jurosFidc = jurosFidc.add(cdiCalculo);
						jurosFidc = jurosFidc.add(taxaFidcCalculo);
						// juros ^ meses
						jurosFidc = CommonsUtil.bigDecimalValue(
								Math.pow(CommonsUtil.doubleValue(jurosFidc), CommonsUtil.doubleValue(quantidadeDeMeses)));
						saldoAtualizado = valorFace.multiply(jurosFidc);
					}
				}
				else if (CommonsUtil.mesmoValor(empresa, "CRI 1")) {
					if (quantidadeDeMeses.compareTo(BigDecimal.ZERO) > 0) {
						saldoAtualizado = vlrParcela.multiply(juros); // parcela * juros
						saldoAtualizado = saldoAtualizado.multiply(multa.add(new BigDecimal(1))); // parcela * juros * multa
					} else {
						// (1 + IPCA + TAXA CRI1)
						
						BigDecimal ipcaCalculo = ipca.divide(new BigDecimal(100));
						BigDecimal taxaCriCalculo = taxaCri1.divide(new BigDecimal(100));
						
						jurosFidc = jurosFidc.add(ipcaCalculo);
						jurosFidc = jurosFidc.add(taxaCriCalculo);
						// juros ^ meses
						jurosFidc = CommonsUtil.bigDecimalValue(
								Math.pow(CommonsUtil.doubleValue(jurosFidc), CommonsUtil.doubleValue(quantidadeDeMeses)));
						saldoAtualizado = valorFace.multiply(jurosFidc);
					}
				}
				else if (CommonsUtil.mesmoValor(empresa, "CRI 2")) {
					if (quantidadeDeMeses.compareTo(BigDecimal.ZERO) > 0) {
						saldoAtualizado = vlrParcela.multiply(juros); // parcela * juros
						saldoAtualizado = saldoAtualizado.multiply(multa.add(new BigDecimal(1))); // parcela * juros * multa
					} else {
						// (1 + IPCA + TAXA CRI2)
						BigDecimal ipcaCalculo = ipca.divide(new BigDecimal(100));
						BigDecimal taxaCriCalculo = taxaCri2.divide(new BigDecimal(100));
						
						
						jurosFidc = jurosFidc.add(ipcaCalculo);
						jurosFidc = jurosFidc.add(taxaCriCalculo);
						// juros ^ meses
						jurosFidc = CommonsUtil.bigDecimalValue(
								Math.pow(CommonsUtil.doubleValue(jurosFidc), CommonsUtil.doubleValue(quantidadeDeMeses)));
						saldoAtualizado = valorFace.multiply(jurosFidc);
					}
				}
				else if (CommonsUtil.mesmoValor(empresa, "CRI 3")) {
					if (quantidadeDeMeses.compareTo(BigDecimal.ZERO) > 0) {
						saldoAtualizado = vlrParcela.multiply(juros); // parcela * juros
						saldoAtualizado = saldoAtualizado.multiply(multa.add(BigDecimal.ONE)); // parcela * juros * multa
					} else {
						// (1 + IPCA + TAXA CRI3)					
						BigDecimal ipcaCalculo = ipca.divide(new BigDecimal(100));
						BigDecimal taxaCriCalculo = taxaCri3.divide(new BigDecimal(100));
						
						jurosFidc = jurosFidc.add(ipcaCalculo);
						jurosFidc = jurosFidc.add(taxaCriCalculo);
						// juros ^ meses
						jurosFidc = CommonsUtil.bigDecimalValue(
								Math.pow(CommonsUtil.doubleValue(jurosFidc), CommonsUtil.doubleValue(quantidadeDeMeses)));
						saldoAtualizado = valorFace.multiply(jurosFidc);
					}
				}
				else if (CommonsUtil.mesmoValor(empresa, "CRI 4")) {
					if (quantidadeDeMeses.compareTo(BigDecimal.ZERO) > 0) {
						saldoAtualizado = vlrParcela.multiply(juros); // parcela * juros
						saldoAtualizado = saldoAtualizado.multiply(multa.add(new BigDecimal(1))); // parcela * juros * multa
					} else {
						// (1 + IPCA + TAXA CRI4)
						
						BigDecimal ipcaCalculo = ipca.divide(new BigDecimal(100));
						BigDecimal taxaCriCalculo = CommonsUtil.bigDecimalValue(taxaCri4) .divide(new BigDecimal(100));
						
						jurosFidc = jurosFidc.add(ipcaCalculo);
						jurosFidc = jurosFidc.add(taxaCriCalculo);
						// juros ^ meses
						jurosFidc = CommonsUtil.bigDecimalValue(
								Math.pow(CommonsUtil.doubleValue(jurosFidc), CommonsUtil.doubleValue(quantidadeDeMeses)));
						saldoAtualizado = valorFace.multiply(jurosFidc);
					}
				}
				else if (CommonsUtil.mesmoValor(empresa, "CRI 5")) {
					if (quantidadeDeMeses.compareTo(BigDecimal.ZERO) > 0) {
						saldoAtualizado = vlrParcela.multiply(juros); // parcela * juros
						saldoAtualizado = saldoAtualizado.multiply(multa.add(new BigDecimal(1))); // parcela * juros * multa
					} else {
						// (1 + IPCA + TAXA CRI5)
						
						BigDecimal ipcaCalculo = ipca.divide(new BigDecimal(100));
						BigDecimal taxaCriCalculo = taxaCri5.divide(new BigDecimal(100));
						
						jurosFidc = jurosFidc.add(ipcaCalculo);
						jurosFidc = jurosFidc.add(taxaCriCalculo);
						// juros ^ meses
						jurosFidc = CommonsUtil.bigDecimalValue(
								Math.pow(CommonsUtil.doubleValue(jurosFidc), CommonsUtil.doubleValue(quantidadeDeMeses)));
						saldoAtualizado = valorFace.multiply(jurosFidc);
					}
				}
				else if (CommonsUtil.mesmoValor(empresa, "CRI 6")) {
					if (quantidadeDeMeses.compareTo(BigDecimal.ZERO) > 0) {
						saldoAtualizado = vlrParcela.multiply(juros); // parcela * juros
						saldoAtualizado = saldoAtualizado.multiply(multa.add(new BigDecimal(1))); // parcela * juros * multa
					} else {
						// (1 + IPCA + TAXA CRI5)
						
						BigDecimal ipcaCalculo = ipca.divide(new BigDecimal(100));
						BigDecimal taxaCriCalculo = taxaCri6.divide(new BigDecimal(100));
						
						jurosFidc = jurosFidc.add(ipcaCalculo);
						jurosFidc = jurosFidc.add(taxaCriCalculo);
						// juros ^ meses
						jurosFidc = CommonsUtil.bigDecimalValue(
								Math.pow(CommonsUtil.doubleValue(jurosFidc), CommonsUtil.doubleValue(quantidadeDeMeses)));
						saldoAtualizado = valorFace.multiply(jurosFidc);
					}
				}
				else {
					if (quantidadeDeMeses.compareTo(BigDecimal.ZERO) >= 0) {
						saldoAtualizado = vlrParcela.multiply(juros).multiply(multa.add(BigDecimal.ONE)); // parcela * juros  * multa
					} else {
						// (1 + CUSTO PONDERADO)
						jurosPonderado = BigDecimal.ONE.add(custoPonderado);
						jurosPonderado = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosPonderado),
								CommonsUtil.doubleValue(quantidadeDeMeses)));
						saldoAtualizado = valorFace.multiply(jurosPonderado);
					}
				}
			}

		
			
			FileWriter  fileOutput = null;
			
			try {
//				FileOutputStream fileStream;
				File file = new File("C:\\tewmp\\log\\" + CommonsUtil.strZero(""+inicio, 5) + ".csv");
				boolean arquivoNovo = false;
				if (!file.exists()) {
					//file.createNewFile();
					arquivoNovo = true;
				}
				// gera arquivo de log;
//				fileStream = new FileOutputStream(file);
//				FileWriter  fileOutput;
				fileOutput = new FileWriter (file, true);
				if (arquivoNovo && CommonsUtil.mesmoValor(0, inicio) ) {
					String header = String.format("%s;%s;%s;%s;%s;%s;%s;%s;\r\n", "contrato", "Data Vencimento",
							"quantidadeDeMeses", "Indice","jurosPonderado", "valorFace", "vlrParcela", "saldoAtualizado");
					fileOutput.write(header);
				}

				fileOutput.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s;\r\n", receberParcela.getNumeroContratoRelatorio(),
						receberParcela.getDataVencimentoRelatorio(),
						CommonsUtil.formataNumero(quantidadeDeMeses,  "#,##0.0000###############"),
						receberParcela.getIndiceContratoRelatorio(),
						jurosPonderado, CommonsUtil.formataValorMonetario(valorFace),
						CommonsUtil.formataValorMonetario(vlrParcela),
						CommonsUtil.formataValorMonetario(saldoAtualizado)));
				
//				fileOutput.flush();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
	            //close resources
	            try {
	            	fileOutput.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
            
			
			if (direitosCreditorios == null) {
				direitosCreditorios = BigDecimal.ZERO;
			}
			this.setDireitosCreditorios(this.getDireitosCreditorios().add(saldoAtualizado));
//		}
	}
	
	public void calcularProvisaoLiquidacaoAntecipada(List<RelatorioBalanco> relatorioBalancoReceber) {
		// A CADA EMPRESTIMO
		long icount = 0l;
		BigDecimal mediaTaxaReceber = new BigDecimal(relatorioBalancoReceber.stream()
				.mapToDouble(balanco -> CommonsUtil.doubleValue(balanco.getTaxaContratoRelatorio().doubleValue()))
				.average().orElse(0.0));
		BigDecimal mediaTaxaCalculo = mediaTaxaReceber.divide(new BigDecimal(100));
		System.out.println("Taxa média Receber %: " +mediaTaxaReceber);
		BigDecimal emprestimo = new BigDecimal (100000000);
		BigDecimal prazoReceber = new BigDecimal (180);
		BigDecimal ipcaCalculo = ipca.divide(new BigDecimal(100));
		System.out.println("IPCA %: " +ipcaCalculo);
		BigDecimal taxaCalculo = mediaTaxaCalculo.add(ipcaCalculo);
		System.out.println("Taxa Calculo:" +taxaCalculo);
		
		BigDecimal parcelaPGTO = BigDecimal
				.valueOf(FinanceLib.pmt(taxaCalculo.divide(BigDecimal.valueOf(1)).doubleValue(), // taxa
						prazoReceber.intValue(), // prazo
						emprestimo.doubleValue(), // valor credito - VP
						Double.valueOf("0"), // VF
						false // pagamento no inico
				));
		System.out.println("Parcela: " +parcelaPGTO);
		
		
		//TRAZENDO A VALOR PRESENTE
		BigDecimal taxaFidcCalculo = taxaFidc.divide(new BigDecimal(100));
		BigDecimal cdiCalculo = cdi.divide(new BigDecimal(100));
		BigDecimal prazoPresente = prazoReceber;
		BigDecimal taxaCalculoPresente = taxaFidcCalculo.add(cdiCalculo);
		
		BigDecimal valorPresente = BigDecimal
				.valueOf(FinanceLib.pv(taxaCalculoPresente.doubleValue(), //taxa
						prazoPresente.intValue(), //prazo
						parcelaPGTO.doubleValue(), //parcela calculada acima
						Double.valueOf("0"), //valor futuro
						false // pagamento no fim
						));
		System.out.println("valorPresente: " +valorPresente);
		
		//VALOR PRESENTE CARTEIRA
		BigDecimal valorPresenteCarteira = this.getDireitosCreditorios();
		System.out.println("valorPresente Carteira: " +valorPresenteCarteira);
		
		//VALOR PRESENTE EMPRESTIMOS
		BigDecimal valorPresenteEmprestimos = emprestimo.multiply(valorPresenteCarteira).divide(valorPresente,MathContext.DECIMAL128);
		System.out.println("valorPresenteEmprestimos: " +valorPresenteEmprestimos);
		
		//PERDA POR LIQUIDAÇÃO ANTECIPADA
		BigDecimal perdaLiquidacaoAntecipada = valorPresenteCarteira.subtract(valorPresenteEmprestimos);
		System.out.println("perdaLiquidacaoAntecipada: " +perdaLiquidacaoAntecipada);
		
		//Percentual de Liquidação Antecipada da Carteira acima de 30 dias
		//BigDecimal liquidacaoAntecipadaKPMG = new BigDecimal (0.0699);
		kpmg = kpmg.divide(new BigDecimal(100));
		System.out.println("liquidacaoAntecipadaKPMG: " +kpmg);
		
		//Provisão para Liquidações Antecipadas
		this.provisaoLiquidAntecipada = perdaLiquidacaoAntecipada.multiply(kpmg).setScale(2, BigDecimal.ROUND_HALF_UP);
		System.out.println("provisaoLiquidAntecipada: " +provisaoLiquidAntecipada);
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
	
	public void calculaValorVendaForcada(List<ImovelEstoque> imoveis) {
		BigDecimal somaValorVendaForcada = BigDecimal.ZERO;
		for (ImovelEstoque imovel : imoveis) {
			if (imovel.getQuitado()== true) {
				somaValorVendaForcada = somaValorVendaForcada.add(imovel.getVendaForcada());			
			}
		}
		this.setEstoque(somaValorVendaForcada);
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
			result=result.add(distribuicao2Pago1.multiply(new BigDecimal (-1)));
		if (!CommonsUtil.semValor(lucroSemestreAnterior))
			result=result.add(lucroSemestreAnterior);
		if (!CommonsUtil.semValor(aumentoCapitalSocial))
			result=result.add(aumentoCapitalSocial.multiply(new BigDecimal (-1)));
		if (!CommonsUtil.semValor(distribuicao1Pago2))
			result=result.add(distribuicao1Pago2.multiply(new BigDecimal (-1)));
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
		if (!CommonsUtil.semValor(provisaoLiquidAntecipada))
			result=result.add(provisaoLiquidAntecipada);
		
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
			result=result.add(distribuicao2Pago1.multiply(new BigDecimal (-1)));
		if (!CommonsUtil.semValor(lucroSemestreAnterior))
			result=result.add(lucroSemestreAnterior);
		if (!CommonsUtil.semValor(aumentoCapitalSocial))
			result=result.add(aumentoCapitalSocial.multiply(new BigDecimal (-1)));
		if (!CommonsUtil.semValor(distribuicao1Pago2))
			result=result.add(distribuicao1Pago2.multiply(new BigDecimal (-1)));
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
			result=result.subtract(distribuicao2Pago1.multiply(new BigDecimal (-1)));
		if (!CommonsUtil.semValor(lucroSemestreAnterior))
			result=result.add(lucroSemestreAnterior);
		if (!CommonsUtil.semValor(aumentoCapitalSocial))
			result=result.subtract(aumentoCapitalSocial.multiply(new BigDecimal (-1)));
		if (!CommonsUtil.semValor(distribuicao1Pago2))
			result=result.subtract(distribuicao1Pago2.multiply(new BigDecimal (-1)));
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
	public BigDecimal getTaxaCri4() {
		return taxaCri4;
	}
	public void setTaxaCri4(BigDecimal taxaCri4) {
		this.taxaCri4 = taxaCri4;
	}
	
	public BigDecimal getTaxaCri5() {
		return taxaCri5;
	}
	public void setTaxaCri5(BigDecimal taxaCri5) {
		this.taxaCri5 = taxaCri5;
	}
	public BigDecimal getKpmg() {
		return kpmg;
	}
	public void setKpmg(BigDecimal kpmg) {
		this.kpmg = kpmg;
	}
	public BigDecimal getTaxaCri6() {
		return taxaCri6;
	}
	public void setTaxaCri6(BigDecimal taxaCri6) {
		this.taxaCri6 = taxaCri6;
	}
}