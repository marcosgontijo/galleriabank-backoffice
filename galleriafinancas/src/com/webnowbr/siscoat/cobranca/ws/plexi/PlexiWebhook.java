package com.webnowbr.siscoat.cobranca.ws.plexi;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.service.FileService;
import com.webnowbr.siscoat.cobranca.vo.FileUploaded;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.jwt.common.JwtUtil;
import io.jsonwebtoken.Jwts;

@Path("/plexi")
public class PlexiWebhook {

	private static final Log LOGGER = LogFactory.getLog(PlexiWebhook.class);

	@POST
	@Path("/webhook/")
	public Response webhookPlexi(String webhookRetorno, @QueryParam("Token") String token) {
		try {
			JSONObject webhookObject = new JSONObject(webhookRetorno);
			PlexiService plexiService = new PlexiService();
			/*try {
				Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);		
			} catch (Exception e) {
				String requestId = webhookObject.getString("requestId");
				System.out.println("token plexi expirado: " + requestId);
				//webhookObject = plexiService.getRetornoPlexi(requestId);
				//webhookObject.put("requestId", requestId);
			}*/

			PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
			if(!webhookObject.has("requestId")) {
				System.out.println("Request ID plexi não foi encontrado: " + webhookRetorno);
				return Response.status(500).entity("requestId não foi encontrado").build();
			}
			PlexiConsulta plexiConsulta = plexiConsultaDao.findByFilter("requestId", 
					webhookObject.getString("requestId"))
					.stream().findFirst().orElse(null);
			
			if(CommonsUtil.semValor(plexiConsulta)) {
				System.out.println("Erro webhook Plexi: " + webhookObject.getString("requestId"));
			}
			
			plexiConsulta.setWebhookRetorno(webhookRetorno);
			if(webhookObject.has("pdf")) {
				plexiConsulta.setPdf(webhookObject.getString("pdf"));
				plexiConsulta.setStatus("Consulta Concluída");
				//plexiService.salvarPdfRetornoPlexi(plexiConsulta, plexiConsultaDao);
			} else 
				plexiConsulta.setStatus("Consulta Sem PDF");
			//plexiConsulta.setStatus("Consulta Concluída");
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
