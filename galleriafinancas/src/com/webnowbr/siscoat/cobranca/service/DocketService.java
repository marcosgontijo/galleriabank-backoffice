package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DataEngine;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DataEngineDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.model.User;

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

	
	
	public FacesMessage engineCriarConsulta(DataEngine engine, User usuarioLogado) {
		return engineCriarConsulta(null, engine, usuarioLogado);
	}
	

	public FacesMessage engineCriarConsulta(DocumentoAnalise documentoAnalise, DataEngine engine, User usuarioLogado) { // POST para gerar consulta
		DataEngineDao engineDao = new DataEngineDao();
//		if (engineDao.findByFilter("pagador", engine.getPagador()).size() > 0) {
////			context.addMessage(null,);	
//			return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta já existente!", "");
//		}
		if (!CommonsUtil.semValor(engine.getIdCallManager())) {
			if (documentoAnalise != null ) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setEngine(engine);
				documentoAnalise.setRetornoEngine("consulta efetuada anteriormente Id: " + engine.getId() );
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
				if (engine.getContrato() != null && engine.getContrato().getId() > 0 )
				cDao.merge(engine.getContrato());
				
				if (engine.getId() <= 0) {
					engineDao.create(engine);
				}
				
				if (documentoAnalise != null ) {
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
					.filter(e -> DateUtil.isAfterDate( DateUtil.adicionarDias(DateUtil.getDataHoje(), -30), e.getData()))
					.sorted(Comparator.comparing(DataEngine::getData).reversed()).findFirst().orElse(null);
		}
		
		if(CommonsUtil.semValor(engine)) {
			engine = new DataEngine(pagadorAdicionar);
		}	
		if(!CommonsUtil.semValor(objetoContratoCobranca) && CommonsUtil.semValor(engine.getContrato())) {
			engine.setContrato(objetoContratoCobranca);
		}
		
		return engine;
		
		
	
		
	}
	
	
	public void baixarDocumentoEngine(DataEngine engine) {
		FacesContext context = FacesContext.getCurrentInstance();
		if(CommonsUtil.semValor(engine.getIdCallManager())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Consulta sem IdCallManager", ""));	
			return;
		}
		if(CommonsUtil.semValor(engine.getPdfBase64())) {
			PegarPDFDataEngine(engine);
		}
	}
	
	public void salvarDetalheDocumentoEngine(DocumentoAnalise documentoAnalise) {
		FacesContext context = FacesContext.getCurrentInstance();
		if(CommonsUtil.semValor(documentoAnalise.getEngine().getIdCallManager())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Consulta sem IdCallManager", ""));	
			return;
		}
		documentoAnalise.setRetornoEngine(PegarDetalheDataEngine(documentoAnalise.getEngine()));
		
		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		documentoAnaliseDao.merge(documentoAnalise);
	}
	

	private void PegarPDFDataEngine(DataEngine engine) {	//POST para pegar pdf	
		FacesContext context = FacesContext.getCurrentInstance();
		if(CommonsUtil.semValor(engine.getIdCallManager())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Não Foi gerado ID da consulta!!!", ""));	
			return;
		}
			
		String idproviderFlow;
		if(!CommonsUtil.semValor(engine.getPagador().getCpf())) {
			idproviderFlow = engineIdproviderFlowPF;
		} else {
			idproviderFlow = engineIdproviderFlowPJ;
		}
		
		try {
			//loginDocket();
			int HTTP_COD_SUCESSO = 200;
			
			URL myURL;
			myURL = new URL(engineUrl + "/dossie/"+ idproviderFlow + "/"+ engine.getIdCallManager() + "?base64=true");		
	
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();		
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.addRequestProperty("x-api-key",  this.engineChaveApi);
			
			JSONObject myResponse = null;	
								
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Engine: Falha  (Cod: " + myURLConnection.getResponseCode() + ")",""));
				System.out.println(myURL.toString());
			} else {
				//docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" , cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));
				BufferedReader in;
				in = new BufferedReader(
						new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
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
	

	private String PegarDetalheDataEngine(DataEngine engine) {	//POST para pegar pdf	
		FacesContext context = FacesContext.getCurrentInstance();
		if(CommonsUtil.semValor(engine.getIdCallManager())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Não Foi gerado ID da consulta!!!", ""));	
			return null;
		}
			
		String idproviderFlow;
		if(!CommonsUtil.semValor(engine.getPagador().getCpf())) {
			idproviderFlow = engineIdproviderFlowPF;
		} else {
			idproviderFlow = engineIdproviderFlowPJ;
		}
		
		try {
			//loginDocket();
			int HTTP_COD_SUCESSO = 200;
			
			URL myURL;
			myURL = new URL(engineUrl + "/api/callmanager/result/"+ engine.getIdCallManager());		
	
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();		
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.addRequestProperty("x-api-key",  this.engineChaveApi);
			
			JSONObject myResponse = null;	
			String result = null;				
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Engine: Falha  (Cod: " + myURLConnection.getResponseCode() + ")",""));
				System.out.println(myURL.toString());
			} else {
				//docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" , cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));
				BufferedReader in;
				in = new BufferedReader(
						new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
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
	
	private JSONObject engineBodyJsonEngine(PagadorRecebedor pagador) {
		JSONObject jsonDocketBodyPedido = new JSONObject();
		if (!CommonsUtil.semValor(pagador.getCpf())) {
			jsonDocketBodyPedido.put("idProviderFlow", engineIdproviderFlowPF);
			jsonDocketBodyPedido.put("fields", engineJsonPF(pagador));
		} else {
			jsonDocketBodyPedido.put("idProviderFlow", engineIdproviderFlowPJ);
			jsonDocketBodyPedido.put("fields", engineJsonPJ(pagador));
		}
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

}
