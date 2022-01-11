package com.webnowbr.siscoat.job;

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
import com.webnowbr.siscoat.common.CommonsUtil;

public class IpcaJobContrato implements Job {
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(IpcaJobContrato.class);
	// private static final Boolean JOB_ATIVO = false;

	private final IpcaJobCalcular ipcaJobCalcular; 
	
	/**
	 * Empty constructor for job initilization
	 */
	public IpcaJobContrato() {
		ipcaJobCalcular = new IpcaJobCalcular();

		LOGGER.debug("IpcaJobContrato: NEW INSTANCE");
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
				LOGGER.debug("IpcaJobContrato.execute: jobKey=" + jobKey + " disparado pelo trigger ["
						+ context.getTrigger().getKey() + "]");
			}
		}

		try {
			atualizaIPCAInicioContrato();
		} catch (Exception e) {
			LOGGER.error("IpcaJobContrato.execute (jobKey=" + jobKey + "): EXCEPTION", e);
		}
	}
	
	private void atualizaIPCAInicioContrato() {
		try {
			IPCADao ipcaDao = new IPCADao();
			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			ContratoCobrancaDetalhesParcialDao contratoCobrancaDetalhesParcialDao = new ContratoCobrancaDetalhesParcialDao();

			List<ContratoCobranca> contratosCobranca = contratoCobrancaDao.getContratosCalculoIpca();
			LOGGER.info("incio job atualizaIPCAInicioContrato");
			for (ContratoCobranca contratoCobranca : contratosCobranca) {
				for (ContratoCobrancaDetalhes parcelaIpca : contratoCobranca.getListContratoCobrancaDetalhes()) {
					if (CommonsUtil.mesmoValor( parcelaIpca.getNumeroParcela() , "0") )
						continue;
					
					try {
						if (!ipcaJobCalcular.calcularIPCA(ipcaDao, contratoCobrancaDetalhesDao, contratoCobrancaDao, contratoCobrancaDetalhesParcialDao, parcelaIpca, contratoCobranca))
							break;
					} catch (Exception e) {
						LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
						continue;
					}
				}
				//contratoCobranca = contratoCobrancaDao.findById(contratoCobranca.getId());
				contratoCobranca.setRecalculaIPCA(false);
				contratoCobrancaDao.merge(contratoCobranca);
			}
			LOGGER.info("Fim job atualizaIPCAInicioContrato");

		} catch (Exception e) {
			LOGGER.error("IpcaJobContrato.execute " + "atualizaIPCAInicioContrato: EXCEPTION", e);
		}
	}
	
}