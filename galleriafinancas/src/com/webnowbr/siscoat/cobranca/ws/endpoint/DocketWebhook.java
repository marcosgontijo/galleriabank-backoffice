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
import com.webnowbr.siscoat.cobranca.db.model.DocketConsulta;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketConsultaDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketDao;
import com.webnowbr.siscoat.cobranca.service.DocketService;
import com.webnowbr.siscoat.cobranca.service.FileService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;

import io.jsonwebtoken.Jwts;

@Path("/docket")
public class DocketWebhook {

	private static final Log LOGGER = LogFactory.getLog(DocketWebhook.class);

	@POST
	@Path("/webhook/")
	public Response webhookDocket(String webhookRetorno, @QueryParam("Token") String token) {
//		LOGGER.debug(webhookRetorno);

		try {
			DocketWebhookRetorno docketWebhookRetorno = GsonUtil.fromJson(webhookRetorno, DocketWebhookRetorno.class);
			/*try {
				Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);		
			} catch (Exception e) {
				System.out.println("token docket expirado: " + webhookRetorno);
			}*/
			
			Optional<ContratoCobranca> objetoContratoCobranca;
			DocketConsultaDao consultaDao = new DocketConsultaDao();
			DocketService docketService = new DocketService();

			if (CommonsUtil.semValor(docketWebhookRetorno) || CommonsUtil.semValor(docketWebhookRetorno.getPedido())) {				
				DocketWebhookRetornoDocumento documentoRetorno = GsonUtil.fromJson(webhookRetorno, DocketWebhookRetornoDocumento.class);			
				if(CommonsUtil.semValor(documentoRetorno.getId())) 
					return Response.status(500).entity("Id inesxistente").build();
				
				DocketConsulta docketConsulta = consultaDao.getConsultasExistentesWebhook(documentoRetorno.id);
				docketConsulta.setStatus("Concluido");
				docketConsulta.setRetorno(GsonUtil.toJson(documentoRetorno));
				consultaDao.merge(docketConsulta);
				String base64 = docketService.getPdfBase64(docketConsulta.getRetorno());
				docketConsulta.setPdf(base64);
				FileService fileService = new FileService();
				fileService.salvarPdfRetorno(docketConsulta.getDocumentoAnalise(), base64, documentoRetorno.getDocumentoNome(), "interno");
				consultaDao.merge(docketConsulta);
				return Response.status(200).entity("Processado ID:" + documentoRetorno.id).build();
			}

			DocketDao docketDao = new DocketDao();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			String lead = docketWebhookRetorno.getPedido().getLead();
			String sNumeroContrato = lead.split("-")[0].trim();

			objetoContratoCobranca = contratoCobrancaDao.findByFilter("numeroContrato", sNumeroContrato).stream().findFirst();
			
			for (DocketWebhookRetornoDocumento documentoRetorno : docketWebhookRetorno.getPedido().getDocumentos()) {	
				DocketConsulta docketConsulta = consultaDao.getConsultasExistentesWebhook(documentoRetorno.id);
				docketConsulta.setStatus("Concluido");
				docketConsulta.setRetorno(GsonUtil.toJson(documentoRetorno));
				String base64 = docketService.getPdfBase64(docketConsulta.getRetorno());
				docketConsulta.setPdf(base64);
				FileService fileService = new FileService();
				fileService.salvarPdfRetorno(docketConsulta.getDocumentoAnalise(), base64, documentoRetorno.getDocumentoNome(), "interno");
				consultaDao.merge(docketConsulta);
			}
			
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
