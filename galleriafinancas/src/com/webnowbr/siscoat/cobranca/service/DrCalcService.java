package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;

import br.com.galleriabank.debit.cliente.model.indice.TabelaIndices;
import br.com.galleriabank.drcalc.cliente.model.DebitosJudiciais;
import br.com.galleriabank.drcalc.cliente.model.DebitosJudiciaisRequest;

public class DrCalcService {

	public DebitosJudiciais criarConsultaAtualizacaoMonetaria(DebitosJudiciaisRequest debitosJudiciaisRequest) { // POST para gerar consulta
		try {
			String retornoConsulta = criarExecutaAtualizacaoMonetaria(debitosJudiciaisRequest);
			if (CommonsUtil.semValor(retornoConsulta)) {
				new Exception("Debit CriarConsultaProcesso: Falha na consulta");
			} else {
				return GsonUtil.fromJson(retornoConsulta, DebitosJudiciais.class);
			}
		} catch (Exception e) {
			new Exception("Debit CriarConsultaProcesso: Falha  (Cod: " + e.getMessage() + ")");
		}
		return null;
	}
	
	public String criarExecutaAtualizacaoMonetaria(DebitosJudiciaisRequest debitosJudiciaisRequest) {
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			String retornoConsulta;

			URL myURL;
			String sUrl = "https://servicos.galleriabank.com.br/drcalc/api/v1";
			//String sUrl = "http://localhost:8085/api/v1";
			myURL = new URL(sUrl);

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			String sBearer = br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos();
			myURLConnection.setRequestProperty("Authorization", "Bearer " + sBearer);
			myURLConnection.setDoOutput(true);

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = GsonUtil.toJson(debitosJudiciaisRequest).getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			
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
