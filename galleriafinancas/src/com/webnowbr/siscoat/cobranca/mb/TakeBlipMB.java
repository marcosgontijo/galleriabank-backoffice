package com.webnowbr.siscoat.cobranca.mb;

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
	
	public void sendWhatsAppMessage() {
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
	}
	

	

}
