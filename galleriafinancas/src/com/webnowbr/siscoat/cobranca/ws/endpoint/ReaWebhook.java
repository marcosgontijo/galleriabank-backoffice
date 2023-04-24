package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.JwtUtil;

import io.jsonwebtoken.Jwts;

@Path("/rea")
public class ReaWebhook {

	private static final Log LOGGER = LogFactory.getLog(ReaWebhook.class);

	@GET
	@Path("/token/")
	public Response webhookReaToken(String webhookRetorno, @QueryParam("Token") String token) {
		if (CommonsUtil.sistemaWindows())
			return Response.status(200).entity(JwtUtil.generateJWTReaWebwook(false)).build();
		else
			return Response.status(200).entity("").build();
	}

	@POST
	@Path("/webhook/")
	public Response webhookRea(String webhookRetorno, @QueryParam("Token") String token) {
		LOGGER.debug(webhookRetorno);

		try {

			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE).build().parseClaimsJws(token);

			System.out.println("---------------- webhookRetorno ---------------- ");
			System.out.println(webhookRetorno);
			System.out.println("---------------- webhookRetorno ---------------- ");
			ReaWebhookRetorno reaWebhookRetorno = GsonUtil.fromJson(webhookRetorno, ReaWebhookRetorno.class);

			DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
			DocumentoAnalise documentoAnalise = documentoAnaliseDao.findByFilter("idRemoto", reaWebhookRetorno.getId())
					.stream().findFirst().orElse(null);
			documentoAnalise.setRetorno(webhookRetorno);
			documentoAnaliseDao.merge(documentoAnalise);
			reaWebhookRetorno.buscaProprietarios();
			if (reaWebhookRetorno.getProprietarioAtual() != null)
				cadastrarPessoRetornoRea(reaWebhookRetorno.getProprietarioAtual(), documentoAnaliseDao,
						documentoAnalise.getContratoCobranca(), "Proprietario Atual");
			if (reaWebhookRetorno.getProprietarioAnterior() != null) {
				Date dataVenda = DateUtil
						.getDecodeDateExtenso(reaWebhookRetorno.getProprietarioAnterior().getConteudo().getTexto());

				if (CommonsUtil.semValor(dataVenda) || DateUtil.isAfterDate(
						DateUtil.adicionarPeriodo(DateUtil.getDataHoje(), -2, Calendar.YEAR),dataVenda)) {
					cadastrarPessoRetornoRea(reaWebhookRetorno.getProprietarioAnterior(), documentoAnaliseDao,
							documentoAnalise.getContratoCobranca(),
							"Proprietario Anterior" + (CommonsUtil.semValor(dataVenda) ? " Data venda não localizada"
									: " Data venda:" + CommonsUtil.formataData(dataVenda, "dd/MM/yyyy")));
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

	private void cadastrarPessoRetornoRea(ReaWebhookRetornoBloco bloco, DocumentoAnaliseDao documentoAnaliseDao,
			ContratoCobranca contratoCobranca, String motivo) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();

		for (ReaWebhookRetornoProprietario propietario : bloco.getConteudo().getExtraido().getProprietarios()
				.getDadosProprietarios()) {

			DocumentoAnalise documentoAnalise = new DocumentoAnalise();
			documentoAnalise.setContratoCobranca(contratoCobranca);
			documentoAnalise.setIdentificacao(propietario.getNome());

			documentoAnalise.setTipoPessoa(propietario.getFisicaJuridica());
			documentoAnalise.setMotivoAnalise(motivo);

			if (documentoAnalise.getTipoPessoa() == "PJ") {
				documentoAnalise.setCnpjcpf(propietario.getCnpj());
				documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
			} else {
				documentoAnalise.setCnpjcpf(propietario.getCpf());
				documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.CREDNET);
			}

			PagadorRecebedor pagador = new PagadorRecebedor();
			pagador.setId(0);
			if (CommonsUtil.mesmoValor(documentoAnalise.getTipoPessoa(), "PF")) {
				if (CommonsUtil.somenteNumeros(propietario.getCpf()).length() == 11)
					pagador.setCpf(CommonsUtil.formataCpf(CommonsUtil.somenteNumeros(propietario.getCpf())));
				else
					pagador.setCpf(CommonsUtil.somenteNumeros(propietario.getCpf()));
				pagador.setRg(propietario.getRg());
			} else {
				if (CommonsUtil.somenteNumeros(propietario.getCnpj()).length() == 14)
					pagador.setCnpj(CommonsUtil.formataCnpj(CommonsUtil.somenteNumeros(propietario.getCnpj())));
				else
					pagador.setCnpj(CommonsUtil.somenteNumeros(propietario.getCnpj()));
			}
			pagador.setNome(propietario.getNome());

			pagador = pagadorRecebedorService.buscaOuInsere(pagador);
			documentoAnalise.setPagador(pagador);

			documentoAnaliseDao.create(documentoAnalise);
		}

	}
}
