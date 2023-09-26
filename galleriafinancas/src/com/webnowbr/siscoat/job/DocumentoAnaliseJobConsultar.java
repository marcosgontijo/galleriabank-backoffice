package com.webnowbr.siscoat.job;

import java.util.List;
import java.util.stream.Collectors;

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
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;

public class DocumentoAnaliseJobConsultar {
	
	public List<DocumentoAnalise> listaDocumentoAnalise;
	public User user;
	public ContratoCobranca objetoContratoCobranca;
	
	private int stepTotal;	
	private int step;
	private String stepDescricao;

	@SuppressWarnings("deprecation")
	public void executarConsultasAnaliseDocumento() {

		EngineService engineService = new EngineService();

		SerasaService serasaService = new SerasaService();

		NetrinService netrinService = new NetrinService();
		BigDataService bigDataService = new BigDataService();

		ScrService scrService = new ScrService();
		
		DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();
		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();

		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		step = 0 ;
		for (DocumentoAnalise documentoAnalise : this.listaDocumentoAnalise.stream().filter(d -> d.isLiberadoAnalise()
				|| d.isLiberadoSerasa() || d.isLiberadoCenprot() || d.isLiberadoScr() || d.isLiberadoProcesso())
				.collect(Collectors.toList())) {

			if (documentoAnalise.isLiberadoAnalise()) {

				EngineRetorno engineRetorno = null;
				if (DocumentosAnaliseEnum.REA.equals(documentoAnalise.getTipoEnum())) {
					stepTotal= 1;
					if (documentoAnalise.isPodeChamarRea()) {
						documentoAnalise.addObservacao("Processando REA");
						//PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
						engineService.uploadREA(documentoAnalise, user);
						step ++;
					}
					continue;
				} else {
					
					stepTotal= 3;
					
//					if (documentoAnalise.isPodeChamarEngine()
//							&& CommonsUtil.semValor(documentoAnalise.getRetornoEngine())) {
//
//						documentoAnalise.addObservacao("Processando Engine");
//						PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
//						DataEngine engine = docketService.engineInserirPessoa(documentoAnalise.getPagador(),
//								objetoContratoCobranca);
//						docketService.engineCriarConsulta(documentoAnalise, engine, loginBean.getUsuarioLogado());
//					}
//					
					
					if (documentoAnalise.isPodeChamarEngine() && !documentoAnalise.isEngineProcessado()) {
						
						DataEngine engine = engineService.engineInserirPessoa(documentoAnalise.getPagador(),
								objetoContratoCobranca);
						step ++;
						stepDescricao= "";
						engineService.engineCriarConsulta(documentoAnalise, engine, user);
						
						
						if(!CommonsUtil.semValor(documentoAnalise.getRetornoEngine())) {
							if (documentoAnalise.getRetornoEngine().startsWith("consulta efetuada anteriormente Id: ") ) {
								engineService.salvarDetalheDocumentoEngine(documentoAnalise);
								
								engineRetorno = GsonUtil.fromJson(documentoAnalise.getRetornoEngine(), EngineRetorno.class);
								if (CommonsUtil.semValor(engineRetorno.getIdCallManager())) {
									engineRetorno.setIdCallManager(engineRetorno.getIdCallManager());
								}
								engineService.processaWebHookEngine( documentoAnaliseService, engineRetorno,
										pagadorRecebedorService, documentoAnaliseDao, documentoAnalise);
							}
						}
						
						engineRetorno = GsonUtil.fromJson(documentoAnalise.getRetornoEngine(), EngineRetorno.class);
						
					} else if (documentoAnalise.isEngineProcessado()) {
						if(!CommonsUtil.semValor(documentoAnalise.getRetornoEngine())) {
							if (documentoAnalise.getRetornoEngine().startsWith("consulta efetuada anteriormente Id: ") ) {
								engineService.salvarDetalheDocumentoEngine(documentoAnalise);
								
								engineRetorno = GsonUtil.fromJson(documentoAnalise.getRetornoEngine(), EngineRetorno.class);
								if (CommonsUtil.semValor(engineRetorno.getIdCallManager())) {
									engineRetorno.setIdCallManager(engineRetorno.getIdCallManager());
								}
								engineService.processaWebHookEngine( documentoAnaliseService, engineRetorno,
										pagadorRecebedorService, documentoAnaliseDao, documentoAnalise);
							}
						}
						engineRetorno = GsonUtil.fromJson(documentoAnalise.getRetornoEngine(), EngineRetorno.class);
					}
				}

//				if (CommonsUtil.semValor(engineRetorno)) {
//					documentoAnalise.addObservacao("Aguardando retorno Engine");
//					documentoAnaliseDao.merge(documentoAnalise);
//					PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
//					continue;
//				}
				
				if (!CommonsUtil.semValor(engineRetorno)) {
					if (!CommonsUtil.booleanValue(documentoAnalise.isLiberadoContinuarAnalise())
							&& (!(CommonsUtil.semValor(engineRetorno.getConsultaAntecedenteCriminais())
									|| ( !CommonsUtil.semValor(engineRetorno.getConsultaAntecedenteCriminais().getResult())  && !CommonsUtil.semValor(engineRetorno.getConsultaAntecedenteCriminais().getResult()
											.get(0).getOnlineCertificates())))
									&& (!CommonsUtil.semValor(engineRetorno.getProcessos()) && CommonsUtil.intValue(
											engineRetorno.getProcessos().getTotal_acoes_judicias_reu()) == 0))) {

						documentoAnalise.addObservacao("Verificar Engine");
						documentoAnaliseDao.merge(documentoAnalise);
						//PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
						continue;
					}
				} else if( documentoAnalise.isPodeChamarEngine()) {
					documentoAnalise.addObservacao("Aguardando retorno Engine");
					documentoAnaliseDao.merge(documentoAnalise);
					//PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
					continue;
				}

				if (documentoAnalise.isPodeChamarProcesso()) {
					documentoAnalise.addObservacao("Processando Processos");
//					PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
					bigDataService.requestProcesso(documentoAnalise);

//					PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
					pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
							DocumentosAnaliseEnum.PROCESSOB, documentoAnalise.getRetornoProcesso());
					
				}

				if (documentoAnalise.isPodeChamarCenprot()) {
					documentoAnalise.addObservacao("Processando Protestos");
					//PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
					step ++;
					stepDescricao= "Processando Protestos";
					netrinService.requestCenprot(documentoAnalise);
					
//					PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
					pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
							DocumentosAnaliseEnum.CENPROT, documentoAnalise.getRetornoCenprot());
					
				}

//				if (documentoAnalise.isPodeChamarSerasa()
//						&& CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
//					documentoAnalise.addObservacao("Processando SERASA");
//					PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
//					serasaService.requestSerasa(documentoAnalise, loginBean.getUsuarioLogado());
//				}

//				if (documentoAnalise.isPodeChamarPpe() && CommonsUtil.semValor(documentoAnalise.getRetornoPpe())) {
//					documentoAnalise.addObservacao("Processando PEP");
//					PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
//					netrinService.requestCadastroPepPF(documentoAnalise);
//				}
				

				if (documentoAnalise.isPodeChamarSCR()) {
					documentoAnalise.addObservacao("Processando SCR");
					//PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
					step ++;
					stepDescricao= "Processando SCR";
					scrService.requestScr(documentoAnalise);
				}
				
				documentoAnalise.addObservacao("Pesquisas finalizadas");
				documentoAnaliseDao.merge(documentoAnalise);
				//PrimeFaces.current().ajax().update("form:ArquivosAnalisados");
			}
		}
	}

	@Override
	public String toString() {
		return "DocumentoAnaliseJobConsultar [listaDocumentoAnalise=" + listaDocumentoAnalise + ", user=" + user
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