package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DataEngine;
import com.webnowbr.siscoat.cobranca.db.model.Docket;
import com.webnowbr.siscoat.cobranca.db.model.DocketConsulta;
import com.webnowbr.siscoat.cobranca.db.model.DocketRetorno;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosDocket;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosPagadorDocket;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DataEngineDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketConsultaDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.model.docket.DocketRetornoConsulta;
import com.webnowbr.siscoat.cobranca.vo.FileUploaded;
import com.webnowbr.siscoat.cobranca.ws.endpoint.DocketWebhookRetornoDocumento;
import com.webnowbr.siscoat.cobranca.ws.endpoint.ReaWebhookRetorno;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.MultipartUtility;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.dataengine.cliente.model.request.DataEngineIdSend;
import br.com.galleriabank.dataengine.cliente.model.retorno.EngineRetorno;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultRelacionamentosPessoaisPJ;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership;
import br.com.galleriabank.dataengine.cliente.model.retorno.consulta.EngineRetornoRequestEnterprisePartnership;
import br.com.galleriabank.jwt.common.JwtUtil;
import br.com.galleriabank.serasarelato.cliente.util.GsonUtil;

public class DocketService {

	private String engineUrl = "https://api.dataengine.com.br/v2";
	private String engineIdproviderFlowPF = "b5986017-3aa6-4564-a125-2b1897fe82b6";
	private String engineIdproviderFlowPJ = "2dd0d245-0ec3-4302-9e09-eed92818ba1d";
	private String engineChaveApi = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZXJ2aWNlX2F1dGhfcmVxdWVzdF92YWxpZGF0aW9uIiwia"
			+ "WF0IjoxNjc4Mzc2NTAyLjk2NDgxMiwiY2xpIjoiYjMyN2E5OGQtMDhhMC00NjFkLTkzNTQtN2Q2YTczZGNlYTNlIn0.IME_xDdVDZzx5LNE7"
			+ "xpEDBN-wHQOILlkgjuQd2mzLtf8mGeousbvLElPlCao2DATTCdpfmNFIFppmYwdBNzdFu9bu7wKsulcZlf5Xuu80M3jZJQp56kfrdmJ-x"
			+ "8Wrqx9DAJEb2ZQW4sz24eDr_q9zUvsvukCmbCIiZPaUngWYR53luh9_Q-OfORxGIwm6RwYf1dATs3GIuhPT8k80S7KfcBBETJ2384UV7Cf"
			+ "ochOvgiT7FUyoNqHhivdFbyPLv8M0q7tHe49yBSyk8qYLHiDm_tKrlhaTFFwQyx4_AryRUcw4z14LykdmgRKX03T7i1pSYmyI2g8d5z9RZQ"
			+ "HsbOLfSG0QIn31Ed3BBOTSA3p6i7La6CYfQI247tWofs7-hRNYMuaAO91NurkvtcbaVDGE3tVzs-IjCjfmsOTEcFI_v3jKgQgaIFLOgs9E7"
			+ "UVQQAynXKR2C-MvoY8YBEZi6636z_Gakqs0MlUZEEvsVIkzbM0teuS2K6DLzTm428MJZWQemNAR7Ib1GXI1BybLN1bAP3T9kUgfN5AWHkW"
			+ "DfQCQ3eXhKpCJbNLZwl2B2mHh36ouDW18upzyLs-MOBO1YHtkNeGkOqQzC83j3XeLsuD7hX5cf66hoh28OQIoWW_raJwklLzHmSQQwQD54M"
			+ "R2t-P-qwzvsS5ADEsY5vTLTqyWo0";

	private String urlHomologacao = "https://sandbox-saas.docket.com.br";
	private String urlProducao = "https://saascompany.docket.com.br";
	private String kitIdGalleria = "02859d48-ff2a-45a4-922b-d6b9842affcc";
	private String kitNomeGalleria = "1 - GALLERIA BANK";
	private String login = "galleria-bank.api";
	private String senha = "5TM*sgZKJ3hoh@J";
	private String loginProd = "tatiane.galleria";
	private String senhaProd = "Nina2021@";
	private String organizacao_url = "galleria-bank";
	private String tokenLogin;

	public FacesMessage engineCriarConsulta(DataEngine engine, User usuarioLogado) {
		return engineCriarConsulta(null, engine, usuarioLogado);
	}

	public FacesMessage engineCriarConsulta(DocumentoAnalise documentoAnalise, DataEngine engine, User usuarioLogado) { // POST
																														// para
																														// gerar
																														// consulta
		DataEngineDao engineDao = new DataEngineDao();
//		if (engineDao.findByFilter("pagador", engine.getPagador()).size() > 0) {
////			context.addMessage(null,);	
//			return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta já existente!", "");
//		}
		if (!CommonsUtil.semValor(engine.getIdCallManager())) {
			if (documentoAnalise != null) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setEngine(engine);
				documentoAnalise.setRetornoEngine("consulta efetuada anteriormente Id: " + engine.getId());
				documentoAnaliseDao.merge(documentoAnalise);
				salvarDetalheDocumentoEngine(documentoAnalise);
				
				EngineRetorno engineRetorno = GsonUtil.fromJson(documentoAnalise.getRetornoEngine(), EngineRetorno.class);
				if (CommonsUtil.semValor(engineRetorno.getIdCallManager())) {
					engineRetorno.setIdCallManager(engine.getIdCallManager());
				}

				DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();
				PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
				
				processaWebHookEngine( documentoAnaliseService, engineRetorno,
						pagadorRecebedorService, documentoAnaliseDao, documentoAnalise);
				
			}

			return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta já existente!" + engine.getIdCallManager(),
					"");
		}
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL;
			myURL = new URL("https://servicos.galleriabank.com.br/dataengine/api/v1/consultar");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization",
					"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
			myURLConnection.setDoOutput(true);

			DataEngineIdSend myResponse = null;
			JSONObject jsonWhatsApp = engineBodyJsonEngine(engine.getPagador());

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = jsonWhatsApp.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {

				System.out.println(jsonWhatsApp.toString());
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Engine: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
				// docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" ,
				// cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());

				myResponse = engineJSONRetorno(myURLConnection.getInputStream());
				if (CommonsUtil.mesmoValor("401", myResponse.getCode())) {
					result = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Engine: Falha  (Cod: "
							+ myResponse.getCode() + " Mensagem: " + myResponse.getMessage() + ")", "");
				}

				engine.setIdCallManager(myResponse.getIdCallManager());
				engine.setData(DateUtil.getDataHoje());
				engine.setUsuario(usuarioLogado.getName());
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				if (engine.getContrato() != null && engine.getContrato().getId() > 0)
					cDao.merge(engine.getContrato());
				else
					engine.setContrato(null);

				// System.out.println("NumeroContrato Erro EngineDocket:" +
				// engine.getContrato().getNumeroContrato() + ", " +
				// engine.getContrato().getId());
				if (engine.getId() <= 0) {
					engineDao.create(engine);
				}

				if (documentoAnalise != null) {
					DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
					documentoAnalise.setEngine(engine);
					documentoAnaliseDao.merge(documentoAnalise);
				}

				result = new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");
			}

			myURLConnection.disconnect();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public FacesMessage engineCriarConsultaOld(DocumentoAnalise documentoAnalise, DataEngine engine,
			User usuarioLogado) { // POST para gerar consulta
		DataEngineDao engineDao = new DataEngineDao();
//		if (engineDao.findByFilter("pagador", engine.getPagador()).size() > 0) {
////			context.addMessage(null,);	
//			return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta já existente!", "");
//		}
		if (!CommonsUtil.semValor(engine.getIdCallManager())) {
			if (documentoAnalise != null) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setEngine(engine);
				documentoAnalise.setRetornoEngine("consulta efetuada anteriormente Id: " + engine.getId());
				documentoAnaliseDao.merge(documentoAnalise);
			}

			return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta já existente!" + engine.getIdCallManager(),
					"");
		}
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL;
			myURL = new URL(engineUrl + "/api/callmanager");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.addRequestProperty("x-api-key", this.engineChaveApi);

			JSONObject myResponse = null;
			JSONObject jsonWhatsApp = engineBodyJsonEngine(engine.getPagador());

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = jsonWhatsApp.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {

				System.out.println(jsonWhatsApp.toString());
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Engine: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
				// docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" ,
				// cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());

				myResponse = engineJSONSucesso(myURLConnection.getInputStream());
				engine.setIdCallManager(myResponse.get("idCallManager").toString());
				engine.setData(DateUtil.getDataHoje());
				engine.setUsuario(usuarioLogado.getName());
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				if (engine.getContrato() != null && engine.getContrato().getId() > 0)
					cDao.merge(engine.getContrato());
				else
					engine.setContrato(null);

				// System.out.println("NumeroContrato Erro EngineDocket:" +
				// engine.getContrato().getNumeroContrato() + ", " +
				// engine.getContrato().getId());
				if (engine.getId() <= 0) {
					engineDao.create(engine);
				}

				if (documentoAnalise != null) {
					DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
					documentoAnalise.setEngine(engine);
					documentoAnaliseDao.merge(documentoAnalise);
				}

				result = new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");
			}

			myURLConnection.disconnect();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public DataEngine engineInserirPessoa(PagadorRecebedor pagadorAdicionar, ContratoCobranca objetoContratoCobranca) {
		DataEngineDao engineDao = new DataEngineDao();
		DataEngine engine = null;

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();

		pagadorAdicionar = pagadorRecebedorService.buscaOuInsere(pagadorAdicionar);

		List<DataEngine> engines = engineDao.findByFilter("pagador", pagadorAdicionar);

		if (engines.size() > 0) {

			engine = engines.stream()
					.filter(e -> DateUtil.isAfterDate(DateUtil.adicionarDias(DateUtil.getDataHoje(), -30), e.getData()))
					.sorted(Comparator.comparing(DataEngine::getData).reversed()).findFirst().orElse(null);
		}

		if (CommonsUtil.semValor(engine)) {
			engine = new DataEngine(pagadorAdicionar);
		}
		if (!CommonsUtil.semValor(objetoContratoCobranca) && CommonsUtil.semValor(engine.getContrato())) {
			engine.setContrato(objetoContratoCobranca);
		}

		return engine;

	}

	public void requestEngine(DocumentoAnalise documentoAnalise, DataEngine engine, User usuarioLogado) {

		if (CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
			engineCriarConsulta(documentoAnalise, engine, usuarioLogado);
		}
		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		documentoAnalise = documentoAnaliseDao.findById(documentoAnalise.getId());

		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa())) {

			EngineRetorno engineRetorno = GsonUtil.fromJson(documentoAnalise.getRetornoEngine(), EngineRetorno.class);

			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

				if (CommonsUtil.semValor(documentoAnalise.getPagador().getDtNascimento()))
					documentoAnalise.getPagador().setDtNascimento(CommonsUtil.dateValue(
							engineRetorno.getEngineRetornoExecutionResultConsultaSerproCPF().getNascimento()));

				if (CommonsUtil.semValor(documentoAnalise.getPagador().getNomeMae()))
					documentoAnalise.getPagador()
							.setNomeMae(engineRetorno.getConsultaCompleta().getBestInfo().getMotherName().getFull());

				pagadorRecebedorDao.merge(documentoAnalise.getPagador());
			}

			if (!CommonsUtil.semValor(engineRetorno.getConsultaCompleta().getEnterpriseData()) &&
					!CommonsUtil.semValor(engineRetorno.getConsultaCompleta().getEnterpriseData().getPartnership()) &&
					!CommonsUtil.semValor(engineRetorno.getConsultaCompleta().getEnterpriseData().getPartnership().getPartnerships())) {
				DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();

				PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();

				for (EngineRetornoRequestEnterprisePartnership partnership : engineRetorno.getConsultaCompleta()
						.getEnterpriseData().getPartnership().getPartnerships()) {

					documentoAnaliseService.cadastrarPessoRetornoEngine(partnership, usuarioLogado, documentoAnaliseDao,
							pagadorRecebedorService, documentoAnalise.getContratoCobranca(),
							( ( CommonsUtil.mesmoValor("INAPTO",  partnership.getCNPJStatus()))?"INAPTO":"" )+  "Empresa Vinculada ao " + documentoAnalise.getMotivoAnalise());
				}
			}

		}

	}

	public void gerarRelacoesEngine(DocumentoAnalise documentoAnalise) {
		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		documentoAnalise = documentoAnaliseDao.findById(documentoAnalise.getId());
		EngineRetorno engineRetorno = null;// = GsonUtil.fromJson(documentoAnalise.getRetornoEngine(), EngineRetorno.class);
		try {
			engineRetorno = GsonUtil.fromJson(documentoAnalise.getRetornoEngine(), EngineRetorno.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();
		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		
		if (CommonsUtil.semValor(engineRetorno)) {
			return;
		}
		if (CommonsUtil.semValor(documentoAnalise.getPagador())) {
			return;
		}
		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa())) {
			if (CommonsUtil.semValor(engineRetorno.getConsultaCompleta())
					|| CommonsUtil.semValor(engineRetorno.getConsultaCompleta().getEnterpriseData())
					|| CommonsUtil.semValor(engineRetorno.getConsultaCompleta().getEnterpriseData().getPartnership())
					|| CommonsUtil.semValor(engineRetorno.getConsultaCompleta().getEnterpriseData().getPartnership()
							.getPartnerships())) {
				return;
			}
			for (EngineRetornoRequestEnterprisePartnership partnership : engineRetorno.getConsultaCompleta()
					.getEnterpriseData().getPartnership().getPartnerships()) {

				PagadorRecebedor empresa = documentoAnaliseService.cadastrarPartnershipRetornoEngine(partnership,
						pagadorRecebedorService);
				pagadorRecebedorService.geraRelacionamento(empresa, partnership.getRelationshipDescription(),
						documentoAnalise.getPagador(),
						CommonsUtil.bigDecimalValue(partnership.getPercentParticipation()));
			}
		} else if (CommonsUtil.mesmoValor("PJ", documentoAnalise.getTipoPessoa())) {
			if (CommonsUtil.semValor(engineRetorno.getRelacionamentosPessoaisPJ())
					|| CommonsUtil.semValor(engineRetorno.getRelacionamentosPessoaisPJ().getResult())
					|| engineRetorno.getRelacionamentosPessoaisPJ().getResult().size() <= 0) {
				return;
			}
			for (EngineRetornoExecutionResultRelacionamentosPessoaisPJ ererrppj : engineRetorno
					.getRelacionamentosPessoaisPJ().getResult()) {
				if (CommonsUtil.semValor(ererrppj.getRelationships()) || ererrppj.getRelationships().getRelationships().size() <= 0) {
					return;
				}

				for (EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership ererr : ererrppj.getRelationships().getRelationships()) {
					String relacao = "";
					if (CommonsUtil.mesmoValor(ererr.getRelationshipName(), "OWNER")) {
						relacao = "Dono";
					} else if ((CommonsUtil.mesmoValor(ererr.getRelationshipName(), "SOCIO-ADMINISTRADOR"))) {
						relacao = "Socio/Administrador";
					} else if ((CommonsUtil.mesmoValor(ererr.getRelationshipName(), "SOCIO"))) {
						relacao = "Socio";
					} else {
						relacao = ererr.getRelationshipName();
					}
					PagadorRecebedor pagadorParticipante = documentoAnaliseService
							.cadastrarPartnershipRetornoEnginePJ(ererr, pagadorRecebedorService);
					pagadorRecebedorService.geraRelacionamento(documentoAnalise.getPagador(), relacao,
							pagadorParticipante, BigDecimal.ZERO);
				}
			}
		}
	}

	public void baixarDocumentoEngine(DataEngine engine) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (CommonsUtil.semValor(engine.getIdCallManager())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Consulta sem IdCallManager", ""));
			return;
		}
		if (CommonsUtil.semValor(engine.getPdfBase64())) {
			PegarPDFDataEngine(engine);
		}
	}

	public void salvarDetalheDocumentoEngine(DocumentoAnalise documentoAnalise) {
		//FacesContext context = FacesContext.getCurrentInstance();
		if (CommonsUtil.semValor(documentoAnalise.getEngine().getIdCallManager())) {
			//context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Consulta sem IdCallManager", ""));
			return;
		}
		documentoAnalise.setRetornoEngine(PegarDetalheDataEngine(documentoAnalise.getEngine()));

		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		documentoAnaliseDao.merge(documentoAnalise);
	}

	public void processaWebHookEngine( DocumentoAnaliseService documentoAnaliseService,
			EngineRetorno engineWebhookRetorno, PagadorRecebedorService pagadorRecebedorService,
			DocumentoAnaliseDao documentoAnaliseDao, DocumentoAnalise documentoAnalise) {
		
		String webhookRetorno = GsonUtil.toJson(engineWebhookRetorno);
		
		documentoAnalise.setRetornoEngine(webhookRetorno);

		SerasaService serasaService = new SerasaService();
		NetrinService netrinService = new NetrinService();
		UserService userService = new UserService();
		ScrService scrService = new ScrService();
		DocketService docketService = new DocketService();

		
		User userSistema = userService.userSistema();
		
		engineWebhookRetorno.getConsultaAntecedenteCriminais();

		if ((CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais())
				|| CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais().getResult()
						.get(0).getOnlineCertificates()))
				&& (CommonsUtil.semValor(engineWebhookRetorno.getProcessos()) || CommonsUtil.intValue(
						engineWebhookRetorno.getProcessos().getTotal_acoes_judicias_reu()) == 0)) {
			// libera a consulta do crednet da PF
//						if (documentoAnalise.isPodeChamarSerasa()) {
//							if (CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
//								documentoAnalise.setLiberadoSerasa(true);
//								serasaService.requestSerasa(documentoAnalise, userService.userSistema());
//							}
//						}
			if (documentoAnalise.isPodeChamarCenprot()) {
				if (CommonsUtil.semValor(documentoAnalise.getRetornoCenprot())) {
					documentoAnalise.setLiberadoCenprot(true);
					documentoAnaliseDao.merge(documentoAnalise);
					netrinService.requestCenprot(documentoAnalise);
				}
			}

			if (documentoAnalise.isPodeChamarSCR()) {
				if (CommonsUtil.semValor(documentoAnalise.getRetornoScr())) {
					documentoAnalise.setLiberadoScr(true);
					documentoAnaliseDao.merge(documentoAnalise);
					scrService.requestScr(documentoAnalise);
				}
			}
//
//						DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();
//						documentoAnaliseService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
//								DocumentosAnaliseEnum.ENGINE, webhookRetorno);

		} else {
			if (!CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais()) 
					&& !CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais().getResult())
					&& !CommonsUtil.semValor(engineWebhookRetorno.getConsultaAntecedenteCriminais()
							.getResult().get(0).getOnlineCertificates())
					&& !CommonsUtil.mesmoValorIgnoreCase("NADA CONSTA",
							engineWebhookRetorno.getConsultaAntecedenteCriminais().getResult().get(0)
									.getOnlineCertificates().get(0).getBaseStatus())) {
				documentoAnalise.addObservacao("Possui antecedentes criminais");
			}
			if (!CommonsUtil.semValor(engineWebhookRetorno.getProcessos())
				&& !CommonsUtil.semValor(engineWebhookRetorno.getProcessos().getTotal_acoes_judicias_reu())
				&& CommonsUtil.mesmoValor(CommonsUtil.intValue(
						engineWebhookRetorno.getProcessos().getTotal_acoes_judicias_reu()),0)) {
				documentoAnalise.addObservacao("Possui Processos");
			}
		}

		documentoAnaliseDao.merge(documentoAnalise);

		String motivo = "Empresa Vinculada ao Proprietario Atual";
		if (!CommonsUtil.mesmoValor(documentoAnalise.getMotivoAnalise().toUpperCase(),
				"PROPRIETARIO ATUAL"))
			motivo = "Empresa Vinculada ao Proprietario Anterior";
		
		if (!CommonsUtil.semValor(engineWebhookRetorno.getConsultaCompleta())
				&& !CommonsUtil.semValor(engineWebhookRetorno.getConsultaCompleta().getEnterpriseData())
				&& !CommonsUtil.semValor(engineWebhookRetorno.getConsultaCompleta().getEnterpriseData().getPartnership())
				&& !CommonsUtil.semValor(engineWebhookRetorno.getConsultaCompleta().getEnterpriseData().getPartnership()
						.getPartnerships())) {

			for (EngineRetornoRequestEnterprisePartnership partnership : engineWebhookRetorno
					.getConsultaCompleta().getEnterpriseData().getPartnership().getPartnerships()) {

				documentoAnaliseService.cadastrarPessoRetornoEngine(partnership, userSistema,
						documentoAnaliseDao, pagadorRecebedorService,
						documentoAnalise.getContratoCobranca(), motivo);

			}
		}

		if (!CommonsUtil.semValor(engineWebhookRetorno.getRelacionamentosPessoaisPJ())
				&& !CommonsUtil.semValor(engineWebhookRetorno.getRelacionamentosPessoaisPJ().getResult())) {

			for (EngineRetornoExecutionResultRelacionamentosPessoaisPJ engineRetornoExecutionResultRelacionamentosPessoaisPJ : engineWebhookRetorno
					.getRelacionamentosPessoaisPJ().getResult()) {
				
				if (!CommonsUtil.semValor(
						engineRetornoExecutionResultRelacionamentosPessoaisPJ.getRelationships())) {

					if (!CommonsUtil.semValor(engineRetornoExecutionResultRelacionamentosPessoaisPJ
							.getRelationships().getRelationships()))
						for (EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership : engineRetornoExecutionResultRelacionamentosPessoaisPJ
								.getRelationships().getRelationships()) {

							if (engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership
									.getRelatedEntityTaxIdType().equalsIgnoreCase("CPF")) {
								motivo = "Sócio Vinculado ao Proprietario Atual";
								if (!CommonsUtil.mesmoValor(documentoAnalise.getMotivoAnalise().toUpperCase(),
										"PROPRIETARIO ATUAL"))
									motivo = "Sócio Vinculado ao Proprietario Anterior";
							} else {
								motivo = "Empresa Vinculada ao Proprietario Atual";
								if (!CommonsUtil.mesmoValor(documentoAnalise.getMotivoAnalise().toUpperCase(),
										"PROPRIETARIO ATUAL"))
									motivo = "Empresa Vinculada ao Proprietario Anterior";
							}

							documentoAnaliseService.cadastrarPessoRetornoEngine(
									engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership, userSistema,
									documentoAnaliseDao, pagadorRecebedorService,
									documentoAnalise.getContratoCobranca(), motivo);
						}

					if (!CommonsUtil.semValor(engineRetornoExecutionResultRelacionamentosPessoaisPJ
							.getRelationships().getCurrentRelationships()))
						for (EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership : engineRetornoExecutionResultRelacionamentosPessoaisPJ
								.getRelationships().getCurrentRelationships()) {
							documentoAnaliseService.cadastrarPessoRetornoEngine(
									engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership,
									userSistema, documentoAnaliseDao, pagadorRecebedorService,
									documentoAnalise.getContratoCobranca(), motivo);
						}

					if (!CommonsUtil.semValor(engineRetornoExecutionResultRelacionamentosPessoaisPJ
							.getRelationships().getHistoricalRelationships()))
						for (EngineRetornoExecutionResultRelacionamentosPessoaisPJPartnership engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership : engineRetornoExecutionResultRelacionamentosPessoaisPJ
								.getRelationships().getHistoricalRelationships()) {
							documentoAnaliseService.cadastrarPessoRetornoEngine(
									engineRetornoExecutionResultRelacionamentosPessoaisPJPartnership,
									userSistema, documentoAnaliseDao, pagadorRecebedorService,
									documentoAnalise.getContratoCobranca(), motivo);
						}

				}
			}

		}
		docketService.gerarRelacoesEngine(documentoAnalise);
		documentoAnalise.adicionaEstados(CommonsUtil.stringToList(engineWebhookRetorno.getEstados()));
		documentoAnalise.setObservacao("Engine processado");
		documentoAnaliseDao.merge(documentoAnalise);
		String base64 = documentoAnalise.getEngine().getPdfBase64();
		salvarPdfRetorno(documentoAnalise, base64, "Processo", "interno");
		
	}
	
	
	private void PegarPDFDataEngine(DataEngine engine) { // POST para pegar pdf
		FacesContext context = FacesContext.getCurrentInstance();
		if (CommonsUtil.semValor(engine.getIdCallManager())) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "Não Foi gerado ID da consulta!!!", ""));
			return;
		}

		String idproviderFlow;
		if (!CommonsUtil.semValor(engine.getPagador().getCpf())) {
			idproviderFlow = engineIdproviderFlowPF;
		} else {
			idproviderFlow = engineIdproviderFlowPJ;
		}

		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;

			URL myURL;
			myURL = new URL(engineUrl + "/dossie/" + idproviderFlow + "/" + engine.getIdCallManager() + "?base64=true");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.addRequestProperty("x-api-key", this.engineChaveApi);

//			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Engine: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", ""));
				System.out.println(myURL.toString());
			} else {
				// docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" ,
				// cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				engine.setPdfBase64(response.toString());
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String PegarDetalheDataEngine(DataEngine engine) { // POST para pegar pdf
		//FacesContext context = FacesContext.getCurrentInstance();
		if (CommonsUtil.semValor(engine.getIdCallManager())) {
			//context.addMessage(null,
			//		new FacesMessage(FacesMessage.SEVERITY_FATAL, "Não Foi gerado ID da consulta!!!", ""));
			return null;
		}

//		String idproviderFlow;
//		if (!CommonsUtil.semValor(engine.getPagador().getCpf())) {
//			idproviderFlow = engineIdproviderFlowPF;
//		} else {
//			idproviderFlow = engineIdproviderFlowPJ;
//		}

		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;

			URL myURL;
			myURL = new URL(engineUrl + "/api/callmanager/result/" + engine.getIdCallManager());

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.addRequestProperty("x-api-key", this.engineChaveApi);

//			JSONObject myResponse = null;
			String result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				//context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
				//		"Engine: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", ""));
				System.out.println(myURL.toString());
			} else {
				// docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" ,
				// cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());
//				context.addMessage(null,
//						new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				result = response.toString();
				in.close();
			}
			myURLConnection.disconnect();
			return result;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unused")
	public void loginDocket(User user) { // POST pra pegar token
		JSONObject jsonObj = new JSONObject();
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL;

			// JSONObject jsonObj = new JSONObject();
			String loginDocket;
			String senhaDocket;

			if (SiscoatConstants.DEV && CommonsUtil.sistemaWindows()) {
				myURL = new URL(urlHomologacao + "/api/v2/auth/login");
				loginDocket = login;
				senhaDocket = senha;

			} else {
				myURL = new URL(urlProducao + "/api/v2/auth/login");
				loginDocket = loginProd;
				senhaDocket = senhaProd;
				if (!CommonsUtil.semValor(user)) {
					if (!CommonsUtil.semValor(user.getLoginDocket()) && !CommonsUtil.semValor(user.getSenhaDocket())) {
						loginDocket = user.getLoginDocket();
						senhaDocket = user.getSenhaDocket();
					}
				}
			}

			jsonObj.put("login", loginDocket);
			jsonObj.put("senha", senhaDocket);

			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");

			myURLConnection.setDoOutput(true);

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = jsonObj.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
				os.close();
			}

			JSONObject myResponse = null;
			int status = myURLConnection.getResponseCode();

			myResponse = engineJSONSucesso(myURLConnection.getInputStream());

			this.tokenLogin = "";

			if (status == HTTP_COD_SUCESSO) {
				if (myResponse.has("token")) {
					if (!myResponse.isNull("token")) {
						this.tokenLogin = myResponse.getString("token");
					}
				}
			} else {
				System.out.println(jsonObj.toString());
				if (status == 401) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Docket - Login] Falha de autenticação. Token inválido!", ""));
				}
				if (status == 400) {
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "[Docket - Login] Erro no login.", ""));
				}
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "[Docket - Login] Erro não conhecido!", ""));
			}

			myURLConnection.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(jsonObj.toString());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public void uploadREA(DocumentoAnalise documentoAnalise, User user) { // POST para gerar pedido
		//FacesContext context = FacesContext.getCurrentInstance();
		
		FileService fileService =  new FileService();
		FileUploaded selectedFile = new FileUploaded();
		selectedFile.setPath(documentoAnalise.getPath().replace(documentoAnalise.getIdentificacao(), ""));
		selectedFile.setName( CommonsUtil.removeAcentos( documentoAnalise.getIdentificacao()));
		selectedFile.setPathOrigin("analise");
		
		InputStream  stream = new ByteArrayInputStream( fileService.abrirDocumentos(selectedFile
				,documentoAnalise.getContratoCobranca().getNumeroContrato(),
				user));
		
//		File file = new File(documentoAnalise.getPath());
		
		String twoHyphens = "--";
		String boundary = "*****";
		String crlf = "\r\n";
		try {
			loginDocket(user);
			int HTTP_COD_SUCESSO = 201;

			URL myURL;
			if (SiscoatConstants.DEV && CommonsUtil.sistemaWindows()) {
				myURL = new URL(urlHomologacao + "/api/v2/" + organizacao_url + "/rea/matriculas");
			} else {
				myURL = new URL(urlProducao + "/api/v2/" + organizacao_url + "/rea/matriculas");
			}

			String webHookJWT = JwtUtil.generateJWTWebhook(true);
			while (webHookJWT.length() > (256 - SiscoatConstants.URL_SISCOAT_REA_WEBHOOK.length())) {
				webHookJWT = JwtUtil.generateJWTWebhook(false);
			}

			String s = new String(webHookJWT.getBytes(), Charset.forName("UTF-8"));

			String urlWebhook = SiscoatConstants.URL_SISCOAT_REA_WEBHOOK + webHookJWT;
			String authorization = "Bearer " + this.tokenLogin;
//			LZString.compress(webHookJWT);
//			LZString.compressToBase64(webHookJWT);
//			LZString.compressToUTF16(webHookJWT);

			MultipartUtility multipart = new MultipartUtility(myURL.toString(), "utf-8", authorization);

			multipart.addFormField("urlWebhook", urlWebhook);

			multipart.addFileInputStream("arquivo", selectedFile.getName(), stream);

			HttpURLConnection myURLConnection = multipart.finish();

			JSONObject myResponse = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				//context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						//"Docket: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", ""));
				//System.out.println(jsonREA.toString());
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
				StringBuffer response = new StringBuffer();
				String inputLine;
				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
				br.close();

				ReaWebhookRetorno reaWebhookRetorno = GsonUtil.fromJson(response.toString(), ReaWebhookRetorno.class);

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();

				documentoAnalise.setIdRemoto(reaWebhookRetorno.getId());
				documentoAnaliseDao.merge(documentoAnalise);
				//context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Matrícula enviada com sucesso", ""));
				// myResponse = getJSONSucesso(myURLConnection.getInputStream());
			}
			myURLConnection.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JSONObject engineBodyJsonEngine(PagadorRecebedor pagador) {
		JSONObject jsonDocketBodyPedido = new JSONObject();
		if (!CommonsUtil.semValor(pagador.getCpf())) {
			jsonDocketBodyPedido.put("idProviderFlow", engineIdproviderFlowPF);
			jsonDocketBodyPedido.put("fields", engineJsonPF(pagador));
		} else {
			jsonDocketBodyPedido.put("idProviderFlow", engineIdproviderFlowPJ);
			jsonDocketBodyPedido.put("fields", engineJsonPJ(pagador));
		}
		String webHookJWT = JwtUtil.generateJWTWebhook(true);
		jsonDocketBodyPedido.put("urlWebhook", SiscoatConstants.URL_SISCOAT_ENGINE_WEBHOOK + webHookJWT);

		return jsonDocketBodyPedido;
	}

	private JSONArray engineJsonPF(PagadorRecebedor pagador) {
		JSONArray jsonDocumentosArray = new JSONArray();

		JSONObject fieldsCPF = new JSONObject();
		fieldsCPF.put("field", "cpf");
		fieldsCPF.put("value", pagador.getCpf());

		JSONObject fieldsNome = new JSONObject();
		fieldsNome.put("field", "nome");
		fieldsNome.put("value", pagador.getNome());

		JSONObject fieldsDtNsc = new JSONObject();
		fieldsDtNsc.put("field", "data_nascimento");
		fieldsDtNsc.put("value", CommonsUtil.formataData(pagador.getDtNascimento(), "dd/MM/yyy"));

		jsonDocumentosArray.put(fieldsCPF);
		jsonDocumentosArray.put(fieldsNome);
		jsonDocumentosArray.put(fieldsDtNsc);

		return jsonDocumentosArray;
	}

	private JSONArray engineJsonPJ(PagadorRecebedor pagador) {
		JSONArray jsonDocumentosArray = new JSONArray();

		JSONObject fieldsCPF = new JSONObject();
		fieldsCPF.put("field", "cnpj");
		fieldsCPF.put("value", pagador.getCnpj());

		JSONObject fieldsNome = new JSONObject();
		fieldsNome.put("field", "nome");
		fieldsNome.put("value", pagador.getNome());

		jsonDocumentosArray.put(fieldsCPF);
		jsonDocumentosArray.put(fieldsNome);

		return jsonDocumentosArray;
	}

	private JSONObject engineJSONSucesso(InputStream inputStream) { // Pega resultado da API
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private DataEngineIdSend engineJSONRetorno(InputStream inputStream) { // Pega resultado da API
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// READ JSON response and print
			DataEngineIdSend myResponse = GsonUtil.fromJson(response.toString(), DataEngineIdSend.class);

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private DocketRetorno docketJSONRetorno(InputStream inputStream) { // Pega resultado da API
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// READ JSON response and print
			DocketRetorno myResponse = GsonUtil.fromJson(response.toString(), DocketRetorno.class);

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	private JSONObject getBodyJsonPedido(ContratoCobranca objetoContratoCobranca, List<PagadorRecebedor> listaPagador) { // JSON
																															// p/
																															// pedido
		JSONObject jsonDocketBodyPedido = new JSONObject();
		jsonDocketBodyPedido.put("pedido", getJsonPedido(objetoContratoCobranca, listaPagador));
		return jsonDocketBodyPedido;
	}

	private JSONObject getJsonPedido(ContratoCobranca objetoContratoCobranca, List<PagadorRecebedor> listaPagador) { // JSON
																														// p/
																														// pedido
		JSONArray jsonDocumentosArray = new JSONArray();
		for (PagadorRecebedor pagador : listaPagador) {
			for (DocumentosPagadorDocket documentos : pagador.getDocumentosDocket()) {
				jsonDocumentosArray.put(getJsonDocumentos(pagador, documentos));
			}
		}

		JSONObject jsonDocketPedido = new JSONObject();
		String nomePedido = "";
		if(CommonsUtil.semValor(objetoContratoCobranca.getId())){
			if(listaPagador.size() > 0) {				
				nomePedido = "00000 - " + listaPagador.get(0).getNome();
			} else {
				nomePedido = "00000 - nome";
			}
		} else {
			nomePedido = objetoContratoCobranca.getNumeroContrato() + " - "
					+ objetoContratoCobranca.getPagador().getNome();
		}
		// jsonDocketPedido = new JSONObject();
		jsonDocketPedido.put("lead", nomePedido);
		jsonDocketPedido.put("documentos", jsonDocumentosArray);
		String webHookJWT = JwtUtil.generateJWTWebhook(true);
		while (webHookJWT.length() > (256 - SiscoatConstants.URL_SISCOAT_DOCKET_WEBHOOK.length())) {
			webHookJWT = JwtUtil.generateJWTWebhook(false);
		}
		String webhook = SiscoatConstants.URL_SISCOAT_DOCKET_WEBHOOK + webHookJWT;
		jsonDocketPedido.put("urlWebHookEntregaDocumento", webhook);
		

		return jsonDocketPedido;
	}

	private JSONObject getJsonDocumentos(PagadorRecebedor pagador, DocumentosPagadorDocket documentosPagador) { // JSON
																												// p/
																												// pedido
		// PagadorRecebedor pagador = new PagadorRecebedor();
		JSONObject jsonDocketDocumentos = new JSONObject();
		DocumentosDocket documento = documentosPagador.getDocumentoDocket();
		String estadoId = documentosPagador.getEstadoId();
		String cidadeId = documentosPagador.getCidadeId();
		// jsonDocketDocumentos = new JSONObject();
		jsonDocketDocumentos.put("documentKitId", documento.getDocumentKitId());
		jsonDocketDocumentos.put("produtoId", documento.getProdutoId());
		jsonDocketDocumentos.put("kitId", kitIdGalleria);
		jsonDocketDocumentos.put("kitNome", kitNomeGalleria);
		jsonDocketDocumentos.put("documentoNome", documento.getDocumentoNome());
		if (!CommonsUtil.semValor(pagador.getCpf())) {
			jsonDocketDocumentos.put("titularTipo", "PESSOA_FISICA");
		} else {
			jsonDocketDocumentos.put("titularTipo", "PESSOA_JURIDICA");
		}
		jsonDocketDocumentos.put("campos", getJsonCampos(pagador, estadoId, cidadeId));

		return jsonDocketDocumentos;
	}

	public JSONObject getJsonCampos(PagadorRecebedor pagador, String estadoId, String cidadeId) { // JSON p/ pedido
		JSONObject jsonDocketCampos = new JSONObject();
		// jsonDocketCampos = new JSONObject();
		if (!CommonsUtil.semValor(pagador.getCpf())) {
			jsonDocketCampos.put("nomeCompleto", pagador.getNome());
			jsonDocketCampos.put("cpf", pagador.getCpf());
			jsonDocketCampos.put("nomeMae", pagador.getNomeMae());
			jsonDocketCampos.put("rg", pagador.getRg());
			jsonDocketCampos.put("dataNascimento", CommonsUtil.formataData(pagador.getDtNascimento(), "yyyy-MM-dd")); // 2003-01-10T02:00:00.000+0000
		} else {
			jsonDocketCampos.put("razaoSocial", pagador.getNome());
			jsonDocketCampos.put("cnpj", pagador.getCnpj());
		}
		jsonDocketCampos.put("cidade", cidadeId);
		jsonDocketCampos.put("estado", estadoId);

		return jsonDocketCampos;
	}

	@SuppressWarnings("unused")
	public FacesMessage criaPedidoDocket(ContratoCobranca objetoContratoCobranca, List<PagadorRecebedor> listaPagador,
			String estadoImovel, String cidadeImovel, User user) {

		// POST para gerar pedido
		FacesContext context = FacesContext.getCurrentInstance();
		DocketDao docketDao = new DocketDao();
		if(!CommonsUtil.semValor(objetoContratoCobranca.getId())) {
			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(objetoContratoCobranca);
			if(docketDao.findByFilter("objetoContratoCobranca", objetoContratoCobranca).size() > 0) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Pedido desse contrato já existe!!!!!!", ""));	
				return null;
			}
		}

		try {
			loginDocket(user);
			int HTTP_COD_SUCESSO = 200;

			URL myURL;
			if (SiscoatConstants.DEV && CommonsUtil.sistemaWindows()) {
				myURL = new URL(urlHomologacao + "/api/v2/" + organizacao_url + "/shopping-documentos/alpha/pedidos");
			} else {
				myURL = new URL(urlProducao + "/api/v2/" + organizacao_url + "/shopping-documentos/alpha/pedidos");
			}

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();

			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer " + this.tokenLogin);
			myURLConnection.setDoOutput(true);

//			JSONObject myResponse = null;
			JSONObject jsonWhatsApp = getBodyJsonPedido(objetoContratoCobranca, listaPagador);

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = jsonWhatsApp.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			/*
			 * try(BufferedReader br = new BufferedReader( new
			 * InputStreamReader(myURLConnection.getInputStream(), "utf-8"))) {
			 * StringBuilder response = new StringBuilder(); String responseLine = null;
			 * while ((responseLine = br.readLine()) != null) {
			 * response.append(responseLine.trim()); }
			 * System.out.println(response.toString()); }
			 */

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Docket: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", ""));
				System.out.println(jsonWhatsApp.toString());
			} else {
				
				DocketRetorno myResponse = docketJSONRetorno(myURLConnection.getInputStream());
				if(!CommonsUtil.semValor(objetoContratoCobranca.getId())) {
					ContratoCobrancaDao cDao = new ContratoCobrancaDao();
					cDao.merge(objetoContratoCobranca);					
				} else {
					objetoContratoCobranca = null;
				}
				Docket docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "", cidadeImovel, "",
						user.getName(), gerarDataHoje(), myResponse.getPedido().getId(), myResponse.getPedido().getIdExibicao());
				docketDao.create(docket);

				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido feito com sucesso", ""));
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private JSONObject getBodyJsonPedido(List<DocumentoAnalise> listaDocumentoAnalise, String etapa) { // JSON
		// p/
		// pedido
		JSONObject jsonDocketBodyPedido = new JSONObject();
		jsonDocketBodyPedido.put("pedido", getJsonPedido(listaDocumentoAnalise, etapa));
		return jsonDocketBodyPedido;
	}

	private JSONObject getJsonPedido(List<DocumentoAnalise> listaDocumentoAnalise, String etapa) { // JSON
		// p/
		// pedido
		JSONArray jsonDocumentosArray = new JSONArray();
		for (DocumentoAnalise docAnalise : listaDocumentoAnalise) {
			PagadorRecebedor pagador = docAnalise.getPagador();
			
			for (DocketConsulta consultas : docAnalise.getDocketConsultas()) {
				if(CommonsUtil.semValor(consultas.getIdDocket())) {
					jsonDocumentosArray.put(getJsonDocumentos(pagador, consultas));
				}
			}
		}
		
		ContratoCobranca contrato = listaDocumentoAnalise.get(0).getContratoCobranca();
	
		JSONObject jsonDocketPedido = new JSONObject();
		String nomePedido = "";
		if (CommonsUtil.semValor(contrato.getId())) {
			if (CommonsUtil.semValor(contrato.getPagador())) {
				nomePedido = "00000 - " + contrato.getPagador().getNome() + " - " + etapa;
			} else {
				nomePedido = "00000 - nome" + " - " + etapa;
			}
		} else {
			nomePedido = contrato.getNumeroContrato() + " - " + contrato.getPagador().getNome().trim() + " - " + etapa;
		}
	// jsonDocketPedido = new JSONObject();
		jsonDocketPedido.put("lead", nomePedido);
		jsonDocketPedido.put("documentos", jsonDocumentosArray);
		String webHookJWT = JwtUtil.generateJWTWebhook(true);
		while (webHookJWT.length() > (256 - SiscoatConstants.URL_SISCOAT_DOCKET_WEBHOOK.length())) {
			webHookJWT = JwtUtil.generateJWTWebhook(false);
		}
		String webhook = SiscoatConstants.URL_SISCOAT_DOCKET_WEBHOOK + webHookJWT;
		jsonDocketPedido.put("urlWebHookEntregaDocumento", webhook);
	
		return jsonDocketPedido;
	}

	private JSONObject getJsonDocumentos(PagadorRecebedor pagador, DocketConsulta consulta) { // JSON
	// p/
	// pedido
	// PagadorRecebedor pagador = new PagadorRecebedor();
		JSONObject jsonDocketDocumentos = new JSONObject();
		DocumentosDocket documento = consulta.getDocketDocumentos();
		String estadoId = consulta.getEstadoId();
		String cidadeId = consulta.getCidadeId();
	// jsonDocketDocumentos = new JSONObject();
		jsonDocketDocumentos.put("documentKitId", documento.getDocumentKitId());
		jsonDocketDocumentos.put("produtoId", documento.getProdutoId());
		jsonDocketDocumentos.put("kitId", kitIdGalleria);
		jsonDocketDocumentos.put("kitNome", kitNomeGalleria);
		jsonDocketDocumentos.put("documentoNome", documento.getDocumentoNome());
		if (!CommonsUtil.semValor(pagador.getCpf())) {
			jsonDocketDocumentos.put("titularTipo", "PESSOA_FISICA");
		} else {
			jsonDocketDocumentos.put("titularTipo", "PESSOA_JURIDICA");
		}
		jsonDocketDocumentos.put("campos", getJsonCampos(pagador, estadoId, cidadeId));
	
		return jsonDocketDocumentos;
	}

	@SuppressWarnings("unused")
	public FacesMessage criaPedidoDocketDocumentoAnalise(List<DocumentoAnalise> listaDocumentoAnalise, User user, String etapa) {

		// POST para gerar pedido
		FacesContext context = FacesContext.getCurrentInstance();
		DocketDao docketDao = new DocketDao();
		
		try {
			loginDocket(user);
			int HTTP_COD_SUCESSO = 200;

			URL myURL;
			if (SiscoatConstants.DEV && CommonsUtil.sistemaWindows()) {
				myURL = new URL(urlHomologacao + "/api/v2/" + organizacao_url + "/shopping-documentos/alpha/pedidos");
			} else {
				myURL = new URL(urlProducao + "/api/v2/" + organizacao_url + "/shopping-documentos/alpha/pedidos");
			}

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();

			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer " + this.tokenLogin);
			myURLConnection.setDoOutput(true);

//			JSONObject myResponse = null;
			JSONObject jsonWhatsApp = getBodyJsonPedido(listaDocumentoAnalise, etapa);

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = jsonWhatsApp.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			/*
			 * try(BufferedReader br = new BufferedReader( new
			 * InputStreamReader(myURLConnection.getInputStream(), "utf-8"))) {
			 * StringBuilder response = new StringBuilder(); String responseLine = null;
			 * while ((responseLine = br.readLine()) != null) {
			 * response.append(responseLine.trim()); }
			 * System.out.println(response.toString()); }
			 */

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Docket: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", ""));
				System.out.println(jsonWhatsApp.toString());
			} else {
				ContratoCobranca objetoContratoCobranca = listaDocumentoAnalise.get(0).getContratoCobranca();
				DocketRetorno myResponse = docketJSONRetorno(myURLConnection.getInputStream());
				if(!CommonsUtil.semValor(objetoContratoCobranca.getId())) {
					ContratoCobrancaDao cDao = new ContratoCobrancaDao();
					cDao.merge(objetoContratoCobranca);					
				} else {
					objetoContratoCobranca = null;
				}
				List<PagadorRecebedor> listPagador = new ArrayList<PagadorRecebedor>();
				for (DocumentoAnalise documentoAnalise : listaDocumentoAnalise) {
					listPagador.add(documentoAnalise.getPagador());
				}
				Docket docket = new Docket(objetoContratoCobranca, listPagador, "", "", "", "",
						user.getName(), gerarDataHoje(), myResponse.getPedido().getId(), myResponse.getPedido().getIdExibicao());
				docketDao.create(docket);
				
				DocketConsultaDao consultaDao = new DocketConsultaDao();
				DocumentoAnaliseDao analiseDao = new DocumentoAnaliseDao();
				
				for (DocumentoAnalise documentoAnalise : listaDocumentoAnalise) {
					for(DocketConsulta docketConsulta : documentoAnalise.getDocketConsultas()) {
						for (DocketWebhookRetornoDocumento retorno : myResponse.getPedido().documentos) {
							if(CommonsUtil.mesmoValor(retorno.documentKitId, docketConsulta.getDocketDocumentos().getDocumentKitId())
								&&CommonsUtil.mesmoValor(retorno.campos.estado, docketConsulta.getEstadoId())
								&& CommonsUtil.mesmoValor(retorno.campos.cidade, docketConsulta.getCidadeId())) {
								if((CommonsUtil.mesmoValor(retorno.campos.cpf, CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf())))
									|| CommonsUtil.mesmoValor(retorno.campos.cnpj, CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf()))){
									docketConsulta.setIdDocket(retorno.id);
									consultaDao.create(docketConsulta);
								}
							}
						}
						docketConsulta.setStatus("Ag. Retorno");
						docketConsulta.setDataConsulta(gerarDataHoje());
						docketConsulta.setUsuario(user);
						consultaDao.merge(docketConsulta);
					}
					analiseDao.merge(documentoAnalise);
				}
				
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido feito com sucesso", ""));
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public DocketRetornoConsulta verificarCertidoesContrato(ContratoCobranca contrato, String idCallManager) {
		
		try {

			loginDocket(null);
			int HTTP_COD_SUCESSO = 200;
			URL myURL;
			if (SiscoatConstants.DEV && CommonsUtil.sistemaWindows()) {
				myURL = new URL(urlHomologacao + "/api/v2/" + organizacao_url + "/shopping-documentos/alpha/pedidos/" + idCallManager);
			} else {
				myURL = new URL(urlProducao + "/api/v2/" + organizacao_url + "/shopping-documentos/alpha/pedidos/" + idCallManager);
			}

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", tokenLogin);
			myURLConnection.setDoOutput(true);

			int certidoesProntas = 0;
			DocketRetornoConsulta docketRetorno =null;
			
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				System.out.println("Não foi possivle consultar docket. IdCallManager: " + idCallManager + " contrato: " + contrato.toString());
			} else {
				JSONObject retornoConsulta = null;
				retornoConsulta = getJsonSucesso(myURLConnection.getInputStream());

//				System.out.println(retornoConsulta.toString());
				docketRetorno =  GsonUtil.fromJson(retornoConsulta.toString(), DocketRetornoConsulta.class);
				
				if(CommonsUtil.semValor(docketRetorno)) {
					return null;
				}				
				
				if( CommonsUtil.semValor(docketRetorno.getPedido())) {
					return null;
				}
				
				if( CommonsUtil.semValor(docketRetorno.getPedido().getDocumentos())) {
					return null;
				}
				certidoesProntas = CommonsUtil.intValue( docketRetorno.getPedido().getDocumentos().stream().filter(d -> CommonsUtil.mesmoValor(d.getStatus(), "ENTREGUE")).count());
				
//				JSONArray documentos = pedido.getJSONArray("documentos");
//				for(int i = 0 ; i < documentos.length(); i++) {
//					JSONObject doc = documentos.getJSONObject(i);
//					if(!doc.has("status")) {
//						continue;
//					}			
//					if(CommonsUtil.mesmoValor(doc.get("status"), "ENTREGUE")) {
//						certidoesProntas++;
//					}
//				}
				contrato.setCertidoesProntas(certidoesProntas);
				contrato.setTotalCertidoesDocket(docketRetorno.getPedido().getDocumentos().size());
			}

			myURLConnection.disconnect();
			return docketRetorno;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject getJsonSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void salvarPdfRetorno(DocumentoAnalise documentoAnalise, String base64, String nomeConsulta, String diretorio) {
		String nomeAnalise = documentoAnalise.getPagador().getNome();
		String numeroContrato = documentoAnalise.getContratoCobranca().getNumeroContrato();
		if(CommonsUtil.semValor(numeroContrato)) {
			return;
		}
		FileUploaded pdfRetorno = new FileUploaded();
		pdfRetorno.setFileBase64(base64);
		pdfRetorno.setName(nomeConsulta + " - " + nomeAnalise + ".pdf");
		FileService fileService = new FileService();
		User user = new UserDao().findById((long) -1);
		fileService.salvarDocumentoBase64(pdfRetorno, numeroContrato, diretorio, user);
	}

	public String getPdfBase64(String documento) {
		DocketWebhookRetornoDocumento documentoPdf = GsonUtil.fromJson(documento, DocketWebhookRetornoDocumento.class);
        try { 
        	String urlComprovante = documentoPdf.getArquivos().get(0).getLinks().get(0).getHref();
            java.net.URL url = new java.net.URL(urlComprovante);
            InputStream is = url.openStream();
            byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
            byte[] encoded = Base64.getEncoder().encode(bytes);
            return new String(encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
