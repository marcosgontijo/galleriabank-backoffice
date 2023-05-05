package com.webnowbr.siscoat.job;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesParcial;
import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesParcialDao;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoIPCADadosV2;
import com.webnowbr.siscoat.simulador.SimulacaoVO;

public class IpcaJobCalcular {

	@SuppressWarnings("deprecation")
	public boolean calcularIPCA(IPCADao ipcaDao, ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao,
			ContratoCobrancaDao contratoCobrancaDao,
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao,
			ContratoCobrancaDetalhes contratoCobrancaDetalhes, ContratoCobranca contratoCobranca) {

		Date dataIPCA = DateUtil.adicionarMes(contratoCobrancaDetalhes.getDataVencimento(), -2);
		IPCA ultimoIpca = ipcaDao.getUltimoIPCA(dataIPCA);
		if (ultimoIpca == null || dataIPCA.getMonth() != ultimoIpca.getData().getMonth()
				|| dataIPCA.getYear() != ultimoIpca.getData().getYear())
			return false;

		// primeira condição é para meses de mesmo ano; segunda condição é para os meses
		// jan e fev da parcela IPCA
		if ( CommonsUtil.mesmoValor(dataIPCA.getMonth(), ultimoIpca.getData().getMonth()) &&
				CommonsUtil.mesmoValor(dataIPCA.getYear(), ultimoIpca.getData().getYear()) ) {

			
			// usar o reparcelamento aqui.

			BigDecimal saldoDevedor = BigDecimal.ZERO;
			BigDecimal valorSeguroMIPCarencia = BigDecimal.ZERO;
			BigDecimal valorSeguroDFI = BigDecimal.ZERO;
			BigDecimal valorTxAdm = SiscoatConstants.TAXA_ADM;
			BigDecimal saldoDevedorAnterior = BigDecimal.ZERO;

			if ((contratoCobranca.isTemSeguroMIP() && contratoCobranca.isTemSeguro())) {
				valorSeguroDFI = contratoCobranca.getValorImovel()
						.multiply(SiscoatConstants.SEGURO_DFI.divide(BigDecimal.valueOf(100)));
			}
			
			Calendar dataCorteParcelasBaixadas = Calendar.getInstance();
			dataCorteParcelasBaixadas.set(Calendar.YEAR, 2021);
			dataCorteParcelasBaixadas.set(Calendar.MONTH, 10);
			dataCorteParcelasBaixadas.set(Calendar.DAY_OF_MONTH, 13);

			for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {

				ContratoCobrancaDetalhes detalhe = contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe);

				if (CommonsUtil.mesmoValor(contratoCobrancaDetalhes.getId(), detalhe.getId())) {

					if (!CommonsUtil.booleanValue(contratoCobranca.isRecalculaIPCA())
							&& detalhe.getIpcaAtualizou() != null
							&& CommonsUtil.mesmoValor(detalhe.getIpcaAtualizou().getId(), ultimoIpca.getId()))
						continue;

					// reparcela
					BigDecimal valorIpca = (saldoDevedor
							.multiply(ultimoIpca.getTaxa().divide(BigDecimal.valueOf(100))));

					BigDecimal saldoDevedorIpca = saldoDevedor.add(valorIpca).setScale(2, BigDecimal.ROUND_HALF_EVEN);

					// saldoDevedorAnterior = saldoDevedorIpca;

					BigDecimal tarifaIOFDiario;
					BigDecimal tarifaIOFAdicional = SiscoatConstants.TARIFA_IOF_ADICIONAL
							.divide(BigDecimal.valueOf(100));
					SimulacaoVO simulador = new SimulacaoVO();

					if (contratoCobranca.getPagador().getCpf() != null) {
						if (DateUtil.isAfterDate(contratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF)) {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF_ANTIGA.divide(BigDecimal.valueOf(100));
						} else {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF.divide(BigDecimal.valueOf(100));
						}
						simulador.setTipoPessoa("PF");
					} else {
						if (DateUtil.isAfterDate(contratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF)) {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ_ANTIGA.divide(BigDecimal.valueOf(100));
						} else {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ.divide(BigDecimal.valueOf(100));
						}
						simulador.setTipoPessoa("PJ");
					}

					simulador.setDataSimulacao(DateUtil.getDataHoje());
					simulador.setTarifaIOFDiario(tarifaIOFDiario);
					simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
					simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
					simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
					// valores
					simulador.setValorCredito(saldoDevedorIpca);
					simulador.setTaxaJuros(contratoCobranca.getTxJurosParcelas());

					if (contratoCobranca.getMesesCarencia() > CommonsUtil.intValue(detalhe.getNumeroParcela()))
						simulador.setCarencia(
								BigInteger.valueOf(CommonsUtil.longValue(contratoCobranca.getMesesCarencia()
										- CommonsUtil.intValue(detalhe.getNumeroParcela()))));
					else if (CommonsUtil.mesmoValor(contratoCobranca.getMesesCarencia(),
							CommonsUtil.intValue(detalhe.getNumeroParcela())))
						simulador.setCarencia(BigInteger.valueOf(contratoCobranca.getMesesCarencia()));
					else
						simulador.setCarencia(BigInteger.ZERO);

					int qtdParcelas = contratoCobranca.getQtdeParcelas();
					if (contratoCobranca.isGeraParcelaFinal()) {
						qtdParcelas++;
						contratoCobranca.setQtdeParcelas(qtdParcelas);
						contratoCobranca.setGeraParcelaFinal(false);
					}

					if (CommonsUtil.intValue(detalhe.getNumeroParcela()) > 0)
						simulador.setQtdParcelas(
								BigInteger.valueOf(qtdParcelas - CommonsUtil.intValue(detalhe.getNumeroParcela()) + 1));
					else
						simulador.setQtdParcelas(BigInteger.valueOf(qtdParcelas));

					simulador.setValorImovel(contratoCobranca.getValorImovel());
//						simulador.setCustoEmissaoValor(custoEmissaoValor);
					simulador.setTipoCalculo(contratoCobranca.getTipoCalculo());

					simulador.setNaoCalcularDFI(!(contratoCobranca.isTemSeguroDFI() && contratoCobranca.isTemSeguro()));
					simulador.setNaoCalcularMIP(!(contratoCobranca.isTemSeguroMIP() && contratoCobranca.isTemSeguro()));
					simulador.setNaoCalcularTxAdm(!(contratoCobranca.isTemTxAdm()));

					simulador.calcular();

					for (SimulacaoDetalheVO parcela : simulador.getParcelas()) {

						if (parcela.getNumeroParcela().compareTo(BigInteger.ZERO) == 0)
							continue;

						Integer parcelaSimuladorReal = 0;

						if (CommonsUtil.intValue(detalhe.getNumeroParcela()) > 1)
							parcelaSimuladorReal = CommonsUtil.intValue(parcela.getNumeroParcela())
									+ CommonsUtil.intValue(detalhe.getNumeroParcela()) - 1;
						else
							parcelaSimuladorReal = CommonsUtil.intValue(parcela.getNumeroParcela());

						final int parcelaProcura = parcelaSimuladorReal;
						ContratoCobrancaDetalhes detalheIpca = contratoCobranca
								.getListContratoCobrancaDetalhes().stream().filter(x -> CommonsUtil
										.mesmoValor(CommonsUtil.intValue(x.getNumeroParcela()), parcelaProcura))
								.findFirst().orElse(null);

//						for (ContratoCobrancaDetalhes detalheIpca : contratoCobranca
//								.getListContratoCobrancaDetalhes()) {

						if (detalheIpca.getVlrParcelaOriginal() == null)
							detalheIpca.setVlrParcelaOriginal(detalheIpca.getVlrParcela());

						if (CommonsUtil.mesmoValor(parcelaSimuladorReal.toString(), detalheIpca.getNumeroParcela())) {
							if (detalheIpca.isParcelaPaga()
									&& !CommonsUtil.booleanValue(contratoCobranca.isRecalculaIPCA())) {
								continue;
							}

							detalheIpca.setIpcaAtualizou(ultimoIpca);
							//detalheIpca.setVlrRecebido(BigDecimal.valueOf(parcelaSimuladorReal.longValue()));

							detalheIpca
									.setVlrParcela(parcela.getValorParcela().setScale(2, BigDecimal.ROUND_HALF_EVEN));

							detalheIpca.setVlrJurosParcela(parcela.getJuros().setScale(2, BigDecimal.ROUND_HALF_EVEN));
							detalheIpca.setVlrAmortizacaoParcela(
									parcela.getAmortizacao().setScale(2, BigDecimal.ROUND_HALF_EVEN));
							// nao precisa pois é pelo valor do imovel, nao muda
							// detalheIpca.setSeguroDFI(parcela.getSeguroDFI());
							int finalCarencia = contratoCobranca.getMesesCarencia() + 1;

							if (CommonsUtil.mesmoValor(parcelaSimuladorReal, finalCarencia)) {
								detalheIpca.setSeguroMIP(parcela.getSeguroMIP().add(valorSeguroMIPCarencia));
								detalheIpca.setSeguroDFI(valorSeguroDFI.multiply(
										CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia() + 1)));
								detalheIpca.setTaxaAdm(valorTxAdm.multiply(
										CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia() + 1)));
								detalheIpca.setVlrParcela(detalheIpca.getVlrParcela().add(valorSeguroMIPCarencia)
										.add(valorSeguroDFI.multiply(
												CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia()))).add(valorTxAdm));
							} else {
								detalheIpca.setSeguroMIP(parcela.getSeguroMIP());
								detalheIpca.setSeguroDFI(parcela.getSeguroDFI());
								detalheIpca.setTaxaAdm(parcela.getTxAdm());
							}

							BigDecimal vlrSaldoParcela = parcela.getSaldoDevedorInicial();

							/*
							 * CODIGO MURTA OLD
							 */
							/*
							if (detalheIpca.isParcelaPaga()) {		
								BigDecimal vlrPago = BigDecimal.ZERO;
								
								List<ContratoCobrancaDetalhesParcial> detalhesParciais = detalheIpca
										.getListContratoCobrancaDetalhesParcial();
								
								if (detalheIpca.getDataVencimento().before(dataCorteParcelasBaixadas.getTime())) {
									for (ContratoCobrancaDetalhesParcial parcial : detalhesParciais) {	
										if( parcial.getDataVencimento().compareTo(parcial.getDataPagamento())<0 &&
										    parcial.getVlrParcela().compareTo(parcial.getVlrRecebido()) < 0) {
											vlrPago = vlrPago.add(CommonsUtil.bigDecimalValue(parcial.getVlrParcela()));
										}else {
											vlrPago = vlrPago.add(CommonsUtil.bigDecimalValue(parcial.getVlrRecebido()));
										}
									}
									if (vlrPago.compareTo(BigDecimal.ZERO) == 1) {

										BigDecimal vlrParcela = vlrPago
												.subtract(CommonsUtil.bigDecimalValue(detalheIpca.getSeguroDFI()))
												.subtract(CommonsUtil.bigDecimalValue(detalheIpca.getSeguroMIP()));

										BigDecimal vlrAmortizacaoOriginal = detalheIpca.getVlrAmortizacaoParcela();

										detalheIpca.setVlrAmortizacaoParcela(vlrParcela.subtract(parcela.getJuros()));

										vlrSaldoParcela = saldoDevedorIpca.subtract(detalheIpca.getVlrAmortizacaoParcela());
										detalheIpca.setVlrParcela(vlrPago);
									}
								} else {	
									for (ContratoCobrancaDetalhesParcial parcial : detalhesParciais) {											
										vlrPago = vlrPago.add(CommonsUtil.bigDecimalValue(parcial.getVlrRecebido()));
									}
									
									detalheIpca.setVlrParcela(vlrPago);
									
									if (vlrPago.compareTo(BigDecimal.ZERO) == 1) {
										//BigDecimal vlrParcela = vlrPago
										//		.subtract(CommonsUtil.bigDecimalValue(detalheIpca.getSeguroDFI()))
										//		.subtract(CommonsUtil.bigDecimalValue(detalheIpca.getSeguroMIP()));
	
										//BigDecimal vlrAmortizacaoOriginal = detalheIpca.getVlrAmortizacaoParcela();
	
										//detalheIpca.setVlrAmortizacaoParcela(vlrParcela.subtract(parcela.getJuros()));
										
										detalheIpca.setVlrAmortizacaoParcela(parcela.getAmortizacao());
	
										vlrSaldoParcela = saldoDevedorIpca.subtract(detalheIpca.getVlrAmortizacaoParcela());										
									}
								}
							}
							*/
							if (detalheIpca.isParcelaPaga()) {		
								BigDecimal vlrPago = BigDecimal.ZERO;

								List<ContratoCobrancaDetalhesParcial> detalhesParciais = detalheIpca
										.getListContratoCobrancaDetalhesParcial();
								
								if (detalheIpca.getDataVencimento().before(dataCorteParcelasBaixadas.getTime())) {
									//detalheIpca.setVlrAmortizacaoParcela(detalheIpca.getVlrAmortizacaoParcela());
									
									BigDecimal vlrParcela = parcela.getJuros().add(detalheIpca.getVlrAmortizacaoParcela())
											.add(detalheIpca.getSeguroMIP()).add(detalheIpca.getSeguroDFI()).add(parcela.getTxAdm());
											
									//detalheIpca.setVlrParcela(parcela.getValorParcela());
									detalheIpca.setVlrParcela(vlrParcela);
								} else {	
									detalheIpca.setVlrParcela(parcela.getValorParcela());
								}
							}

							detalheIpca.setVlrSaldoParcela(vlrSaldoParcela.setScale(2, BigDecimal.ROUND_HALF_EVEN));

//							if (parcela.getValorParcela().compareTo(BigDecimal.ZERO) == 0) {
//								detalheIpca.setParcelaPaga(true);
//								detalheIpca.setDataPagamento(detalheIpca.getDataVencimento());
//								detalheIpca.setVlrParcela(BigDecimal.ZERO);
//							}

							if (!detalheIpca.isParcelaPaga()) {
								if (DateUtil.isAfterDate(detalheIpca.getDataVencimento(), DateUtil.getDataHoje())
										&& !detalheIpca.isParcelaPaga()) {
									detalheIpca.setParcelaVencida(true);
								} else
									detalheIpca.setParcelaVencida(false);

								if (DateUtil.isDataHoje(detalhe.getDataVencimento()) && !detalheIpca.isParcelaPaga()) {
									detalheIpca.setParcelaVencendo(true);
								} else
									detalheIpca.setParcelaVencendo(false);
							}
							if (CommonsUtil.mesmoValor(detalheIpca.getId(), detalhe.getId())) {
								detalheIpca.setVlrSaldoInicial(saldoDevedorIpca);
							} else
								detalheIpca.setVlrSaldoInicial(saldoDevedorAnterior);
								//detalheIpca.setVlrSaldoInicial(parcela.getSaldoDevedorInicial());
							
							saldoDevedorAnterior = detalheIpca.getVlrSaldoParcela();
						}

						detalheIpca.setIpca(detalheIpca.getVlrParcela().subtract(detalheIpca.getVlrParcelaOriginal()));

					}

					break;
				}

				// detalhe.setVlrSaldoInicial(detalhe.setVlrSaldoInicial().add(detalhe.ge()));
				saldoDevedor = detalhe.getVlrSaldoParcela();
				saldoDevedorAnterior = detalhe.getVlrSaldoParcela();

				if ((contratoCobranca.isTemSeguroMIP() && contratoCobranca.isTemSeguro())
						&& CommonsUtil.intValue(detalhe.getNumeroParcela()) <= contratoCobranca.getMesesCarencia()
						&& CommonsUtil.intValue(detalhe.getNumeroParcela()) > 0) {
					BigDecimal valorSeguroMIP = detalhe.getVlrSaldoParcela()
							.multiply(SiscoatConstants.SEGURO_MIP.divide(BigDecimal.valueOf(100)));
					valorSeguroMIPCarencia = valorSeguroMIPCarencia.add(valorSeguroMIP);
				}

				if (CommonsUtil.mesmoValor(CommonsUtil.intValue(detalhe.getNumeroParcela()),
						contratoCobranca.getMesesCarencia() + 1)) {
					valorSeguroMIPCarencia = BigDecimal.ZERO;
				}

			}

			contratoCobrancaDao.merge(contratoCobranca);
			return true;
			/*
			 * if (contratoCobrancaDetalhes.getIpca() == null &&
			 * CommonsUtil.booleanValue(contratoCobranca.isCorrigidoIPCA())) { BigDecimal
			 * valorIpca = (contratoCobrancaDetalhes.getVlrSaldoParcela()
			 * .add(contratoCobrancaDetalhes.getVlrAmortizacaoParcela()))
			 * .multiply(ultimoIpca.getTaxa().divide(BigDecimal.valueOf(100)));
			 * contratoCobrancaDetalhes.setVlrParcela((contratoCobrancaDetalhes.
			 * getVlrParcela().add(valorIpca)) .setScale(2, BigDecimal.ROUND_HALF_EVEN));
			 * contratoCobrancaDetalhes.setIpca(valorIpca); return true; }
			 */
		}

		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean calcularIPCACustom(IPCADao ipcaDao, ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao,
			ContratoCobrancaDao contratoCobrancaDao,
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao,
			ContratoCobrancaDetalhes contratoCobrancaDetalhes, ContratoCobranca contratoCobranca) {

		Date dataIPCA = DateUtil.adicionarMes(contratoCobrancaDetalhes.getDataVencimento(), -2);
		// este método pegara o último IPCA na base, com data anterior a data base, mesmo que não do mesmo mês.
		IPCA ultimoIpca = ipcaDao.getUltimoIPCA(dataIPCA);
		
		if (dataIPCA.getMonth() != ultimoIpca.getData().getMonth()
				|| dataIPCA.getYear() != ultimoIpca.getData().getYear()) {
			ultimoIpca = new IPCA();
			ultimoIpca.setData(dataIPCA);
			ultimoIpca.setId(-1);
			ultimoIpca.setTaxa(new BigDecimal(0.0));
		}

		// primeira condição é para meses de mesmo ano; segunda condição é para os meses
		// jan e fev da parcela IPCA
		if ( CommonsUtil.mesmoValor(dataIPCA.getMonth(), ultimoIpca.getData().getMonth()) &&
				CommonsUtil.mesmoValor(dataIPCA.getYear(), ultimoIpca.getData().getYear()) ) {

			
			// usar o reparcelamento aqui.

			BigDecimal saldoDevedor = BigDecimal.ZERO;
			BigDecimal valorSeguroMIPCarencia = BigDecimal.ZERO;
			BigDecimal valorSeguroDFI = BigDecimal.ZERO;
			BigDecimal valorTxAdm = SiscoatConstants.TAXA_ADM;
			BigDecimal saldoDevedorAnterior = BigDecimal.ZERO;

			if ((contratoCobranca.isTemSeguroMIP() && contratoCobranca.isTemSeguro())) {
				valorSeguroDFI = contratoCobranca.getValorImovel()
						.multiply(SiscoatConstants.SEGURO_DFI.divide(BigDecimal.valueOf(100)));
			}
			
			Calendar dataCorteParcelasBaixadas = Calendar.getInstance();
			dataCorteParcelasBaixadas.set(Calendar.YEAR, 2021);
			dataCorteParcelasBaixadas.set(Calendar.MONTH, 9);
			dataCorteParcelasBaixadas.set(Calendar.DAY_OF_MONTH, 13);
			
			Calendar dataCorteParcelasMalucas = Calendar.getInstance();
			dataCorteParcelasMalucas.set(Calendar.YEAR, 2022);
			dataCorteParcelasMalucas.set(Calendar.MONTH, 11);
			dataCorteParcelasMalucas.set(Calendar.DAY_OF_MONTH, 15);

			for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {

				ContratoCobrancaDetalhes detalhe = contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe);

				if (CommonsUtil.mesmoValor(contratoCobrancaDetalhes.getId(), detalhe.getId())) {

					/*
					if (!CommonsUtil.booleanValue(contratoCobranca.isRecalculaIPCA())
							&& detalhe.getIpcaAtualizou() != null
							&& CommonsUtil.mesmoValor(detalhe.getIpcaAtualizou().getId(), ultimoIpca.getId()))
						continue;
						*/

					// reparcela
					BigDecimal valorIpca = (saldoDevedor
							.multiply(ultimoIpca.getTaxa().divide(BigDecimal.valueOf(100))));

					BigDecimal saldoDevedorIpca = saldoDevedor.add(valorIpca).setScale(2, BigDecimal.ROUND_HALF_EVEN);

					// saldoDevedorAnterior = saldoDevedorIpca;

					BigDecimal tarifaIOFDiario;
					BigDecimal tarifaIOFAdicional = SiscoatConstants.TARIFA_IOF_ADICIONAL
							.divide(BigDecimal.valueOf(100));
					SimulacaoVO simulador = new SimulacaoVO();

					if (contratoCobranca.getPagador().getCpf() != null) {
						if (DateUtil.isAfterDate(contratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF)) {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF_ANTIGA.divide(BigDecimal.valueOf(100));
						} else {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF.divide(BigDecimal.valueOf(100));
						}
						simulador.setTipoPessoa("PF");
					} else {
						if (DateUtil.isAfterDate(contratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF)) {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ_ANTIGA.divide(BigDecimal.valueOf(100));
						} else {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ.divide(BigDecimal.valueOf(100));
						}
						simulador.setTipoPessoa("PJ");
					}

					simulador.setDataSimulacao(DateUtil.getDataHoje());
					simulador.setTarifaIOFDiario(tarifaIOFDiario);
					simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
					simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
					simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
					// valores
					simulador.setValorCredito(saldoDevedorIpca);
					simulador.setTaxaJuros(contratoCobranca.getTxJurosParcelas());

					if (contratoCobranca.getMesesCarencia() > CommonsUtil.intValue(detalhe.getNumeroParcela()))
						simulador.setCarencia(
								BigInteger.valueOf(CommonsUtil.longValue(contratoCobranca.getMesesCarencia()
										- CommonsUtil.intValue(detalhe.getNumeroParcela()))));
					else if (CommonsUtil.mesmoValor(contratoCobranca.getMesesCarencia(),
							CommonsUtil.intValue(detalhe.getNumeroParcela())))
						simulador.setCarencia(BigInteger.valueOf(contratoCobranca.getMesesCarencia()));
					else
						simulador.setCarencia(BigInteger.ZERO);

					int qtdParcelas = contratoCobranca.getQtdeParcelas();
					if (contratoCobranca.isGeraParcelaFinal()) {
						qtdParcelas++;
						contratoCobranca.setQtdeParcelas(qtdParcelas);
						contratoCobranca.setGeraParcelaFinal(false);
					}

					if (CommonsUtil.intValue(detalhe.getNumeroParcela()) > 0)
						simulador.setQtdParcelas(
								BigInteger.valueOf(qtdParcelas - CommonsUtil.intValue(detalhe.getNumeroParcela()) + 1));
					else
						simulador.setQtdParcelas(BigInteger.valueOf(qtdParcelas));

					simulador.setValorImovel(contratoCobranca.getValorImovel());
//						simulador.setCustoEmissaoValor(custoEmissaoValor);
					simulador.setTipoCalculo(contratoCobranca.getTipoCalculo());

					simulador.setNaoCalcularDFI(!(contratoCobranca.isTemSeguroDFI() && contratoCobranca.isTemSeguro()));
					simulador.setNaoCalcularMIP(!(contratoCobranca.isTemSeguroMIP() && contratoCobranca.isTemSeguro()));
					simulador.setNaoCalcularTxAdm(!(contratoCobranca.isTemTxAdm()));

					simulador.calcular();

					for (SimulacaoDetalheVO parcela : simulador.getParcelas()) {

						if (parcela.getNumeroParcela().compareTo(BigInteger.ZERO) == 0)
							continue;

						Integer parcelaSimuladorReal = 0;

						if (CommonsUtil.intValue(detalhe.getNumeroParcela()) > 1)
							parcelaSimuladorReal = CommonsUtil.intValue(parcela.getNumeroParcela())
									+ CommonsUtil.intValue(detalhe.getNumeroParcela()) - 1;
						else
							parcelaSimuladorReal = CommonsUtil.intValue(parcela.getNumeroParcela());

						final int parcelaProcura = parcelaSimuladorReal;
						ContratoCobrancaDetalhes detalheIpca = contratoCobranca
								.getListContratoCobrancaDetalhes().stream().filter(x -> CommonsUtil
										.mesmoValor(CommonsUtil.intValue(x.getNumeroParcela()), parcelaProcura))
								.findFirst().orElse(null);

//						for (ContratoCobrancaDetalhes detalheIpca : contratoCobranca
//								.getListContratoCobrancaDetalhes()) {

						if (detalheIpca.getVlrParcelaOriginal() == null)
							detalheIpca.setVlrParcelaOriginal(detalheIpca.getVlrParcela());

						if (CommonsUtil.mesmoValor(parcelaSimuladorReal.toString(), detalheIpca.getNumeroParcela())) {
							if (detalheIpca.isParcelaPaga()
									&& !CommonsUtil.booleanValue(contratoCobranca.isRecalculaIPCA())) {
								continue;
							}

							if (ultimoIpca.getId() >= 0) {
								detalheIpca.setIpcaAtualizou(ultimoIpca);
							}
							//detalheIpca.setVlrRecebido(BigDecimal.valueOf(parcelaSimuladorReal.longValue()));

							detalheIpca
									.setVlrParcela(parcela.getValorParcela().setScale(2, BigDecimal.ROUND_HALF_EVEN));

							detalheIpca.setVlrJurosParcela(parcela.getJuros().setScale(2, BigDecimal.ROUND_HALF_EVEN));
							detalheIpca.setVlrAmortizacaoParcela(
									parcela.getAmortizacao().setScale(2, BigDecimal.ROUND_HALF_EVEN));
							// nao precisa pois é pelo valor do imovel, nao muda
							// detalheIpca.setSeguroDFI(parcela.getSeguroDFI());
							int finalCarencia = contratoCobranca.getMesesCarencia() + 1;

							if (CommonsUtil.mesmoValor(parcelaSimuladorReal, finalCarencia)) {
								detalheIpca.setSeguroMIP(parcela.getSeguroMIP().add(valorSeguroMIPCarencia));
								detalheIpca.setSeguroDFI(valorSeguroDFI.multiply(
										CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia() + 1)));
								detalheIpca.setTaxaAdm(valorTxAdm.multiply(
										CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia() + 1)));
								detalheIpca.setVlrParcela(detalheIpca.getVlrParcela().add(valorSeguroMIPCarencia)
										.add(valorSeguroDFI.multiply(
												CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia()))).add(valorTxAdm));
							} else {
								detalheIpca.setSeguroMIP(parcela.getSeguroMIP());
								detalheIpca.setSeguroDFI(parcela.getSeguroDFI());
								detalheIpca.setTaxaAdm(parcela.getTxAdm());
							}

							BigDecimal vlrSaldoParcela = parcela.getSaldoDevedorInicial();

							if (detalheIpca.isParcelaPaga()) {		
								BigDecimal vlrPago = BigDecimal.ZERO;

								List<ContratoCobrancaDetalhesParcial> detalhesParciais = detalheIpca
										.getListContratoCobrancaDetalhesParcial();
								
								if (detalheIpca.getDataVencimento().before(dataCorteParcelasBaixadas.getTime())) {
									//detalheIpca.setVlrAmortizacaoParcela(detalheIpca.getVlrAmortizacaoParcela());
									
									BigDecimal vlrParcela = parcela.getJuros().add(detalheIpca.getVlrAmortizacaoParcela())
											.add(detalheIpca.getSeguroMIP()).add(detalheIpca.getSeguroDFI()).add(parcela.getTxAdm());
											
									//detalheIpca.setVlrParcela(parcela.getValorParcela());
									detalheIpca.setVlrParcela(vlrParcela);
									
									for (ContratoCobrancaDetalhesParcial parcial : detalhesParciais) {		
										vlrPago = vlrPago.add(CommonsUtil.bigDecimalValue(parcial.getVlrRecebido()));
									}
									
									BigDecimal diferencaPagto = vlrPago.subtract(vlrParcela);
									
									if (diferencaPagto.compareTo(BigDecimal.ZERO) == 1) {
										detalheIpca.setVlrAmortizacaoParcela(detalheIpca.getVlrAmortizacaoParcela().add(diferencaPagto));
										vlrSaldoParcela = vlrSaldoParcela.subtract(diferencaPagto);
									}
								} else {
									detalheIpca.setVlrParcela(parcela.getValorParcela());
								}
							}

							detalheIpca.setVlrSaldoParcela(vlrSaldoParcela.setScale(2, BigDecimal.ROUND_HALF_EVEN));

							if (!detalheIpca.isParcelaPaga()) {
								if (DateUtil.isAfterDate(detalheIpca.getDataVencimento(), DateUtil.getDataHoje())
										&& !detalheIpca.isParcelaPaga()) {
									detalheIpca.setParcelaVencida(true);
								} else
									detalheIpca.setParcelaVencida(false);

								if (DateUtil.isDataHoje(detalhe.getDataVencimento()) && !detalheIpca.isParcelaPaga()) {
									detalheIpca.setParcelaVencendo(true);
								} else
									detalheIpca.setParcelaVencendo(false);
							}
							if (CommonsUtil.mesmoValor(detalheIpca.getId(), detalhe.getId())) {
								detalheIpca.setVlrSaldoInicial(saldoDevedorIpca);
							} else
								detalheIpca.setVlrSaldoInicial(saldoDevedorAnterior);
								//detalheIpca.setVlrSaldoInicial(parcela.getSaldoDevedorInicial());
							
							saldoDevedorAnterior = detalheIpca.getVlrSaldoParcela();
						}

						detalheIpca.setIpca(detalheIpca.getVlrParcela().subtract(detalheIpca.getVlrParcelaOriginal()));

					}

					break;
				}

				// detalhe.setVlrSaldoInicial(detalhe.setVlrSaldoInicial().add(detalhe.ge()));
				saldoDevedor = detalhe.getVlrSaldoParcela();
				saldoDevedorAnterior = detalhe.getVlrSaldoParcela();

				if ((contratoCobranca.isTemSeguroMIP() && contratoCobranca.isTemSeguro())
						&& CommonsUtil.intValue(detalhe.getNumeroParcela()) <= contratoCobranca.getMesesCarencia()
						&& CommonsUtil.intValue(detalhe.getNumeroParcela()) > 0) {
					BigDecimal valorSeguroMIP = detalhe.getVlrSaldoParcela()
							.multiply(SiscoatConstants.SEGURO_MIP.divide(BigDecimal.valueOf(100)));
					valorSeguroMIPCarencia = valorSeguroMIPCarencia.add(valorSeguroMIP);
				}

				if (CommonsUtil.mesmoValor(CommonsUtil.intValue(detalhe.getNumeroParcela()),
						contratoCobranca.getMesesCarencia() + 1)) {
					valorSeguroMIPCarencia = BigDecimal.ZERO;
				}

			}

			contratoCobrancaDao.merge(contratoCobranca);
			return true;
			/*
			 * if (contratoCobrancaDetalhes.getIpca() == null &&
			 * CommonsUtil.booleanValue(contratoCobranca.isCorrigidoIPCA())) { BigDecimal
			 * valorIpca = (contratoCobrancaDetalhes.getVlrSaldoParcela()
			 * .add(contratoCobrancaDetalhes.getVlrAmortizacaoParcela()))
			 * .multiply(ultimoIpca.getTaxa().divide(BigDecimal.valueOf(100)));
			 * contratoCobrancaDetalhes.setVlrParcela((contratoCobrancaDetalhes.
			 * getVlrParcela().add(valorIpca)) .setScale(2, BigDecimal.ROUND_HALF_EVEN));
			 * contratoCobrancaDetalhes.setIpca(valorIpca); return true; }
			 */
		}

		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean calcularIPCACustomMaluco(IPCADao ipcaDao, ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao,
			ContratoCobrancaDao contratoCobrancaDao,
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao,
			ContratoCobrancaDetalhes contratoCobrancaDetalhes, ContratoCobranca contratoCobranca, Date dataCorteBaixa) {

		Date dataIPCA = DateUtil.adicionarMes(contratoCobrancaDetalhes.getDataVencimento(), -2);
		IPCA ultimoIpca = ipcaDao.getUltimoIPCA(dataIPCA);
		if (ultimoIpca == null || dataIPCA.getMonth() != ultimoIpca.getData().getMonth()
				|| dataIPCA.getYear() != ultimoIpca.getData().getYear())
			return false;

		// primeira condição é para meses de mesmo ano; segunda condição é para os meses
		// jan e fev da parcela IPCA
		if ( CommonsUtil.mesmoValor(dataIPCA.getMonth(), ultimoIpca.getData().getMonth()) &&
				CommonsUtil.mesmoValor(dataIPCA.getYear(), ultimoIpca.getData().getYear()) ) {

			
			// usar o reparcelamento aqui.

			BigDecimal saldoDevedor = BigDecimal.ZERO;
			BigDecimal valorSeguroMIPCarencia = BigDecimal.ZERO;
			BigDecimal valorSeguroDFI = BigDecimal.ZERO;
			BigDecimal valorTxAdm = SiscoatConstants.TAXA_ADM;
			BigDecimal saldoDevedorAnterior = BigDecimal.ZERO;

			if ((contratoCobranca.isTemSeguroMIP() && contratoCobranca.isTemSeguro())) {
				valorSeguroDFI = contratoCobranca.getValorImovel()
						.multiply(SiscoatConstants.SEGURO_DFI.divide(BigDecimal.valueOf(100)));
			}
			
			Calendar dataCorteParcelasBaixadas = Calendar.getInstance();
			dataCorteParcelasBaixadas.set(Calendar.YEAR, 2021);
			dataCorteParcelasBaixadas.set(Calendar.MONTH, 9);
			dataCorteParcelasBaixadas.set(Calendar.DAY_OF_MONTH, 13);
			
			Calendar dataCorteParcelasMalucas = Calendar.getInstance();
			dataCorteParcelasMalucas.setTime(dataCorteBaixa);

			for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {

				ContratoCobrancaDetalhes detalhe = contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe);

				if (CommonsUtil.mesmoValor(contratoCobrancaDetalhes.getId(), detalhe.getId())) {

					/*
					if (!CommonsUtil.booleanValue(contratoCobranca.isRecalculaIPCA())
							&& detalhe.getIpcaAtualizou() != null
							&& CommonsUtil.mesmoValor(detalhe.getIpcaAtualizou().getId(), ultimoIpca.getId()))
						continue;
						*/

					// reparcela
					BigDecimal valorIpca = (saldoDevedor
							.multiply(ultimoIpca.getTaxa().divide(BigDecimal.valueOf(100))));

					BigDecimal saldoDevedorIpca = saldoDevedor.add(valorIpca).setScale(2, BigDecimal.ROUND_HALF_EVEN);

					// saldoDevedorAnterior = saldoDevedorIpca;

					BigDecimal tarifaIOFDiario;
					BigDecimal tarifaIOFAdicional = SiscoatConstants.TARIFA_IOF_ADICIONAL
							.divide(BigDecimal.valueOf(100));
					SimulacaoVO simulador = new SimulacaoVO();

					if (contratoCobranca.getPagador().getCpf() != null) {
						if (DateUtil.isAfterDate(contratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF)) {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF_ANTIGA.divide(BigDecimal.valueOf(100));
						} else {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF.divide(BigDecimal.valueOf(100));
						}
						simulador.setTipoPessoa("PF");
					} else {
						if (DateUtil.isAfterDate(contratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF)) {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ_ANTIGA.divide(BigDecimal.valueOf(100));
						} else {
							tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ.divide(BigDecimal.valueOf(100));
						}
						simulador.setTipoPessoa("PJ");
					}

					simulador.setDataSimulacao(DateUtil.getDataHoje());
					simulador.setTarifaIOFDiario(tarifaIOFDiario);
					simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
					simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
					simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
					// valores
					simulador.setValorCredito(saldoDevedorIpca);
					simulador.setTaxaJuros(contratoCobranca.getTxJurosParcelas());

					if (contratoCobranca.getMesesCarencia() > CommonsUtil.intValue(detalhe.getNumeroParcela()))
						simulador.setCarencia(
								BigInteger.valueOf(CommonsUtil.longValue(contratoCobranca.getMesesCarencia()
										- CommonsUtil.intValue(detalhe.getNumeroParcela()))));
					else if (CommonsUtil.mesmoValor(contratoCobranca.getMesesCarencia(),
							CommonsUtil.intValue(detalhe.getNumeroParcela())))
						simulador.setCarencia(BigInteger.valueOf(contratoCobranca.getMesesCarencia()));
					else
						simulador.setCarencia(BigInteger.ZERO);

					int qtdParcelas = contratoCobranca.getQtdeParcelas();
					if (contratoCobranca.isGeraParcelaFinal()) {
						qtdParcelas++;
						contratoCobranca.setQtdeParcelas(qtdParcelas);
						contratoCobranca.setGeraParcelaFinal(false);
					}

					if (CommonsUtil.intValue(detalhe.getNumeroParcela()) > 0)
						simulador.setQtdParcelas(
								BigInteger.valueOf(qtdParcelas - CommonsUtil.intValue(detalhe.getNumeroParcela()) + 1));
					else
						simulador.setQtdParcelas(BigInteger.valueOf(qtdParcelas));

					simulador.setValorImovel(contratoCobranca.getValorImovel());
//						simulador.setCustoEmissaoValor(custoEmissaoValor);
					simulador.setTipoCalculo(contratoCobranca.getTipoCalculo());

					simulador.setNaoCalcularDFI(!(contratoCobranca.isTemSeguroDFI() && contratoCobranca.isTemSeguro()));
					simulador.setNaoCalcularMIP(!(contratoCobranca.isTemSeguroMIP() && contratoCobranca.isTemSeguro()));
					simulador.setNaoCalcularTxAdm(!(contratoCobranca.isTemTxAdm()));

					simulador.calcular();

					for (SimulacaoDetalheVO parcela : simulador.getParcelas()) {

						if (parcela.getNumeroParcela().compareTo(BigInteger.ZERO) == 0)
							continue;

						Integer parcelaSimuladorReal = 0;

						if (CommonsUtil.intValue(detalhe.getNumeroParcela()) > 1)
							parcelaSimuladorReal = CommonsUtil.intValue(parcela.getNumeroParcela())
									+ CommonsUtil.intValue(detalhe.getNumeroParcela()) - 1;
						else
							parcelaSimuladorReal = CommonsUtil.intValue(parcela.getNumeroParcela());

						final int parcelaProcura = parcelaSimuladorReal;
						ContratoCobrancaDetalhes detalheIpca = contratoCobranca
								.getListContratoCobrancaDetalhes().stream().filter(x -> CommonsUtil
										.mesmoValor(CommonsUtil.intValue(x.getNumeroParcela()), parcelaProcura))
								.findFirst().orElse(null);

//						for (ContratoCobrancaDetalhes detalheIpca : contratoCobranca
//								.getListContratoCobrancaDetalhes()) {

						if (detalheIpca.getVlrParcelaOriginal() == null)
							detalheIpca.setVlrParcelaOriginal(detalheIpca.getVlrParcela());

						if (CommonsUtil.mesmoValor(parcelaSimuladorReal.toString(), detalheIpca.getNumeroParcela())) {
							if (detalheIpca.isParcelaPaga()
									&& !CommonsUtil.booleanValue(contratoCobranca.isRecalculaIPCA())) {
								continue;
							}

							detalheIpca.setIpcaAtualizou(ultimoIpca);
							//detalheIpca.setVlrRecebido(BigDecimal.valueOf(parcelaSimuladorReal.longValue()));

							detalheIpca
									.setVlrParcela(parcela.getValorParcela().setScale(2, BigDecimal.ROUND_HALF_EVEN));

							detalheIpca.setVlrJurosParcela(parcela.getJuros().setScale(2, BigDecimal.ROUND_HALF_EVEN));
							detalheIpca.setVlrAmortizacaoParcela(
									parcela.getAmortizacao().setScale(2, BigDecimal.ROUND_HALF_EVEN));
							// nao precisa pois é pelo valor do imovel, nao muda
							// detalheIpca.setSeguroDFI(parcela.getSeguroDFI());
							int finalCarencia = contratoCobranca.getMesesCarencia() + 1;

							if (CommonsUtil.mesmoValor(parcelaSimuladorReal, finalCarencia)) {
								BigDecimal seguroMIPAcumulado = BigDecimal.ZERO;
								BigDecimal seguroDFIAcumulado = BigDecimal.ZERO;
								BigDecimal seguroTaxaADMAcumulado = BigDecimal.ZERO;
								
								
								// percorre todas as parcelas da carência
								/*
								for (SimulacaoDetalheVO parcelaSeguros : simulador.getParcelas()) {
									// se número parcela maior que 0 soma ao seguro
									if (CommonsUtil.intValue(parcelaSeguros.getNumeroParcela()) < CommonsUtil.intValue(detalheIpca.getNumeroParcela())
										&& CommonsUtil.intValue(parcelaSeguros.getNumeroParcela()) > 0) {
										seguroDFIAcumulado = seguroDFIAcumulado.add(parcelaSeguros.getSeguroDFI());
										seguroTaxaADMAcumulado = seguroTaxaADMAcumulado.add(parcelaSeguros.getSeguroMIP());
									}						
								}	
								*/
								
								for (ContratoCobrancaDetalhes detalheSeguroMIP : contratoCobranca.getListContratoCobrancaDetalhes()) {
									if (CommonsUtil.intValue(detalheSeguroMIP.getNumeroParcela()) < CommonsUtil.intValue(detalheIpca.getNumeroParcela())
											&& CommonsUtil.intValue(detalheSeguroMIP.getNumeroParcela()) > 0) {
										seguroMIPAcumulado = seguroMIPAcumulado.add(detalheSeguroMIP.getVlrSaldoInicial().multiply(SiscoatConstants.SEGURO_MIP_5_DIGITOS));
									}
								}
								
								detalheIpca.setSeguroMIP(seguroMIPAcumulado);
								detalheIpca.setSeguroDFI(parcela.getSeguroDFI().multiply(CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia())));
								detalheIpca.setTaxaAdm(valorTxAdm.multiply(CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia())));
								//detalheIpca.setTaxaAdm(valorTxAdm.multiply(
								//		CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia() + 1)));
								detalheIpca.setVlrParcela(detalheIpca.getVlrParcela().add(detalheIpca.getSeguroMIP())
										.add(detalheIpca.getSeguroDFI()).add(detalheIpca.getTaxaAdm()));
								
								// adiciona os valores da parcela 
								
								detalheIpca.setSeguroMIP(detalheIpca.getSeguroMIP().add(parcela.getSeguroMIP()));
								detalheIpca.setSeguroDFI(detalheIpca.getSeguroDFI().add(parcela.getSeguroDFI()));
								detalheIpca.setTaxaAdm(detalheIpca.getTaxaAdm().add(valorTxAdm));
							} else {
								detalheIpca.setSeguroMIP(parcela.getSeguroMIP());
								detalheIpca.setSeguroDFI(parcela.getSeguroDFI());
								detalheIpca.setTaxaAdm(parcela.getTxAdm());
							}

							BigDecimal vlrSaldoParcela = parcela.getSaldoDevedorInicial();

							if (detalheIpca.isParcelaPaga()) {		
								BigDecimal vlrPago = BigDecimal.ZERO;

								List<ContratoCobrancaDetalhesParcial> detalhesParciais = detalheIpca
										.getListContratoCobrancaDetalhesParcial();
								
								/*								
								if (detalheIpca.getDataVencimento().before(dataCorteParcelasBaixadas.getTime())) {
									//detalheIpca.setVlrAmortizacaoParcela(detalheIpca.getVlrAmortizacaoParcela());
									
									estaNoCorte = true;
									
									BigDecimal vlrParcela = parcela.getJuros().add(detalheIpca.getVlrAmortizacaoParcela())
											.add(detalheIpca.getSeguroMIP()).add(detalheIpca.getSeguroDFI()).add(detalheIpca.getTaxaAdm());
											
									//detalheIpca.setVlrParcela(parcela.getValorParcela());
									detalheIpca.setVlrParcela(vlrParcela);
									
									for (ContratoCobrancaDetalhesParcial parcial : detalhesParciais) {		
										vlrPago = vlrPago.add(CommonsUtil.bigDecimalValue(parcial.getVlrRecebido()));
									}
									
									BigDecimal diferencaPagto = vlrPago.subtract(vlrParcela);
									
									if (diferencaPagto.compareTo(BigDecimal.ZERO) == 1) {
										detalheIpca.setVlrAmortizacaoParcela(detalheIpca.getVlrAmortizacaoParcela().add(diferencaPagto));
										vlrSaldoParcela = vlrSaldoParcela.subtract(diferencaPagto);
									} 
								} 
								*/
								
								boolean estaNoCorte = false;
								
								if (detalheIpca.getDataVencimento().before(dataCorteParcelasMalucas.getTime()) && !estaNoCorte) {
									//BigDecimal vlrParcela = parcela.getJuros().add(detalheIpca.getVlrAmortizacaoParcela())
									//		.add(detalheIpca.getSeguroMIP()).add(detalheIpca.getSeguroDFI()).add(detalheIpca.getTaxaAdm());
									
									//detalheIpca.setVlrParcela(vlrParcela);
												
									estaNoCorte = true;
									
									for (ContratoCobrancaDetalhesParcial parcial : detalhesParciais) {		
										vlrPago = vlrPago.add(CommonsUtil.bigDecimalValue(parcial.getVlrRecebido()));
									}
									
									BigDecimal diferencaPagto = vlrPago.subtract(detalheIpca.getVlrParcela());
									
									if (diferencaPagto.compareTo(BigDecimal.ZERO) == 1) {
										detalheIpca.setVlrAmortizacaoParcela(detalheIpca.getVlrAmortizacaoParcela().add(diferencaPagto));
										
										//vlrParcela = parcela.getJuros().add(detalheIpca.getVlrAmortizacaoParcela())
										//		.add(detalheIpca.getSeguroMIP()).add(detalheIpca.getSeguroDFI()).add(detalheIpca.getTaxaAdm());
										
										//detalheIpca.setVlrParcela(vlrParcela);
										
										vlrSaldoParcela = vlrSaldoParcela.subtract(diferencaPagto); 
									}
								}
								
								if (!estaNoCorte) {
									detalheIpca.setVlrParcela(parcela.getValorParcela());
								}
							}

							detalheIpca.setVlrSaldoParcela(vlrSaldoParcela.setScale(2, BigDecimal.ROUND_HALF_EVEN));

							if (!detalheIpca.isParcelaPaga()) {
								if (DateUtil.isAfterDate(detalheIpca.getDataVencimento(), DateUtil.getDataHoje())
										&& !detalheIpca.isParcelaPaga()) {
									detalheIpca.setParcelaVencida(true);
								} else
									detalheIpca.setParcelaVencida(false);

								if (DateUtil.isDataHoje(detalhe.getDataVencimento()) && !detalheIpca.isParcelaPaga()) {
									detalheIpca.setParcelaVencendo(true);
								} else
									detalheIpca.setParcelaVencendo(false);
							}
							if (CommonsUtil.mesmoValor(detalheIpca.getId(), detalhe.getId())) {
								detalheIpca.setVlrSaldoInicial(saldoDevedorIpca);
							} else
								detalheIpca.setVlrSaldoInicial(saldoDevedorAnterior);
								//detalheIpca.setVlrSaldoInicial(parcela.getSaldoDevedorInicial());
							
							saldoDevedorAnterior = detalheIpca.getVlrSaldoParcela();
						}

						detalheIpca.setIpca(detalheIpca.getVlrParcela().subtract(detalheIpca.getVlrParcelaOriginal()));

					}

					break;
				}

				// detalhe.setVlrSaldoInicial(detalhe.setVlrSaldoInicial().add(detalhe.ge()));
				saldoDevedor = detalhe.getVlrSaldoParcela();
				saldoDevedorAnterior = detalhe.getVlrSaldoParcela();
			}

			contratoCobrancaDao.merge(contratoCobranca);
			return true;
			/*
			 * if (contratoCobrancaDetalhes.getIpca() == null &&
			 * CommonsUtil.booleanValue(contratoCobranca.isCorrigidoIPCA())) { BigDecimal
			 * valorIpca = (contratoCobrancaDetalhes.getVlrSaldoParcela()
			 * .add(contratoCobrancaDetalhes.getVlrAmortizacaoParcela()))
			 * .multiply(ultimoIpca.getTaxa().divide(BigDecimal.valueOf(100)));
			 * contratoCobrancaDetalhes.setVlrParcela((contratoCobrancaDetalhes.
			 * getVlrParcela().add(valorIpca)) .setScale(2, BigDecimal.ROUND_HALF_EVEN));
			 * contratoCobrancaDetalhes.setIpca(valorIpca); return true; }
			 */
		}

		return false;
	}
}