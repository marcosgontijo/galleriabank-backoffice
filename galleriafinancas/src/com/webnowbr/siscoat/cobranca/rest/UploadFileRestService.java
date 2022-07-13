package com.webnowbr.siscoat.cobranca.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.webnowbr.siscoat.cobranca.rest.api.ListaUploadDocumentos;
import com.webnowbr.siscoat.cobranca.rest.api.UploadDocumentos;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

@Path("/documentos")
public class UploadFileRestService {
	
	private static final String NO_EXIST = "NO_EXIST";
	private static final String FALTANTE = "faltante";
	private static final String PASTA_FALTANTE = "/faltante";
	
	@POST
	@Path("/uploads/{numeroContrato}/{subpasta}/{nomeArquivo}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentoUploadNumeroContrato(byte[] documentos, @PathParam(value = "numeroContrato") String numeroContrato, 
			@PathParam(value = "subpasta") String subpasta,
			@PathParam(value = "nomeArquivo") String nomeArquivo) { 
		
		System.out.println("Executando o contrato {} "+numeroContrato+" upload do arquivo {} "+nomeArquivo+" - Inicio");
		String pathContrato = NO_EXIST;
				
		try {
			if (StringUtils.isNotBlank(numeroContrato)) {
				// recupera local onde será gravado o arquivo
				ParametrosDao pDao = new ParametrosDao();
				if(subpasta.equals(NO_EXIST)) {
				   pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
							.concat(numeroContrato).concat("/");
				}if(subpasta.equals(FALTANTE)) {
					   pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
								.concat(numeroContrato).concat("/").concat(subpasta).concat("/");
				}
				
				// cria o diretório, caso não exista
				// Teste Localhost "C:/Desenvolvimento".concat(pathContrato));
				try {

					File file = new File(pathContrato.concat(nomeArquivo));

					FileOutputStream fop = new FileOutputStream(file);
					fop.write(documentos);
					fop.flush();
					fop.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

				String message = "{\"retorno\": \"Enviado com sucesso Nome do Arquivo {}" + nomeArquivo +"\"}";
				System.out.println("O arquivo foi enviado com sucesso "+nomeArquivo+" - Fim");
				
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
	
	@DELETE
	@Path("/uploads/{numeroContrato}/{subpasta}/{nomeArquivo}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentoDeletarNumeroContrato(@PathParam(value = "numeroContrato") String numeroContrato, 
			@PathParam(value = "subpasta") String subpasta,
			@PathParam(value = "nomeArquivo") String nomeArquivo) throws IOException { 
		
		System.out.println("Deletando da pasta {} "+numeroContrato+" o arquivo {} "+nomeArquivo+" - Inicio");
		String pathContrato = NO_EXIST;
		try {
			if (StringUtils.isNotBlank(numeroContrato)) {
				// recupera local onde será gravado o arquivo
				ParametrosDao pDao = new ParametrosDao();
				if(subpasta.equals(NO_EXIST)) {
					pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
							.concat(numeroContrato).concat("/");
				}if(subpasta.equals(FALTANTE)) {
					pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
							.concat(numeroContrato).concat("/").concat(subpasta).concat("/");
				}
					
				// cria o diretório, caso não exista
				// Teste Localhost "C:/Desenvolvimento".concat(pathContrato));
				
		        Files.delete(Paths.get(pathContrato.concat(nomeArquivo)));

				String message = "{\"retorno\": \"Deletado com sucesso Nome do Arquivo {} " + nomeArquivo +"\"}";
				System.out.println("Deletado com sucesso Nome do Arquivo {} "+nomeArquivo+" - Fim");
				
				return Response
						.status(Response.Status.OK)
						.entity(message)
						.type(MediaType.APPLICATION_JSON)
						.build();
			} else {
				String message = "{\"retorno\": \"O arquivo "+nomeArquivo+" não foi deletado !!! \"}";
				
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
	
	@POST
	@Path("/pastas/{numeroContrato}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response novaPastaNumeroContrato(@PathParam(value = "numeroContrato") String numeroContrato) { 
		
		System.out.println("Executando o comando para criar a nova pasta e subpasta "+numeroContrato+" - Inicio");
				
		try {
			if (StringUtils.isNotBlank(numeroContrato)) {
				// recupera local onde será gravado o arquivo
				ParametrosDao pDao = new ParametrosDao();
				String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
						.concat(numeroContrato);
				
				// cria o diretório, caso não exista
				// Teste Localhost "C:/Desenvolvimento".concat(pathContrato));
				
				File diretorio = new File(pathContrato);
				try {
					if (!diretorio.isDirectory()) {
						diretorio.mkdir();
					}
				}catch(Exception e) {
					String message = "{\"retorno\": \"\"Ocorreu um erro ao tentar criar na pasta "+numeroContrato+", tente novamente.\"}";
					return Response
							.status(Response.Status.BAD_GATEWAY)
							.entity(message)
							.type(MediaType.APPLICATION_JSON)
							.build();
				}
				
				String pathContratoFaltante = diretorio.getAbsolutePath().concat(PASTA_FALTANTE);
				
				File diretorioFaltante = new File(pathContratoFaltante);
				try {
					if (!diretorioFaltante.isDirectory()) {
						diretorioFaltante.mkdir();
					}
				}catch(Exception e) {
					String message = "{\"retorno\": \"\"Ocorreu um erro ao tentar criar na pasta "+numeroContrato+", tente novamente.\"}";
					return Response
							.status(Response.Status.BAD_GATEWAY)
							.entity(message)
							.type(MediaType.APPLICATION_JSON)
							.build();
				}
				
				String message = "{\"retorno\": \"Pasta e Subpasta criada com sucesso!!! " + numeroContrato +"\"}";
				System.out.println("Pasta e subpasta criada com sucesso "+numeroContrato+" - Fim");
				
				return Response
						.status(Response.Status.OK)
						.entity(message)
						.type(MediaType.APPLICATION_JSON)
						.build();
			} else {
				String message = "{\"retorno\": \"Nova pasta e subpasta "+numeroContrato+" não foi criada !!! \"}";
				
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
	
	@GET
	@Path("/pastas/{numeroContrato}/{subpasta}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response consultarPastaNumeroContrato(@PathParam(value = "numeroContrato") String numeroContrato, 
			@PathParam(value = "subpasta") String subpasta) { 
		
		System.out.println("Executando o comando para consultar os arquivos dentro da pasta "
				+numeroContrato+ " e subpasta "+subpasta+" - Inicio");
		
		String message = NO_EXIST;
		ListaUploadDocumentos listaUploadDocumentos = new ListaUploadDocumentos();
		listaUploadDocumentos.setListaUploadDocumentos(new ArrayList<UploadDocumentos>());
		
		try {
			if (StringUtils.isNotBlank(numeroContrato)) {
				// recupera local onde será gravado o arquivo
				ParametrosDao pDao = new ParametrosDao();
				String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString().concat(numeroContrato);
				
				File novoDiretorio = new File(pathContrato);
				
				try { 
					if (!novoDiretorio.isDirectory()) {
						novoDiretorio.mkdir();
					}
				}catch(Exception e) {
					message = "{\"retorno\": \"\"Ocorreu um erro ao tentar criar na pasta "+numeroContrato+", tente novamente.\"}";
					return Response
							.status(Response.Status.BAD_GATEWAY)
							.entity(message)
							.type(MediaType.APPLICATION_JSON)
							.build();
				}
				
				String pathContratoFaltante = pathContrato.concat(PASTA_FALTANTE);
				
				File diretorioFaltante = new File(pathContratoFaltante);
				try {
					if (!diretorioFaltante.isDirectory()) {
						diretorioFaltante.mkdir();
					}
				}catch(Exception e) {
					message = "{\"retorno\": \"\"Ocorreu um erro ao tentar criar na pasta faltante, tente novamente.\"}";
					return Response
						      .status(Response.Status.BAD_GATEWAY)
						      .entity(message)
						      .type(MediaType.APPLICATION_JSON)
						      .build();
				}
				
				if(subpasta.equals(NO_EXIST)) {
					listaUploadDocumentos = listarDiretorios(pathContrato, listaUploadDocumentos);
				}else if(subpasta.equals(FALTANTE)) {
					listaUploadDocumentos = listarDiretorios(pathContratoFaltante, listaUploadDocumentos);
				}
				
				System.out.println("Arquivos encontrados com sucesso na pasta "+numeroContrato+" - Fim");
				
				return Response
						.status(Response.Status.OK)
						.entity(ListaUploadDocumentos.converterFromListJson(listaUploadDocumentos))
						.type(MediaType.APPLICATION_JSON)
						.build();
			} else {
				message = "{\"retorno\": \"\"Ocorreu um erro os arquivos não foram encontrados na pasta "+numeroContrato+".\"}";
				
				return Response
					      .status(Response.Status.BAD_REQUEST)
					      .entity(message)
					      .type(MediaType.APPLICATION_JSON)
					      .build();
			}
			
		} catch (org.json.JSONException exception) {
			return Response
				      .status(Response.Status.INTERNAL_SERVER_ERROR)
				      .entity("Ocorreu um erro ao tentar consultar arquivos na pasta !!!")
				      .type(MediaType.APPLICATION_JSON)
				      .build();
		}
	}

	private ListaUploadDocumentos listarDiretorios(String diretorioContrato, ListaUploadDocumentos listaUploadDocumentos) {
		// cria o diretório, caso não exista
		// Teste Localhost "C:/Desenvolvimento".concat(pathContrato));
		
		File diretorio = new File(diretorioContrato);
		File arqs[] = diretorio.listFiles();
		if (arqs != null && arqs.length > 0) {
			for (int i = 0; i < arqs.length; i++) {
				if(arqs[i].isFile() == true) {
					System.out.println(arqs[i]);
					File arquivo = arqs[i];
					listaUploadDocumentos.getListaUploadDocumentos().add(new UploadDocumentos(arquivo.getName(), diretorioContrato));
				}
			}
		}
		return listaUploadDocumentos;
	}
	
}