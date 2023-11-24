package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.webnowbr.siscoat.cobranca.model.cep.CepResult;
import com.webnowbr.siscoat.common.GsonUtil;

public class CepService {

	public CepResult consultaCep(String cep) {

		CepResult result = new CepResult();
		try {
			String inputCep = cep.replace("-", "");

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String myResponse = null;
			if (myURLConnection.getResponseCode() == HTTP_COD_SUCESSO) {
				myResponse = getResponse(myURLConnection.getInputStream());
				result = GsonUtil.fromJson(myResponse, CepResult.class);
			}
			myURLConnection.disconnect();
		} catch (Exception e) {
			result.setErro(e.getMessage());
			e.printStackTrace();
		}

		return result;

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