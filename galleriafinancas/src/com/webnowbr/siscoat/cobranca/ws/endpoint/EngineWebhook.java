package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.DataEngine;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.op.DataEngineDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.DocketService;
import com.webnowbr.siscoat.cobranca.service.DocumentoAnaliseService;
import com.webnowbr.siscoat.cobranca.service.NetrinService;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.cobranca.service.ScrService;
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.cobranca.service.UserService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultRelacionamentosPessoaisPJ;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoRequestEnterprisePartnership;
import io.jsonwebtoken.Jwts;

@Path("/engine")
public class EngineWebhook {

	private static final Log LOGGER = LogFactory.getLog(EngineWebhook.class);

	@POST
	@Path("/webhook/")
	public Response webhookEngine(String webhookRetorno, @QueryParam("Token") String token) {
//		LOGGER.debug(webhookRetorno);

		try {

			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);

			DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();

			
			EngineRetorno engineWebhookRetorno = GsonUtil.fromJson(webhookRetorno, EngineRetorno.class);

			DataEngineDao dataEngineDao = new DataEngineDao();
			List<DataEngine> engines = dataEngineDao.findByFilter("idCallManager",
					engineWebhookRetorno.getIdCallManager());

			DataEngine dataEngine = null;
			if (engines.size() > 0) {
				dataEngine = engines.get(0);

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();

				DocumentoAnalise documentoAnalise = documentoAnaliseDao.findByFilter("engine", dataEngine).stream()
						.findFirst().orElse(null);
				
				PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
				pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(dataEngine.getPagador(),
						DocumentosAnaliseEnum.ENGINE, webhookRetorno);
				

				if (!CommonsUtil.semValor(documentoAnalise)) {

					documentoAnalise.setRetornoEngine(webhookRetorno);
					
					SerasaService serasaService = new SerasaService();
					NetrinService netrinService = new NetrinService();
					UserService userService = new UserService();
					ScrService scrService = new ScrService();
					DocketService docketService = new DocketService();

					
					User userSistema = userService.userSistema();
					
					engineWebhookRetorno.getConsultaAntecedenteCriminais();

					if ((CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais())
							|| CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais().getResult()
									.get(0).getOnlineCertificates()))
							&& (CommonsUtil.semValor(engineWebhookRetorno.getProcessos()) || CommonsUtil.intValue(
									engineWebhookRetorno.getProcessos().getTotal_acoes_judicias_reu()) == 0)) {
						// libera a consulta do crednet da PF
//						if (documentoAnalise.isPodeChamarSerasa()) {
//							if (CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
//								documentoAnalise.setLiberadoSerasa(true);
//								serasaService.requestSerasa(documentoAnalise, userService.userSistema());
//							}
//						}
						if (documentoAnalise.isPodeChamarCenprot()) {
							if (CommonsUtil.semValor(documentoAnalise.getRetornoCenprot())) {
								documentoAnalise.setLiberadoCenprot(true);
								netrinService.requestCenprot(documentoAnalise);
							}
						}

//						if (documentoAnalise.isPodeChamarSCR()) {
//							if (CommonsUtil.semValor(documentoAnalise.getRetornoScr())) {
//								documentoAnalise.setLiberadoScr(true);
//								scrService.requestScr(documentoAnalise);
//							}
//						}
//
//						DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();
//						documentoAnaliseService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
//								DocumentosAnaliseEnum.ENGINE, webhookRetorno);

					} else {
						if (!CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais().getResult())
								&& !CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais()
										.getResult().get(0).getOnlineCertificates())
								&& !CommonsUtil.mesmoValorIgnoreCase("NADA CONSTA",
										engineWebhookRetorno.getConsultaAntecedenteCriminais().getResult().get(0)
												.getOnlineCertificates().get(0).getBaseStatus())) {
							documentoAnalise.addObservacao("Possui antecedentes criminais");
						}
						if (!CommonsUtil.semValor(engineWebhookRetorno.getProcessos())
							&& !CommonsUtil.semValor(engineWebhookRetorno.getProcessos().getTotal_acoes_judicias_reu())
							&& CommonsUtil.mesmoValor(CommonsUtil.intValue(
									engineWebhookRetorno.getProcessos().getTotal_acoes_judicias_reu()),0)) {
							documentoAnalise.addObservacao("Possui Processos");
						}
					}

					documentoAnaliseDao.merge(documentoAnalise);
					

					String motivo = "Empresa Vinculada ao Proprietario Atual";
					if (!CommonsUtil.mesmoValor(documentoAnalise.getMotivoAnalise().toUpperCase(),
							"PROPRIETARIO ATUAL"))
						motivo = "Empresa Vinculada ao Proprietario Anterior";
					
					if (!CommonsUtil
							.semValor(engineWebhookRetorno.getConsultaCompleta().getEnterpriseData().getPartnership())
							&& !CommonsUtil.semValor(engineWebhookRetorno.getConsultaCompleta().getEnterpriseData()
									.getPartnership().getPartnerships())) {

						for (EngineRetornoRequestEnterprisePartnership partnership : engineWebhookRetorno
								.getConsultaCompleta().getEnterpriseData().getPartnership().getPartnerships()) {

							documentoAnaliseService.cadastrarPessoRetornoEngine(partnership, userSistema,
									documentoAnaliseDao, pagadorRecebedorService,
									documentoAnalise.getContratoCobranca(), motivo);

						}
					}

					if (!CommonsUtil.semValor(engineWebhookRetorno.getRelacionamentosPessoaisPJ())
							&& !CommonsUtil.semValor(engineWebhookRetorno.getRelacionamentosPessoaisPJ().getResult())) {

						for (EngineRetornoExecutionResultRelacionamentosPessoaisPJ engineRetornoExecutionResultRelacionamentosPessoaisPJ : engineWebhookRetorno
								.getRelacionamentosPessoaisPJ().getResult()) {

							if (!CommonsUtil.semValor(
									engineRetornoExecutionResultRelacionamentosPessoaisPJ.getRelationships())) {

								if (!CommonsUtil.semValor(engineRetornoExecutionResultRelacionamentosPessoaisPJ
										.getRelationships().getRelationships()))
									for (EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership : engineRetornoExecutionResultRelacionamentosPessoaisPJ
											.getRelationships().getRelationships()) {
										documentoAnaliseService.cadastrarPessoRetornoEngine(
												engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership,
												userSistema, documentoAnaliseDao, pagadorRecebedorService,
												documentoAnalise.getContratoCobranca(), motivo);
									}

								if (!CommonsUtil.semValor(engineRetornoExecutionResultRelacionamentosPessoaisPJ
										.getRelationships().getCurrentRelationships()))
									for (EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership : engineRetornoExecutionResultRelacionamentosPessoaisPJ
											.getRelationships().getCurrentRelationships()) {
										documentoAnaliseService.cadastrarPessoRetornoEngine(
												engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership,
												userSistema, documentoAnaliseDao, pagadorRecebedorService,
												documentoAnalise.getContratoCobranca(), motivo);
									}

								if (!CommonsUtil.semValor(engineRetornoExecutionResultRelacionamentosPessoaisPJ
										.getRelationships().getHistoricalRelationships()))
									for (EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership : engineRetornoExecutionResultRelacionamentosPessoaisPJ
											.getRelationships().getHistoricalRelationships()) {
										documentoAnaliseService.cadastrarPessoRetornoEngine(
												engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership,
												userSistema, documentoAnaliseDao, pagadorRecebedorService,
												documentoAnalise.getContratoCobranca(), motivo);
									}

							}
						}

					}
					
				}
			}
			return Response.status(200).entity("Processado").build();
		} catch (io.jsonwebtoken.ExpiredJwtException eJwt) {
			eJwt.printStackTrace();
			return Response.status(500).entity("Token Expirado").build();
		} catch (Exception e) {
			System.out.println("---------------- Data Engine webhookRetorno ---------------- ");
			System.out.println(webhookRetorno);
			System.out.println("---------------- Data Engine webhookRetorno ---------------- ");
			e.printStackTrace();
			return Response.status(500).entity("Erro interno").build();
		}
	}
}