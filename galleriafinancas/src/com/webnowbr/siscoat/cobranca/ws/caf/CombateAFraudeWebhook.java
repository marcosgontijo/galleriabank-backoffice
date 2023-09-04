package com.webnowbr.siscoat.cobranca.ws.caf;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.ws.endpoint.EngineWebhook;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;

import io.jsonwebtoken.Jwts;

@Path("/caf")
public class CombateAFraudeWebhook {

	private static final Log LOGGER = LogFactory.getLog(EngineWebhook.class);
	
	@POST
	@Path("/webhook/")
	public Response webhookCaF(String webhookRetorno, @QueryParam("Token") String token) {
		try {
			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);
			CombateAFraudeWebhookRetorno cafWebhookRetorno = GsonUtil.fromJson(webhookRetorno, CombateAFraudeWebhookRetorno.class);
			CombateAFraudeDao cafDao = new CombateAFraudeDao();
			CombateAFraude caf = cafDao.findByFilter("uuid", cafWebhookRetorno.uuid).get(0);
			if(!CommonsUtil.mesmoValor(cafWebhookRetorno.status, "PROCESSING")){
				caf.setType(cafWebhookRetorno.type);
				caf.setStatus(cafWebhookRetorno.status);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				Date d = sdf.parse(cafWebhookRetorno.date);
				caf.setDate(d);
				for(CombateAFraudeStatusReasons reason : cafWebhookRetorno.statusReasons) {
					if(CommonsUtil.semValor(reason.description)) {
						continue;
					}
					if(CommonsUtil.semValor(caf.getObs())) {
						caf.setObs("");
					}
					caf.setObs(caf.getObs() + reason.description  + "\n");
				}
				caf.setRetorno(webhookRetorno);
				cafDao.merge(caf);				
			}
			//System.out.println(webhookRetorno);		
			return Response.status(200).entity("Processado").build();
		} catch (io.jsonwebtoken.ExpiredJwtException eJwt) {
			eJwt.printStackTrace();
			return Response.status(500).entity("Token Expirado").build();
		} catch (Exception e) {
			System.out.println("---------------- CaF webhookRetorno ---------------- ");
			System.out.println(webhookRetorno);
			System.out.println("---------------- CaF webhookRetorno ---------------- ");
			e.printStackTrace();
			return Response.status(500).entity("Erro interno").build();
		}
	}

	
}
