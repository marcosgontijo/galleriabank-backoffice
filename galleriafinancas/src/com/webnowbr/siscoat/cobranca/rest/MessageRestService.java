package com.webnowbr.siscoat.cobranca.rest;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB;

@Path("/services")
public class MessageRestService {
	
	public static void main(String[] args) {
		
		String authorization = "Basic d2Vibm93YnI6IVNpc0NvQXRAMjAyMSo=";
		
		String[] tokens;
		String username = "";
		String password = "";
		
		authorization = authorization.replace("Basic ", "");
		
		try {
			tokens = (new String(Base64.getDecoder().decode(authorization), "UTF-8")).split(":");

			username = tokens[0];
			password = tokens[1];
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("LeadBySite - username: " + authorization);
		System.out.println("LeadBySite - password: " + authorization);
    }



	@GET
	@Path("/LeadBySiteGET")
	public Response leadBySiteGET() {
		String retorno = "TESTE OK";

		String message = "{\"hello\": \"This is a JSON response\"}";

	    return Response
	      .status(Response.Status.OK)
	      .entity(message)
	      .type(MediaType.APPLICATION_JSON)
	      .build();
	}
	
	/***
	 * Modelo JSON
	 * {
		    "nome" : "Tania Cristina Santos Vaini",
		    "email" : "tscvaini@gmail.com",
		    "valor_desejado" : "100.000,00",
		    "cpf" : "094.874.948-20",
		    "finalidade_emprestimo": "Quitar dívidas",
		    "telefone": "(11) 4037-0688",
		    "cep" : "05585-040",
		    "logradouro" : "Rua José Piragibe",
		    "numeroimovel": "156",
		    "complemento": "Bloco 4 ",
		    "cidade": "Campinas",
		    "estado": "SP",		    
		    "tipo_imovel" : "Casa residencial", 
		    "ocupacao" : "Ocupado por mim",
		    "valor_imovel" : "450.000,00",
		    "quitado" : "Sim"
		} 
		
		FINALIDADE
			Quitar dívidas
			Abrir o meu negócio
			Investir na minha empresa
			Capital de giro na minha empresa
			Outro motivo
			
		TIPO IMOVEL
			Apartamento residencial
			Casa residencial
			Comercial
			Terreno
			Galpão
			Chácara
			Rural
			
		OCUPACAO
			Ocupado por mim
			Alugado
			Vazio
			
		QUITADO
			Sim
			Não
	 */
	
	@POST
	@Path("/LeadBySite")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response leadBySite(String leadBySite, @HeaderParam("Token") String token, @HeaderParam("Authorization") String authorization) { 
		System.out.println("LeadBySite - Authorization: " + authorization);
	
		if (authorization == null || !authorization.startsWith("Basic")) {
			String message = "{\"retorno\": \"Authentication Failed!!!\"}";
			
			return Response
				      .status(Response.Status.FORBIDDEN)
				      .entity(message)
				      .type(MediaType.APPLICATION_JSON)
				      .build();
		} else {
			
			/// decoda token de autenticação
			String[] tokens;
			String username = "";
			String password = ""; 
			
			authorization = authorization.replace("Basic ", "");
			
			try {
				tokens = (new String(Base64.getDecoder().decode(authorization), "UTF-8")).split(":");

				username = tokens[0];
				password = tokens[1];
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (username.equals("webnowbr") && password.equals("!SisCoAt@2021*")) {
				try {
					JSONObject lead = new JSONObject(leadBySite);
					
					ContratoCobrancaMB contratoCobrancaMB = new ContratoCobrancaMB();
					contratoCobrancaMB.clearPreContratoCustomizado();
					
					// popula contrato
					contratoCobrancaMB.setCodigoResponsavel("lead");
					
					String valorDesejado = lead.getString("valor_desejado").replace(".", "").replace(",", ".");
					
					contratoCobrancaMB.getObjetoContratoCobranca().setQuantoPrecisa(new BigDecimal(valorDesejado));
	
					contratoCobrancaMB.getObjetoContratoCobranca().setFinalidade(lead.getString("finalidade_emprestimo"));
					
					if (lead.has("url")) {
						if (!lead.isNull("url")) {
							
							if (lead.getString("url").contains("refinanciamento-de-imovel")) {
								contratoCobrancaMB.getObjetoContratoCobranca().setUrlLead("Refinanciamento de Imóvel");
							}
							
							if (lead.getString("url").contains("emprestimo-para-negativados")) {
								contratoCobrancaMB.getObjetoContratoCobranca().setUrlLead("Empréstimo para negativados");
							}
							
							if (lead.getString("url").contains("emprestimo-online")) {
								contratoCobrancaMB.getObjetoContratoCobranca().setUrlLead("Empréstimo online");
							}
							
							if (lead.getString("url").contains("emprestimo-home-equity")) {
								contratoCobrancaMB.getObjetoContratoCobranca().setUrlLead("Empréstimo Home Equity");
							}
							
							if (lead.getString("url").contains("emprestimo-com-terreno-em-garantia")) {
								contratoCobrancaMB.getObjetoContratoCobranca().setUrlLead("Empréstimo com terreno em garantia");
							}
							
							if (lead.getString("url").contains("emprestimo-online-yt")) {
								contratoCobrancaMB.getObjetoContratoCobranca().setUrlLead("Empréstimo online YT");
							}
							
							if (lead.getString("url").contains("simulador-online")) {
								contratoCobrancaMB.getObjetoContratoCobranca().setUrlLead("Simulador online");
							}
							
							if (contratoCobrancaMB.getObjetoContratoCobranca().getUrlLead() != null) {
								if (contratoCobrancaMB.getObjetoContratoCobranca().getUrlLead().equals("")) {
									contratoCobrancaMB.getObjetoContratoCobranca().setUrlLead(lead.getString("url"));
								}
							}							
						}						
					}
					if (lead.has("posted_url")) {
						if (!lead.isNull("posted_url")) {
							contratoCobrancaMB.getObjetoContratoCobranca().setUrlLead(lead.getString("posted_url"));
						}						
					}
					
					// popula pagador
					contratoCobrancaMB.getObjetoPagadorRecebedor().setNome(lead.getString("nome"));
					contratoCobrancaMB.setTipoPessoaIsFisica(true);
					contratoCobrancaMB.getObjetoPagadorRecebedor().setCpf(lead.getString("cpf"));
					contratoCobrancaMB.getObjetoPagadorRecebedor().setTelCelular(lead.getString("telefone"));			
					contratoCobrancaMB.getObjetoPagadorRecebedor().setEmail(lead.getString("email"));
					
					// popula imovel								
					contratoCobrancaMB.getObjetoImovelCobranca().setCep(lead.getString("cep")); 
					contratoCobrancaMB.getObjetoImovelCobranca().setEndereco(lead.getString("logradouro") + lead.getString("numeroimovel"));
					contratoCobrancaMB.getObjetoImovelCobranca().setComplemento(lead.getString("complemento"));
					contratoCobrancaMB.getObjetoImovelCobranca().setBairro("");
					contratoCobrancaMB.getObjetoImovelCobranca().setCidade(lead.getString("cidade"));
					contratoCobrancaMB.getObjetoImovelCobranca().setEstado(lead.getString("estado"));
					contratoCobrancaMB.getObjetoImovelCobranca().setOcupacao(lead.getString("ocupacao"));
					contratoCobrancaMB.getObjetoImovelCobranca().setPossuiDivida(lead.getString("quitado")); 
					
					String valorImovel = lead.getString("valor_imovel").replace(".", "").replace(",", ".");
					
					if (valorImovel != null && valorImovel.equals("")) {
						contratoCobrancaMB.getObjetoImovelCobranca().setValoEstimado(new BigDecimal(valorImovel));
					}
					
					String tipoImovel = lead.getString("tipo_imovel");
					
					if (tipoImovel.equals("Apartamento residencial")) {
						tipoImovel = "Apartamento";
					}
					
					if (tipoImovel.equals("Casa residencial")) {
						tipoImovel = "Casa";
					}
					
					if (tipoImovel.equals("Comercial")) {
						tipoImovel = "Sala Comercial";
					}
					
					contratoCobrancaMB.getObjetoImovelCobranca().setTipo(tipoImovel);
	
					// salva LEAD
					contratoCobrancaMB.addPreContratoLeadSite();
				
					String message = "{\"retorno\": \"Lead recebido com sucesso!!!\"}";
		
				    return Response
				      .status(Response.Status.OK)
				      .entity(message)
				      .type(MediaType.APPLICATION_JSON)
				      .build();
				} catch (org.json.JSONException exception) {
					return Response
						      .status(Response.Status.BAD_REQUEST)
						      .entity("O campo " + exception.getMessage() + " não foi encontrado no payload recebido!!!")
						      .type(MediaType.APPLICATION_JSON)
						      .build();
				}
			} else {	
				String message = "{\"retorno\": \"Authentication Failed!!!\"}";
				
				return Response
					      .status(Response.Status.FORBIDDEN)
					      .entity(message)
					      .type(MediaType.APPLICATION_JSON)
					      .build();				
			}
		}
	}
}