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

import org.json.JSONObject;

import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.dataengine.cliente.model.request.DataEngineIdSend;

public class PlexiService {

	String token = "Bearer omA1k5xkozRljXBw0M0EviFVhh2F5qvJAqA8y8wbYMC75tIMd4GQsBsmvCQ2OTcO81XoXBczfj1BUwhhTISUouWX55g0EPleclII";
	private String urlHomologacao = "https://sandbox.plexi.com.br"; 
	private String urlProducao = "https://sandbox.plexi.com.br";//https://api.plexi.com.br
	
	
	public FacesMessage PedirConsulta(PlexiConsulta plexiCosulta, User usuarioLogado) {
		String plexiJson = GsonUtil.toJson(plexiCosulta);
		
		try {
			int HTTP_COD_SUCESSO = 201;
			int HTTP_COD_SUCESSO2 = 202;
			int HTTP_COD_SUCESSO3 = 409;
			URL myURL;
			myURL = new URL(urlHomologacao + plexiCosulta.getPlexiDocumentos().getUrl());
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", token);
			myURLConnection.setDoOutput(true);
			
			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = plexiJson.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO 
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO3) {
				System.out.println(plexiJson);
				System.out.println(getJsonSucesso(myURLConnection.getInputStream()).toString());
			} else {
				JSONObject retorno = null;
				retorno = getJsonSucesso(myURLConnection.getInputStream());
				if (retorno.has("requestId")) {
					plexiCosulta.setRequestId(retorno.getString("requestId"));
				}
				
				PlexiConsultaDao plexiDao = new PlexiConsultaDao();
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
		return null;
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
}
