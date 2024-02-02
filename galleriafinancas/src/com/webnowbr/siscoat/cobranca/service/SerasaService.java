package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
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
import br.com.galleriabank.serasarelato.cliente.model.Administrador;
import br.com.galleriabank.serasarelato.cliente.model.Participada;
import br.com.galleriabank.serasarelato.cliente.model.Participante;
import br.com.galleriabank.serasarelato.cliente.model.Relato;
import br.com.galleriabank.serasarelato.cliente.model.Socio;
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
		
		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		DocumentoAnaliseService documentoAnaliseService = new DocumentoAnaliseService();
		
		if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa())) {

			CredNet credNet = GsonUtil.fromJson(documentoAnalise.getRetornoSerasa(), CredNet.class);

			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
				documentoAnalise.getPagador().setDtNascimento(credNet.getPessoa().getDataNascimentoFundacao());
				documentoAnalise.getPagador().setNomeMae(credNet.getPessoa().getNomeMae());
				pagadorRecebedorDao.merge(documentoAnalise.getPagador());
			}

			if (!CommonsUtil.semValor(credNet.getParticipacoes())) {
				for (PessoaParticipacao pessoaParticipacao : credNet.getParticipacoes()) {
					PagadorRecebedor empresa = documentoAnaliseService.cadastrarPessoRetornoCredNet(pessoaParticipacao, user, documentoAnaliseDao,
							pagadorRecebedorService, documentoAnalise.getContratoCobranca(),
							"Empresa Vinculada ao " + documentoAnalise.getMotivoAnalise());
					BigDecimal porcentagem = CommonsUtil.bigDecimalValue(pessoaParticipacao.getParticipacao());
					pagadorRecebedorService.geraRelacionamento(empresa, "Socio", documentoAnalise.getPagador(),
							porcentagem, "Serasa");
				}
			}
		} else if (CommonsUtil.mesmoValor("PJ", documentoAnalise.getTipoPessoa())) {
			
			Relato relato = GsonUtil.fromJson(documentoAnalise.getRetornoSerasa(), Relato.class);
			PagadorRecebedor pagador = null;
			if(!CommonsUtil.semValor(relato.getDadosCadastrais())) {
				pagador = documentoAnaliseService.cadastrarPagadorRetornoRelato(relato.getDadosCadastrais(), pagadorRecebedorService);
			}
			
			if(!CommonsUtil.semValor(relato.getParticipacoes())
					&& !CommonsUtil.semValor(relato.getParticipacoes().getParticipadas())) {
				for (Participada participada : relato.getParticipacoes().getParticipadas()) {
					PagadorRecebedor pagadorParticipada = documentoAnaliseService
							.cadastrarParticipadaRetornoRelato(participada, pagadorRecebedorService);
					for (Participante participantes : participada.getParticipantes()) {
						BigDecimal porcentagem = CommonsUtil.bigDecimalValue(participantes.getParticipacao() / 100);
						PagadorRecebedor pagadorParticipante = documentoAnaliseService
								.cadastrarParticipanteRetornoRelato(participantes, pagadorRecebedorService);
						pagadorRecebedorService.geraRelacionamento(pagadorParticipada, "Socio", pagadorParticipante,
								porcentagem, "Serasa");
					}
					pagadorRecebedorService.geraRelacionamento(pagador, "Participada", pagadorParticipada,
							BigDecimal.ZERO, "Serasa");
				}
			}
			
			if(!CommonsUtil.semValor(relato.getQuadroAdminsitrativo())) {
				for (Administrador administrador : relato.getQuadroAdminsitrativo().getAdministradores()) {
					PagadorRecebedor administradorPagador =
							documentoAnaliseService.cadastrarAdministradorRetornoRelato(administrador, pagadorRecebedorService);					
					pagadorRecebedorService.geraRelacionamento(pagador, "Administrador", administradorPagador,
							BigDecimal.ZERO, "Serasa");
				}
				
				for (Socio socio : relato.getQuadroAdminsitrativo().getSocios()) {
					PagadorRecebedor socioPagador =
							documentoAnaliseService.cadastrarSocioRetornoRelato(socio, pagadorRecebedorService);					
					BigDecimal porcentagem = CommonsUtil.bigDecimalValue(socio.getParticipacao());
					pagadorRecebedorService.geraRelacionamento(pagador, "Socio", socioPagador,
							porcentagem, "Serasa");
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
				// cidadeImovel, "", getNomeUsuarioLogado(), DateUtil.gerarDataHoje());

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

	private String executaConsultaSerasa(String tipoPessoa, String cnpjcpf) {

		if (  SiscoatConstants.DEV)
			return "{\"consDtCadastro\":null,\"consCdCnpjcpf\":null,\"consDtConsulta\":\"2023-06-26T15:35:26.000-03:00\",\"consResultadoConsulta\":null,\"pessoa\":{\"pessTpFisicaJuridica\":\"F\",\"cnpjcpf\":\"02167765975\",\"nomeRazaoSocial\":\"ADRIANA FLORENCIO DOS SANTOS BORGES\",\"dataNascimentoFundacao\":\"1973-10-25T00:00:00.000-03:00\",\"pessCdSituacao\":\"2\",\"descricaoSituacao\":\"REGULAR\",\"dataSituacaoDocumento\":\"2023-01-12T00:00:00.000-03:00\",\"nomeMae\":\"MARIA LUIZA FLORENCIO DOS SANTOS\"},\"codigoServicoConsultaCredito\":null,\"codigoServicoConsultaCreditoSituacao\":null,\"htmlRelatorioBoaVistaPF\":null,\"relatorioHtml\":null,\"cmc7\":null,\"dataEmissaoCheque\":null,\"valorCheque\":null,\"qtdeCheque\":null,\"telefoneCheque\":null,\"cepCheque\":null,\"tipoConsultaCheque\":null,\"codigoDocumentoBanisys\":null,\"flagInformacaoEleitoral\":null,\"flagExtraAcao\":null,\"flagExtraPendencia\":null,\"flagExtraProtesto\":null,\"cepPesquisado\":null,\"featuresAdicionais\":null,\"flagFeaturesAdicionais\":null,\"tipoTratamentoTelefone\":\"S\",\"consultas\":[{\"codigoPessoaConsultaCrednet\":null,\"tipoRegistro\":\"N440\",\"subtipoRegistro\":\"03\",\"mensagem\":null,\"qtdeConsultaUltimo15dias\":2,\"qtdeConsultaUltimo30dias\":0,\"qtdeConsultaUltimo31e60dias\":2,\"qtdeConsultaUltimo61e90dias\":1}],\"rendaEstimada\":{\"subTipo\":null,\"mensagem\":null,\"renda\":25000.0},\"capacidadePagamento\":{\"subTipo\":null,\"mensagem\":null,\"valor\":8600.0},\"comprometimentoRenda\":{\"subTipo\":null,\"mensagem\":null,\"percentualComprometimento\":66.0},\"scorePostivo\":{\"tipo\":null,\"score\":742,\"range\":\"C\",\"taxa\":7.6,\"mensagem\":\"ESPACO RESERVADO PARA MENSAGEM DA INSTITUICAO\",\"codigoMensagem\":\"\"},\"participacoes\":[],\"alertasDocumento\":{\"subTipoAlerta\":null,\"codigoMensagem\":null,\"totalMensagem\":null,\"tipoDocumento\":null,\"numeroDocumento\":null,\"codigoMotivoOcorrencia\":null,\"dataOcorrencia\":null,\"codigoDDDTelefone1\":null,\"numeroTelefone1\":null,\"codigoDDDTelefone2\":null,\"numeroTelefone2\":null,\"codigoDDDTelefone3\":null,\"numeroTelefone3\":null,\"mensagem\":null,\"listaAlertaDocumentoDetalhes\":[]},\"pendenciasFinanceiras\":[],\"protesto\":null,\"chequeSemFundo\":null,\"acaoJudicial\":null,\"habitoPagamentos\":[{\"sequenciaEnviada\":1,\"feature\":\"REIV\",\"subTipo\":\"99\",\"faixaDias\":\"NAO HA\",\"nota\":\"HI\",\"faixaPgto\":\"STORICO\"}],\"indiceRelacionamentos\":[{\"sequenciaEnviada\":null,\"faixa\":\"A3\",\"calculado\":\"S\",\"mensagem\":\"ALTO RELACIONAMENTO COM O MERCADO COM TENDENCIA DE ALTA\",\"setor\":\"\",\"relacionamento\":\"Alto relacionamento\",\"tendencia\":\"Tendência de alta\"}],\"obitos\":[{\"mensagem\":\"NADA CONSTA -\"}],\"relatorioPdf\":null,\"pefin\":null,\"refin\":null,\"dividaVencida\":null,\"pefinResumo\":{\"codigoPendenciasFinanceiras\":null,\"codigoPessoa\":null,\"ppfiDeMensagem\":null,\"mensagem\":null,\"ppfiTpPendencia\":null,\"ppfiDtMaisAntiga\":null,\"ppfiDtMaisRecente\":null,\"ppfiQtTotalPendencia\":null,\"ppfiVlTotalPendencia\":null,\"listaPendenciasFinanceirasDetalhe\":null},\"refinResumo\":{\"codigoPendenciasFinanceiras\":null,\"codigoPessoa\":null,\"ppfiDeMensagem\":null,\"mensagem\":null,\"ppfiTpPendencia\":null,\"ppfiDtMaisAntiga\":null,\"ppfiDtMaisRecente\":null,\"ppfiQtTotalPendencia\":null,\"ppfiVlTotalPendencia\":null,\"listaPendenciasFinanceirasDetalhe\":null},\"dividaVencidaResumo\":{\"codigoPendenciasFinanceiras\":null,\"codigoPessoa\":null,\"ppfiDeMensagem\":null,\"mensagem\":null,\"ppfiTpPendencia\":null,\"ppfiDtMaisAntiga\":null,\"ppfiDtMaisRecente\":null,\"ppfiQtTotalPendencia\":null,\"ppfiVlTotalPendencia\":null,\"listaPendenciasFinanceirasDetalhe\":null},\"chequeSemFundoLista\":[],\"protestoLista\":[],\"alertasDocumentoLista\":[],\"acoesCivil\":null,\"falencias\":null,\"falenciasInsucesso\":null,\"relacionamentoMercado\":null,\"relacionamentoBanco\":null,\"relacionamentoTelecomunicacoes\":null,\"relacionamentoOutrosSetores\":null,\"possuiHabitoPagamentos\":false,\"habitoPagamentosPontual\":null,\"habitoPagamentos001_007\":null,\"habitoPagamentos008_014\":null,\"habitoPagamentos015_020\":null,\"habitoPagamentos021_030\":null,\"habitoPagamentosMais30\":null,\"habitoPagamentosMais60\":null}";
		
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String serasaCriarConsulta(String cnpjCpf) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjCpf);

		String tipoPessoa = CommonsUtil.pessoaFisicaJuridicaCnpjCpf(cnpjCpf);

		DocumentosAnaliseEnum consultaTipo;
		if (CommonsUtil.mesmoValor("PF", tipoPessoa))
			consultaTipo = DocumentosAnaliseEnum.CREDNET;
		else
			consultaTipo = DocumentosAnaliseEnum.RELATO;

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, consultaTipo);

		
		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta()))
			return pagadorRecebedorConsulta.getRetornoConsulta();
		else {
			String consultaRetorno = executaConsultaSerasa(tipoPessoa, cnpjCpf);
			pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
					consultaTipo, consultaRetorno);
			return consultaRetorno;
		}		

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
	}

}
