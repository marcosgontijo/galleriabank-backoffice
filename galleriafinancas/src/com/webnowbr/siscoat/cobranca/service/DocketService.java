package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Docket;
import com.webnowbr.siscoat.cobranca.db.model.DocketConsulta;
import com.webnowbr.siscoat.cobranca.db.model.DocketRetorno;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosDocket;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosPagadorDocket;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketConsultaDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.model.docket.DocketDocumento;
import com.webnowbr.siscoat.cobranca.model.docket.DocketRetornoConsulta;
import com.webnowbr.siscoat.cobranca.vo.FileUploaded;
import com.webnowbr.siscoat.cobranca.ws.endpoint.DocketWebhookRetornoDocumento;
import com.webnowbr.siscoat.cobranca.ws.endpoint.ReaWebhookRetorno;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.MultipartUtility;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.jwt.common.JwtUtil;
import br.com.galleriabank.serasarelato.cliente.util.GsonUtil;

public class DocketService {

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

	@SuppressWarnings("unused")
	public void loginDocket(User user) { // POST pra pegar token
		JSONObject jsonObj = new JSONObject();
		try {
			//FacesContext context = FacesContext.getCurrentInstance();
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

			myResponse = docketJSONSucesso(myURLConnection.getInputStream());

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
					System.out.println("[Docket - Login] Falha de autenticação. Token inválido!");
				}
				if (status == 400) {
					System.out.println("[Docket - Login] Erro no login.");
				}
				System.out.println("[Docket - Login] Erro não conhecido!");
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
				System.out.println("---------------- Docket Falha no Pedido ---------------- ");
				System.out.println(jsonWhatsApp.toString());
				System.out.println("---------------- Docket Falha no Pedido ---------------- ");
			} else {
				
				DocketRetorno myResponse = docketJSONRetorno(myURLConnection.getInputStream());
				if(!CommonsUtil.semValor(objetoContratoCobranca.getId())) {
					ContratoCobrancaDao cDao = new ContratoCobrancaDao();
					cDao.merge(objetoContratoCobranca);					
				} else {
					objetoContratoCobranca = null;
				}
				Docket docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "", cidadeImovel, "",
						user.getName(), DateUtil.gerarDataHoje(), myResponse.getPedido().getId(), myResponse.getPedido().getIdExibicao());
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
						user.getName(), DateUtil.gerarDataHoje(), myResponse.getPedido().getId(), myResponse.getPedido().getIdExibicao());
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
						docketConsulta.setDataConsulta(DateUtil.gerarDataHoje());
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
	
	public DocketRetornoConsulta buscarRetornoPedido(String idCallManager) {
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

			DocketRetornoConsulta docketRetorno =null;
			
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				System.out.println("Não foi possivle consultar docket. IdCallManager: " + idCallManager);
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

	public String getPdfBase64(String documento) {
		DocketWebhookRetornoDocumento documentoPdf = GsonUtil.fromJson(documento, DocketWebhookRetornoDocumento.class);
		return getPdfBase64(documentoPdf);
    }
	
	public String getPdfBase64(DocketWebhookRetornoDocumento documentoPdf) {
		try {
			loginDocket(null);
			int HTTP_COD_SUCESSO = 200;
			URL myURL;
			
			String idCallManager = documentoPdf.getArquivos().get(0).getId();
			if (SiscoatConstants.DEV && CommonsUtil.sistemaWindows()) {
				myURL = new URL(urlHomologacao + "/api/v2/" + organizacao_url + "/downloads/" + idCallManager);
			} else {
				myURL = new URL(urlProducao + "/api/v2/" + organizacao_url + "/downloads/" + idCallManager);
			}

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Authorization", tokenLogin);
			myURLConnection.setDoOutput(true);

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				System.out.println("Erro: "+ myURLConnection.getResponseCode() + " Não foi possivel consultar pdf docket. IdCallManager: " + idCallManager);
			} else {
				byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(myURLConnection.getInputStream());
				byte[] encoded = Base64.getEncoder().encode(bytes);
				return new String(encoded);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }
	
	private JSONObject docketJSONSucesso(InputStream inputStream) { // Pega resultado da API
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
	
	public void atualizaRetorno(List<DocumentoAnalise> listDocAnalise) {
		for(DocumentoAnalise docAnalise : listDocAnalise) {
			if(CommonsUtil.semValor(docAnalise.getDocketConsultas()) 
				|| docAnalise.getDocketConsultas().size() <= 0) 
				continue;
			for(DocketConsulta docket : docAnalise.getDocketConsultas()) {
				atualizaRetornoCertidaoDocket(docket, docAnalise.getContratoCobranca());
			}
		}
		return;
	}
	
	public void atualizaRetornoCertidaoDocket(DocketConsulta docket, ContratoCobranca contrato) {
		DocketConsultaDao consultaDao = new DocketConsultaDao();
		DocketService docketService = new DocketService();
		String retorno = "";
		if(!CommonsUtil.semValor(docket.getRetorno()) && !CommonsUtil.semValor(docket.getPdf())) {
			docket.setStatus("Consulta Concluída");
			consultaDao.merge(docket);
			return;
		}
		if(CommonsUtil.semValor(docket.getIdDocket())) {
			docket.setStatus("Falha: Consulta Sem Id Docket. Favor Consultar Novamente");
			consultaDao.merge(docket);
			return;
		}
		if(CommonsUtil.semValor(docket.getPdf())) {
			DocketDao docketDao = new DocketDao();
			List<Docket> lista = docketDao.findByFilter("objetoContratoCobranca", contrato);
			if(lista.size() > 0) {
				DocketRetornoConsulta retornoObject = docketService.buscarRetornoPedido(docketDao.findByFilter("objetoContratoCobranca", contrato).get(0).getIdCallManager());
				for(DocketDocumento documento : retornoObject.getPedido().getDocumentos()) {
					if(CommonsUtil.mesmoValor(documento.getId(), docket.getIdDocket())) {
						docket.setRetorno(GsonUtil.toJson(documento));
					}
				}
			} else {
				return;
			}
		}
		
		if(!CommonsUtil.semValor(docket.getRetorno())) {
			retorno = docket.getRetorno();
		} else {
			docket.setStatus("Falha: Consulta Sem Retorno. Verificar Plataforma");
			consultaDao.merge(docket);
			return;
		}
		
		DocketWebhookRetornoDocumento documentoRetorno = GsonUtil.fromJson(retorno, DocketWebhookRetornoDocumento.class);			
		DocketConsulta docketConsulta = consultaDao.getConsultasExistentesWebhook(documentoRetorno.id);
		docketConsulta.setStatus("Concluido");
		docketConsulta.setRetorno(GsonUtil.toJson(documentoRetorno));
		consultaDao.merge(docketConsulta);
		DocketWebhookRetornoDocumento documentoPdf = GsonUtil.fromJson(docketConsulta.getRetorno(), DocketWebhookRetornoDocumento.class);
		if(!CommonsUtil.semValor(documentoPdf.getArquivos()) && documentoPdf.getArquivos().size() > 0) {
			String base64 = docketService.getPdfBase64(documentoPdf);
			docketConsulta.setPdf(base64);
			FileService fileService = new FileService();
			fileService.salvarPdfRetorno(docketConsulta.getDocumentoAnalise(), base64, documentoRetorno.getDocumentoNome(), "interno");
		} else {
			docketConsulta.setStatus("Consulta Sem PDF");
		}
		consultaDao.merge(docketConsulta);
	}
}
