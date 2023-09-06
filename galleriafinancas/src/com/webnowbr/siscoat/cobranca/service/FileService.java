package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webnowbr.siscoat.cobranca.vo.FileSiscoat;
import com.webnowbr.siscoat.cobranca.vo.FileUploaded;
import com.webnowbr.siscoat.cobranca.vo.ResponseApi;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.PropertyLoader;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.jwt.common.JwtUtil;

public class FileService {

	/** Log. */
	private Log logger = LogFactory.getLog(FileService.class);
	
	public List<FileUploaded> documentoConsultarTodos(String numeroContrato, User usuario) {
		// Query Url
		String serverPrincipalUrl = PropertyLoader
				.getString("client.galleria.financas.upload.lista.todos.doc.rest.url");

		logger.info("INFO file server documentoConsultarTodos {} GET: "
				.concat(serverPrincipalUrl.replace("{numeroContrato}", numeroContrato)));
//		HttpServletRequest request;

		URL myURL;
		try {
			myURL = new URL(serverPrincipalUrl.replace("{numeroContrato}", numeroContrato));

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setRequestProperty("Authorization",
					"Bearer " + JwtUtil.generateJWTSite(usuario.getId(), usuario.getLogin(), "BACKOFFICE"));
			myURLConnection.setDoOutput(true);

			String retornoConsulta = null;
			if (myURLConnection.getResponseCode() == SiscoatConstants.HTTP_COD_SUCESSO) {
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				retornoConsulta = response.toString();
			}

			if (!CommonsUtil.semValor(retornoConsulta)) {
				ResponseApi teste  = GsonUtil.fromJson(retornoConsulta, ResponseApi.class);
				Collection<FileUploaded> result = new ArrayList<FileUploaded>();
				
				
				result = GsonUtil.fromJson(teste.getClasse(), new TypeToken<ArrayList<FileUploaded>>() {
	            }.getType());
				return  result.stream().collect(Collectors.toList());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<FileUploaded>();
	}
	
	public byte[] abrirDocumentos(FileUploaded documentoSelecionado, String numeroContrato, User usuario) {
		
		String serverPrincipalUrl = PropertyLoader
				.getString("client.galleria.financas.upload.abrir.doc.rest.url");
		logger.info("INFO file server {} documentoSelecionadosAbrir POST: ".concat(serverPrincipalUrl.replace("{numeroContrato}", numeroContrato)));

		URL myURL;
		try {
			myURL = new URL(serverPrincipalUrl.replace("{numeroContrato}", numeroContrato));
			
			byte[] postDataBytes = GsonUtil.toJson(documentoSelecionado).getBytes();
			
			String token = "Bearer " + JwtUtil.generateJWTSite(usuario.getId(), usuario.getLogin(), "BACKOFFICE");
			
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setRequestProperty("Authorization", token);
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			String retornoConsulta = null;
			if (myURLConnection.getResponseCode() == SiscoatConstants.HTTP_COD_SUCESSO) {
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				retornoConsulta = response.toString();
			}

			if (!CommonsUtil.semValor(retornoConsulta)) {
				ResponseApi teste  = GsonUtil.fromJson(retornoConsulta, ResponseApi.class);
				FileSiscoat result = null;
				Gson gson = new Gson();
				
				result = gson.fromJson(teste.getClasse(), FileSiscoat.class);
				return result.getFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public String salvarDocumento(byte[] documentoSelecionado, String numeroContrato,String arquivo ,String subpasta, User usuario) {
		
		String serverPrincipalUrl = PropertyLoader
				.getString("client.galleria.financas.upload.rest.url");
		logger.info("INFO file server {} POST: ".concat(serverPrincipalUrl.replace("{numeroContrato}", numeroContrato)
  				.replace("{subpasta}", subpasta
  				.replace("{nomeArquivo}", arquivo))));

		URL myURL;
		try {
			arquivo  = CommonsUtil.removeAcentos(arquivo);
			//arquivo = "teste";
			String surl = serverPrincipalUrl.replace("{numeroContrato}", numeroContrato)
					.replace("{subpasta}", subpasta).replace("{nomeArquivo}", arquivo).replace(" ", "%20");
			myURL = new URL(surl);

			byte[] postDataBytes = GsonUtil.toJson(documentoSelecionado).getBytes();
 
			String token = "Bearer " + JwtUtil.generateJWTSite(usuario.getId(), usuario.getLogin(), "BACKOFFICE");
			
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setRequestProperty("Authorization", token);
			myURLConnection.setDoOutput(true);
			myURLConnection.setDoInput(true);
			myURLConnection.getOutputStream().write(documentoSelecionado);
//			myURLConnection.getOutputStream().write(Base64.getEncoder().encodeToString(documentoSelecionado).getBytes());

			String retornoConsulta = null;
			if (myURLConnection.getResponseCode() == SiscoatConstants.HTTP_COD_SUCESSO) {
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				retornoConsulta = response.toString();
			}

			if (!CommonsUtil.semValor(retornoConsulta)) {
				ResponseApi teste = GsonUtil.fromJson(retornoConsulta, ResponseApi.class);
//				FileSiscoat result = null;
//				Gson gson = new Gson();
				return teste.getMensagem();
//				result = gson.fromJson(teste.getClasse(), FileSiscoat.class);
//				return result.getFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	
}
