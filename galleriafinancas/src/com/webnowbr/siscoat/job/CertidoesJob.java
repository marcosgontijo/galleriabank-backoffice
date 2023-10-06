package com.webnowbr.siscoat.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.infra.db.model.User;

public class CertidoesJob implements Job {
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(CertidoesJob.class);

	private final  CertidoesJobConsultar certidoesJobConsultar; 
	
	/**
	 * Empty constructor for job initilization
	 */
	public CertidoesJob() {
		certidoesJobConsultar = new CertidoesJobConsultar();
		System.out.println();
		System.out.println("CertidoesJob: NEW INSTANCE");
	}

	/**
	 * @throws JobExecutionException Exceção na execução da tarefa
	 */
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobKey = "";
		if (context != null) {
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
	        //fetch parameters from JobDataMap
			List<DocumentoAnalise> listaDocumentoAnalise = (List<DocumentoAnalise>) dataMap.get("listaDocumentoAnalise");
	        User user = (User) dataMap.get("user");
	        ContratoCobranca objetoContratoCobranca = (ContratoCobranca) dataMap.get("objetoContratoCobranca");
	        
	        certidoesJobConsultar.listaDocumentoAnalise = listaDocumentoAnalise;
	        certidoesJobConsultar.user = user;
	        certidoesJobConsultar.objetoContratoCobranca = objetoContratoCobranca;
	        
			jobKey = "" + context.getJobDetail().getKey();
			//if (LOGGER.isDebugEnabled()) {
				System.out.println("CertidoesJob.execute: jobKey=" + jobKey + " disparado pelo trigger ["
						+ context.getTrigger().getKey() + "]");
			//}
		}
		try {
			consultarPesquisas();
		} catch (Exception e) {
			System.out.println("1 parameter value : " + certidoesJobConsultar.listaDocumentoAnalise);
	        System.out.println("2 parameter value : " + certidoesJobConsultar.user);
	        System.out.println("3 parameter value : " + certidoesJobConsultar.objetoContratoCobranca);
			System.out.println("CertidoesJob.execute (jobKey=" + jobKey + "): EXCEPTION" + e + " - " + certidoesJobConsultar);
			e.printStackTrace();
		}
	}

	public void consultarPesquisas() {
		try {
			System.out.println("incio CertidoesJob" + certidoesJobConsultar.objetoContratoCobranca.getNumeroContrato());
			certidoesJobConsultar.executarConsultasCertidoes();
			System.out.println("Fim CertidoesJob - " + certidoesJobConsultar.objetoContratoCobranca.getNumeroContrato());
 		} catch (Exception e) {
			System.out.println("CertidoesJob.execute " + "CertidoesJob: EXCEPTION" +  e.toString() + " - " + certidoesJobConsultar);
			e.printStackTrace();
		}
	}
}
