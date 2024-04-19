package com.webnowbr.siscoat.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.DateUtil;

public class ContratosBaixaroAutomaticoJob implements Job {
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(IpcaJob.class);

	/**
	 * Empty constructor for job initilization
	 */
	public ContratosBaixaroAutomaticoJob() {
		LOGGER.debug("ContratosBaixarJob: NEW INSTANCE");
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobKey = "";
		if (context != null) {
			jobKey = "" + context.getJobDetail().getKey();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ContratosBaixaroAutomaticoJob.execute: jobKey=" + jobKey + " disparado pelo trigger ["
						+ context.getTrigger().getKey() + "]");
			}
		}

		try {
			setContratoBaixado();
		} catch (Exception e) {
			LOGGER.error("ContratosBaixaroAutomaticoJob.execute (jobKey=" + jobKey + "): EXCEPTION", e);
		}
	}

	private void setContratoBaixado() {

		List<ContratoCobranca> ccList = new ArrayList<ContratoCobranca>();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		Date hoje = DateUtil.gerarDataHoje();

		ccList = cDao.ConsultaContratosASeremBaixados();

		for (ContratoCobranca contrato : ccList) {
			try {
				ContratoCobranca cc = cDao.findById(contrato.getId());

				if (!cc.isEsteiraLiberadaParaBaixar())
					continue;

				if (cc.isOkCliente() && !cc.isAcrescimoProvavelBaixaDataUsado()
						&& DateUtil.getDifferenceDays(cc.getPajurFavoravelData(), DateUtil.gerarDataHoje()) > 23) {
					cc.setDataProvavelBaixa(DateUtil.adicionarDias(cc.getDataProvavelBaixa(), 7));
					cc.setAcrescimoProvavelBaixaDataUsado(true);
				}

				if (hoje.compareTo(cc.getDataProvavelBaixa()) > 0) {
					cc.setStatus("Baixado");
				}
			} catch (Exception e) {
				LOGGER.error("ContratosBaixaroAutomaticoJob.execute " + "baixarContratos: EXCEPTION", e);
			}
		}
	}

}
