package com.webnowbr.siscoat.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;

public class PajuLuvisonJob implements Job {
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(PajuLuvisonJob.class);
	// private static final Boolean JOB_ATIVO = false;

	private final  PajuLuvisonJobConsultar luvisonJobConsultar; 
	
	/**
	 * Empty constructor for job initilization
	 */
	public PajuLuvisonJob() {
		luvisonJobConsultar = new PajuLuvisonJobConsultar();
		System.out.println();
		System.out.println("LuvisonJob: NEW INSTANCE");
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
				System.out.println("LuvisonJob.execute: jobKey=" + jobKey + " disparado pelo trigger ["
						+ context.getTrigger().getKey() + "]");
			}
		}

		try {
			//consultarPajus();
		} catch (Exception e) {
			System.out.println("LuvisonJob.execute (jobKey=" + jobKey + "): EXCEPTION" + e);
		}
	}
	
	/**
	 * mesmo método do IPCAMB atualizaIPCAPorContrato
	 * @param numeroContrato
	 */
	public void consultarPajus() {
		try {
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

			System.out.println("incio ConsultaPajuLuvison");
			
			int qtdeContratosLuvison = luvisonJobConsultar.buscarContratosPajuLuvison(contratoCobrancaDao);
			System.out.println(qtdeContratosLuvison);
			if(qtdeContratosLuvison <= 5) {
				luvisonJobConsultar.enviarMensagem(qtdeContratosLuvison);
			}
			
			System.out.println("Fim ConsultaPajuLuvison");

		} catch (Exception e) {
			System.out.println("LuvisonJob.execute " + "ConsultaPajuLuvison: EXCEPTION" +  e.toString());
		}
	}
}
