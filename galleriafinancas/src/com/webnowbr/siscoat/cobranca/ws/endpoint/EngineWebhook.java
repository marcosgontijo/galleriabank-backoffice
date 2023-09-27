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
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DataEngineDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.DocumentoAnaliseService;
import com.webnowbr.siscoat.cobranca.service.EngineService;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;

import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.jwt.common.JwtUtil;

@Path("/engine")
public class EngineWebhook {

	private static final Log LOGGER = LogFactory.getLog(EngineWebhook.class);

	@POST
	@Path("/webhook/")
	public Response webhookEngine(String webhookRetorno, @QueryParam("Token") String token) {
//		LOGGER.debug(webhookRetorno);

		try {
			if (JwtUtil.isTokenExpiredWebhook(token))
				return null;
//			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);

			DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();

			
			/*
			 * System.out.
			 * println("---------------- Data Engine webhookRetorno ---------------- ");
			 * System.out.println(webhookRetorno); System.out.
			 * println("---------------- Data Engine webhookRetorno ---------------- ");
			 */
			EngineRetorno engineWebhookRetorno = GsonUtil.fromJson(webhookRetorno, EngineRetorno.class);

			DataEngineDao dataEngineDao = new DataEngineDao();
			List<DataEngine> engines = dataEngineDao.findByFilter("idCallManager",
					engineWebhookRetorno.getIdCallManager());

			DataEngine dataEngine = null;
			if (engines.size() > 0) {
				dataEngine = engines.get(0);
				
				EngineService engineService = new EngineService();
				
				PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
				
				PagadorRecebedor pagaRecebedor = dataEngine.getPagador();
				
				pagadorRecebedorService.preecheDadosReceita(pagaRecebedor);

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();

				DocumentoAnalise documentoAnalise = documentoAnaliseDao.findByFilter("engine", dataEngine).stream()
						.findFirst().orElse(null);

				
				pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagaRecebedor,
						DocumentosAnaliseEnum.ENGINE, webhookRetorno);

				if (!CommonsUtil.semValor(documentoAnalise)) {
					engineService.processaWebHookEngine( documentoAnaliseService, engineWebhookRetorno,
							pagadorRecebedorService, documentoAnaliseDao, documentoAnalise);
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