package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;

import br.com.galleriabank.debit.cliente.model.indice.TabelaIndices;

public class DebitService {

	

	public TabelaIndices requestIPCA() {
		return criarConsultaTabela("ipca");
	}
	

	public TabelaIndices criarConsultaTabela(String indice) { // POST para gerar consulta
		try {
			String retornoConsulta = criarExecutaConsultaIndice(indice);
			if (CommonsUtil.semValor(retornoConsulta)) {
				new Exception("Debit CriarConsultaProcesso: Falha na consulta");
			} else {
				return GsonUtil.fromJson(retornoConsulta, TabelaIndices.class);
			}
		} catch (Exception e) {
			new Exception("Debit CriarConsultaProcesso: Falha  (Cod: " + e.getMessage() + ")");
		}
		return null;
	}
	
	public String criarExecutaConsultaIndice(String indice) {
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			String retornoConsulta;

			URL myURL;
			String sUrl = "https://servicos.galleriabank.com.br/debit/api/v1/indice/" + indice;
			myURL = new URL(sUrl);

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			String sBearer = br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos();
			myURLConnection.setRequestProperty("Authorization", "Bearer " + sBearer);
			myURLConnection.setDoOutput(true);

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				retornoConsulta = null;
			} else {
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				retornoConsulta = response.toString();

			}
			myURLConnection.disconnect();
			return retornoConsulta;
		} catch (

		MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
