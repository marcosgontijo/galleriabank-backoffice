package com.webnowbr.siscoat.job;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.shaded.owasp.esapi.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;

public class IpcaJob implements Job {
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(IpcaJob.class);
	// private static final Boolean JOB_ATIVO = false;

	/**
	 * Empty constructor for job initilization
	 */
	public IpcaJob() {
		LOGGER.debug("IpcaJob: NEW INSTANCE");
	}

	/**
	 * Dispara calculo de dias. Acionado pelo
	 * <code>{@link org.quartz.Scheduler}</code> conforme configuração em
	 * <code>quartz_data.xml</code>.
	 * 
	 * @throws JobExecutionException Exceção na execução da tarefa
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobKey = "";
		if (context != null) {
			jobKey = "" + context.getJobDetail().getKey();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("IpcaJob.execute: jobKey=" + jobKey + " disparado pelo trigger ["
						+ context.getTrigger().getKey() + "]");
			}
		}

		try {
			atualizaIPCA();
		} catch (Exception e) {
			LOGGER.error("IpcaJob.execute (jobKey=" + jobKey + "): EXCEPTION", e);
		}
	}

	private void atualizaIPCA() {
		try {
			IPCADao ipcaDao = new IPCADao();
			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

			List<ContratoCobrancaDetalhes> contratoCobrancaDetalhes = contratoCobrancaDetalhesDao
					.getParcelasCalculoIpca();
			LOGGER.info("incio job");
			for (ContratoCobrancaDetalhes parcelaIpca : contratoCobrancaDetalhes) {
				try {
					calcularIPCA(ipcaDao, contratoCobrancaDetalhesDao, contratoCobrancaDao, parcelaIpca);
				}catch (Exception e) {
					LOGGER.error("IpcaJob.execute " + "atualizaIPCA: EXCEPTION", e);
					continue;
				}
			}

			LOGGER.info("Fim job");

		} catch (Exception e) {
			LOGGER.error("IpcaJob.execute " + "atualizaIPCA: EXCEPTION", e);
		}
	}

	private boolean calcularIPCA(IPCADao ipcaDao, ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao,
			ContratoCobrancaDao contratoCobrancaDao, ContratoCobrancaDetalhes contratoCobrancaDetalhes) {

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
			BigDecimal saldoDevedorAnterior = BigDecimal.ZERO;
			
			for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {

				ContratoCobrancaDetalhes detalhe = contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe);

				if (CommonsUtil.mesmoValor(contratoCobrancaDetalhes.getId(), detalhe.getId())) {
					// reparcela
					BigDecimal valorIpca = (saldoDevedor
							.multiply(ultimoIpca.getTaxa().divide(BigDecimal.valueOf(100))));

					BigDecimal saldoDevedorIpca = saldoDevedor.add(valorIpca).setScale(2, BigDecimal.ROUND_HALF_EVEN);
					
					saldoDevedorAnterior = saldoDevedorIpca;

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
							parcelaSimuladorReal = CommonsUtil.intValue(parcela.getNumeroParcela())
									+ CommonsUtil.intValue(detalhe.getNumeroParcela());
						
						final int parcelaProcura = parcelaSimuladorReal;
						ContratoCobrancaDetalhes detalheIpca = contratoCobranca.getListContratoCobrancaDetalhes().stream().filter(x -> CommonsUtil.mesmoValor( CommonsUtil.intValue( x.getNumeroParcela()) ,parcelaProcura)).findFirst().orElse(null);
						
//						for (ContratoCobrancaDetalhes detalheIpca : contratoCobranca
//								.getListContratoCobrancaDetalhes()) {

							
							if (detalheIpca.getVlrParcelaOriginal() == null)
								detalheIpca.setVlrParcelaOriginal(detalheIpca.getVlrParcela());

							if (CommonsUtil.mesmoValor(parcelaSimuladorReal.toString(),
									detalheIpca.getNumeroParcela())) {
								if (detalheIpca.isParcelaPaga()) {
									continue;
								}
							
								detalheIpca.setIpcaAtualizou(ultimoIpca);
								detalheIpca.setVlrRecebido(BigDecimal.valueOf(parcelaSimuladorReal.longValue()));

								detalheIpca.setVlrSaldoParcela(
										parcela.getSaldoDevedorInicial().setScale(2, BigDecimal.ROUND_HALF_EVEN));

								detalheIpca.setVlrParcela(
										parcela.getValorParcela().setScale(2, BigDecimal.ROUND_HALF_EVEN));
								detalheIpca
										.setVlrJurosParcela(parcela.getJuros().setScale(2, BigDecimal.ROUND_HALF_EVEN));
								detalheIpca.setVlrAmortizacaoParcela(
										parcela.getAmortizacao().setScale(2, BigDecimal.ROUND_HALF_EVEN));
								// nao precisa pois é pelo valor do imovel, nao muda
								// detalheIpca.setSeguroDFI(parcela.getSeguroDFI());
								if (CommonsUtil.mesmoValor(parcelaSimuladorReal,
										contratoCobranca.getMesesCarencia() + 1)) {
									detalheIpca.setSeguroMIP(parcela.getSeguroMIP().add(valorSeguroMIPCarencia));
								}else {
									detalheIpca.setSeguroMIP(parcela.getSeguroMIP());
								}
								
								
								if (parcela.getValorParcela().compareTo(BigDecimal.ZERO) == 0) {
									detalheIpca.setParcelaPaga(true);
									detalheIpca.setDataPagamento(detalheIpca.getDataVencimento());
									detalheIpca.setVlrParcela(BigDecimal.ZERO);
								}

								if (DateUtil.isAfterDate(detalheIpca.getDataVencimento(), DateUtil.getDataHoje())
										&& !detalheIpca.isParcelaPaga()) {
									detalheIpca.setParcelaVencida(true);
								} else
									detalheIpca.setParcelaVencida(false);

								if (DateUtil.isDataHoje(detalhe.getDataVencimento()) && !detalheIpca.isParcelaPaga()) {
									detalheIpca.setParcelaVencendo(true);
								} else
									detalheIpca.setParcelaVencendo(false);
								

								detalheIpca.setVlrSaldoInicial(saldoDevedorAnterior);
								saldoDevedorAnterior = detalheIpca.getVlrSaldoParcela();
							}

							
							detalheIpca
									.setIpca(detalheIpca.getVlrParcela().subtract(detalheIpca.getVlrParcelaOriginal()));
							
						
					}

					break;
				}

				detalhe.setVlrSaldoInicial(saldoDevedorAnterior);
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
