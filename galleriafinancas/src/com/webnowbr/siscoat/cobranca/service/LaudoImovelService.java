package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

import org.json.JSONObject;
import org.primefaces.PrimeFaces;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GsonUtil;

import br.com.galleriabank.laudoimovel.cliente.model.request.LaudoImovelAssessingObject;
import br.com.galleriabank.laudoimovel.cliente.model.request.LaudoImovelAssessingTypologyObject;
import br.com.galleriabank.laudoimovel.cliente.model.retorno.LaudoImovelRetornoObterStatus;
import br.com.galleriabank.laudoimovel.cliente.model.retorno.LaudoImovelRetornoPadrao;

public class LaudoImovelService {
	public HashMap<String, String> requestIdAvaliacao(ImovelCobranca imovelCobranca) {
		HashMap<String, String> retornoMap = new HashMap<String, String>();
		LaudoImovelAssessingObject laudoRoboAssessingObject = new LaudoImovelAssessingObject();
		
		imovelCobranca.separaEnderecoNumero(imovelCobranca.getEndereco());

		laudoRoboAssessingObject.setCategory_id(imovelCobranca.getCategoria());
		laudoRoboAssessingObject.setLat(null);
		laudoRoboAssessingObject.setLon(null);
		laudoRoboAssessingObject.setNeighborhood(imovelCobranca.getBairro());
		laudoRoboAssessingObject.setArea(Integer.parseInt(imovelCobranca.getAreaConstruida()));
		laudoRoboAssessingObject.setStreet(imovelCobranca.getEnderecoSemNumero());
		laudoRoboAssessingObject.setNumber(imovelCobranca.getNumeroImovel());
		laudoRoboAssessingObject.setSub_category_id(imovelCobranca.getSubCategoria());
		laudoRoboAssessingObject.setCity(imovelCobranca.getCidade());
		laudoRoboAssessingObject.setPostal_code(imovelCobranca.getCep());
		laudoRoboAssessingObject.setState(imovelCobranca.getEstado());
		laudoRoboAssessingObject.setComplement(imovelCobranca.getComplemento());
		laudoRoboAssessingObject.setTypology(new LaudoImovelAssessingTypologyObject(imovelCobranca.getNumeroQuartos(), 
																					imovelCobranca.getNumeroSuites(),
																					imovelCobranca.getNumeroGaragens(),
																					imovelCobranca.getNumeroBanheiros()));
		
		String json = GsonUtil.toJson(laudoRoboAssessingObject);

		if(!imovelCobranca.getAreaConstruida().isEmpty()) {
			imovelCobranca.setAreaConstruida(imovelCobranca.getAreaConstruida());
			retornoMap.put("atualizar", "true");
		}
		
		LaudoImovelRetornoPadrao laudoRoboRetornoPadrao = new LaudoImovelRetornoPadrao();
		
		try {
			URL myURL = new URL("https://servicos.galleriabank.com.br/laudoImovel/api/v1/laudo/imovel/start");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setRequestProperty("Authorization",
					"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
			myURLConnection.setDoOutput(true);

			try(OutputStream os = myURLConnection.getOutputStream()) {
			    byte[] input = json.getBytes("utf-8");
			    os.write(input, 0, input.length);
			    os.close();
			}
			
			if (myURLConnection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
				BufferedReader in = new BufferedReader(new InputStreamReader(myURLConnection.getErrorStream(), "utf-8"));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				retornoMap.put("erroLaudo", "true");
				JSONObject responseObj = new JSONObject(response.toString());
				System.out.println("Retorno postER: " + responseObj);
			}
			
			if (myURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "utf-8"));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				laudoRoboRetornoPadrao = GsonUtil.fromJson(response.toString(), LaudoImovelRetornoPadrao.class);
				retornoMap.put("idAval", laudoRoboRetornoPadrao.getData());
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return retornoMap;
	}
	
	public boolean getLaudoStatus(String avaliacaoString, ContratoCobranca contratoCobranca) {
		LaudoImovelRetornoObterStatus laudoRoboRetornoObterStatus = new LaudoImovelRetornoObterStatus();
		boolean isLaudoDone = false;
		int quantidadePesquisa = 0;
		
		if (CommonsUtil.mesmoValor(avaliacaoString, "")) {
			return false;
		}
		
		while(!isLaudoDone && quantidadePesquisa < 7) {
			try {
				Thread.sleep(5000);
				URL myURL = new URL("https://servicos.galleriabank.com.br/laudoImovel/api/v1/laudo/imovel/valuationstatus/"
						+ avaliacaoString);

				HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setRequestProperty("Authorization",
						"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
				myURLConnection.setDoOutput(true);

				if (myURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();

					while ((inputLine = in.readLine()) != null) {					
						response.append(inputLine);
					}
					in.close();

					laudoRoboRetornoObterStatus = GsonUtil.fromJson(response.toString(), LaudoImovelRetornoObterStatus.class);
					if (laudoRoboRetornoObterStatus.getData() != null) {
						if (laudoRoboRetornoObterStatus.getData().getProgress() != null) {
							if (CommonsUtil.mesmoValor(laudoRoboRetornoObterStatus.getData().getError_message(), "Numero mínimo de amostras selecionadas não foi atingido.")) {
								break;
							} else if (laudoRoboRetornoObterStatus.getData().getProgress().isSelection() 
									&& laudoRoboRetornoObterStatus.getData().getProgress().isSearch()
									&& laudoRoboRetornoObterStatus.getData().getProgress().isPrice()) {
								isLaudoDone = true;
								contratoCobranca.setValorPreLaudo(laudoRoboRetornoObterStatus.getData().getPrice());
							}
						}
					}
				}
				myURLConnection.disconnect();
				quantidadePesquisa++;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			return isLaudoDone;
	}
	
	public HashMap<String, String> requestLaudoPdf(String idAval, ContratoCobranca contratoCobranca) {
		HashMap<String, String> retornoMap = new HashMap<String, String>();
		try {
			URL myURL = new URL("https://servicos.galleriabank.com.br/laudoImovel/api/v1/laudo/imovel/reportpdf/" + idAval);
			
			HttpURLConnection myURLConnectionPdf = (HttpURLConnection) myURL.openConnection();
			myURLConnectionPdf.setRequestMethod("GET");
			myURLConnectionPdf.setUseCaches(false);
			myURLConnectionPdf.setRequestProperty("Accept", "application/json");
			myURLConnectionPdf.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnectionPdf.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnectionPdf.setRequestProperty("Authorization",
					"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());

			if (myURLConnectionPdf.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(myURLConnectionPdf.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				LaudoImovelRetornoPadrao laudoRoboRetornoPadrao = GsonUtil.fromJson(response.toString(), LaudoImovelRetornoPadrao.class);

				if (laudoRoboRetornoPadrao.getData() != null) {
					retornoMap.put("laudoRetorno", laudoRoboRetornoPadrao.getData());
					retornoMap.put("atualizar", "true");
				    FileService fileService = new FileService();
				    fileService.salvarPdfRetorno("", contratoCobranca.getNumeroContrato(), retornaBase64(laudoRoboRetornoPadrao.getData()), "LaudoRobo", "interno");
				    fileService.salvarPdfRetorno("", contratoCobranca.getNumeroContrato(), retornaBase64(laudoRoboRetornoPadrao.getData()), "LaudoRobo", "juridico");
				}
			}
			myURLConnectionPdf.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retornoMap;
	}
	
	public String retornaBase64(String str) throws Exception {
		java.net.URL url = new java.net.URL(str);
        InputStream is = url.openStream();
        byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
        byte[] encoded = Base64.getEncoder().encode(bytes);
        return new String(encoded);
	}
}
