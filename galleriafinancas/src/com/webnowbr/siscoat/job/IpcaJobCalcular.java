package com.webnowbr.siscoat.job;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

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
import com.webnowbr.siscoat.simulador.SimulacaoVO;

public class IpcaJobCalcular {

	@SuppressWarnings("deprecation")
	public boolean calcularIPCA(IPCADao ipcaDao, ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao,
			ContratoCobrancaDao contratoCobrancaDao,
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao,
			ContratoCobrancaDetalhes contratoCobrancaDetalhes) {

		Date dataIPCA = DateUtil.adicionarMes(contratoCobrancaDetalhes.getDataVencimento(), -2);
		IPCA ultimoIpca = ipcaDao.getUltimoIPCA(dataIPCA);
		if (ultimoIpca == null || dataIPCA.getMonth() != ultimoIpca.getData().getMonth()
				|| dataIPCA.getYear() != ultimoIpca.getData().getYear())
			return false;

		// primeira condição é para meses de mesmo ano; segunda condição é para os meses
		// jan e fev da parcela IPCA
		if (contratoCobrancaDetalhes.getDataVencimento().getMonth() - ultimoIpca.getData().getMonth() <= 2
				|| contratoCobrancaDetalhes.getDataVencimento().getMonth() - ultimoIpca.getData().getMonth() <= -10) {

			ContratoCobranca contratoCobranca = contratoCobrancaDetalhesDao
					.getContratoCobranca(contratoCobrancaDetalhes.getId());
			// usar o reparcelamento aqui.

			BigDecimal saldoDevedor = BigDecimal.ZERO;
			BigDecimal valorSeguroMIPCarencia = BigDecimal.ZERO;
			BigDecimal valorSeguroDFI = BigDecimal.ZERO;
			BigDecimal saldoDevedorAnterior = BigDecimal.ZERO;

			if ((contratoCobranca.isTemSeguroMIP() && contratoCobranca.isTemSeguro())) {
				valorSeguroDFI = contratoCobranca.getValorImovel()
						.multiply(SiscoatConstants.SEGURO_DFI.divide(BigDecimal.valueOf(100)));
			}

			for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {

				ContratoCobrancaDetalhes detalhe = contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe);

				if (CommonsUtil.mesmoValor(contratoCobrancaDetalhes.getId(), detalhe.getId())) {

					if (!CommonsUtil.booleanValue(contratoCobranca.isRecalculaIPCA())
							&& detalhe.getIpcaAtualizou() != null
							&& CommonsUtil.mesmoValor(CommonsUtil.dateValue(detalhe.getIpcaAtualizou()),
									CommonsUtil.dateValue(ultimoIpca)))
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
								detalheIpca.setVlrParcela(detalheIpca.getVlrParcela().add(valorSeguroMIPCarencia)
										.add(valorSeguroDFI.multiply(
												CommonsUtil.bigDecimalValue(contratoCobranca.getMesesCarencia()))));
							} else {
								detalheIpca.setSeguroMIP(parcela.getSeguroMIP());
								detalheIpca.setSeguroDFI(parcela.getSeguroDFI());

							}

							BigDecimal vlrSaldoParcela = parcela.getSaldoDevedorInicial();

							if (detalheIpca.isParcelaPaga()) {
								BigDecimal vlrPago = BigDecimal.ZERO;

//								List<ContratoCobrancaDetalhesParcial> detalhesParciais = contratoCobrancaDetalhesParcialDao
//										.getContratoCobrancaDetalhesParcial(detalheIpca.getId());
								List<ContratoCobrancaDetalhesParcial> detalhesParciais = detalheIpca
										.getListContratoCobrancaDetalhesParcial();

								for (ContratoCobrancaDetalhesParcial parcial : detalhesParciais) {
									vlrPago = vlrPago.add(CommonsUtil.bigDecimalValue(parcial.getVlrRecebido()));
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
							}

							detalheIpca.setVlrSaldoParcela(vlrSaldoParcela.setScale(2, BigDecimal.ROUND_HALF_EVEN));

							if (parcela.getValorParcela().compareTo(BigDecimal.ZERO) == 0) {
								detalheIpca.setParcelaPaga(true);
								detalheIpca.setDataPagamento(detalheIpca.getDataVencimento());
								detalheIpca.setVlrParcela(BigDecimal.ZERO);
							}

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
}