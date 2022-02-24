package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.cobranca.db.op.TransferenciasObservacoesIUGUDao;
import com.webnowbr.siscoat.common.CommonsUtil;

public class TakeBlipMB {
	
	
	// URL
	// https://http.msging.net/messages
			
	// Authorization
	// Key Z2FsbGVyaWE6dzZ5ZzBwSTNMSnhqMHhuNmNtRlA=
	
	// namespace
	// 37de7635_839c_4792_92a6_5d40dc299b2a
	
	// contrato_dado_entrada_cartorio
	/*
	 * 
		Olá {{1}}, tudo pronto!
	
		Contrato do Cliente {{2}} do Contrato {{3}} já foi dado entrada no Cartório de Registro.
	
		Agora é só aguardar o registro para fazermos o pagamento 🤑
	*/
	
	// contrato_pronto_para_assinatura
	/*
	 	Olá {{1}}, estamos quase lá!
		Contrato do Cliente {{2}} do Contrato {{3}} já está disponível para assinatura 😜
	 */
	
	// contrato_recebido_laudo_paju
	/*
	 	Olá {{1}}, tudo certo?
		Já recebemos o laudo e paju do Cliente {{2}} do Contrato {{3}}, o próximo passo é o comitê 😉
	 */
	
	// contrato_pre_aprovado
	/*
	 	Olá {{1}}, ótima notícia! 😆
		Cliente {{2}} do Contrato {{3}} foi pré aprovado com taxa de {{4}}% + IPCA e prazo de {{5}} meses
	 */
	
	/*
	   {
		   "id":"123e4567-e89b-12d3-a456-4266551234562",
		   "to":"5519991653911@wa.gw.msging.net",
		   "type":"application/json",
		   "content":{
		      "type":"template",
		      "template":{
		         "namespace":"37de7635_839c_4792_92a6_5d40dc299b2a",
		         "name":"sample_issue_resolution",
		         "language":{
		            "code":"pt_BR",
		            "policy":"deterministic"
		         },
		         "components":[
		            {
		                "type": "body",
		                "parameters": [
		                    {
		                        "type": "text",
		                        "text": "Hermes Jr"
		                    }
		                ]
		            }
		          ]
		        }
		    }
		}

	 */
	
	/**
	 * ENVIA MENSAGEM DO WHATSAPP
	 */
	public void senderWhatsAppMessage(JSONObject jsonWhatsApp) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		try {		
			int HTTP_COD_SUCESSO = 202;

			URL myURL = new URL("https://http.msging.net/messages");	

			byte[] postDataBytes = jsonWhatsApp.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Authorization", "Key Z2FsbGVyaWE6dzZ5ZzBwSTNMSnhqMHhuNmNtRlA=");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Take Blip: Falha ao enviar a mensagem WhatsApp (Cod: " + myURLConnection.getResponseCode() + ")",
				""));
			} else {				
				System.out.println("Take Blip: Informativo WhatsApp enviado com sucesso!");
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendWhatsAppMessage(Responsavel responsavel, String nomeTemplateMensagem, String nomeDoCliente, String numeroDoContrato, String taxaJuros, String prazo) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURL(responsavel));
		//jsonWhatsApp.put("to", "5519999933015@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
		
			JSONArray jsonWhatsAppParameters = new JSONArray();
			JSONObject jsonWhatsAppParameter = new JSONObject();
			//contrato_pre_aprovado
			if (nomeTemplateMensagem.equals("contrato_pre_aprovado")) {
				// Nome do notificado
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", CommonsUtil.removeAcentos(responsavel.getNome()));
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Nome do cliente
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", CommonsUtil.removeAcentos(nomeDoCliente));
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Número do pedido
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", numeroDoContrato);
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Taxa ( 15,05 )
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", taxaJuros);
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Prazo
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", prazo);
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);				
			} else {
				//contrato_dado_entrada_cartorio
				//contrato_pronto_para_assinatura
				//contrato_recebido_laudo_paju
				if (nomeTemplateMensagem.equals("contrato_comite")) {
					// Nome do notificado
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", CommonsUtil.removeAcentos(responsavel.getNome()));
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Número do pedido
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", numeroDoContrato);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				} else {
					// Nome do notificado
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", CommonsUtil.removeAcentos(responsavel.getNome()));
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Nome do cliente
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", CommonsUtil.removeAcentos(nomeDoCliente));
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Número do pedido
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", numeroDoContrato);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				}
			}				
										
			jsonWhatsAppComponent.put("parameters", jsonWhatsAppParameters);
			
		jsonWhatsAppComponents.put(jsonWhatsAppComponent);
		
		JSONObject jsonWhatsAppLanguage = new JSONObject();
		jsonWhatsAppLanguage.put("code", "pt_BR");
		jsonWhatsAppLanguage.put("policy", "deterministic");				
			
		JSONObject jsonWhatsAppTemplate = new JSONObject();
		jsonWhatsAppTemplate.put("namespace", "37de7635_839c_4792_92a6_5d40dc299b2a");
		jsonWhatsAppTemplate.put("name", nomeTemplateMensagem);		
		jsonWhatsAppTemplate.put("components", jsonWhatsAppComponents);
		jsonWhatsAppTemplate.put("language", jsonWhatsAppLanguage);	
		
		JSONObject jsonWhatsAppConteudo = new JSONObject();
		jsonWhatsAppConteudo.put("type", "template");
		
		jsonWhatsAppConteudo.put("template", jsonWhatsAppTemplate);	
		
		jsonWhatsApp.put("content", jsonWhatsAppConteudo);
		
		senderWhatsAppMessage(jsonWhatsApp);
	}
	
	/**
	 * CRIA ENDEREÇO DA MENSAGEM DO WHATSAPP
	 */
	public String getWhatsAppURL(Responsavel responsavel) {
		FacesContext context = FacesContext.getCurrentInstance();
	
		boolean existeWhatsAppNumber = true;
		String whatsAppNumber = "";
		
		if (responsavel.getWhatsAppNumero() != null) {
			if (!responsavel.getWhatsAppNumero().equals("")) {				
				whatsAppNumber = responsavel.getWhatsAppNumero();
			} else {
				existeWhatsAppNumber = false;
			}
		} else {
			existeWhatsAppNumber = false;
		}
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Take Blip: Iniciando envio WhatsApp (Nome: " + responsavel.getNome() + ")",""));
		
		if (!existeWhatsAppNumber) {
			ResponsavelDao rDao = new ResponsavelDao();
			
			try {		
				int HTTP_COD_SUCESSO = 200;
	
				URL myURL = new URL("https://http.msging.net/commands");	
				
				JSONObject jsonWhatsAppURL = new JSONObject();			
				jsonWhatsAppURL.put("id", generateUUID());
				jsonWhatsAppURL.put("to", "postmaster@wa.gw.msging.net");
				jsonWhatsAppURL.put("method", "get");
				jsonWhatsAppURL.put("uri", "lime://wa.gw.msging.net/accounts/+" + formatTelefoneWhatsApp(responsavel.getTelCelular()));
	
				byte[] postDataBytes = jsonWhatsAppURL.toString().getBytes();
	
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("POST");
				myURLConnection.setRequestProperty("Authorization", "Key Z2FsbGVyaWE6dzZ5ZzBwSTNMSnhqMHhuNmNtRlA=");
				myURLConnection.setRequestProperty("Content-Type", "application/json");
				myURLConnection.setDoOutput(true);
				myURLConnection.getOutputStream().write(postDataBytes);
	
				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					context.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Take Blip: Falha ao buscar URL do responsável no WhatsApp (Cod: " + myURLConnection.getResponseCode() + " / Celular: " + responsavel.getTelCelular() + ")",
					""));
				} else {				
					// Seta o ID da fatura na Parcela do Siscoat
					JSONObject retornoWhatsAPP = null;
	
					retornoWhatsAPP = getJsonSucesso(myURLConnection.getInputStream());
					
					JSONObject resource = retornoWhatsAPP.getJSONObject("resource");
					
					whatsAppNumber = resource.getString("alternativeAccount");
					
					responsavel.setWhatsAppNumero(whatsAppNumber);
					
					rDao.merge(responsavel);
					
					System.out.println("Take Blip: URL do WhatsApp criada com sucesso para o Responsável " + responsavel.getNome());
				}
	
				myURLConnection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return whatsAppNumber;
	}
	
	public String formatTelefoneWhatsApp(String telefone) {		
		String telefoneFormatado = "55" + telefone;
		
		telefoneFormatado = telefoneFormatado.replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
		
		return telefoneFormatado;
	}
		
	public String generateUUID() {		
		UUID uuid = UUID.randomUUID();
		
		return uuid.toString();
	}
	
	public JSONObject getJsonSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
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
