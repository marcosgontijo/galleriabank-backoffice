package com.webnowbr.siscoat.job;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DataEngine;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.BigDataService;
import com.webnowbr.siscoat.cobranca.service.DocketService;
import com.webnowbr.siscoat.cobranca.service.DocumentoAnaliseService;
import com.webnowbr.siscoat.cobranca.service.EngineService;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.cobranca.service.ScrService;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.cobranca.ws.netrin.NetrinConsulta;
import com.webnowbr.siscoat.cobranca.ws.netrin.NetrinConsultaDao;
import com.webnowbr.siscoat.cobranca.ws.plexi.PlexiService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;

public class CertidoesJobConsultar {
	
	public List<DocumentoAnalise> listaDocumentoAnalise;
	public User user;
	public ContratoCobranca objetoContratoCobranca;
	public String tipoProcesso;
	
	private int stepTotal;	
	private int step;
	private String stepDescricao;
	
	public void atualizarConsultasCertidoes() {
		PlexiService plexiService = new PlexiService();
		plexiService.atualizaRetorno(listaDocumentoAnalise, user);
		NetrinService netrinService = new NetrinService();
		//netrinService.atualizaRetorno(listaDocumentoAnalise, user);
		DocketService docketService = new DocketService();
		docketService.atualizaRetorno(listaDocumentoAnalise);
	}

	public void executarConsultasCertidoes() {
		executarConsultaNetrin();
	}
	
	public void executarConsultaNetrin() {
		//POST para gerar pedido
		NetrinService netrinService = new NetrinService();
		NetrinConsultaDao netrinConsultaDao = new NetrinConsultaDao();
		User userConsulta = null;
		if(!CommonsUtil.semValor(user)) {
			userConsulta = user;
		}
		
		for(DocumentoAnalise docAnalise : listaDocumentoAnalise) {
			List<NetrinConsulta> consultasExistentes = new ArrayList<NetrinConsulta>();
			List<NetrinConsulta> consultasExistentesDB = new ArrayList<NetrinConsulta>();
			for(NetrinConsulta netrinConsulta : docAnalise.getNetrinConsultas()) {
				List<NetrinConsulta> consultasExistentesRetorno = netrinConsultaDao.getConsultasExistentes(netrinConsulta);
				if(consultasExistentesRetorno.size() > 0) {
					System.out.println("CertidoesJobConsultar WARN: " + 
						netrinConsulta.getNetrinDocumentos().getNome() + 
						" - " + netrinConsulta.getCpfCnpj() + ": Já existente");
					consultasExistentes.add(netrinConsulta);
					consultasExistentesDB.add(consultasExistentesRetorno.get(0));
					continue;
				}
			}
			docAnalise.getNetrinConsultas().removeAll(consultasExistentes);
			List<NetrinConsulta> consultasFalhadas = new ArrayList<NetrinConsulta>();
			for(NetrinConsulta netrinConsulta : docAnalise.getNetrinConsultas()) {
				if(!CommonsUtil.semValor(netrinConsulta.getRetorno())) {
					continue;
				}
				FacesMessage facesMessage = netrinService.pedirConsulta(netrinConsulta, userConsulta);
				if(CommonsUtil.semValor(facesMessage) || CommonsUtil.mesmoValor(facesMessage.getSeverity(), 
						FacesMessage.SEVERITY_ERROR)) {
					consultasFalhadas.add(netrinConsulta);
				}
			}
			docAnalise.getNetrinConsultas().removeAll(consultasFalhadas);
			docAnalise.getNetrinConsultas().addAll(consultasExistentesDB);
			DocumentoAnaliseDao docAnaliseDao = new DocumentoAnaliseDao(); 
			docAnaliseDao.merge(docAnalise);
		}
	}

	@Override
	public String toString() {
		return "CertidoesJobConsultar [listaDocumentoAnalise=" + listaDocumentoAnalise + ", user=" + user
				+ ", objetoContratoCobranca=" + objetoContratoCobranca + "]";
	}

	public int getStepTotal() {
		return stepTotal;
	}

	public void setStepTotal(int stepTotal) {
		this.stepTotal = stepTotal;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getStepDescricao() {
		return stepDescricao;
	}

	public void setStepDescricao(String stepDescricao) {
		this.stepDescricao = stepDescricao;
	}
	
	
	
	
}