package com.webnowbr.siscoat.cobranca.ws.caf;

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

public class CombateAFraudeService {

	String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MzA0ZTI3YzcxZmY4NTAwMDliZjVmYTYiLCJpYXQiOjE2NjEyNjQ1MDl9.NNco5L0Izoj5yM_heMxQAAdSAl9YjFUz-uyV4wqMwEo";

	public FacesMessage ChamarCombateAFraude(CombateAFraudeTransaction combateAFraudeTransaction, User usuarioLogado) {
		String cafJson = GsonUtil.toJson(combateAFraudeTransaction);
		
		try {
			int HTTP_COD_SUCESSO = 200;
			URL myURL;
			myURL = new URL("https://api.combateafraude.com/v1/transactions?origin=TRUST");
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", token);
			myURLConnection.setDoOutput(true);
			DataEngineIdSend myResponse = null;
			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = cafJson.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				System.out.println(cafJson);
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"CaF: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
				JSONObject retorno = null;
				retorno = getJsonSucesso(myURLConnection.getInputStream());
				if (retorno.has("requestId")) {
					combateAFraudeTransaction.setRequestId(retorno.getString("requestId"));
				}
				if (retorno.has("id")) {
					combateAFraudeTransaction.setId(retorno.getString("id"));
				}
				
				CombateAFraude caf = new CombateAFraude();
				caf.setCpf(combateAFraudeTransaction.attributes.cpf);
				caf.setTemplateId(combateAFraudeTransaction.templateId);
				caf.setUuid(combateAFraudeTransaction.getId());
				caf.setRequestId(combateAFraudeTransaction.getRequestId());
				caf.setStatus("Aguradando Retorno");
				
				CombateAFraudeDao cafDao = new CombateAFraudeDao(); 
				cafDao.create(caf);
				
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
