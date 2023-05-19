package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;

import org.jasypt.commons.CommonUtils;
import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DataEngine;
import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.serasacrednet.cliente.model.CredNet;
import br.com.galleriabank.serasacrednet.cliente.model.PessoaParticipacao;
import br.com.galleriabank.serasarelato.cliente.util.GsonUtil;

public class SerasaService {

	public void requestSerasa(DocumentoAnalise documentoAnalise, User user) {

		if (CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
			serasaCriarConsulta(documentoAnalise);
		}
		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		documentoAnalise = documentoAnaliseDao.findById(documentoAnalise.getId());

		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa())) {
			CredNet credNet = GsonUtil.fromJson(documentoAnalise.getRetornoSerasa(), CredNet.class);

			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.semValor(documentoAnalise.getPagador().getDtNascimento()))
					documentoAnalise.getPagador().setDtNascimento(credNet.getPessoa().getDataNascimentoFundacao());

				if (CommonsUtil.semValor(documentoAnalise.getPagador().getNomeMae()))
					documentoAnalise.getPagador().setNomeMae(credNet.getPessoa().getNomeMae());
			}

			if (!CommonsUtil.semValor(credNet.getParticipacoes())) {
				DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();

				PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();

				for (PessoaParticipacao pessoaParticipacao : credNet.getParticipacoes()) {

					documentoAnaliseService.cadastrarPessoRetornoCredNet(pessoaParticipacao, user, documentoAnaliseDao,
							pagadorRecebedorService, documentoAnalise.getContratoCobranca(),
							"Empresa Vinculada ao " + documentoAnalise.getMotivoAnalise());
				}
			}

		}

	}

	public FacesMessage serasaCriarConsulta(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			FacesMessage result = null;

			URL myURL;
			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}
			if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
				myURL = new URL(
						"https://servicos.galleriabank.com.br/crednet/api/v1/" + CommonsUtil.somenteNumeros(cnpjcpf));
			else
				myURL = new URL(
						"https://servicos.galleriabank.com.br/relato/api/v1/" + CommonsUtil.somenteNumeros(cnpjcpf));

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer " +  br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
			myURLConnection.setDoOutput(true);

			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Serasa: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
				// docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" ,
				// cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());

				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoSerasa(response.toString());
				documentoAnaliseDao.merge(documentoAnalise);

				result = new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

			}
			myURLConnection.disconnect();
			return result;
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

	public String baixarDocumento(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {
			String base64 = null;
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL;
			if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
				myURL = new URL("https://servicos.galleriabank.com.br/crednet/api/v1/");
			else
				myURL = new URL("https://servicos.galleriabank.com.br/relato/api/v1/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer " +  br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
			myURLConnection.setDoOutput(true);

			JSONObject myResponse = null;

//			JSONObject jsonWhatsApp = engineBodyJsonEngine(engine.getPagador());

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = documentoAnalise.getRetornoSerasa().getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Engine: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
				// docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" ,
				// cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());
				result = new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				base64 = response.toString();
				in.close();
			}

			myURLConnection.disconnect();
			return base64;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
