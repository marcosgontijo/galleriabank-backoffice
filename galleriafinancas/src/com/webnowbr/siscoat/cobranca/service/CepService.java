package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import com.webnowbr.siscoat.cobranca.model.cep.CepResult;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;

public class CepService {

	int HTTP_COD_SUCESSO = 200;
	
	public CepResult consultaCep(String cep) {

		
		CepResult result = new CepResult();
		try {
			String inputCep = cep.replace("-", "");
			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");
			String myResponse = executaConsulta(myURL);
			result = GsonUtil.fromJson(myResponse, CepResult.class);			
		} catch (Exception e) {
			result.setErro(e.getMessage());
			e.printStackTrace();
		}

		
		if (!CommonsUtil.semValor(result.getErro())) {
			try {
				String inputCep = CommonsUtil.formataCEP(CommonsUtil.somenteNumeros(cep)).replace(".", "");
				URL myURL = new URL("https://cdn.apicep.com/file/apicep/" + inputCep + ".json");
				String myResponse = executaConsulta(myURL);
				result = GsonUtil.fromJson(myResponse, CepResult.class);
			} catch (Exception e) {
				result.setErro(e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}

	private String executaConsulta(URL myURL) throws IOException, ProtocolException {
		HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
		myURLConnection.setUseCaches(false);
		myURLConnection.setRequestMethod("GET");
//		myURLConnection.setRequestProperty("Accept", "application/json");
//		myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
//		myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		myURLConnection.setDoOutput(true);
		String response = null;
		if (myURLConnection.getResponseCode() == HTTP_COD_SUCESSO) {
			response = getResponse(myURLConnection.getInputStream());
		} else {
			CepResult result = new CepResult();
			result.setErro("Erro ao efetuar a consulta - consulta vazia");
			response = GsonUtil.toJson(result);
		}
		myURLConnection.disconnect();

		return response;
	}

	private String getResponse(InputStream inputStream) {
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

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}
}