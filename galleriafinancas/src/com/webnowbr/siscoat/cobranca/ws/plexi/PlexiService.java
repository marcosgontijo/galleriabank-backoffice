package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.service.FileService;
import com.webnowbr.siscoat.cobranca.vo.FileUploaded;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.jwt.common.JwtUtil;

public class PlexiService {

	String token = "Bearer omA1k5xkozRljXBw0M0EviFVhh2F5qvJAqA8y8wbYMC75tIMd4GQsBsmvCQ2OTcO81XoXBczfj1BUwhhTISUouWX55g0EPleclII";
	private String urlHomologacao = "https://sandbox.plexi.com.br"; 
	private String urlProducao = "https://api.plexi.com.br";//https://api.plexi.com.br
	
	
	public FacesMessage PedirConsulta(PlexiConsulta plexiCosulta, User usuarioLogado, DocumentoAnalise docAnalise) {
		String plexiJson = GsonUtil.toJson(plexiCosulta);
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
				System.out.println(getJsonSucesso(myURLConnection.getInputStream()).toString());
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
				plexiDao.create(plexiCosulta);
				
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
				System.out.println("Não foi encontrato retorno de requestId:" + requestId);
			} else {
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
	
	public void atualizaRetornoPlexi(String requestId) {
		JSONObject webhookObject = getRetornoPlexi(requestId);
		if(CommonsUtil.semValor(webhookObject)) {
			return;
		}
		webhookObject.put("requestId", requestId);
		PlexiConsultaDao plexiConsultaDao = new PlexiConsultaDao();
		if(!webhookObject.has("requestId")) {
			return;
		}
		PlexiConsulta plexiConsulta = plexiConsultaDao.findByFilter("requestId", 
				webhookObject.getString("requestId"))
				.stream().findFirst().orElse(null);
		
		if(CommonsUtil.semValor(plexiConsulta)) {
			System.out.println("Erro Plexi: " + webhookObject.getString("requestId"));
		}
		
		plexiConsulta.setWebhookRetorno(webhookObject.toString());
		if(webhookObject.has("pdf")) {
			plexiConsulta.setPdf(webhookObject.getString("pdf"));
			//salvarPdfRetorno(plexiConsulta, plexiConsultaDao);
		}
		plexiConsulta.setStatus("Consulta Concluída");
		plexiConsultaDao.merge(plexiConsulta);
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
