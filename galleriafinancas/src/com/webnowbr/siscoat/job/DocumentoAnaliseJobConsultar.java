package com.webnowbr.siscoat.job;

import java.util.List;
import java.util.stream.Collectors;

import org.primefaces.PrimeFaces;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DataEngine;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.DocketService;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.cobranca.service.ScrService;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.netrin.cliente.model.PPE.PpeResponse;

public class DocumentoAnaliseJobConsultar {
	
	public List<DocumentoAnalise> listaDocumentoAnalise;
	public User user;
	public ContratoCobranca objetoContratoCobranca;

	@SuppressWarnings("deprecation")
	public void executarConsultasAnaliseDocumento() {

		DocketService docketService = new DocketService();

		SerasaService serasaService = new SerasaService();

		NetrinService netrinService = new NetrinService();

		ScrService scrService = new ScrService();

		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();

		for (DocumentoAnalise documentoAnalise : this.listaDocumentoAnalise.stream().filter(d -> d.isLiberadoAnalise()
				|| d.isLiberadoSerasa() || d.isLiberadoCenprot() || d.isLiberadoScr() || d.isLiberadoProcesso())
				.collect(Collectors.toList())) {

			if (documentoAnalise.isLiberadoAnalise()) {

				EngineRetorno engineRetorno = null;
				if (DocumentosAnaliseEnum.REA.equals(documentoAnalise.getTipoEnum())) {
					if (documentoAnalise.isPodeChamarRea()) {
						documentoAnalise.addObservacao("Processando REA");
						PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
						docketService.uploadREA(documentoAnalise, user);
					}
					continue;
				} else {
					
					
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
						
						DataEngine engine = docketService.engineInserirPessoa(documentoAnalise.getPagador(),
								objetoContratoCobranca);
						docketService.engineCriarConsulta(documentoAnalise, engine, user);
						
						
						engineRetorno = GsonUtil.fromJson(documentoAnalise.getRetornoEngine(), EngineRetorno.class);
						
					} else if (documentoAnalise.isEngineProcessado()) {
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
									|| !CommonsUtil.semValor(engineRetorno.getConsultaAntecedenteCriminais().getResult()
											.get(0).getOnlineCertificates()))
									&& (!CommonsUtil.semValor(engineRetorno.getProcessos()) || CommonsUtil.intValue(
											engineRetorno.getProcessos().getTotal_acoes_judicias_reu()) == 0))) {

						documentoAnalise.addObservacao("Verificar Engine");
						documentoAnaliseDao.merge(documentoAnalise);
						PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
						continue;
					}
				} else {
					documentoAnalise.addObservacao("Aguardando retorno Engine");
					documentoAnaliseDao.merge(documentoAnalise);
					PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
					continue;
				}

//				if (!documentoAnalise.isProcessoProcessado()) {
//					documentoAnalise.addObservacao("Processando Processos");
//					PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
//					netrinService.requestProcesso(documentoAnalise);
//
//					PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
//					pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
//							DocumentosAnaliseEnum.PROCESSO, documentoAnalise.getRetornoProcesso());
//					
//				}

				if (CommonsUtil.semValor(documentoAnalise.getRetornoCenprot())) {
					documentoAnalise.addObservacao("Processando Protestos");
					PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
					netrinService.requestCenprot(documentoAnalise);
					
					PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
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
				

				if (CommonsUtil.semValor(documentoAnalise.getRetornoScr())) {
					documentoAnalise.addObservacao("Processando SCR");
					PrimeFaces.current().ajax().update("form:ArquivosSalvosAnalise");
					scrService.requestScr(documentoAnalise);
				}
				documentoAnalise.addObservacao("Pesquisas finalizadas");
				documentoAnaliseDao.merge(documentoAnalise);
				PrimeFaces.current().ajax().update("form:ArquivosAnalisados");
			}
		}
	}

	@Override
	public String toString() {
		return "DocumentoAnaliseJobConsultar [listaDocumentoAnalise=" + listaDocumentoAnalise + ", user=" + user
				+ ", objetoContratoCobranca=" + objetoContratoCobranca + "]";
	}
	
	
}