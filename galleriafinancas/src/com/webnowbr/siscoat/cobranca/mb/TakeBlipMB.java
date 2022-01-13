package com.webnowbr.siscoat.cobranca.mb;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

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
	
	public void sendWhatsAppMessage(String numeroTelefone, String nomeTemplateMensagem, String nomeDoNotificado, String nomeDoCliente, String numeroDoContrato, String taxaJuros, String prazo) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());
		jsonWhatsApp.put("to", numeroTelefone + "@wa.gw.msging.net");
		jsonWhatsApp.put("type", "application/json");
		
		JSONObject jsonWhatsAppConteudo = new JSONObject();
		jsonWhatsAppConteudo.put("type", "template");
		
		JSONObject jsonWhatsAppTemplate = new JSONObject();
		jsonWhatsAppTemplate.put("namespace", "37de7635_839c_4792_92a6_5d40dc299b2a");
		jsonWhatsAppTemplate.put("name", nomeTemplateMensagem);
				
		JSONObject jsonWhatsAppLanguage = new JSONObject();
		jsonWhatsAppLanguage.put("code", "pt_BR");
		jsonWhatsAppLanguage.put("policy", "deterministic");		
		jsonWhatsAppTemplate.put("language", jsonWhatsAppLanguage);	
		
		JSONObject jsonWhatsAppComponents = new JSONObject();
		jsonWhatsAppComponents.put("type", "body");
		
			JSONArray jsonWhatsAppParameters = new JSONArray();
			JSONObject jsonWhatsAppParameter = new JSONObject();
			//contrato_pre_aprovado
			if (nomeTemplateMensagem.equals("contrato_pre_aprovado")) {
				// Nome do notificado
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", "TESTE");
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Nome do cliente
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", "TESTE");
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Número do pedido
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", "TESTE");
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Taxa ( 15,05 )
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", "TESTE");
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Prazo
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", "TESTE");
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);				
			} else {
				//contrato_dado_entrada_cartorio
				//contrato_pronto_para_assinatura
				//contrato_recebido_laudo_paju
				
				// Nome do notificado
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", "TESTE");
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Nome do cliente
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", "TESTE");
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Número do pedido
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", "TESTE");
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
			}				
										
			jsonWhatsAppComponents.put("parameters", jsonWhatsAppParameters);
		
		jsonWhatsAppTemplate.put("components", jsonWhatsAppComponents);
		
		jsonWhatsAppConteudo.put("template", jsonWhatsAppTemplate);	
		
		jsonWhatsApp.put("content", jsonWhatsAppConteudo);		
	}
		
	public String generateUUID() {		
		UUID uuid = UUID.randomUUID();
		
		return uuid.toString();
	}
}
