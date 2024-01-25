package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorConsulta;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;

public class CredlocalizaService {

	public void requestFrotaVeiculos(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {
			String response = null;
			
			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.VEICULOS);

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				response = pagadorRecebedorConsulta.getRetornoConsulta();
			} else {
				String cpfCnpj = null;
				if(!CommonsUtil.semValor(documentoAnalise.getPagador().getCpf())) 
					cpfCnpj = documentoAnalise.getPagador().getCpf();
				else if(!CommonsUtil.semValor(documentoAnalise.getPagador().getCnpj())) 
					cpfCnpj = documentoAnalise.getPagador().getCnpj();
				response = ConsultaFrotaVeiculos(cpfCnpj);
			}
			DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
			documentoAnalise.setRetornoFrotaVeiculos(response);
			documentoAnaliseDao.merge(documentoAnalise);
			
			PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
					DocumentosAnaliseEnum.VEICULOS, response);
			return;
		} catch (Exception e) {
			new Exception("Credlocaliza ConsultaFrotaVeiculos: Falha  (Cod: " + e.getMessage() + ")");
		}
		return;
	}
	
	public String ConsultaFrotaVeiculos(String cpfCnpj) { // POST para gerar consulta
		try {
			cpfCnpj =  CommonsUtil.somenteNumeros(cpfCnpj);
			String tipo = null;
			if (CommonsUtil.semValor(cpfCnpj)) 
				return null;
			
			switch (cpfCnpj.length()) {
				case 11:
					tipo = "cpf";
				case 14:
					tipo = "cnpj";
			}
			if (CommonsUtil.semValor(tipo)) 
				return null;
			
			String retornoConsulta = ConsultaFrotaVeiculos(tipo, cpfCnpj);
			if (CommonsUtil.semValor(retornoConsulta)) {
				new Exception("Credlocaliza ConsultaFrotaVeiculos: Falha na consulta");
			} else {
				return retornoConsulta;
			}
		} catch (Exception e) {
			new Exception("Credlocaliza ConsultaFrotaVeiculos: Falha  (Cod: " + e.getMessage() + ")");
		}
		return null;
	}
	
	public String ConsultaFrotaVeiculos(String tipoDocumento, String documento) {
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			String retornoConsulta;

			URL myURL;
			String sUrl = "https://credlocaliza.com.br/sistema/services/veiculo/v2/pesquisa?tipoRetorno=json"
					+ "&id_pesquisa=128&login=43682144803&senha=Galleria1234"
					+ "&campo=" + tipoDocumento + "&valor=" + documento;
			myURL = new URL(sUrl);

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			//String sBearer = br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos();
			//myURLConnection.setRequestProperty("Authorization", "Bearer " + sBearer);
			myURLConnection.setDoOutput(true);
			
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				retornoConsulta = null;
			} else {
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
			myURLConnection.disconnect();
			return retornoConsulta;
		} catch (

		MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
