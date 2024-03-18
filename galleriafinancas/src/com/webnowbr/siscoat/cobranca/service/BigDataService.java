package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import br.com.galleriabank.bigdata.cliente.model.financas.FinancasResponse;
import br.com.galleriabank.bigdata.cliente.model.processos.ProcessoResult;
import br.com.galleriabank.bigdata.cliente.model.relacionamentos.Relacionamento;
import br.com.galleriabank.bigdata.cliente.model.relacionamentos.RelacionamentoResult;

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

			if (!CommonsUtil.semValor(documentoAnalise.getRetornoProcesso())
					&& CommonsUtil.mesmoValor("B", documentoAnalise.getTipoProcesso()))
				try {
					ProcessoResult retornoProcessoB = GsonUtil.fromJson(documentoAnalise.getRetornoProcesso(),
							ProcessoResult.class);
					documentoAnalise.adicionaEstados(CommonsUtil.stringToList(retornoProcessoB.getEstados()));
					
					if(!CommonsUtil.semValor(retornoProcessoB.getStatus()) &&
							!CommonsUtil.semValor(retornoProcessoB.getStatus().getDate_of_birth_validation()) &&
							retornoProcessoB.getStatus().getDate_of_birth_validation().size() > 0 ) {
						String mesagem = retornoProcessoB.getStatus().getDate_of_birth_validation().get(0).getMessage();
						if(mesagem.contains("MINOR")) {
							documentoAnalise.addObservacao("Pessoa menor de idade");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

		}
	}
	

	public FacesMessage criarConsultaProcesso(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {

			String retornoConsulta;

			NetrinService netrinService = new NetrinService();

			// busca dados da receita se nao tiver ainda
			netrinService.atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();
			
			if ( !CommonsUtil.mesmoValorIgnoreCase(documentoAnalise.getIdentificacao(), nomeConsultado)) {
				documentoAnalise.setIdentificacao(nomeConsultado);
			}

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			retornoConsulta = criarExecutaConsultaProcesso(documentoAnalise.getTipoPessoa(), cnpjcpf, nomeConsultado);

			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Big Data CriarConsultaProcesso: Falha na consulta", "");
			} else {

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoProcesso(retornoConsulta);
				documentoAnalise.setTipoProcesso("B");

				documentoAnaliseDao.merge(documentoAnalise);

				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.PROCESSOB, retornoConsulta);

				//String base64 = baixarDocumentoProcesso(documentoAnalise);
				FileService fileService = new FileService();
				//fileService.salvarPdfRetorno(documentoAnalise, base64, "Processo", "interno");
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
			String consultaRetorno = criarExecutaConsultaProcesso(CommonsUtil.pessoaFisicaJuridicaCnpjCpf(cnpjCpf),
					cnpjCpf, pagadorRecebedor.getNome());
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
					+ CommonsUtil.somenteNumeros(cnpjcpf) + "/"
					+ new String(java.util.Base64.getEncoder().encode(pagadorRecebedor.getNome().getBytes()));
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
			if(CommonsUtil.semValor(retornoProcesso))
				return null;

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

			myURL = new URL("https://servicos.galleriabank.com.br/bigData/api/v1/cadastro/pj/"
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public void requestFinancas(DocumentoAnalise documentoAnalise) {
		if (CommonsUtil.semValor(documentoAnalise.getRetornoFinancas())) {

			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.FINANCASBB);

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoFinancas(pagadorRecebedorConsulta.getRetornoConsulta());;
				documentoAnaliseDao.merge(documentoAnalise);
			} else {
				String retornoFinancas;
				retornoFinancas = bigDataCriarConsultaFinancas(documentoAnalise.getCnpjcpf());
			
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoFinancas(retornoFinancas);
				documentoAnaliseDao.merge(documentoAnalise);
				
				PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
				pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.FINANCASBB, retornoFinancas);
			}
		}
	}
	
	public String bigDataCriarConsultaFinancas(String cpf) {
		try {
			int HTTP_COD_SUCESSO = 200;
			String financas = null;
			URL myURL;

			myURL = new URL("https://servicos.galleriabank.com.br/bigData/api/v1/financas/"
					+ CommonsUtil.somenteNumeros(cpf));

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
			} else {
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				financas = response.toString();
			}
			myURLConnection.disconnect();

			return financas;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public void requestRelacionamentos(DocumentoAnalise documentoAnalise) {

		if (CommonsUtil.semValor(documentoAnalise.getRetornoProcesso())) {

			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
							DocumentosAnaliseEnum.RELACIONAMENTO);

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoRelacionamento(pagadorRecebedorConsulta.getRetornoConsulta());
				documentoAnaliseDao.merge(documentoAnalise);
			} else
				criarConsultaRelacionamento(documentoAnalise);

			try {
				RelacionamentoResult retornoProcessoB = GsonUtil.fromJson(documentoAnalise.getRetornoRelacionamento(),
						RelacionamentoResult.class);

				List<String> tipoPessoa = new ArrayList<String>(Arrays.asList("CPF", "CNPJ"));
				PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
				if(!CommonsUtil.semValor(retornoProcessoB)
						&& !CommonsUtil.semValor(retornoProcessoB.getRelacionamento()) 
						&& !CommonsUtil.semValor(retornoProcessoB.getRelacionamento().getCurrentRelationships())) {
					for (Relacionamento relacionamento : retornoProcessoB.getRelacionamento().getCurrentRelationships()
							.stream().filter(r -> tipoPessoa.contains(r.getRelatedEntityTaxIdType()))
							.collect(Collectors.toList())) {
						PagadorRecebedor pagadorRecebedor = new PagadorRecebedor("requestRelacionamentos");
						pagadorRecebedor.setNome(relacionamento.getRelatedEntityName());
						if (CommonsUtil.mesmoValor("CPF", relacionamento.getRelatedEntityTaxIdType()))
							pagadorRecebedor.setCpf(relacionamento.getRelatedEntityTaxIdNumber());
						else
							pagadorRecebedor.setCnpj(relacionamento.getRelatedEntityTaxIdNumber());
	
						pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(pagadorRecebedor);
						String relacao = "SOCIO/";
						if (!relacionamento.getRelationshipName().contains("SOCIO"))
							relacao = relacao + relacionamento.getRelationshipName();
						else
							relacao = relacionamento.getRelationshipName();
	
						pagadorRecebedorService.geraRelacionamento(pagadorRecebedor, relacao, documentoAnalise.getPagador(),
								null, "BigData");
	
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	
	public FacesMessage criarConsultaRelacionamento(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {

			String retornoConsulta;
			
			NetrinService netrinService = new NetrinService();

			// busca dados da receita se nao tiver ainda
			netrinService.atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();
			if ( !CommonsUtil.mesmoValorIgnoreCase(documentoAnalise.getIdentificacao(), nomeConsultado)) {
				documentoAnalise.setIdentificacao(nomeConsultado);
			}

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			
			retornoConsulta = criarExecutaConsultaRelacionamento(documentoAnalise.getTipoPessoa(), cnpjcpf,
					nomeConsultado);

			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Big Data criarConsultaRelacionamento: Falha na consulta",
						"");
			} else {
				
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoRelacionamento(retornoConsulta);
//				documentoAnalise.setTipoProcesso("B");
				
				documentoAnaliseDao.merge(documentoAnalise);

				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.RELACIONAMENTO, retornoConsulta);
				
//				String base64 = baixarDocumentoProcesso(documentoAnalise);
//				FileService fileService = new FileService();
//				fileService.salvarPdfRetorno(documentoAnalise, base64, "Processo", "interno");
				return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

			}

		} catch (Exception e) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Big Data criarConsultaRelacionamento: Falha  (Cod: " + e.getMessage() + ")", "");
		}
	}	

	public String criarExecutaConsultaRelacionamento(String tipoPessoa, String cnpjcpf, String nomeConsultado) { 

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
			String sUrl = "https://servicos.galleriabank.com.br/bigData/api/v1/relacionamento/64/"
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
	
	
	public FacesMessage criarConsultaGrupoEmpresarial(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {

			String retornoConsulta;
			
			NetrinService netrinService = new NetrinService();

			// busca dados da receita se nao tiver ainda
			//netrinService.atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();
			if ( !CommonsUtil.mesmoValorIgnoreCase(documentoAnalise.getIdentificacao(), nomeConsultado)) {
				documentoAnalise.setIdentificacao(nomeConsultado);
			}

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					return null;
			}

			
			retornoConsulta = criarExecutaConsultaGrupoEmpresarial(documentoAnalise.getTipoPessoa(), cnpjcpf);

			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Big Data criarConsultaGrupoEmpresarial: Falha na consulta",
						"");
			} else {
				
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoGrupoEmpresarial(retornoConsulta);
				
				documentoAnaliseDao.merge(documentoAnalise);

				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.GRUPOEMPRESARIAL, retornoConsulta);
				
//				String base64 = baixarDocumentoProcesso(documentoAnalise);
//				FileService fileService = new FileService();
//				fileService.salvarPdfRetorno(documentoAnalise, base64, "Processo", "interno");
				return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");
			}

		} catch (Exception e) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Big Data criarConsultaRelacionamento: Falha  (Cod: " + e.getMessage() + ")", "");
		}
	}	

	public String criarExecutaConsultaGrupoEmpresarial(String tipoPessoa, String cnpjcpf) { 

		if (SiscoatConstants.DEV)
			return "{\"cpfCnpj\":\"762.197.204-15\",\"data\":\"2024-02-27T12:45:28.984-03:00\",\"grupoEmpresarialResult\":[{\"economicGroups\":[{\"mainCompanyTaxId\":\"00485443000110\",\"economicGroupType\":\"OWNERSHIP\",\"totalCompanies\":3,\"totalHeadquarters\":3,\"totalBranches\":0,\"totalStates\":1,\"totalCities\":1,\"totalActiveCompanies\":2,\"totalInactiveCompanies\":1,\"minCompanyAge\":12,\"maxCompanyAge\":31,\"averageCompanyAge\":23.66666667,\"minActivityLevel\":0.22,\"maxActivityLevel\":0.28125,\"averageActivityLevel\":0.25041667,\"minIncomeRange\":\"SEM INFORMACAO\",\"maxIncomeRange\":\"ACIMA DE 10MM ATE 25MM\",\"averageIncomeRange\":\"ACIMA DE 5MM ATE 10MM\",\"totalEmployeesRange\":\"050 A 099\",\"minEmployeesRange\":\"SEM VINCULOS\",\"maxEmployeesRange\":\"050 A 099\",\"averageEmployeesRange\":\"020 A 049\",\"totalPeople\":0,\"totalOwners\":0,\"totalPEPs\":0,\"totalSanctioned\":1,\"totalLawsuits\":33,\"totalWebsites\":6,\"totalAddresses\":3,\"totalPhones\":90,\"totalEmails\":140,\"totalPassages\":136,\"totalBadPassages\":0,\"monthAveragePassages\":1,\"firstPassageDate\":\"2015-07-13T04:12:52.566Z\",\"lastPassageDate\":\"2024-02-22T17:48:45.901Z\",\"last3MonthsPassages\":0,\"last6MonthsPassages\":0,\"last12MonthsPassages\":0,\"last18MonthsPassages\":0,\"economicActivities\":[\"5611204\",\"4120400\",\"2330301\",\"4211101\",\"4212000\",\"4213800\",\"4222701\",\"4292802\",\"4311801\",\"4312600\",\"4313400\",\"4319300\",\"4321500\",\"4329103\",\"4329104\",\"4330401\",\"4391600\",\"4399101\",\"4399102\",\"6821801\",\"6822600\",\"7111100\",\"7112000\",\"7719599\",\"7732201\",\"7732202\",\"8111700\"],\"entitiesByLevel\":{\"0\":3.0},\"incomeRangeDistribution\":{\"EMPRESA NAO ATIVA\":0.0,\"EMPRESA ISENTA\":0.0,\"SEM INFORMACAO\":1.0,\"ATE 250K\":0.0,\"ACIMA DE 250K ATE 500K\":1.0,\"ACIMA DE 500K ATE 1MM\":0.0,\"ACIMA DE 1MM ATE 2.5MM\":0.0,\"ACIMA DE 2.5MM ATE 5MM\":0.0,\"ACIMA DE 5MM ATE 10MM\":0.0,\"ACIMA DE 10MM ATE 25MM\":1.0,\"ACIMA DE 25MM ATE 50MM\":0.0,\"ACIMA DE 50MM ATE 100MM\":0.0,\"ACIMA DE 100MM\":0.0},\"companyDocNumbers\":[\"00485443000110\",\"14506544000134\",\"40786519000161\"],\"partyDonationDistribution\":{},\"candidateDonationDistribution\":{},\"totalValueInPartyDonationsInLastElection\":0,\"totalValueInPartyDonationsInPenultimateElection\":0,\"totalValueInCandidateDonationsInLastElection\":0,\"totalValueInCandidateDonationsInPenultimateElection\":0,\"totalDistinctPartyDonatedToInLastElection\":0,\"totalDistinctPartyDonatedToInPenultimateElection\":0,\"totalDistinctCandidateDonatedToInLastElection\":0,\"totalDistinctCandidateDonatedToInPenultimateElection\":0,\"totalCompaniesWithElectoralDonationInLastElection\":0,\"totalCompaniesWithElectoralDonationInPenultimateElection\":0,\"totalIncomeRange\":\"ACIMA DE 25MM ATE 50MM\",\"stateDistribution\":{\"AC\":0.0,\"AL\":0.0,\"AM\":0.0,\"AP\":0.0,\"BA\":0.0,\"CE\":0.0,\"DF\":0.0,\"ES\":0.0,\"GO\":0.0,\"MA\":0.0,\"MG\":0.0,\"MS\":0.0,\"MT\":0.0,\"PA\":0.0,\"PB\":0.0,\"PE\":0.0,\"PI\":0.0,\"PR\":0.0,\"RJ\":0.0,\"RN\":3.0,\"RO\":0.0,\"RR\":0.0,\"RS\":0.0,\"SC\":0.0,\"SE\":0.0,\"SP\":0.0,\"TO\":0.0,\"EX\":0.0},\"cityDistribution\":{\"NATAL\":3.0},\"employeeRangeDistribution\":{\"SEM INFORMACAO\":0.0,\"SEM VINCULOS\":2.0,\"ATE 01\":0.0,\"002 A 005\":0.0,\"006 A 009\":0.0,\"010 A 019\":0.0,\"020 A 049\":0.0,\"050 A 099\":1.0,\"100 A 499\":0.0,\">= 500\":0.0}}],\"matchKeys\":\"doc{76219720415}\"}],\"queryId\":\"10b0c078-6788-47ba-868a-b58c7dc3f39f\",\"elapsedMilliseconds\":\"1380\",\"queryDate\":\"2024-02-27T15:45:27.9148823Z\",\"status\":{\"company_group_ownership\":[{\"Code\":0.0,\"Message\":\"OK\"}]},\"evidences\":{}}";

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjcpf);
		// TODO: Verifcar consultas anteriores
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			String retornoConsulta;

			URL myURL;
			String sUrl = "https://servicos.galleriabank.com.br/bigData/api/v1/grupoEmpresarial/"
					+ CommonsUtil.somenteNumeros(cnpjcpf);
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
}
