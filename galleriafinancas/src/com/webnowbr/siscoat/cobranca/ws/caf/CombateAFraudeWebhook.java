package com.webnowbr.siscoat.cobranca.ws.caf;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.dataengine.cliente.model.request.DataEngineIdSend;
import io.jsonwebtoken.Jwts;

public class CombateAFraudeWebhook {

	String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MzA0ZTI3YzcxZmY4NTAwMDliZjVmYTYiLCJpYXQiOjE2NjEyNjQ1MDl9.NNco5L0Izoj5yM_heMxQAAdSAl9YjFUz-uyV4wqMwEo";
	
	public FacesMessage ChamarCombateAFraude(CombateAFraudeTransaction combateAFraudeTransaction, User usuarioLogado) { 
		String cafJson = GsonUtil.toJson(combateAFraudeTransaction);
		System.out.println(cafJson);
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
			System.out.println(cafJson);
			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR, "CaF: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
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
	

	@POST
	@Path("/webhook/")
	public Response webhookCaF(String webhookRetorno, @QueryParam("Token") String token) {
		try {
			Jwts.parserBuilder().setSigningKey(CommonsUtil.CHAVE_WEBHOOK).build().parseClaimsJws(token);

			CombateAFraudeWebhookRetorno cafWebhookRetorno = GsonUtil.fromJson(webhookRetorno, CombateAFraudeWebhookRetorno.class);

			return Response.status(200).entity("Processado").build();
		} catch (io.jsonwebtoken.ExpiredJwtException eJwt) {
			eJwt.printStackTrace();
			return Response.status(500).entity("Token Expirado").build();
		} catch (Exception e) {
			System.out.println("---------------- CaF webhookRetorno ---------------- ");
			System.out.println(webhookRetorno);
			System.out.println("---------------- CaF webhookRetorno ---------------- ");
			e.printStackTrace();
			return Response.status(500).entity("Erro interno").build();
		}
	}
	
}
