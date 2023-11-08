package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorConsulta;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;

import br.com.galleriabank.bigdata.cliente.model.cadastro.DadosBasicosResultPj;
import br.com.galleriabank.bigdata.cliente.model.processos.ProcessoResult;

public class BigDataService {

	

	public void requestProcesso(DocumentoAnalise documentoAnalise) {

		if (CommonsUtil.semValor(documentoAnalise.getRetornoProcesso())) {

			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.PROCESSOB);

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoProcesso(pagadorRecebedorConsulta.getRetornoConsulta());
				documentoAnalise.setTipoProcesso("B");
				documentoAnaliseDao.merge(documentoAnalise);
			} else
				criarConsultaProcesso(documentoAnalise);
		}
	}

	public FacesMessage criarConsultaProcesso(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {

			String retornoConsulta;
			
			NetrinService netrinService = new NetrinService();

			// busca dados da receita se nao tiver ainda
			netrinService.atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			
			retornoConsulta = criarExecutaConsultaProcesso(documentoAnalise.getTipoPessoa(), cnpjcpf,
					nomeConsultado);

			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Big Data CriarConsultaProcesso: Falha na consulta",
						"");
			} else {
				
				try {
					ProcessoResult retornoProcessoB  = GsonUtil.fromJson(retornoConsulta, ProcessoResult.class);
					documentoAnalise.adicionaEstados(CommonsUtil.stringToList(retornoProcessoB.getEstados()));
				} catch (Exception e){
					e.printStackTrace();
				}
				
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoProcesso(retornoConsulta);
				documentoAnalise.setTipoProcesso("B");
				
				documentoAnaliseDao.merge(documentoAnalise);

				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.PROCESSOB, retornoConsulta);
				
				String base64 = baixarDocumentoProcesso(documentoAnalise);
				FileService fileService = new FileService();
				fileService.salvarPdfRetorno(documentoAnalise, base64, "Processo", "interno");
				return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

			}

		} catch (Exception e) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Big DataCriarConsultaProcesso: Falha  (Cod: " + e.getMessage() + ")", "");
		}
	}

	public String criarConsultaProcesso(String cnpjCpf) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjCpf);

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.PROCESSOB);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta()))
			return pagadorRecebedorConsulta.getRetornoConsulta();
		else {
			String consultaRetorno = criarExecutaConsultaProcesso(
					CommonsUtil.pessoaFisicaJuridicaCnpjCpf(cnpjCpf), cnpjCpf, pagadorRecebedor.getNome());
			pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
					DocumentosAnaliseEnum.PROCESSOB, consultaRetorno);
			return consultaRetorno;
		}

	}

	public String criarExecutaConsultaProcesso(String tipoPessoa, String cnpjcpf, String nomeConsultado) { 

		if (SiscoatConstants.DEV)
			return "{\"cpf\":\"431.804.298-79\",\"nome\":\"Edielma Candido da Silva\",\"data\":\"2023-07-11T15:53:55.096-03:00\",\"processosCPF\":{\"totalProcessos\":0,\"totalProcessosAutor\":0,\"totalProcessosReu\":0,\"processosUltimos180dias\":0,\"processos\":[{\"numero\":\"\",\"dataNotificacao\":\"\",\"tipo\":\"\",\"assuntoPrincipal\":\"\",\"status\":\"\",\"varaJulgadora\":\"\",\"tribunal\":\"\",\"tribunalLevel\":\"\",\"tribunalTipo\":\"\",\"tribunalCidade\":\"\",\"estado\":\"\",\"partes\":null,\"dataNotificacaoDate\":null}],\"code\":null,\"message\":null},\"processoResumo\":{\"criminal\":null,\"trabalhista\":null,\"tituloExtraJudicial\":null,\"tituloExecucaoFiscal\":null,\"outros\":null,\"processos\":false,\"extraJudicialProtesto\":null,\"execucaoFiscalProtesto\":null,\"criminalProtesto\":null,\"trabalhistaProtesto\":null,\"outrosProtesto\":null}}";

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjcpf);
		// TODO: Verifcar consultas anteriores
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			String retornoConsulta;

			URL myURL;
			String sUrl = "https://servicos.galleriabank.com.br/bigData/api/v1/processo/64/"
					+ CommonsUtil.somenteNumeros(cnpjcpf) + "/" + new String(java.util.Base64.getEncoder().encode(pagadorRecebedor.getNome().getBytes()));
			myURL = new URL(sUrl);

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			String sBearer = br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos();
			myURLConnection.setRequestProperty("Authorization", "Bearer " + sBearer);
			myURLConnection.setDoOutput(true);

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				retornoConsulta = null;
			} else {
				// docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" ,
				// cidadeImovel, "", getNomeUsuarioLogado(), DateUtil.gerarDataHoje());

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

	public String baixarDocumentoProcesso(DocumentoAnalise documentoAnalise) {
		return baixarDocumentoProcesso(documentoAnalise.getRetornoProcesso());
	}
	
	public String baixarDocumentoProcesso(String retornoProcesso) {
		try {
			String base64 = null;
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL = new URL("https://servicos.galleriabank.com.br/bigData/api/v1/processo/true");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization",
					"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
			myURLConnection.setDoOutput(true);

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = retornoProcesso.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Processo: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
				// docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" ,
				// cidadeImovel, "", getNomeUsuarioLogado(), DateUtil.gerarDataHoje());
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
	
	public DadosBasicosResultPj requestCadastroPJ(PagadorRecebedor pagadorRecebedor) {

		FacesMessage facesMessage = new FacesMessage();
		DadosBasicosResultPj dadosBasicosResultPj;

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.CADASTROBB);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
				&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
						DateUtil.getDataHoje()) <= 30) {
			dadosBasicosResultPj = GsonUtil.fromJson(pagadorRecebedorConsulta.getRetornoConsulta(), DadosBasicosResultPj.class);

		} else
			dadosBasicosResultPj = bigDataCriarConsultaCadastroPJ(pagadorRecebedor.getCnpj(), facesMessage);

		if (!CommonsUtil.semValor(dadosBasicosResultPj.getResult().get(0).getBasicData().getOfficialName())) {
			pagadorRecebedor.setNome(dadosBasicosResultPj.getResult().get(0).getBasicData().getOfficialName());
			pagadorRecebedor.setEstado(dadosBasicosResultPj.getResult().get(0).getBasicData().getHeadquarterState());
		}
		
		if (CommonsUtil.semValor(dadosBasicosResultPj.getResult().get(0).getBasicData().getFoundedDate())) {
			pagadorRecebedor.setInicioEmpresa(dadosBasicosResultPj.getResult().get(0).getBasicData().getFoundedDate());
		}
		return dadosBasicosResultPj;
	}
	
	public DadosBasicosResultPj bigDataCriarConsultaCadastroPJ(String sCpfCnpj, FacesMessage facesMessage) { 
		try {
			int HTTP_COD_SUCESSO = 200;
			DadosBasicosResultPj resultPJ = null;
			URL myURL;

			myURL = new URL("https://servicos.galleriabank.com.br/bigdata/api/v1/cadastro/pj/"
					+ CommonsUtil.somenteNumeros(sCpfCnpj));

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization",
					"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
			myURLConnection.setDoOutput(true);

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"BigData: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				resultPJ = GsonUtil.fromJson(response.toString(), DadosBasicosResultPj.class);

				facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

			}
			myURLConnection.disconnect();

			return resultPJ;
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
