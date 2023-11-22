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
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.infra.db.model.User;

public class DocumentoAnaliseJob implements Job {
	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(DocumentoAnaliseJob.class);

	private final DocumentoAnaliseJobConsultar documentoAnaliseJobConsultar;

	/**
	 * Empty constructor for job initilization
	 */
	public DocumentoAnaliseJob() {
		documentoAnaliseJobConsultar = new DocumentoAnaliseJobConsultar();
		System.out.println();
		System.out.println("DocumentoAnaliseJob: NEW INSTANCE");
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
	        
	        String urlWenhook =  (String) dataMap.get("urlWenhook");
	        
	        ContratoCobranca objetoContratoCobranca = (ContratoCobranca) dataMap.get("objetoContratoCobranca");
	        
	        documentoAnaliseJobConsultar.listaDocumentoAnalise = listaDocumentoAnalise;
	        documentoAnaliseJobConsultar.user = user;
	        documentoAnaliseJobConsultar.urlWenhook = urlWenhook;
	        documentoAnaliseJobConsultar.objetoContratoCobranca = objetoContratoCobranca;
	        
			jobKey = "" + context.getJobDetail().getKey();
			if (LOGGER.isDebugEnabled()) {
				System.out.println("DocumentoAnaliseJob.execute: jobKey=" + jobKey + " disparado pelo trigger ["
						+ context.getTrigger().getKey() + "]");
			}
		}
		try {
			consultarPesquisas();
		} catch (Exception e) {
			System.out.println("1 parameter value : " + documentoAnaliseJobConsultar.listaDocumentoAnalise);
			System.out.println("2 parameter value : " + documentoAnaliseJobConsultar.user);
			System.out.println("3 parameter value : " + documentoAnaliseJobConsultar.objetoContratoCobranca);
			System.out.println("DocumentoAnaliseJob.execute (jobKey=" + jobKey + "): EXCEPTION" + e + " - "
					+ documentoAnaliseJobConsultar);
			e.printStackTrace();
		}
	}

	public void consultarPesquisas() {
		try {
			System.out.println("incio DocumentoAnaliseJob"
					+ documentoAnaliseJobConsultar.objetoContratoCobranca.getNumeroContrato());
			documentoAnaliseJobConsultar.executarConsultasAnaliseDocumento();
			System.out.println("Fim DocumentoAnaliseJob - "
					+ documentoAnaliseJobConsultar.objetoContratoCobranca.getNumeroContrato());
		} catch (Exception e) {
			System.out.println("DocumentoAnaliseJob.execute " + "DocumentoAnaliseJob: EXCEPTION" + e.toString() + " - "
					+ documentoAnaliseJobConsultar);
			e.printStackTrace();
		}
	}
}
