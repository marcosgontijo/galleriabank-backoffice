package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;

public class TakeBlipMB {
	
	
	// URL
	// https://http.msging.net/messages
			
	// Authorization
	// Key Z2FsbGVyaWE6dzZ5ZzBwSTNMSnhqMHhuNmNtRlA=
	
	// namespace
	// 37de7635_839c_4792_92a6_5d40dc299b2a
		
	//recebimento_lead_comercial
	/*
	 	Olá {{1}}, o lead {{2}} do cliente {{3}} acabou de chegar, favor entrar em contato.
	 */
	
	// contrato_pre_aprovado
	/*
	 	Olá {{1}}, ótima notícia! 😆
		Cliente {{2}} do Contrato {{3}} foi pré aprovado com taxa de {{4}}% + IPCA e prazo de {{5}} meses
	 */
	
	// contrato_recebido_paju
	/*
	 	Olá {{1}}, tudo certo?
		Já recebemos o paju do Cliente {{2}} do Contrato {{3}}. 😉
	 */
	
	// contrato_recebido_laudo
	/*
	 	Olá {{1}}, tudo certo?
		Já recebemos o laudo do Cliente {{2}} do Contrato {{3}}. 😉
	 */
	
	// contrato_recebido_laudo_paju
	/*
	 	Olá {{1}}, tudo certo?
		Já recebemos o laudo e paju do Cliente {{2}} do Contrato {{3}}, o próximo passo é o comitê 😉
	 */
	
	// ag_comentarios_juridico
	/*
	 	Olá {{1}}, contrato {{2}} do cliente {{3}} está aguardando seu comentário jurídico
	 */
	
	// comentado_juridico_interno
	/*
	 	Olá {{1}}, contrato {{2}} do cliente {{2}} já foi comentado pelo jurídico interno 
	 */
	
	// ag_validacao_documentos
	/*
	 	Olá {{1}}, contrato {{2}} do cliente {{3}} está aguardando sua validação de documentos
	 */
	
	// aprovado_comite_ag_doc
	/*
	 	Olá {{1}}, contrato {{2}} do cliente {{3}} foi aprovado pelo comitê e está pronto para fazer a análise dos documentos
	 */
	
	// aprovado_comite_ag_ccb
	/*
	 	Olá {{1}}, contrato {{2}} do cliente {{3}} foi aprovado pelo comitê e está pronto para fazer a CCB
	 */
	
	// contrato_pronto_para_assinatura
	/*
	 	Olá {{1}}, estamos quase lá!
		Contrato do Cliente {{2}} do Contrato {{3}} já está disponível para assinatura 😜
	 */
	
	// contrato_dado_entrada_cartorio
	/* 
		Olá {{1}}, tudo pronto!
	
		Contrato do Cliente {{2}} do Contrato {{3}} já foi dado entrada no Cartório de Registro.
	
		Agora é só aguardar o registro para fazermos o pagamento 🤑
	*/
	
	// aprovacao_credito_compass_v2
	/* 
		Olá {{1}}!!! 

		Parabéns pela pré-aprovação do seu Crédito com Imóvel em Garantia aqui na Galleria Bank 🤑
		
		Nós, junto com o seu consultor financeiro, queremos fazer parte dos seus projetos.
		
		Logo mais entraremos em contato com você para a agendar o Laudo de Avaliação do seu imóvel.
		
		Conte sempre com a gente
		
		Até mais.
	*/
	
	// aprovacao_credito_galache
	/* 
		Olá {{1}}!!! 

		Parabéns pela pré-aprovação do seu Crédito com Imóvel em Garantia aqui na Galleria Bank 🤑
		
		Nós, junto com o seu consultor financeiro, queremos fazer parte dos seus projetos.
		
		Logo mais, a empresa Galache estará entrando em contato com você para a agendar o Laudo de Avaliação do seu imóvel.
		
		Conte sempre com a gente
		
		Até mais.
	*/
	
	// confirmacao_vistoria
	/* 
		Olá {{1}}, aqui é da Galleria Bank.
		Confirmando a vistoria do imóvel para {{2}} ({{3}}) às {{4}}.
		A visita será feita por {{5}}.
		Até logo
	*/
	
	// operacao_baixada
	/* 
		Olá, Contrato {{1}} baixado por {{2}} em {{3}}
	*/
	
	// notificacao_cartorio
	/* 
		Olá {{1}}, contrato {{2}} do cliente {{3}} precisa de atualização referente ao cartório. Próxima notificação marcada para {{4}}
	*/
	
	// comprovante_anexado
	/* 
		Olá, {{1}} anexou o comprovante {{2}} no contrato {{3}} 
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
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setDoOutput(true);
			//myURLConnection.getOutputStream().write(postDataBytes);
			
			try(OutputStream os = myURLConnection.getOutputStream()) {
			    byte[] input = jsonWhatsApp.toString().getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}

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
	
	public void sendWhatsAppNotificaResponsavel(Responsavel responsavel, String nomeTemplateMensagem, String frase1, String frase2) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURL(responsavel));
		//jsonWhatsApp.put("to", "5519991653911@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
		
		JSONArray jsonWhatsAppParameters = new JSONArray();
		JSONObject jsonWhatsAppParameter = new JSONObject();
				
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", responsavel.getNome());
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", frase1);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", frase2);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
										
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
				jsonWhatsAppParameter.put("text", responsavel.getNome());
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Nome do cliente
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", nomeDoCliente);
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
					jsonWhatsAppParameter.put("text", responsavel.getNome());
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Número do pedido
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", numeroDoContrato);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				} else if (nomeTemplateMensagem.equals("ag_comentarios_juridico")
						|| nomeTemplateMensagem.equals("comentado_juridico_interno")
						|| nomeTemplateMensagem.equals("ag_validacao_documentos")
						|| nomeTemplateMensagem.equals("aprovado_comite_ag_ccb")
						|| nomeTemplateMensagem.equals("recebimento_lead_comercial")) {
					// Nome do notificado
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", responsavel.getNome());
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);

					// Número do pedido
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", numeroDoContrato);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Nome do cliente
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", nomeDoCliente);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				} else {
					// Nome do notificado
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", responsavel.getNome());
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Nome do cliente
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", nomeDoCliente);
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
	
	public void sendWhatsAppMessagePagadorRecebedor(PagadorRecebedor pessoa, String nomeTemplateMensagem, String nomeDoCliente, String numeroDoContrato, String taxaJuros, String prazo) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURLPagadorRecebedor(pessoa));
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
				jsonWhatsAppParameters = new JSONArray();
				
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", pessoa.getNome());
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Nome do cliente
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", nomeDoCliente);
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
					jsonWhatsAppParameters = new JSONArray();
					
					// Nome do notificado
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", pessoa.getNome());
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Número do pedido
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", numeroDoContrato);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				} else if (nomeTemplateMensagem.equals("ag_comentarios_juridico")
						|| nomeTemplateMensagem.equals("comentado_juridico_interno")
						|| nomeTemplateMensagem.equals("ag_validacao_documentos")
						|| nomeTemplateMensagem.equals("aprovado_comite_ag_ccb")
						|| nomeTemplateMensagem.equals("recebimento_lead_comercial")) {
					
					jsonWhatsAppParameters = new JSONArray();
					
					// Nome do notificado
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", pessoa.getNome());
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);

					// Número do pedido
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", numeroDoContrato);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Nome do cliente
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", nomeDoCliente);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				} else if(nomeTemplateMensagem.equals("aprovacao_credito_compass")
						|| nomeTemplateMensagem.equals("aprovacao_credito_galache")
						|| nomeTemplateMensagem.equals("aprovacao_credito_compass_v2")) {
					
					jsonWhatsAppParameters = new JSONArray();
					
					// Nome do notificado
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", pessoa.getNome());
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				} else {
					jsonWhatsAppParameters = new JSONArray();
					
					// Nome do notificado
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", pessoa.getNome());
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Nome do cliente
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", nomeDoCliente);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
					
					// Número do pedido
					jsonWhatsAppParameter = new JSONObject();
					jsonWhatsAppParameter.put("type", "text");
					jsonWhatsAppParameter.put("text", numeroDoContrato);
					jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				}
			}
			
			if (nomeTemplateMensagem.equals("recebimento_lead_galleria")) {
				jsonWhatsAppParameters = new JSONArray();
				
				// Nome do cliente
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", pessoa.getNome());
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);				
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
	
	public void sendWhatsAppMessageVistoria(PagadorRecebedor pessoa, Date dataVistoria, String nomevistoriador) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURLPagadorRecebedor(pessoa));
		//jsonWhatsApp.put("to", "5519999933015@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
		
		JSONArray jsonWhatsAppParameters = new JSONArray();
		JSONObject jsonWhatsAppParameter = new JSONObject();
		//contrato_pre_aprovado
		
		// Nome do notificado
		jsonWhatsAppParameters = new JSONArray();
		
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", pessoa.getNome());
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Data Vistoria (dd/MM)
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", CommonsUtil.formataData(dataVistoria, "dd/MM"));
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Data Vistoria (dia da semana)
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", DateUtil.getDiaDaSemana(dataVistoria));
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Data Vistoria (HH:mm)
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", CommonsUtil.formataData(dataVistoria, "HH:mm"));
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Nome Vistoriador
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", nomevistoriador);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);				
	
										
		jsonWhatsAppComponent.put("parameters", jsonWhatsAppParameters);
			
		jsonWhatsAppComponents.put(jsonWhatsAppComponent);
		
		JSONObject jsonWhatsAppLanguage = new JSONObject();
		jsonWhatsAppLanguage.put("code", "pt_BR");
		jsonWhatsAppLanguage.put("policy", "deterministic");				
			
		JSONObject jsonWhatsAppTemplate = new JSONObject();
		jsonWhatsAppTemplate.put("namespace", "37de7635_839c_4792_92a6_5d40dc299b2a");
		jsonWhatsAppTemplate.put("name", "confirmacao_vistoria");		
		jsonWhatsAppTemplate.put("components", jsonWhatsAppComponents);
		jsonWhatsAppTemplate.put("language", jsonWhatsAppLanguage);	
		
		JSONObject jsonWhatsAppConteudo = new JSONObject();
		jsonWhatsAppConteudo.put("type", "template");
		
		jsonWhatsAppConteudo.put("template", jsonWhatsAppTemplate);	
		
		jsonWhatsApp.put("content", jsonWhatsAppConteudo);
		
		senderWhatsAppMessage(jsonWhatsApp);
	}
	
	public void sendWhatsAppMessageVistoria(Responsavel responsavel, Date dataVistoria, String nomevistoriador) {
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
		
		// Nome do notificado
		jsonWhatsAppParameters = new JSONArray();
		
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", responsavel.getNome());
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Data Vistoria (dd/MM)
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", CommonsUtil.formataData(dataVistoria, "dd/MM"));
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Data Vistoria (dia da semana)
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", DateUtil.getDiaDaSemana(dataVistoria));
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Data Vistoria (HH:mm)
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", CommonsUtil.formataData(dataVistoria, "HH:mm"));
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Nome Vistoriador
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", nomevistoriador);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);				
	
										
		jsonWhatsAppComponent.put("parameters", jsonWhatsAppParameters);
			
		jsonWhatsAppComponents.put(jsonWhatsAppComponent);
		
		JSONObject jsonWhatsAppLanguage = new JSONObject();
		jsonWhatsAppLanguage.put("code", "pt_BR");
		jsonWhatsAppLanguage.put("policy", "deterministic");				
			
		JSONObject jsonWhatsAppTemplate = new JSONObject();
		jsonWhatsAppTemplate.put("namespace", "37de7635_839c_4792_92a6_5d40dc299b2a");
		jsonWhatsAppTemplate.put("name", "confirmacao_vistoria");		
		jsonWhatsAppTemplate.put("components", jsonWhatsAppComponents);
		jsonWhatsAppTemplate.put("language", jsonWhatsAppLanguage);	
		
		JSONObject jsonWhatsAppConteudo = new JSONObject();
		jsonWhatsAppConteudo.put("type", "template");
		
		jsonWhatsAppConteudo.put("template", jsonWhatsAppTemplate);	
		
		jsonWhatsApp.put("content", jsonWhatsAppConteudo);
		
		senderWhatsAppMessage(jsonWhatsApp);
	}
	
	public void sendWhatsAppMessagePagadorBoleto(PagadorRecebedor pessoa, String nomeTemplateMensagem, String linkBoleto) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURLPagadorRecebedor(pessoa));
		//jsonWhatsApp.put("to", "5519999933015@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
		
			JSONArray jsonWhatsAppParameters = new JSONArray();
			JSONObject jsonWhatsAppParameter = new JSONObject();
			//contrato_pre_aprovado
			if (nomeTemplateMensagem.equals("envio_boleto_cobranca")) {
				// Nome do notificado
				jsonWhatsAppParameters = new JSONArray();
				
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", pessoa.getNome());
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
				
				// Link Do Boleto
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", linkBoleto);
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);				
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
	
	public void sendWhatsAppMessagePagadorLeadStandby(PagadorRecebedor pessoa, String nomeTemplateMensagem) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURLPagadorRecebedor(pessoa));
		//jsonWhatsApp.put("to", "5519999933015@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
		
			JSONArray jsonWhatsAppParameters = new JSONArray();
			JSONObject jsonWhatsAppParameter = new JSONObject();
			//contrato_pre_aprovado
			if (nomeTemplateMensagem.equals("leads_standby")) {
				// Nome do notificado
				jsonWhatsAppParameters = new JSONArray();
				
				jsonWhatsAppParameter = new JSONObject();
				jsonWhatsAppParameter.put("type", "text");
				jsonWhatsAppParameter.put("text", pessoa.getNome());
				jsonWhatsAppParameters.put(jsonWhatsAppParameter);
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
	
	public void sendWhatsAppMessageCartorio(Responsavel responsavel, String nomeTemplateMensagem, String numeroDoContrato, String nomeCliente, Date dataNotificacao) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURL(responsavel));
		//jsonWhatsApp.put("to", "5519983099338@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
		
		JSONArray jsonWhatsAppParameters = new JSONArray();
		JSONObject jsonWhatsAppParameter = new JSONObject();
		//contrato_pre_aprovado

		// Nome do notificado
		jsonWhatsAppParameters = new JSONArray();
		
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", responsavel.getNome());
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Número do pedido
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", numeroDoContrato);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Nome do cliente
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", nomeCliente);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Data Proxima notificacao
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", CommonsUtil.formataData(dataNotificacao, "dd/MM/yyyy"));
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
						
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
	
	public void sendWhatsAppMessageContratoBaixado(Responsavel responsavel, String nomeTemplateMensagem, String nomeDoUsuario, String numeroDoContrato, String dataHoje) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURL(responsavel));
		//jsonWhatsApp.put("to", "5519983099338@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
	
		JSONArray jsonWhatsAppParameters = new JSONArray();
		JSONObject jsonWhatsAppParameter = new JSONObject();
		//contrato_pre_aprovado

		// Número do pedido
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", numeroDoContrato);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Nome do cliente
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", nomeDoUsuario);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Data
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", dataHoje);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
						
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
	
	public void sendWhatsAppMessageComprovante(Responsavel responsavel, String nomeTemplateMensagem, String nomeDoUsuario, String numeroDoContrato, String nomeArquivo) {
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURL(responsavel));
		//jsonWhatsApp.put("to", "5519983099338@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
	
		JSONArray jsonWhatsAppParameters = new JSONArray();
		JSONObject jsonWhatsAppParameter = new JSONObject();
		//contrato_pre_aprovado

		
		
		// Nome do usuario
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", nomeDoUsuario);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);

		// Nome Arquivo
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", "'" + nomeArquivo + "'");
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Número do pedido
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", numeroDoContrato);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
						
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
	
	public void sendWhatsAppEmitirNota(ContratoCobranca contrato) {
		String nomeTemplateMensagem = "solicitar_nota";
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		jsonWhatsApp.put("to", getWhatsAppURL(contrato.getResponsavel()));
		//jsonWhatsApp.put("to", "5519999933015@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
		
		JSONArray jsonWhatsAppParameters = new JSONArray();
		JSONObject jsonWhatsAppParameter = new JSONObject();
		
		// Nome do notificado
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", contrato.getResponsavel().getNome());
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Número do pedido
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", contrato.getNumeroContrato());
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Nome do cliente
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", contrato.getPagador().getNome());
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Valor do pedido
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", CommonsUtil.formataValorMonetario(contrato.getValorCCB(), "R$ "));
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Valor da Nota
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", CommonsUtil.formataValorMonetario(contrato.getValorNotaFiscal(), "R$ "));
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
		// Nome Gerente
		String nomeDono = "";
		if(!CommonsUtil.semValor(contrato.getResponsavel().getDonoResponsavel().getNome())) {
			nomeDono =  contrato.getResponsavel().getDonoResponsavel().getNome();
		}
		jsonWhatsAppParameter = new JSONObject();
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", nomeDono);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);				
	
									
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
					
					if (retornoWhatsAPP.has("resource")) {
						JSONObject resource = retornoWhatsAPP.getJSONObject("resource");
						whatsAppNumber = resource.getString("alternativeAccount");
					}
					
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
	
	/**
	 * CRIA ENDEREÇO DA MENSAGEM DO WHATSAPP
	 */
	public String getWhatsAppURLPagadorRecebedor(PagadorRecebedor pessoa) {
		FacesContext context = FacesContext.getCurrentInstance();
	
		boolean existeWhatsAppNumber = true;
		String whatsAppNumber = "";
		
		if (pessoa.getWhatsAppNumero() != null) {
			if (!pessoa.getWhatsAppNumero().equals("")) {				
				whatsAppNumber = pessoa.getWhatsAppNumero();
			} else {
				existeWhatsAppNumber = false;
			}
		} else {
			existeWhatsAppNumber = false;
		}
		
		if (!existeWhatsAppNumber) {
			PagadorRecebedorDao pDao = new PagadorRecebedorDao();
			
			try {		
				int HTTP_COD_SUCESSO = 200;
	
				URL myURL = new URL("https://http.msging.net/commands");	
				
				JSONObject jsonWhatsAppURL = new JSONObject();			
				jsonWhatsAppURL.put("id", generateUUID());
				jsonWhatsAppURL.put("to", "postmaster@wa.gw.msging.net");
				jsonWhatsAppURL.put("method", "get");
				jsonWhatsAppURL.put("uri", "lime://wa.gw.msging.net/accounts/+" + formatTelefoneWhatsApp(pessoa.getTelCelular()));
	
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
									"Take Blip: Falha ao buscar URL do responsável no WhatsApp (Cod: " + myURLConnection.getResponseCode() + " / Celular: " + pessoa.getTelCelular() + ")",
					""));
				} else {				
					// Seta o ID da fatura na Parcela do Siscoat
					JSONObject retornoWhatsAPP = null;
	
					retornoWhatsAPP = getJsonSucesso(myURLConnection.getInputStream());
					
					if(retornoWhatsAPP.has("resource")){
						JSONObject resource = retornoWhatsAPP.getJSONObject("resource");
						whatsAppNumber = resource.getString("alternativeAccount");
						pessoa.setWhatsAppNumero(whatsAppNumber);
						pDao.merge(pessoa);
					
						System.out.println("Take Blip: URL do WhatsApp criada com sucesso para o Responsável " + pessoa.getNome());
					}
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
	
	/**
	 * CRIA ENDEREÇO DA MENSAGEM DO WHATSAPP
	 */
	public String getWhatsAppURLNovoResponsavel(Responsavel responsavel) {
		FacesContext context = FacesContext.getCurrentInstance();
	
		String whatsAppNumber = "";

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
				
				if(retornoWhatsAPP.has("resource")) {
					JSONObject resource = retornoWhatsAPP.getJSONObject("resource");
					whatsAppNumber = resource.getString("alternativeAccount");
				}
				
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
		
		return whatsAppNumber;
	}
	
	/**
	 * CRIA ENDEREÇO DA MENSAGEM DO WHATSAPP
	 */
	public String getWhatsAppURLNovoPagadorRecebedor(PagadorRecebedor pessoa) {
		FacesContext context = FacesContext.getCurrentInstance();
	
		String whatsAppNumber = "";

		try {		
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://http.msging.net/commands");	
			
			JSONObject jsonWhatsAppURL = new JSONObject();			
			jsonWhatsAppURL.put("id", generateUUID());
			jsonWhatsAppURL.put("to", "postmaster@wa.gw.msging.net");
			jsonWhatsAppURL.put("method", "get");
			jsonWhatsAppURL.put("uri", "lime://wa.gw.msging.net/accounts/+" + formatTelefoneWhatsApp(pessoa.getTelCelular()));

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
								"Take Blip: Falha ao buscar URL do responsável no WhatsApp (Cod: " + myURLConnection.getResponseCode() + " / Celular: " + pessoa.getTelCelular() + ")",
				""));
			} else {				
				// Seta o ID da fatura na Parcela do Siscoat
				JSONObject retornoWhatsAPP = null;

				retornoWhatsAPP = getJsonSucesso(myURLConnection.getInputStream());
				if(retornoWhatsAPP.has("resource")) {
					JSONObject resource = retornoWhatsAPP.getJSONObject("resource");
					whatsAppNumber = resource.getString("alternativeAccount");
				}				
				
				System.out.println("Take Blip: URL do WhatsApp criada com sucesso para o Responsável " + pessoa.getNome());
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return whatsAppNumber;
	}
	public void sendWhatsAppMessageNovaConta(String numeroDoContrato) {
				
		JSONObject jsonWhatsApp = new JSONObject();
		jsonWhatsApp.put("id", generateUUID());

		//jsonWhatsApp.put("to", getWhatsAppURLPagadorRecebedor(pessoa));
		jsonWhatsApp.put("to", "5519996733216@wa.gw.msging.net");
				
		jsonWhatsApp.put("type", "application/json"); 
		
		JSONArray jsonWhatsAppComponents = new JSONArray();
		JSONObject jsonWhatsAppComponent = new JSONObject();
		jsonWhatsAppComponent.put("type", "body");
		
		JSONArray jsonWhatsAppParameters = new JSONArray();
		JSONObject jsonWhatsAppParameter = new JSONObject();
		
		jsonWhatsAppParameter.put("type", "text");
		jsonWhatsAppParameter.put("text", numeroDoContrato);
		jsonWhatsAppParameters.put(jsonWhatsAppParameter);
		
			jsonWhatsAppComponent.put("parameters", jsonWhatsAppParameters);
			
			jsonWhatsAppComponents.put(jsonWhatsAppComponent);
			
			JSONObject jsonWhatsAppLanguage = new JSONObject();
			jsonWhatsAppLanguage.put("code", "pt_BR");
			jsonWhatsAppLanguage.put("policy", "deterministic");				
				
			JSONObject jsonWhatsAppTemplate = new JSONObject();
			jsonWhatsAppTemplate.put("namespace", "37de7635_839c_4792_92a6_5d40dc299b2a");
			jsonWhatsAppTemplate.put("name", "nova_despesa");		
			jsonWhatsAppTemplate.put("components", jsonWhatsAppComponents);
			jsonWhatsAppTemplate.put("language", jsonWhatsAppLanguage);	
			
			JSONObject jsonWhatsAppConteudo = new JSONObject();
			jsonWhatsAppConteudo.put("type", "template");
			
			jsonWhatsAppConteudo.put("template", jsonWhatsAppTemplate);	
			
			jsonWhatsApp.put("content", jsonWhatsAppConteudo);
			
			senderWhatsAppMessage(jsonWhatsApp);
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
