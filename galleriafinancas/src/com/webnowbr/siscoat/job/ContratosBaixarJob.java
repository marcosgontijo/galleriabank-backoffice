package com.webnowbr.siscoat.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;

public class ContratosBaixarJob implements Job {
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(IpcaJob.class);
	
	/**
	 * Empty constructor for job initilization
	 */
	public ContratosBaixarJob() {
		LOGGER.debug("ContratosBaixarJob: NEW INSTANCE");
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobKey = "";
		if (context != null) {
			jobKey = "" + context.getJobDetail().getKey();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ContratosBaixarJob.execute: jobKey=" + jobKey + " disparado pelo trigger ["
						+ context.getTrigger().getKey() + "]");
			}
		}

		try {
			setContratosPossiveisParaBaixar();
		} catch (Exception e) {
			LOGGER.error("ContratosBaixarJob.execute (jobKey=" + jobKey + "): EXCEPTION", e);
		}
	}

	private void setContratosPossiveisParaBaixar() {
		try {
			List<ContratoCobranca> ccList = new ArrayList<ContratoCobranca>();
			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			
			ccList = cDao.ConsultaContratosASeremBaixados();
			
			for (ContratoCobranca contrato : ccList) {
				ContratoCobranca cc = cDao.findById(contrato.getId());
				String statusEsteira = cc.getStatusEsteira();
				
				cc.setEsteiraLiberadaParaBaixar(false);
				
				if (CommonsUtil.mesmoValor(statusEsteira, "Laudo + Paju Pendente") ||
					CommonsUtil.mesmoValor(statusEsteira, "Análise Pendente") ||
					CommonsUtil.mesmoValor(statusEsteira, "Análise Pré-Aprovada")) {
					cc.setDataProvavelBaixa(DateUtil.adicionarDias(DateUtil.gerarDataHoje(), 10));
					cc.setEsteiraLiberadaParaBaixar(true);
				}
				
				if (CommonsUtil.mesmoValor(statusEsteira, "Análise Comercial")) {
					cc.setDataProvavelBaixa(DateUtil.adicionarDias(cc.getPajurFavoravelData(), 30));
					cc.setEsteiraLiberadaParaBaixar(true);
				}
				
				if (CommonsUtil.mesmoValor(statusEsteira, "Ag. Ok Cliente")) {
					if(DateUtil.getDifferenceDays(cc.getPajurFavoravelData(), DateUtil.gerarDataHoje()) > 23 && !cc.isAcrescimoProvavelBaixaDataUsado()) {
						cc.setDataProvavelBaixa(DateUtil.adicionarDias(cc.getDataProvavelBaixa(), 7));
						cc.setAcrescimoProvavelBaixaDataUsado(true);
					} 
					cc.setEsteiraLiberadaParaBaixar(true);
				}

				if (CommonsUtil.mesmoValor(statusEsteira, "Ag. DOC") ||
					CommonsUtil.mesmoValor(statusEsteira, "Ag. Certificado") ||
					CommonsUtil.mesmoValor(statusEsteira, "Ag. CCB") ||
					CommonsUtil.mesmoValor(statusEsteira, "Ag. Conferência")) {
					cc.setDataProvavelBaixa(cc.getOkClienteData());
					cc.setEsteiraLiberadaParaBaixar(true);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ContratosBaixarJob.execute " + "baixarContratos: EXCEPTION", e);
		}
	}

}
