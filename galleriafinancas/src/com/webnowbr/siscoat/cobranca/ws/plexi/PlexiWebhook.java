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
			if(JwtUtil.isTokenExpiredWebhook(token) && webhookObject.has("requestId")) {
				String requestId = webhookObject.getString("requestId");
				PlexiService plexiService = new PlexiService();
				webhookObject = plexiService.getRetornoPlexi(requestId);
				webhookObject.put("requestId", requestId);
			} else {
				Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);
			}

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
				salvarPdfRetorno(plexiConsulta, plexiConsultaDao);
			}
			plexiConsulta.setStatus("Consulta Concluída");
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
	
	public void salvarPdfRetorno(PlexiConsulta plexiConsulta, PlexiConsultaDao plexiConsultaDao) {
		String numeroContrato = plexiConsultaDao.getNumeroContratoAnalise(plexiConsulta);
		if(CommonsUtil.semValor(numeroContrato)) {
			return;
		}
		String nomeAnalise = plexiConsultaDao.getNomeAnalise(plexiConsulta);
		FileUploaded pdfRetorno = new FileUploaded();
		pdfRetorno.setFileBase64(plexiConsulta.getPdf());
		pdfRetorno.setName(plexiConsulta.getNomeCompleto() + " - " + nomeAnalise + ".pdf");
		FileService fileService = new FileService();
		User user = new UserDao().findById((long) -1);
		fileService.salvarDocumentoBase64(pdfRetorno, numeroContrato, "interno", user);
	}
}
