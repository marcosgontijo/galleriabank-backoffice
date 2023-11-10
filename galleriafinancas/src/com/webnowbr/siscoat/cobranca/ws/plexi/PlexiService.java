package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.service.FileService;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.jwt.common.JwtUtil;

public class PlexiService {

	String token = "Bearer omA1k5xkozRljXBw0M0EviFVhh2F5qvJAqA8y8wbYMC75tIMd4GQsBsmvCQ2OTcO81XoXBczfj1BUwhhTISUouWX55g0EPleclII";
	private String urlHomologacao = "https://sandbox.plexi.com.br"; 
	private String urlProducao = "https://api.plexi.com.br";//https://api.plexi.com.br
	
	
	public FacesMessage PedirConsulta(PlexiConsulta plexiCosulta, User usuarioLogado) {
		Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).create();
		String plexiJson = gson.toJson(plexiCosulta);
		FacesMessage result = null;
		try {
			int HTTP_COD_SUCESSO = 201;
			int HTTP_COD_SUCESSO2 = 202;
			URL myURL;
			
			if (SiscoatConstants.DEV && CommonsUtil.sistemaWindows()) {
				myURL = new URL(urlHomologacao + plexiCosulta.getPlexiDocumentos().getUrl());
			} else {
				myURL = new URL(urlProducao + plexiCosulta.getPlexiDocumentos().getUrl());
			}
			
			String webHookJWT = JwtUtil.generateJWTWebhook(true);
			String webhook = SiscoatConstants.URL_SISCOAT_PLEXI_WEBHOOK + webHookJWT;
			
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", token);
			myURLConnection.addRequestProperty("Callback", webhook);
			
			myURLConnection.setDoOutput(true);
			
			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = plexiJson.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO 
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				System.out.println(plexiJson);
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
						"Erro: " +  plexiCosulta.getPlexiDocumentos().getNome() 
						+ " / HTTP:" + myURLConnection.getResponseCode(), "");
				System.out.println(myURL);
				System.out.println(getJsonSucessoStr(myURLConnection.getErrorStream()).toString());
				System.out.println(myURLConnection.getResponseMessage());
			} else {
				JSONObject retorno = null;
				retorno = getJsonSucesso(myURLConnection.getInputStream());
				if (retorno.has("requestId")) {
					plexiCosulta.setRequestId(retorno.getString("requestId"));
				}
				
				PlexiConsultaDao plexiDao = new PlexiConsultaDao();
				plexiCosulta.setUsuario(usuarioLogado);
				plexiCosulta.setDataConsulta(DateUtil.gerarDataHoje());
				plexiCosulta.setStatus("Aguardando Retorno");
				if(plexiCosulta.getId() <=0) {
					plexiDao.create(plexiCosulta);
				} else {
					plexiDao.merge(plexiCosulta);
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
		return result;
	}
	
	public JSONObject getRetornoPlexi(String requestId) {
		JSONObject retorno = null;
		try {
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO1 = 201;
			int HTTP_COD_SUCESSO2 = 202;
			URL myURL;
			
			if (SiscoatConstants.DEV && CommonsUtil.sistemaWindows()) {
				myURL = new URL(urlHomologacao + "/api/maestro/result/" + requestId);
			} else {
				myURL = new URL(urlProducao + "/api/maestro/result/" + requestId);
			}
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", token);
			myURLConnection.setDoOutput(true);
			
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO 
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO1
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				if(CommonsUtil.mesmoValor(myURLConnection.getResponseCode(), 404)){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("erro404", "true");
					return jsonObject;
				}
				
			} else {
				if(!CommonsUtil.mesmoValor(myURLConnection.getResponseCode(), 202))
					retorno = getJsonSucesso(myURLConnection.getInputStream());
			}
			myURLConnection.disconnect();
			return retorno;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retorno;
	}
	
	public void atualizaRetorno(List<DocumentoAnalise> listDocAnalise, User user) {
		for(DocumentoAnalise docAnalise : listDocAnalise) {
			if(CommonsUtil.semValor(docAnalise.getPlexiConsultas()) 
				|| docAnalise.getPlexiConsultas().size() <= 0) 
				continue;
			for(PlexiConsulta plexi : docAnalise.getPlexiConsultas()) {
				if(CommonsUtil.semValor(plexi.getRequestId()))
					continue;
				if(CommonsUtil.mesmoValor(plexi.getStatus(), "Consulta expirada"))
					continue;
				atualizaRetornoPlexi(plexi, user);
			}
		}
		return;
	}
	
	public void atualizaRetornoPlexi(PlexiConsulta plexi, User user) {
		JSONObject webhookObject;
		String requestId = plexi.getRequestId();
		if(!CommonsUtil.semValor(plexi.getWebhookRetorno()) && !CommonsUtil.semValor(plexi.getPdf())) {
			return;
		} else if(!CommonsUtil.semValor(plexi.getWebhookRetorno())) {
			webhookObject = new JSONObject(plexi.getWebhookRetorno());
		} else {
			webhookObject = getRetornoPlexi(requestId);
			if(CommonsUtil.semValor(webhookObject)) {
				return;
			}
		}
		PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
		if(webhookObject.has("erro404")) {
			plexi.setStatus("Consulta expirada");
			plexi.setRequestId(null);
			atualizarDocumentos(plexi);
			if(plexi.verificaCamposDoc())
				PedirConsulta(plexi, user);
			plexiConsultaDao.merge(plexi);
			return;
		} else if(webhookObject.has("error") && CommonsUtil.mesmoValor(webhookObject.get("error"), true)) {
			plexi.setStatus("Consulta com erro");
			plexi.setRequestId(null);
			atualizarDocumentos(plexi);
			if(plexi.verificaCamposDoc())
				PedirConsulta(plexi, user);
			plexiConsultaDao.merge(plexi);
		}
	
		if(!webhookObject.has("requestId")) 
			webhookObject.put("requestId", requestId);	
		plexi.setWebhookRetorno(webhookObject.toString());
		if(webhookObject.has("pdf")) {
			plexi.setPdf(webhookObject.getString("pdf"));
		}
		plexi.setStatus("Consulta Concluída");	
		plexiConsultaDao.merge(plexi);
		salvarPdfRetornoPlexi(plexi);
	}
	
	public void atualizarDocumentos(PlexiConsulta plexiConsulta) {
		DocumentoAnalise docAnalise = plexiConsulta.documentoAnalise;
		plexiConsulta.populatePagadorRecebedor(docAnalise.getPagador());
		if(!CommonsUtil.semValor(plexiConsulta.getOrgaosStr())) {
			String[] orgaos = plexiConsulta.getOrgaos();
			plexiConsulta.setOrgaos(orgaos);
		}
	}

	public JSONObject getJsonSucesso(InputStream inputStream) {
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
	
	public String getJsonSucessoStr(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();		

			return response.toString();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void salvarPdfRetornoPlexi(PlexiConsulta plexiConsulta) {
		PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
		salvarPdfRetornoPlexi(plexiConsulta, plexiConsultaDao);
	}
	
	public void salvarPdfRetornoPlexi(PlexiConsulta plexiConsulta, PlexiConsultaDao plexiConsultaDao) {
		FileService fileService = new FileService();
				
		if(!CommonsUtil.semValor(plexiConsulta.getDocumentoAnalise())) {
			fileService.salvarPdfRetorno(plexiConsulta.getDocumentoAnalise(),
					plexiConsulta.getPdf(), plexiConsulta.getNomeCompleto(), "interno");
			return;
		}
		String nomeAnalise = plexiConsultaDao.getNomeAnalise(plexiConsulta);
		String numeroContrato = plexiConsultaDao.getNumeroContratoAnalise(plexiConsulta);
		fileService.salvarPdfRetorno(nomeAnalise, numeroContrato, plexiConsulta.getPdf(), plexiConsulta.getNomeCompleto(), "interno");
	}
}
