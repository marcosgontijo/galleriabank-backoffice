package com.webnowbr.siscoat.cobranca.rest;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.model.UniProof;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.cobranca.db.op.UniProofDao;
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
					
					// Se o lead veio da EdanBank
					if (lead.has("origem")) {
						if (!lead.isNull("origem")) {
							if (lead.getString("origem").equals("EDANBANK")) {								
								// id 429 -11610 - Edanbank codigo 
								contratoCobrancaMB.setCodigoResponsavel("11610");
								Responsavel responsavel = new Responsavel();
								ResponsavelDao rDao = new ResponsavelDao();
								responsavel = rDao.findById(Long.getLong("429"));
								contratoCobrancaMB.getObjetoContratoCobranca().setResponsavel(responsavel);
							}
						}
					}
					
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
					
					if (lead.has("cpf")) {
						contratoCobrancaMB.getObjetoPagadorRecebedor().setCpf(lead.getString("cpf"));
					}
				
					contratoCobrancaMB.getObjetoPagadorRecebedor().setTelCelular(lead.getString("telefone"));			
					contratoCobrancaMB.getObjetoPagadorRecebedor().setEmail(lead.getString("email"));
					
					// popula imovel								
					contratoCobrancaMB.getObjetoImovelCobranca().setCep(lead.getString("cep")); 
					contratoCobrancaMB.getObjetoImovelCobranca().setEndereco(lead.getString("logradouro") + lead.getString("numeroimovel"));
					contratoCobrancaMB.getObjetoImovelCobranca().setComplemento(lead.getString("complemento"));
					contratoCobrancaMB.getObjetoImovelCobranca().setBairro("");
					contratoCobrancaMB.getObjetoImovelCobranca().setCidade(lead.getString("cidade"));
					contratoCobrancaMB.getObjetoImovelCobranca().setEstado(lead.getString("estado"));
					
					String valorImovel = lead.getString("valor_imovel").replace(".", "").replace(",", ".");
					
					if (valorImovel != null && valorImovel.equals("")) {
						contratoCobrancaMB.getObjetoImovelCobranca().setValoEstimado(new BigDecimal(valorImovel));
					}
					
					if (lead.has("tipo_imovel")) {
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
					}
	
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
	
	@POST
	@Path("/StatusPropostaMoneyPlus")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response statusPropostaMoneyPlus(@QueryParam("proposta") String proposta, @QueryParam("situacao") String situacao, @QueryParam("identificador") String identificador) { 
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		ContratoCobranca contratoCobranca = new ContratoCobranca();
		
		System.out.println("StatusPropostaMoneyPlus - Inicio");
				
		try {
		
			contratoCobranca = cDao.getContratoPropostaMoneyPlus(identificador, proposta);
			
			if (contratoCobranca.getId() > 0) {
				
				contratoCobranca.setStatusPropostaMoneyPlus(situacao);
				
				cDao.merge(contratoCobranca);
				
				String message = "{\"retorno\": \"Status da operação atualizado com sucesso!!! " + proposta +  situacao + identificador + "\"}";

			    return Response
			      .status(Response.Status.OK)
			      .entity(message)
			      .type(MediaType.APPLICATION_JSON)
			      .build();
			} else {
				String message = "{\"retorno\": \"Operação não encontrada!!! \"}";
				
				return Response
					      .status(Response.Status.BAD_REQUEST)
					      .entity(message)
					      .type(MediaType.APPLICATION_JSON)
					      .build();
			}
			
		} catch (org.json.JSONException exception) {
			return Response
				      .status(Response.Status.BAD_REQUEST)
				      .entity("erro na Atualização do Status da proposta!!!")
				      .type(MediaType.APPLICATION_JSON)
				      .build();
		}
	}
	
	@POST
	@Path("/uniproof/lot/management")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uniproofLotManagement(String data) { 
		try {
			UniProof processo = new UniProof();
			UniProofDao uniProofDao = new UniProofDao();
			JSONObject dataObject = new JSONObject(data);
			boolean isUpdate = false;
			
			// verifica se já existe o processo na base
			if (dataObject.has("lotItemId")) {
				if (!dataObject.isNull("lotItemId")) {
					processo = uniProofDao.getProcessoByLotItemId(dataObject.getString("lotItemId"));
					
					// se existe processo a operação será de update
					if (processo != null) {
						isUpdate = true;
					} else {
						processo = new UniProof();
					}
				}
			}
	        
	        if (dataObject.has("createdAt")) {
				if (!dataObject.isNull("createdAt")) {
					SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					try {
						Date createAt = isoFormat.parse(dataObject.getString("createdAt"));
						processo.setCreatedAt(createAt);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			}
			
			if (dataObject.has("updatedAt")) {
				if (!dataObject.isNull("updatedAt")) {
					SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					try {
						Date updatedAt = isoFormat.parse(dataObject.getString("updatedAt"));
						processo.setUpdatedAt(updatedAt);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			}
			
			if (dataObject.has("companyToken")) {
				if (!dataObject.isNull("companyToken")) {
					processo.setCompanyToken(dataObject.getString("companyToken"));
				}			
			}
			
			if (dataObject.has("lotId")) {
				if (!dataObject.isNull("lotId")) {
					processo.setLotId(dataObject.getString("lotId"));
				}			
			}
			
			if (dataObject.has("lotItemId")) {
				if (!dataObject.isNull("lotItemId")) {
					processo.setLotItemId(dataObject.getString("lotItemId"));
				}			
			}
			
			if (dataObject.has("folderId")) {
				if (!dataObject.isNull("folderId")) {
					processo.setFolderId(dataObject.getString("folderId"));
				}			
			}
			
			if (dataObject.has("folderName")) {
				if (!dataObject.isNull("folderName")) {
					processo.setFolderName(dataObject.getString("folderName"));
				}			
			}
			
			if (dataObject.has("folderDescription")) {
				if (!dataObject.isNull("folderDescription")) {
					processo.setFolderDescription(dataObject.getString("folderDescription"));
				}			
			}
			
			if (dataObject.has("serviceName")) {
				if (!dataObject.isNull("serviceName")) {
					processo.setServiceName(dataObject.getString("serviceName"));
				}			
			}

			if (dataObject.has("protocol")) {
				if (!dataObject.isNull("protocol")) {
					processo.setProtocol(dataObject.getString("protocol"));
				}			
			}

			if (dataObject.has("cityId")) {
				if (!dataObject.isNull("cityId")) {
					processo.setCityId(dataObject.getString("cityId"));
				}			
			}

			if (dataObject.has("cityName")) {
				if (!dataObject.isNull("cityName")) {
					processo.setCityName(dataObject.getString("cityName"));
				}			
			}

			if (dataObject.has("statusName")) {
				if (!dataObject.isNull("statusName")) {
					processo.setStatusName(dataObject.getString("statusName"));
				}			
			}
		
			if (dataObject.has("statusDescription")) {
				if (!dataObject.isNull("statusDescription")) {
					processo.setStatusDescription(dataObject.getString("statusDescription"));
				}			
			}

			if (dataObject.has("statusLabel")) {
				if (!dataObject.isNull("statusLabel")) {
					processo.setStatusLabel(dataObject.getString("statusLabel"));
				}			
			}
			
			if (isUpdate) {
				uniProofDao.merge(processo);
			} else {
				uniProofDao.create(processo);
			}
			
			String message = "{\"retorno\": \"Processo " + processo.getLotItemId() + " recebido com sucesso!!!\"}";

		    return Response
		      .status(Response.Status.CREATED)
		      .entity(message)
		      .type(MediaType.APPLICATION_JSON)
		      .build();
		} catch (org.json.JSONException exception) {
			return Response
				      .status(Response.Status.BAD_REQUEST)
				      .entity("Erro ao processar processo!!!")
				      .type(MediaType.APPLICATION_JSON)
				      .build();
		}
	}
}