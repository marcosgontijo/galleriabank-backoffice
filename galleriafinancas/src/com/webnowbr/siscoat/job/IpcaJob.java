package com.webnowbr.siscoat.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesParcialDao;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;





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

	

	private void atualizaIPCA() {
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
					ipcaJobCalcular.calcularIPCA(ipcaDao, contratoCobrancaDetalhesDao, contratoCobrancaDao, contratoCobrancaDetalhesParcialDao, parcelaIpca);
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
