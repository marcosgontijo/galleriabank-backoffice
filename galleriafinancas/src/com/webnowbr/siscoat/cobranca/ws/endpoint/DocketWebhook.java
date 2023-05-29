package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.Optional;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Docket;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;

import io.jsonwebtoken.Jwts;

@Path("/docket")
public class DocketWebhook {

	private static final Log LOGGER = LogFactory.getLog(DocketWebhook.class);

	@POST
	@Path("/webhook/")
	public Response webhookDocket(String webhookRetorno, @QueryParam("Token") String token) {
//		LOGGER.debug(webhookRetorno);

		try {

			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);

			/*
			 * System.out.println("---------------- REA webhookRetorno ---------------- ");
			 * System.out.println(webhookRetorno);
			 * System.out.println("---------------- REA webhookRetorno ---------------- ");
			 */
			DocketWebhookRetorno docketWebhookRetorno = GsonUtil.fromJson(webhookRetorno, DocketWebhookRetorno.class);

			Optional<ContratoCobranca> objetoContratoCobranca;

			if (docketWebhookRetorno.getPedido().getLead() == null) {
				return null;
			}

			DocketDao docketDao = new DocketDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			String lead = docketWebhookRetorno.getPedido().getLead();
			String sNumeroContrato = lead.split("-")[0].trim();

			objetoContratoCobranca = contratoCobrancaDao.findByFilter("numeroContrato", sNumeroContrato).stream()
					.findFirst();

			if (objetoContratoCobranca.isPresent()) {
				Optional<Docket> docket = docketDao.findByFilter("objetoContratoCobranca", objetoContratoCobranca.get())
						.stream().findFirst();
				if (docket.isPresent()) {
					docket.get().setRetorno(webhookRetorno);
					docketDao.merge(docket.get());
				}
			}

			return Response.status(200).entity("Processado").build();
		} catch (io.jsonwebtoken.ExpiredJwtException eJwt) {
			eJwt.printStackTrace();
			return Response.status(500).entity("Token Expirado").build();
		} catch (Exception e) {
			System.out.println("---------------- Docket webhookRetorno ---------------- ");
			System.out.println(webhookRetorno);
			System.out.println("---------------- Docket webhookRetorno ---------------- ");
			e.printStackTrace();
			return Response.status(500).entity("Erro interno").build();
		}
	}
}
