package com.webnowbr.siscoat.cobranca.rest;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

@Path("/documentos/upload")
public class UploadFileRestService {
	
	@GET
	@Path("/nova-pasta/{numeroContrato}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response novaPastaNumeroContrato(@PathParam(value = "numeroContrato") String numeroContrato) { 
		
		System.out.println("Executando o comando para criar a nova pasta "+numeroContrato+" - Inicio");
				
		try {
			if (StringUtils.isNotBlank(numeroContrato)) {
				// recupera local onde será gravado o arquivo
				ParametrosDao pDao = new ParametrosDao();
				String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
						.concat(numeroContrato).concat("/");

				// cria o diretório, caso não exista
				// Teste Localhost "C:/Desenvolvimento".concat(pathContrato));
				
				File diretorio = new File(pathContrato);
				if (!diretorio.isDirectory()) {
					diretorio.mkdir();
				}
				
				String message = "{\"retorno\": \"Pasta criada com sucesso!!! " + numeroContrato +"\"}";
				
				System.out.println("Pasta criada com sucesso "+numeroContrato+" - Fim");

			    return Response
			      .status(Response.Status.OK)
			      .entity(message)
			      .type(MediaType.APPLICATION_JSON)
			      .build();
			} else {
				String message = "{\"retorno\": \"Nova pasta "+numeroContrato+" não foi criada !!! \"}";
				
				return Response
					      .status(Response.Status.BAD_REQUEST)
					      .entity(message)
					      .type(MediaType.APPLICATION_JSON)
					      .build();
			}
			
		} catch (org.json.JSONException exception) {
			return Response
				      .status(Response.Status.INTERNAL_SERVER_ERROR)
				      .entity("Ocorreu um erro ao tentar criar uma nova pasta !!!")
				      .type(MediaType.APPLICATION_JSON)
				      .build();
		}
	}
	
}