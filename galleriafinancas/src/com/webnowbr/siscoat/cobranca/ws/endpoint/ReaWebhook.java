package com.webnowbr.siscoat.cobranca.ws.endpoint;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;

import io.jsonwebtoken.Jwts;

@Path("/rea")
public class ReaWebhook {

	private static final Log LOGGER = LogFactory.getLog(ReaWebhook.class);

	@POST
	@Path("/webhook/")
	public Response olaMundo(String webhookRetorno, @QueryParam("Token") String token) {
		LOGGER.debug(webhookRetorno);

		try {
			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE).build().parseClaimsJws(token);

			ReaWebhookRetorno reaWebhookRetorno = GsonUtil.fromJson(webhookRetorno, ReaWebhookRetorno.class);
			
			ReaWebhookRetornoBloco proprietarioAtual =reaWebhookRetorno.getProprietarioAtual();
			ReaWebhookRetornoBloco proprietarioAnterior =reaWebhookRetorno.getProprietarioAnterior();
			proprietarioAtual.getConteudo().getExtraido().getProprietarios().getNome();
			
			
			
			DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
			DocumentoAnalise documentoAnalise = documentoAnaliseDao.findByFilter("idRemoto", reaWebhookRetorno.getId())
					.stream().findFirst().orElse(null);
			documentoAnalise.setRetorno(webhookRetorno);
			documentoAnaliseDao.merge(documentoAnalise);
			
			cadastrarPessoRetornoRea(proprietarioAtual, documentoAnaliseDao, documentoAnalise.getContratoCobranca(), "Proprietario Atual" );

			cadastrarPessoRetornoRea(proprietarioAnterior, documentoAnaliseDao, documentoAnalise.getContratoCobranca(), "Proprietario Anterior");
			

			return Response.status(200).entity("Processado").build();
		} catch (io.jsonwebtoken.ExpiredJwtException eJwt) {
			eJwt.printStackTrace();
			return Response.status(500).entity("Token Expirado").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Erro interno").build();
		}
	}

	private void cadastrarPessoRetornoRea(ReaWebhookRetornoBloco bloco, DocumentoAnaliseDao documentoAnaliseDao,
			ContratoCobranca contratoCobranca, String motivo) {
		DocumentoAnalise documentoAnalise = new DocumentoAnalise();
		documentoAnalise.setContratoCobranca(contratoCobranca);
		documentoAnalise.setIdentificacao(bloco.getConteudo().getExtraido().getProprietarios().getNome());

		documentoAnalise.setTipoPessoa(bloco.getConteudo().getExtraido().getProprietarios().getFisicaJuridica());
		documentoAnalise.setMotivoAnalise(motivo);
		if (documentoAnalise.getTipoPessoa() == "PJ") {
			documentoAnalise.setCnpjcpf(bloco.getConteudo().getExtraido().getProprietarios().getCPF());
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
			documentoAnalise.setPath(bloco.getConteudo().getExtraido().getProprietarios().getCPF());
		} else {
			documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.CREDNET);
			documentoAnalise.setCnpjcpf(bloco.getConteudo().getExtraido().getProprietarios().getCNPJ());
		}
		documentoAnaliseDao.create(documentoAnalise);
	}
}
