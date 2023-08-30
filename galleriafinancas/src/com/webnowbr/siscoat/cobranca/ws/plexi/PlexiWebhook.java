package com.webnowbr.siscoat.cobranca.ws.plexi;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.webnowbr.siscoat.common.CommonsUtil;

import io.jsonwebtoken.Jwts;

@Path("/plexi")
public class PlexiWebhook {

	private static final Log LOGGER = LogFactory.getLog(PlexiWebhook.class);

	@POST
	@Path("/webhook/")
	public Response webhookPlexi(String webhookRetorno, @QueryParam("Token") String token) {
		try {

			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);

			JSONObject webhookObject = new JSONObject(webhookRetorno);

			PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
			if(!webhookObject.has("requestId")) {
				return Response.status(500).entity("requestId não foi encontrato").build();
			}
			PlexiConsulta plexiConsulta = plexiConsultaDao.findByFilter("requestId", 
					webhookObject.getString("requestId"))
					.stream().findFirst().orElse(null);
			
			if(CommonsUtil.semValor(plexiConsulta)) {
				System.out.println("Erro Plexi: " + webhookObject.getString("requestId"));
			}
			
			plexiConsulta.setWebhookRetorno(webhookRetorno);
			if(webhookObject.has("pdf")) {
				plexiConsulta.setPdf(webhookObject.getString("pdf"));
			}
			plexiConsultaDao.merge(plexiConsulta);

			return Response.status(200).entity("Processado").build();
		} catch (io.jsonwebtoken.ExpiredJwtException eJwt) {
			eJwt.printStackTrace();
			return Response.status(500).entity("Token Expirado").build();
		} catch (Exception e) {
			System.out.println("---------------- Plexi webhookRetorno ---------------- ");
			System.out.println(webhookRetorno);
			System.out.println("---------------- Plexi webhookRetorno ---------------- ");
			e.printStackTrace();
			return Response.status(500).entity("Erro interno").build();
		}
	}
}
