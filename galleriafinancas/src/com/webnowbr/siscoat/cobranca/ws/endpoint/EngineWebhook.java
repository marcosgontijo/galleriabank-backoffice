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
import com.webnowbr.siscoat.cobranca.service.SerasaService;
import com.webnowbr.siscoat.cobranca.service.UserService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import io.jsonwebtoken.Jwts;

@Path("/engine")
public class EngineWebhook {

	private static final Log LOGGER = LogFactory.getLog(EngineWebhook.class);

	@POST
	@Path("/webhook/")
	public Response webhookRea(String webhookRetorno, @QueryParam("Token") String token) {
		LOGGER.debug(webhookRetorno);

		try {

			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);

			System.out.println("---------------- Data Engine webhookRetorno ---------------- ");
			System.out.println(webhookRetorno);
			System.out.println("---------------- Data Engine webhookRetorno ---------------- ");
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

				if (!CommonsUtil.semValor(documentoAnalise)) {

					documentoAnalise.setRetornoEngine(webhookRetorno);

					SerasaService serasaService = new SerasaService();
					UserService userService = new UserService();

					engineWebhookRetorno.getConsultaAntecedenteCriminais();

					if ((CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais())
							|| CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais().getResult()
									.get(0).getOnlineCertificates()))
							&& (CommonsUtil.semValor(engineWebhookRetorno.getProcessos()) || CommonsUtil.intValue(
									engineWebhookRetorno.getProcessos().getTotal_acoes_judicias_reu()) == 0)) {
						// libera a consulta do crednet da PF
						if (documentoAnalise.isPodeChamarSerasa()) {
							if (CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
								documentoAnalise.setLiberadoSerasa(true);
								serasaService.requestSerasa(documentoAnalise, userService.userSistema());
							}
						}
					} else {
						if (!CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais().getResult())
								&& !CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais()
										.getResult().get(0).getOnlineCertificates())) {
							documentoAnalise.addObservacao("Possui antecedentes criminais");
						}
						if (CommonsUtil.mesmoValor(
								CommonsUtil.intValue(engineWebhookRetorno.getProcessos().getTotal_acoes_judicias_reu()),
								0)) {
							documentoAnalise.addObservacao("Possui Processos");
						}
					}

					documentoAnaliseDao.merge(documentoAnalise);
				}
			}

			return Response.status(200).entity("Processado").build();
		} catch (io.jsonwebtoken.ExpiredJwtException eJwt) {
			eJwt.printStackTrace();
			return Response.status(500).entity("Token Expirado").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Erro interno").build();
		}
	}
}
