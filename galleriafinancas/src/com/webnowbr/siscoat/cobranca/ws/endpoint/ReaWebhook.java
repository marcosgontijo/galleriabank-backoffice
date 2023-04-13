package com.webnowbr.siscoat.cobranca.ws.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.JwtUtil;

import io.jsonwebtoken.Jwts;

@Path("/rea")
public class ReaWebhook {

	private static final Log LOGGER = LogFactory.getLog(ReaWebhook.class);

	@GET
	@Path("/")
	public String token() {
		return JwtUtil.generateJWTReaWebwook(false);
	}

	@POST
	@Path("/webhook/")
	public Response olaMundo(String webhookRetorno, @QueryParam("Token") String token) {
		LOGGER.debug(webhookRetorno);

		try {
			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE).build().parseClaimsJws(token);

			ReaWebhookRetorno reaWebhookRetorno = GsonUtil.fromJson(webhookRetorno, ReaWebhookRetorno.class);

			DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
			DocumentoAnalise documentoAnalise = documentoAnaliseDao.findByFilter("idRemoto", reaWebhookRetorno.getId())
					.stream().findFirst().orElse(null);
			documentoAnalise.setRetorno(webhookRetorno);
			documentoAnaliseDao.merge(documentoAnalise);

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
