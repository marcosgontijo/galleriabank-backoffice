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

import com.webnowbr.siscoat.omie.request.OmieRequestBase;
import com.webnowbr.siscoat.omie.response.OmieListarExtratoResponse;
import com.webnowbr.siscoat.omie.response.OmieObterResumoFinResponse;

import br.com.galleriabank.serasarelato.cliente.util.GsonUtil;

public class OmieService {

	private String omieUrl = "https://app.omie.com.br/api/v1/";
	
	
	public OmieObterResumoFinResponse obterResumoFinancas(OmieRequestBase request) {
		
		 // POST para gerar consulta
		OmieObterResumoFinResponse result = new OmieObterResumoFinResponse();
		
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL;
			myURL = new URL(omieUrl + "/financas/resumo/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);

			String myResponse = null;

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = GsonUtil.toJson(request).toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {

				System.out.println(GsonUtil.toJson(request).toString());
			} else {

				myResponse = getResposta(myURLConnection.getInputStream());
				result = GsonUtil.fromJson(myResponse, OmieObterResumoFinResponse.class);
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
	
	public OmieListarExtratoResponse listarExtratoResponse(OmieRequestBase request) {
		
		 // POST para gerar consulta
		OmieListarExtratoResponse result = new OmieListarExtratoResponse();
		
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL;
			myURL = new URL(omieUrl + "/financas/extrato/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);

			String myResponse = null;

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = GsonUtil.toJson(request).toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {

				System.out.println(GsonUtil.toJson(request).toString());
			} else {

				myResponse = getResposta(myURLConnection.getInputStream());
				result = GsonUtil.fromJson(myResponse, OmieListarExtratoResponse.class);
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
	
	
	private String getResposta(InputStream inputStream) { // Pega resultado da API
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
}
	
	

