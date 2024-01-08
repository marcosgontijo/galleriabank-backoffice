package com.webnowbr.siscoat.job;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesParcialDao;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;
import com.webnowbr.siscoat.cobranca.service.IpcaService;
import com.webnowbr.siscoat.common.CommonsUtil;





public class IpcaJob implements Job {
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(IpcaJob.class);
	// private static final Boolean JOB_ATIVO = false;

	private final  IpcaJobCalcular ipcaJobCalcular; 
	
	/**
	 * Empty constructor for job initilization
	 */
	public IpcaJob() {
		ipcaJobCalcular = new IpcaJobCalcular();
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
	
	/**
	 * mesmo método do IPCAMB atualizaIPCAPorContrato
	 * @param numeroContrato
	 */
	public void atualizaIPCA() {
		try {
			IPCADao ipcaDao = new IPCADao();
			IpcaService ipcaService = new IpcaService();
			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao = new ContratoCobrancaDetalhesParcialDao();
			

			LOGGER.info("incio atualizaIPCAPorContrato");
			LOGGER.info("atualiza o ipca");
			ipcaService.verificaNovoIPCA();
			//buscar somente contratos com parcelas para atualizar
			List<ContratoCobranca> contratosCobranca = contratoCobrancaDao.findAll();

			
			//atualiza o ipca
			
			if (contratosCobranca.size() > 0) {								
				for (ContratoCobranca contratoCobranca : contratosCobranca) {	
					// se não é Hibrido - Corrigido IPCA
					if (!contratoCobranca.isCorrigidoIPCAHibrido()) {
						if (contratoCobranca.isCorrigidoIPCA() && !contratoCobranca.isCorrigidoNovoIPCA()) {
							contratoCobranca.setRecalculaIPCA(true);
							
							for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {
								if (CommonsUtil.mesmoValor(contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe).getNumeroParcela() , "0") )
									continue;
								
								try {
									if (!ipcaJobCalcular.calcularIPCACustom(ipcaDao, contratoCobrancaDetalhesDao, contratoCobrancaDao, contratoCobrancaDetalhesParcialDao, 
													contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe), contratoCobranca))
										break;
								} catch (Exception e) {
									LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
									continue;
								}
							}
							
							contratoCobranca.setRecalculaIPCA(false);
							contratoCobrancaDao.merge(contratoCobranca);
							
							LOGGER.info("[Contrato " + contratoCobranca.getNumeroContrato() + "] IPCA Reprocessado com sucesso!" );
						} else {							
							LOGGER.info("[Contrato " + contratoCobranca.getNumeroContrato() + "] Novo IPCA Reprocessado com sucesso!" );
						}
					} else {
						contratoCobranca.setRecalculaIPCA(true);
						contratoCobranca.setCorrigidoIPCA(true);
						contratoCobranca.setCorrigidoNovoIPCA(false);
						
						Calendar dataCorteParcelasMalucas = Calendar.getInstance();
						
						if (contratoCobranca.getDataCorteBaixaIPCAHibrido() != null) {
							dataCorteParcelasMalucas.setTime(contratoCobranca.getDataCorteBaixaIPCAHibrido());
						} else {
							dataCorteParcelasMalucas.set(Calendar.YEAR, 2023);
							dataCorteParcelasMalucas.set(Calendar.MONTH, 0);
							dataCorteParcelasMalucas.set(Calendar.DAY_OF_MONTH, 1);
						}
							
						for (int iDetalhe = 0; iDetalhe < contratoCobranca.getListContratoCobrancaDetalhes().size(); iDetalhe++) {
							if (CommonsUtil.mesmoValor(contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe).getNumeroParcela() , "0") )
								continue;
							
							try {
								if (!ipcaJobCalcular.calcularIPCACustomMaluco(ipcaDao, contratoCobrancaDetalhesDao, contratoCobrancaDao, contratoCobrancaDetalhesParcialDao, contratoCobranca.getListContratoCobrancaDetalhes().get(iDetalhe), contratoCobranca, dataCorteParcelasMalucas.getTime()))
									break;
							} catch (Exception e) {
								LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
								continue;
							}
						}
						
						contratoCobranca.setRecalculaIPCA(false);
						contratoCobrancaDao.merge(contratoCobranca);
						
						LOGGER.info("[Contrato " + contratoCobranca.getNumeroContrato() + "] IPCA Hibrido Reprocessado com sucesso!" );
					}
				}
			}
			//contratoCobranca = contratoCobrancaDao.findById(contratoCobranca.getId());
				
			LOGGER.info("Fim atualizaIPCAPorContrato");

		} catch (Exception e) {
			LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
		}
	}
	
	private void atualizaIPCAOld() {
		try {
			IPCADao ipcaDao = new IPCADao();
			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao = new ContratoCobrancaDetalhesParcialDao();
			
			List<ContratoCobrancaDetalhes> contratoCobrancaDetalhes = contratoCobrancaDetalhesDao
					.getParcelasCalculoIpca();
			LOGGER.info("incio job");
			for (ContratoCobrancaDetalhes parcelaIpca : contratoCobrancaDetalhes) {
				try {
					ContratoCobranca contratoCobranca = contratoCobrancaDetalhesDao
							.getContratoCobranca(parcelaIpca.getId());
					
					ipcaJobCalcular.calcularIPCA(ipcaDao, contratoCobrancaDetalhesDao, contratoCobrancaDao, contratoCobrancaDetalhesParcialDao, parcelaIpca, contratoCobranca);
				} catch (Exception e) {
					LOGGER.error("IpcaJob.execute " + "atualizaIPCA: EXCEPTION", e);
					continue;
				}
			}

			LOGGER.info("Fim job");

		} catch (Exception e) {
			LOGGER.error("IpcaJob.execute " + "atualizaIPCA: EXCEPTION", e);
		}
	}
}
