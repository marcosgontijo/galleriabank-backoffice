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
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorConsulta;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.model.User;

import br.com.galleriabank.serasacrednet.cliente.model.CredNet;
import br.com.galleriabank.serasacrednet.cliente.model.PessoaParticipacao;
import br.com.galleriabank.serasarelato.cliente.util.GsonUtil;

public class SerasaService {
	

	public void requestSerasa(DocumentoAnalise documentoAnalise, User user) {


		DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
		
		if (CommonsUtil.semValor(documentoAnalise.getRetornoSerasa())) {
			
			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta;

			if (documentoAnalise.getTipoPessoa() == "PJ")
				pagadorRecebedorConsulta = pagaPagadorRecebedorService
						.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.RELATO);
			else {
				pagadorRecebedorConsulta = pagaPagadorRecebedorService
						.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.CREDNET);
			}

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				documentoAnalise.setRetornoSerasa(pagadorRecebedorConsulta.getRetornoConsulta());

				documentoAnaliseDao.merge(documentoAnalise);
			} else
				serasaCriarConsulta(documentoAnalise);
		}
		

		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa())) {

			CredNet credNet = GsonUtil.fromJson(documentoAnalise.getRetornoSerasa(), CredNet.class);

			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
				documentoAnalise.getPagador().setDtNascimento(credNet.getPessoa().getDataNascimentoFundacao());
				documentoAnalise.getPagador().setNomeMae(credNet.getPessoa().getNomeMae());
				pagadorRecebedorDao.merge(documentoAnalise.getPagador());
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
			String retornoConsulta = null;

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			retornoConsulta = executaConsultaSerasa(documentoAnalise.getTipoPessoa(), cnpjcpf);
			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Serasa: Falha na consulta", "");
			} else {
				// docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" ,
				// cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoSerasa(retornoConsulta);
				documentoAnaliseDao.merge(documentoAnalise);

				PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();

				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
							DocumentosAnaliseEnum.CREDNET, retornoConsulta);
				else
					pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
							DocumentosAnaliseEnum.RELATO, retornoConsulta);

				return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

			}

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

	private String executaConsultaSerasa(String tipoPessoa, String cnpjcpf) {

		if (  SiscoatConstants.DEV)
			return "{\"consDtCadastro\":null,\"consCdCnpjcpf\":null,\"consDtConsulta\":\"2023-06-12T17:49:10.000-03:00\",\"consResultadoConsulta\":null,\"pessoa\":{\"pessTpFisicaJuridica\":\"F\",\"cnpjcpf\":\"02481669053\",\"nomeRazaoSocial\":\"YORK MOREIRA ANGELO\",\"dataNascimentoFundacao\":\"1930-05-12T00:00:00.000-03:00\",\"pessCdSituacao\":\"7\",\"descricaoSituacao\":\"TITULAR FALECIDO\",\"dataSituacaoDocumento\":\"2022-09-30T00:00:00.000-03:00\",\"nomeMae\":\"CELINA MOREIRA ANGELO\"},\"codigoServicoConsultaCredito\":null,\"codigoServicoConsultaCreditoSituacao\":null,\"htmlRelatorioBoaVistaPF\":null,\"relatorioHtml\":null,\"cmc7\":null,\"dataEmissaoCheque\":null,\"valorCheque\":null,\"qtdeCheque\":null,\"telefoneCheque\":null,\"cepCheque\":null,\"tipoConsultaCheque\":null,\"codigoDocumentoBanisys\":null,\"flagInformacaoEleitoral\":null,\"flagExtraAcao\":null,\"flagExtraPendencia\":null,\"flagExtraProtesto\":null,\"cepPesquisado\":null,\"featuresAdicionais\":null,\"flagFeaturesAdicionais\":null,\"tipoTratamentoTelefone\":\"S\",\"consultas\":[{\"codigoPessoaConsultaCrednet\":null,\"tipoRegistro\":null,\"subtipoRegistro\":null,\"mensagem\":null,\"qtdeConsultaUltimo15dias\":null,\"qtdeConsultaUltimo30dias\":null,\"qtdeConsultaUltimo31e60dias\":null,\"qtdeConsultaUltimo61e90dias\":null}],\"rendaEstimada\":{\"subTipo\":null,\"mensagem\":null,\"renda\":1450},\"capacidadePagamento\":{\"subTipo\":null,\"mensagem\":null,\"valor\":450},\"comprometimentoRenda\":{\"subTipo\":null,\"mensagem\":null,\"percentualComprometimento\":70},\"scorePostivo\":{\"tipo\":null,\"score\":497,\"range\":\"F\",\"taxa\":28,\"mensagem\":\"ESPACO RESERVADO PARA MENSAGEM DA INSTITUICAO\",\"codigoMensagem\":\"\"},\"participacoes\":[],\"alertasDocumento\":{\"subTipoAlerta\":null,\"codigoMensagem\":null,\"totalMensagem\":null,\"tipoDocumento\":null,\"numeroDocumento\":null,\"codigoMotivoOcorrencia\":null,\"dataOcorrencia\":null,\"codigoDDDTelefone1\":null,\"numeroTelefone1\":null,\"codigoDDDTelefone2\":null,\"numeroTelefone2\":null,\"codigoDDDTelefone3\":null,\"numeroTelefone3\":null,\"mensagem\":null,\"listaAlertaDocumentoDetalhes\":[]},\"pendenciasFinanceiras\":[],\"protesto\":null,\"chequeSemFundo\":null,\"acaoJudicial\":null,\"habitoPagamentos\":[{\"sequenciaEnviada\":1,\"feature\":\"REIV\",\"subTipo\":\"99\",\"faixaDias\":\"NAO HA\",\"nota\":\"HI\",\"faixaPgto\":\"STORICO\"}],\"indiceRelacionamentos\":[{\"sequenciaEnviada\":null,\"faixa\":\"SR\",\"calculado\":\"S\",\"mensagem\":\"SEM RELACIONAMENTO\",\"setor\":\"01\",\"relacionamento\":\"Sem relacionamento\",\"tendencia\":\"Sem relacionamento\"},{\"sequenciaEnviada\":null,\"faixa\":\"B2\",\"calculado\":\"S\",\"mensagem\":\"BAIXO RELACIONAMENTO COM O MERCADO COM TENDENCIA ESTAVEL\",\"setor\":\"02\",\"relacionamento\":\"Baixo relacionamento\",\"tendencia\":\"Tendência estável\"},{\"sequenciaEnviada\":null,\"faixa\":\"SR\",\"calculado\":\"S\",\"mensagem\":\"SEM RELACIONAMENTO\",\"setor\":\"03\",\"relacionamento\":\"Sem relacionamento\",\"tendencia\":\"Sem relacionamento\"},{\"sequenciaEnviada\":null,\"faixa\":\"B2\",\"calculado\":\"S\",\"mensagem\":\"BAIXO RELACIONAMENTO COM O MERCADO COM TENDENCIA ESTAVEL\",\"setor\":\"99\",\"relacionamento\":\"Baixo relacionamento\",\"tendencia\":\"Tendência estável\"}],\"obitos\":[{\"mensagem\":\"ULTIMA ATUALIZACAO DAS INFORMACOES EM 02/05/2021\"},{\"mensagem\":\"NOME DO TITULAR: YORK MOREIRA ANGELO\"},{\"mensagem\":\"DATA DE NASCIMENTO: 12/05/1930\"},{\"mensagem\":\"ANO DO OBITO: 2015\"},{\"mensagem\":\"NOME DA MAE: CELINA MOREIRA ANGELO\"}],\"relatorioPdf\":null,\"pefin\":[],\"refin\":[],\"dividaVencida\":[],\"pefinResumo\":null,\"refinResumo\":null,\"dividaVencidaResumo\":null,\"chequeSemFundoLista\":[],\"protestoLista\":[],\"alertasDocumentoLista\":[],\"acoesCivil\":null,\"falencias\":null,\"falenciasInsucesso\":null,\"relacionamentoMercado\":{\"sequenciaEnviada\":null,\"faixa\":\"B2\",\"calculado\":\"S\",\"mensagem\":\"BAIXO RELACIONAMENTO COM O MERCADO COM TENDENCIA ESTAVEL\",\"setor\":\"99\",\"relacionamento\":\"Baixo relacionamento\",\"tendencia\":\"Tendência estável\"},\"relacionamentoBanco\":{\"sequenciaEnviada\":null,\"faixa\":\"SR\",\"calculado\":\"S\",\"mensagem\":\"SEM RELACIONAMENTO\",\"setor\":\"01\",\"relacionamento\":\"Sem relacionamento\",\"tendencia\":\"Sem relacionamento\"},\"relacionamentoTelecomunicacoes\":{\"sequenciaEnviada\":null,\"faixa\":\"B2\",\"calculado\":\"S\",\"mensagem\":\"BAIXO RELACIONAMENTO COM O MERCADO COM TENDENCIA ESTAVEL\",\"setor\":\"02\",\"relacionamento\":\"Baixo relacionamento\",\"tendencia\":\"Tendência estável\"},\"relacionamentoOutrosSetores\":{\"sequenciaEnviada\":null,\"faixa\":\"SR\",\"calculado\":\"S\",\"mensagem\":\"SEM RELACIONAMENTO\",\"setor\":\"03\",\"relacionamento\":\"Sem relacionamento\",\"tendencia\":\"Sem relacionamento\"},\"possuiHabitoPagamentos\":false,\"habitoPagamentosPontual\":null,\"habitoPagamentos001_007\":null,\"habitoPagamentos008_014\":null,\"habitoPagamentos015_020\":null,\"habitoPagamentos021_030\":null,\"habitoPagamentosMais30\":null,\"habitoPagamentosMais60\":null}";
		
		try {
			int HTTP_COD_SUCESSO = 200;

			String retornoConsulta;
			URL myURL;
			if (CommonsUtil.mesmoValor("PF", tipoPessoa))
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
			myURLConnection.setRequestProperty("Authorization",
					"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
			myURLConnection.setDoOutput(true);

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				retornoConsulta = null;
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

				retornoConsulta = response.toString();

			}
			myURLConnection.disconnect();
			return retornoConsulta;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String serasaCriarConsulta(String scnpjCpf) {
		
		return executaConsultaSerasa(CommonsUtil.pessoaFisicaJuridicaCnpjCpf(scnpjCpf), scnpjCpf);

	}
	
	public String baixarDocumentoConsulta(String retornoSerasa, String tipoPessoa)
			throws MalformedURLException, IOException {
		String base64 = null;
		// loginDocket();
		int HTTP_COD_SUCESSO = 200;
		int HTTP_COD_SUCESSO2 = 201;

		URL myURL;
		if (CommonsUtil.mesmoValor("PF", tipoPessoa))
			myURL = new URL("https://servicos.galleriabank.com.br/crednet/api/v1/");
		else
			myURL = new URL("https://servicos.galleriabank.com.br/relato/api/v1/");

		HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
		myURLConnection.setRequestMethod("POST");
		myURLConnection.setUseCaches(false);
		myURLConnection.setRequestProperty("Accept", "application/json");
		myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
		myURLConnection.setRequestProperty("Content-Type", "application/json");
		myURLConnection.setRequestProperty("Authorization",
				"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
		myURLConnection.setDoOutput(true);

//			JSONObject jsonWhatsApp = engineBodyJsonEngine(engine.getPagador());

		try (OutputStream os = myURLConnection.getOutputStream()) {
			byte[] input = retornoSerasa.getBytes("utf-8");
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
	}

}
