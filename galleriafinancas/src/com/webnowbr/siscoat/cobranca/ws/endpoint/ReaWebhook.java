package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.GravamesRea;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.db.op.GravamesReaDao;
import com.webnowbr.siscoat.cobranca.service.PagadorRecebedorService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;

import br.com.galleriabank.jwt.common.JwtUtil;
import io.jsonwebtoken.Jwts;

@Path("/rea")
public class ReaWebhook {

	private static final Log LOGGER = LogFactory.getLog(ReaWebhook.class);

	@GET
	@Path("/token/")
	public Response webhookReaToken(String webhookRetorno, @QueryParam("Token") String token) {
		if (CommonsUtil.sistemaWindows())
			return Response.status(200).entity(JwtUtil.generateJWTWebhook(false)).build();
		else
			return Response.status(200).entity("").build();
	}

	@POST
	@Path("/webhook/")
	public Response webhookRea(String webhookRetorno, @QueryParam("Token") String token) {
//		LOGGER.debug(webhookRetorno);

		try {

			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);

			/*
			 * System.out.println("---------------- REA webhookRetorno ---------------- ");
			 * System.out.println(webhookRetorno);
			 * System.out.println("---------------- REA webhookRetorno ---------------- ");
			 */
			ReaWebhookRetorno reaWebhookRetorno = GsonUtil.fromJson(webhookRetorno, ReaWebhookRetorno.class);

			DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
			DocumentoAnalise documentoAnalise = documentoAnaliseDao.findByFilter("idRemoto", reaWebhookRetorno.getId())
					.stream().findFirst().orElse(null);
			documentoAnalise.setRetorno(webhookRetorno);
			documentoAnalise.setObservacao("Retorno REA recebido");
			documentoAnaliseDao.merge(documentoAnalise);
			reaWebhookRetorno.buscaProprietarios();
			Date dataVendaAtual = null;

			if (reaWebhookRetorno.getProprietarioAtual() != null) {
				cadastrarPessoRetornoRea(reaWebhookRetorno.getProprietarioAtual(), documentoAnaliseDao,
						documentoAnalise.getContratoCobranca(), "Proprietario Atual", documentoAnalise.getUsuarioCadastro(), documentoAnalise.isReanalise());
				dataVendaAtual = DateUtil
						.getDecodeDateExtenso(reaWebhookRetorno.getProprietarioAtual().getConteudo().getTexto());
			}
//			if ( CommonsUtil.semValor((!CommonsUtil.semValor(dataVendaAtual) ) || (!CommonsUtil.semValor(dataVendaAtual) && DateUtil.isAfterDate(
//					DateUtil.adicionarPeriodo(DateUtil.getFirstDayOfMonth( DateUtil.getDataHoje() ), -2, Calendar.YEAR), dataVendaAtual))) {

			if (!CommonsUtil.semValor(reaWebhookRetorno.getProprietariosAnterior())) {
				Date dataVendaAnterior = DateUtil.getDataHoje();
				for (ReaWebhookRetornoBloco proprietarioAnterior : reaWebhookRetorno.getProprietariosAnterior()) {
					Date dataVenda = DateUtil.getDecodeDateExtenso(proprietarioAnterior.getConteudo().getTexto());

//					if (!CommonsUtil.semValor(dataVendaAnterior) || CommonsUtil.semValor(dataVenda) || DateUtil.isAfterDate(
//							DateUtil.adicionarPeriodo(DateUtil.getFirstDayOfMonth( DateUtil.getDataHoje() ), -2, Calendar.YEAR), dataVenda)) {
//						dataVendaAnterior = dataVenda;
//						if ( (!CommonsUtil.semValor(dataVenda) &&  DateUtil.isAfterDate(
//							DateUtil.adicionarPeriodo(DateUtil.getFirstDayOfMonth( DateUtil.getDataHoje() ), -2, Calendar.YEAR), dataVenda))  || CommonsUtil.semValor(dataVenda))
//							
					cadastrarPessoRetornoRea(proprietarioAnterior, documentoAnaliseDao,
							documentoAnalise.getContratoCobranca(),
							"Proprietario Anterior" + (CommonsUtil.semValor(dataVenda) ? " Data venda não localizada"
									: " Data venda:" + CommonsUtil.formataData(dataVenda, "dd/MM/yyyy")), documentoAnalise.getUsuarioCadastro(), documentoAnalise.isReanalise());
//					}

				}
//			}
			}
			List<ReaWebhookRetornoBloco> blocosGravameAberto = reaWebhookRetorno.buscaGravameAbertos();
			if(!CommonsUtil.semValor(blocosGravameAberto)) {
				GravamesReaDao gravamesReaDao = new GravamesReaDao();
				for(ReaWebhookRetornoBloco gravameAberto : blocosGravameAberto) {
					GravamesRea gravameRea = new GravamesRea(documentoAnalise, gravameAberto);
					gravamesReaDao.create(gravameRea);
				}
			}
			documentoAnalise.setObservacao("REA processado");
			documentoAnaliseDao.merge(documentoAnalise);
			
			return Response.status(200).entity("Processado").build();
		} catch (io.jsonwebtoken.ExpiredJwtException eJwt) {
			eJwt.printStackTrace();
			return Response.status(500).entity("Token Expirado").build();
		} catch (Exception e) {
			System.out.println("---------------- REA webhookRetorno ---------------- ");
			System.out.println(webhookRetorno);
			System.out.println("---------------- REA webhookRetorno ---------------- ");
			e.printStackTrace();
			return Response.status(500).entity("Erro interno").build();
		}
	}

	private void cadastrarPessoRetornoRea(ReaWebhookRetornoBloco bloco, DocumentoAnaliseDao documentoAnaliseDao,
			ContratoCobranca contratoCobranca, String motivo, String usuarioConsultaREA, boolean reanalise) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();

		for (ReaWebhookRetornoProprietario propietario : bloco.getConteudo().getExtraido().getProprietarios()
				.getDadosProprietarios()) {

			DocumentoAnalise documentoAnalise = new DocumentoAnalise();
			documentoAnalise.setContratoCobranca(contratoCobranca);
			documentoAnalise.setIdentificacao(propietario.getNome());

			documentoAnalise.setTipoPessoa(propietario.getFisicaJuridica());
			documentoAnalise.setMotivoAnalise(motivo);
			boolean cnpjCpfValido = false;

			if (documentoAnalise.getTipoPessoa() == "PJ") {
				propietario.setCnpj(CommonsUtil.strZero(CommonsUtil.somenteNumeros(propietario.getCnpj()),14));
				try {												
					cnpjCpfValido = ValidaCNPJ.isCNPJ(propietario.getCnpj());
				} catch (Exception e) {
					cnpjCpfValido = false;
				}
				if (cnpjCpfValido)
					documentoAnalise.setCnpjcpf(CommonsUtil.formataCnpjCpf(propietario.getCnpj(), false));
				else
					documentoAnalise.setCnpjcpf("CNPJ esta inválido");
				documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.RELATO);
			} else {
				propietario.setCpf(CommonsUtil.strZero(CommonsUtil.somenteNumeros(propietario.getCpf()),11));
				try {
					cnpjCpfValido = ValidaCPF.isCPF(propietario.getCpf());
				} catch (Exception e) {
					cnpjCpfValido = false;
				}
				if (cnpjCpfValido)
					documentoAnalise.setCnpjcpf(CommonsUtil.formataCnpjCpf(propietario.getCpf(), false));
				else
					documentoAnalise.setCnpjcpf("CPF esta inválido");
				documentoAnalise.setTipoEnum(DocumentosAnaliseEnum.CREDNET);
			}

			if (cnpjCpfValido) {
				DocumentoAnalise documentoAnaliseCadastrado = documentoAnaliseDao.cadastradoAnalise(contratoCobranca, documentoAnalise.getCnpjcpf(), documentoAnalise.isReanalise());
				if (CommonsUtil.semValor(documentoAnaliseCadastrado)) {
					
					documentoAnalise.setOrigem("REA");
					documentoAnalise.setDataCadastro(DateUtil.getDataHoraAgora());
					documentoAnalise.setUsuarioCadastro(usuarioConsultaREA);

					PagadorRecebedor pagador = new PagadorRecebedor();
					pagador.setId(0);
					if (CommonsUtil.mesmoValor(documentoAnalise.getTipoPessoa(), "PF")) {
						pagador.setCpf(propietario.getCpf());
						pagador.setRg(propietario.getRg());
					} else {
						pagador.setCnpj(propietario.getCnpj());
					}
					pagador.setNome(propietario.getNome());
					pagador = pagadorRecebedorService.buscaOuInsere(pagador);
					documentoAnalise.setPagador(pagador);
					documentoAnalise.adiconarEstadosPeloCadastro();
					documentoAnaliseDao.create(documentoAnalise);
				} else if(reanalise) {
					documentoAnalise = new DocumentoAnalise(documentoAnaliseCadastrado);
					documentoAnalise.setOrigem("REA");
					documentoAnalise.setDataCadastro(DateUtil.getDataHoraAgora());
					documentoAnalise.setUsuarioCadastro(usuarioConsultaREA);
					documentoAnalise.setAnaliseOriginal(documentoAnaliseCadastrado);
					documentoAnalise.setReanalise(true);
					documentoAnaliseDao.create(documentoAnalise);
				} else {
					documentoAnaliseCadastrado.setExcluido(false);
					documentoAnaliseDao.merge(documentoAnaliseCadastrado);					
				}
			}
		}
	}
}
