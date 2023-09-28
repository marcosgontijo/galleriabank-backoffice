package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.DocumentoAnalise;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorConsulta;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.ws.netrin.NetrinConsulta;
import com.webnowbr.siscoat.cobranca.ws.netrin.NetrinConsultaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;
import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;

import br.com.galleriabank.netrin.cliente.model.cenprot.CenprotResponse;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaContaBancariaRequest;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaContaBancariaResponse;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaPixRequest;
import br.com.galleriabank.netrin.cliente.model.contabancaria.ValidaPixResponse;
import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPF;
import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPJ;

public class NetrinService {

	public void requestCenprot(DocumentoAnalise documentoAnalise) {

		if (CommonsUtil.semValor(documentoAnalise.getRetornoCenprot())) {
			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.CENPROT);

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				documentoAnalise.setRetornoCenprot(pagadorRecebedorConsulta.getRetornoConsulta());
			} else
				netrinCriarConsultaCenprot(documentoAnalise);
		}
	}

	public String netrinCriarConsultaCenprot(String cnpjCpf) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjCpf);

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.CENPROT);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta()))
			return pagadorRecebedorConsulta.getRetornoConsulta();
		else {
			String consultaRetorno = netrinCriarExecutaConsultaCenprot(cnpjCpf);
			pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.CENPROT,
					consultaRetorno);
			return consultaRetorno;
		}
	}

	public FacesMessage netrinCriarConsultaCenprot(DocumentoAnalise documentoAnalise) { // POST para gerar consulta

		FacesMessage result = null;

		String cnpjcpf = documentoAnalise.getCnpjcpf();
		if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
			if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
				cnpjcpf = documentoAnalise.getPagador().getCpf();
			else
				cnpjcpf = documentoAnalise.getPagador().getCnpj();
		}

		try {
			
			String response = netrinCriarExecutaConsultaCenprot(cnpjcpf);
			try {
				CenprotResponse retornoCenprot = GsonUtil.fromJson(response, CenprotResponse.class);
				documentoAnalise.adicionaEstados(CommonsUtil.stringToList(retornoCenprot.getEstados()));
			} catch (Exception e){
				e.printStackTrace();
			}
			DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
			documentoAnalise.setRetornoCenprot(response);

			documentoAnaliseDao.merge(documentoAnalise);

			PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
			pagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
					DocumentosAnaliseEnum.CENPROT, response);
			
			String base64 = baixarDocumento(documentoAnalise);
			FileService fileService = new FileService();
			fileService.salvarPdfRetorno(documentoAnalise, base64, "Cenprot", "interno");
			result = new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

		} catch (Exception e) {
			result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"netrinCriarConsultaCenprot: Falha  (Cod: " + e.getMessage() + ")", "");
		}

		return result;
	}

	public String netrinCriarExecutaConsultaCenprot(String cnpjcpf) { // POST para gerar consulta
		String retornoConsulta;
		if (SiscoatConstants.DEV) {
			retornoConsulta = "{\"cpfCnpj\":\"43180429879\",\"data\":\"2023-07-11T15:53:59.070-03:00\",\"cenprotProtestos\":{\"code\":606,\"message\":\"Não encontrado\",\"ac\":null,\"al\":null,\"ap\":null,\"am\":null,\"ba\":null,\"ce\":null,\"df\":null,\"es\":null,\"go\":null,\"ma\":null,\"mt\":null,\"ms\":null,\"mg\":null,\"pa\":null,\"pb\":null,\"pr\":null,\"pe\":null,\"pi\":null,\"rj\":null,\"rn\":null,\"rs\":null,\"ro\":null,\"rr\":null,\"sc\":null,\"sp\":null,\"se\":null,\"to\":null,\"cartorios\":null,\"protestosBrasil\":{\"estados\":[]}}}";
			return retornoConsulta;
		}
		try {
			// loginDocket();
			
			int HTTP_COD_SUCESSO = 200;

			URL myURL;

			myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/protesto/"
					+ CommonsUtil.somenteNumeros(cnpjcpf));

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
		} catch ( MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String baixarDocumento(DocumentoAnalise documentoAnalise) {
		return baixarDocumentoCenprot(documentoAnalise.getRetornoCenprot());
	}
	
	public String baixarDocumentoCenprot(String retornoCenprot) {
		try {
			String base64 = null;
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/protesto/false");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "text/html");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization",
					"Bearer " + br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos());
			myURLConnection.setDoOutput(true);

			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = retornoCenprot.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Cenprot: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
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

	public FacesMessage requestCadastroPepPF(DocumentoAnalise documentoAnalise) {
		FacesMessage result = new FacesMessage();

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.PPE);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
				&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
						DateUtil.getDataHoje()) <= 30) {
			DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
			documentoAnalise.setRetornoPpe(pagadorRecebedorConsulta.getRetornoConsulta());

			documentoAnaliseDao.merge(documentoAnalise);

		} else
			result = netrinCriarConsultaCadastroPpePF(documentoAnalise);
		return result;
	}

	public ReceitaFederalPF requestCadastroPF(String sCpfCnpj) {
		FacesMessage facesMessage = new FacesMessage();
		return netrinCriarConsultaCadastroPF(sCpfCnpj, facesMessage);
	}

	public ReceitaFederalPF requestCadastroPF(PagadorRecebedor pagadorRecebedor) {
		FacesMessage facesMessage = new FacesMessage();

		ReceitaFederalPF receitaFederalPF;
		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.RECEITA_FEDERAL);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
				&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
						DateUtil.getDataHoje()) <= 30) {
			receitaFederalPF = GsonUtil.fromJson(pagadorRecebedorConsulta.getRetornoConsulta(), ReceitaFederalPF.class);
		} else
			receitaFederalPF = netrinCriarConsultaCadastroPF(pagadorRecebedor.getCpf(), facesMessage);

		pagadorRecebedor.setNome(receitaFederalPF.getCpfBirthdate().getNome());
		pagadorRecebedor.setDtNascimento(
				CommonsUtil.dateValue(receitaFederalPF.getCpfBirthdate().getDataNascimento(), "dd/MM/yyyy"));
		if (CommonsUtil.mesmoValor("M", receitaFederalPF.getCpfBirthdate().getGenero()))
			pagadorRecebedor.setSexo("MASCULINO");
		else if (CommonsUtil.mesmoValor("F", receitaFederalPF.getCpfBirthdate().getGenero()))
			pagadorRecebedor.setSexo("FEMININO");

		pagadorRecebedor.setNomeMae(receitaFederalPF.getCpfBirthdate().getNomeMae());

		return receitaFederalPF;
	}

	public ReceitaFederalPF netrinCriarConsultaCadastroPF(String sCpfCnpj, FacesMessage facesMessage) { // POST para
																										// gerar
																										// consulta
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			ReceitaFederalPF resultPF = null;

			String numeorsCpfCnpj = CommonsUtil.somenteNumeros(sCpfCnpj);

			String tipoConsulta = CommonsUtil.pessoaFisicaJuridicaCnpjCpf(numeorsCpfCnpj);
			URL myURL;

			myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/cadastro/pf/"
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
				resultPF = GsonUtil.fromJson(response.toString(), ReceitaFederalPF.class);

				facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

			}
			myURLConnection.disconnect();

			return resultPF;

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

	public FacesMessage netrinCriarConsultaCadastroPpePF(DocumentoAnalise documentoAnalise) {
		try {
			// loginDocket();
			FacesMessage result = null;

			String retornoConsulta = null;

			atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();
			String numeorsCpfCnpj = CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf());

			retornoConsulta = netrinCriarExecutaConsultaCadastroPpePF(numeorsCpfCnpj, nomeConsultado);

			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR, "PPE: Falha: Falha na consulta", "");
			} else {

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoPpe(retornoConsulta);

				documentoAnaliseDao.merge(documentoAnalise);

				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.PPE, retornoConsulta);

				result = new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");
			}
			return result;

		} catch (Exception e) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR, "PPE: Falha  (Cod: " + e.getMessage() + ")", "");
		}
	}

	public String netrinCriarConsultaCadastroPpePF(String cnpjCpf) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjCpf);

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.PPE);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta()))
			return pagadorRecebedorConsulta.getRetornoConsulta();
		else {
			String consultaRetorno = netrinCriarExecutaConsultaCadastroPpePF(cnpjCpf,
					pagadorRecebedor.getNome());
			pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
					DocumentosAnaliseEnum.PPE, consultaRetorno);
			return consultaRetorno;
		}
	}

	public String netrinCriarExecutaConsultaCadastroPpePF(String cnpjcpf, String nomeConsultado) {

		if (SiscoatConstants.DEV)
			return "{\"cpf\":\"43180429879\",\"nome\":\"Edielma Candido da Silva\",\"data\":\"2023-07-11T15:53:53.771-03:00\",\"pepKyc\":{\"currentlySanctioned\":\"Não\",\"last30DaysSanctions\":0,\"last90DaysSanctions\":0,\"last180DaysSanctions\":0,\"last365DaysSanctions\":0,\"currentlyPEP\":\"Não\",\"lastYearOccurencePEP\":0,\"last3YearsOccurencePEP\":0,\"last5YearsOccurencePEP\":0,\"last5PlusYearsOccurencePEP\":0,\"historyPEP\":[{\"currentlySanctioned\":null,\"level\":\"\",\"jobTitle\":\"\",\"department\":\"\",\"motive\":\"\",\"startDate\":\"\",\"endDate\":\"\"}],\"sanctionsHistory\":[{\"source\":\"\",\"type\":\"\",\"standardizedSanctionType\":\"\",\"matchRate\":\"\",\"nameUniquenessScore\":\"\",\"startDate\":\"\",\"endDate\":\"\",\"details\":{},\"detailsTratado\":[]}],\"code\":null,\"message\":null}}";

		try {
			int HTTP_COD_SUCESSO = 200;
			String retornoConsulta;

//			atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			// String nomeConsultado = documentoAnalise.getPagador().getNome();
			String numeorsCpfCnpj = CommonsUtil.somenteNumeros(cnpjcpf);

			URL myURL;
			myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/ppe/" + numeorsCpfCnpj + "/"
					+ (nomeConsultado.replace(" ", "%20").replace("/", "%2F")));
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

	public ReceitaFederalPJ requestCadastroPJ(String sCpfCnpj) {

		FacesMessage facesMessage = new FacesMessage();
		return netrinCriarConsultaCadastroPJ(sCpfCnpj, facesMessage);
	}

	public ReceitaFederalPJ requestCadastroPJ(PagadorRecebedor pagadorRecebedor) {

		FacesMessage facesMessage = new FacesMessage();
		ReceitaFederalPJ receitaFederalPJ;

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.RECEITA_FEDERAL);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
				&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
						DateUtil.getDataHoje()) <= 30) {
			receitaFederalPJ = GsonUtil.fromJson(pagadorRecebedorConsulta.getRetornoConsulta(), ReceitaFederalPJ.class);

		} else
			receitaFederalPJ = netrinCriarConsultaCadastroPJ(pagadorRecebedor.getCnpj(), facesMessage);

		if (!CommonsUtil.semValor(receitaFederalPJ.getReceitaFederal().getRazaoSocial())) {
			pagadorRecebedor.setNome(receitaFederalPJ.getReceitaFederal().getRazaoSocial());
			pagadorRecebedor.setEndereco(receitaFederalPJ.getReceitaFederal().getLogradouro());
			pagadorRecebedor.setNumero(receitaFederalPJ.getReceitaFederal().getNumero());
			pagadorRecebedor.setComplemento(receitaFederalPJ.getReceitaFederal().getComplemento());
			pagadorRecebedor.setBairro(receitaFederalPJ.getReceitaFederal().getBairro());
			pagadorRecebedor.setCidade(receitaFederalPJ.getReceitaFederal().getMunicipio());
			pagadorRecebedor.setCep(receitaFederalPJ.getReceitaFederal().getCep());
			pagadorRecebedor.setEstado(receitaFederalPJ.getReceitaFederal().getUf());
			pagadorRecebedor.setEmail(receitaFederalPJ.getReceitaFederal().getEmail());
//			pagadorRecebedor.sett(receitaFederalPJ.getReceitaFederal().getTelefone());
		}
		return receitaFederalPJ;
	}

	public ReceitaFederalPJ netrinCriarConsultaCadastroPJ(String sCpfCnpj, FacesMessage facesMessage) { // POST para
																										// gerar
																										// consulta
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			ReceitaFederalPJ resultPJ = null;

			String numeorsCpfCnpj = CommonsUtil.somenteNumeros(sCpfCnpj);

//			String tipoConsulta = CommonsUtil.pessoaFisicaJuridicaCnpjCpf(numeorsCpfCnpj);
			URL myURL;

			myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/cadastro/pj/"
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

				resultPJ = GsonUtil.fromJson(response.toString(), ReceitaFederalPJ.class);

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

	public void requestProcesso(DocumentoAnalise documentoAnalise) {

		if (CommonsUtil.semValor(documentoAnalise.getRetornoProcesso())) {

			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.PROCESSO);

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoProcesso(pagadorRecebedorConsulta.getRetornoConsulta());
				documentoAnalise.setTipoProcesso("N");
				documentoAnaliseDao.merge(documentoAnalise);
			} else
				netrinCriarConsultaProcesso(documentoAnalise);
		}
	}

	public FacesMessage netrinCriarConsultaProcesso(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {

			String retornoConsulta;

			// busca dados da receita se nao tiver ainda
			atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			
			retornoConsulta = netrinCriarExecutaConsultaProcesso(documentoAnalise.getTipoPessoa(), cnpjcpf,
					nomeConsultado);

			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR, "netrinCriarConsultaProcesso: Falha na consulta",
						"");
			} else {

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoProcesso(retornoConsulta);
				documentoAnalise.setTipoProcesso("N");

				documentoAnaliseDao.merge(documentoAnalise);

				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.PROCESSO, retornoConsulta);

				return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

			}

		} catch (Exception e) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"netrinCriarConsultaProcesso: Falha  (Cod: " + e.getMessage() + ")", "");
		}
	}

	public String netrinCriarConsultaProcesso(String cnpjCpf) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjCpf);

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.PROCESSO);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta()))
			return pagadorRecebedorConsulta.getRetornoConsulta();
		else {
			String consultaRetorno = netrinCriarExecutaConsultaProcesso(
					CommonsUtil.pessoaFisicaJuridicaCnpjCpf(cnpjCpf), cnpjCpf, pagadorRecebedor.getNome());
			pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
					DocumentosAnaliseEnum.PROCESSO, consultaRetorno);
			return consultaRetorno;
		}

	}

	public String netrinCriarExecutaConsultaProcesso(String tipoPessoa, String cnpjcpf, String nomeConsultado) { 

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
			String sUrl = "https://servicos.galleriabank.com.br/netrin/api/v1/processo/"
					+ CommonsUtil.somenteNumeros(cnpjcpf) + "/" + (pagadorRecebedor.getNome().replace(" ", "%20").replace("/", "%2F"));
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

	public String atualizaDadosPagadoRecebedorComReceitaFederal(PagadorRecebedor pagadorRecebedor) {
		String nomeConsultado = "";

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();

		if (!CommonsUtil.semValor(pagadorRecebedor.getCpf())) {
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.RECEITA_FEDERAL);
			ReceitaFederalPF receitaFederalPF = null;
			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta().replace("{}", ""))) {
				receitaFederalPF = GsonUtil.fromJson(pagadorRecebedorConsulta.getRetornoConsulta(),
						ReceitaFederalPF.class);
			} else {
				receitaFederalPF = requestCadastroPF(pagadorRecebedor.getCpf());
			}

			if (!CommonsUtil.semValor(receitaFederalPF) && !CommonsUtil.semValor(pagadorRecebedor.getId())) {
				pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
						DocumentosAnaliseEnum.RECEITA_FEDERAL, GsonUtil.toJson(receitaFederalPF));
			}

			if (!CommonsUtil.semValor(receitaFederalPF))
				nomeConsultado = receitaFederalPF.getCpfBirthdate().getNome();
		} else {

			if (!CommonsUtil.semValor(pagadorRecebedor.getCnpj())) {

				PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
						.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.RECEITA_FEDERAL);
				ReceitaFederalPJ receitaFederalPJ = null;
				if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
						&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
						&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta().replace("{}", ""))) {
					receitaFederalPJ = GsonUtil.fromJson(pagadorRecebedorConsulta.getRetornoConsulta(),
							ReceitaFederalPJ.class);
				} else {
					receitaFederalPJ = requestCadastroPJ(pagadorRecebedor.getCnpj());
				}

				if (!CommonsUtil.semValor(receitaFederalPJ) && !CommonsUtil.semValor(pagadorRecebedor.getId())) {
					pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
							DocumentosAnaliseEnum.RECEITA_FEDERAL, GsonUtil.toJson(receitaFederalPJ));
				}

				if (!CommonsUtil.semValor(receitaFederalPJ))
					nomeConsultado = receitaFederalPJ.getReceitaFederal().getRazaoSocial();
			}

		}
		return nomeConsultado;
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

			URL myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/processo/true");

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

	public String baixarDocumentoPpe(DocumentoAnalise documentoAnalise) {
		return baixarDocumentoPpe(documentoAnalise.getRetornoPpe());
	}
	
	public String baixarDocumentoPpe(String retornoPpe) {
		try {
			String base64 = null;
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/ppe");

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
				byte[] input = retornoPpe.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"PPE: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
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

	public String baixarDocumentoDossie(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {
			String base64 = null;
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			boolean detalhadoProtesto = false;
			boolean detalhadoProcesso = true;

			URL myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/dossie/" + detalhadoProtesto + "/"
					+ detalhadoProcesso);

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
				byte[] input = documentoAnalise.getDossieRequest().getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Dossie: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
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

	public ValidaContaBancariaResponse requestValidaContaBancaria(ValidaContaBancariaRequest validaContaBancariaRequest,
			FacesContext context) {
		ValidaContaBancariaResponse result = new ValidaContaBancariaResponse();
		result = netrinCriarConsultaContaBancaria(validaContaBancariaRequest, context);
		return result;
	}

	public ValidaContaBancariaResponse netrinCriarConsultaContaBancaria(
			ValidaContaBancariaRequest validaContaBancariaRequest, FacesContext context) { // POST para
		// gerar
		// consulta
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			ValidaContaBancariaResponse resultContaBancaria = null;

			URL myURL;

			myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/contabancaria/contabancaria");

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
				byte[] input = GsonUtil.toJson(validaContaBancariaRequest).getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Valida Conta Bancaria: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", ""));
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
				resultContaBancaria = GsonUtil.fromJson(response.toString(), ValidaContaBancariaResponse.class);

				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));

			}
			myURLConnection.disconnect();

			return resultContaBancaria;

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

	public ValidaPixResponse requestValidaPix(ValidaPixRequest validaPixRequest, FacesContext context) {
		ValidaPixResponse result = new ValidaPixResponse();
		result = netrinCriarConsultaPix(validaPixRequest, context);
		return result;
	}

	public ValidaPixResponse netrinCriarConsultaPix(ValidaPixRequest validaPixRequest, FacesContext context) { // POST
																												// para
		// gerar
		// consulta
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			ValidaPixResponse resultPix = null;

			URL myURL;

			myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/contabancaria/pix");

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
				byte[] input = GsonUtil.toJson(validaPixRequest).getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Valida Pix: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", ""));
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
				resultPix = GsonUtil.fromJson(response.toString(), ValidaPixResponse.class);

				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));

			}
			myURLConnection.disconnect();

			return resultPix;

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

	/// \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ CND Trabalhista TST
	public void requestCNDTrabalhistaTST(DocumentoAnalise documentoAnalise) {
		if (CommonsUtil.semValor(documentoAnalise.getRetornoCNDTrabalhistaTST())) {
			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.CNDTTST);

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoCNDTrabalhistaTST(pagadorRecebedorConsulta.getRetornoConsulta());
				documentoAnaliseDao.merge(documentoAnalise);
			} else
				netrinCriarConsultaCNDTrabalhistaTST(documentoAnalise);
		}
	}

	public FacesMessage netrinCriarConsultaCNDTrabalhistaTST(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {

			String retornoConsulta;

			// busca dados da receita se nao tiver ainda
			atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			
			retornoConsulta = netrinCriarExecutaConsultaCNDTrabalhistaTST(cnpjcpf);

			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR, "netrinCriarConsultaCNDTrabalhistaTST: Falha na consulta",
						"");
			} else {

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoCNDTrabalhistaTST(retornoConsulta);

				documentoAnaliseDao.merge(documentoAnalise);

				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.CNDTTST, retornoConsulta);
				
				String base64 = baixarDocumentoCNDTrabalhistaTST(documentoAnalise);
				FileService fileService = new FileService();
				fileService.salvarPdfRetorno(documentoAnalise, base64, "CNDT TST", "interno");
				
				return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

			}

		} catch (Exception e) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"netrinCriarConsultaCNDTrabalhistaTST: Falha  (Cod: " + e.getMessage() + ")", "");
		}
	}

	public String netrinCriarConsultaCNDTrabalhistaTST(String cnpjCpf) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjCpf);

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.CNDTTST);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta()))
			return pagadorRecebedorConsulta.getRetornoConsulta();
		else {
			String consultaRetorno = netrinCriarExecutaConsultaCNDTrabalhistaTST(cnpjCpf);
			pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
					DocumentosAnaliseEnum.CNDTTST, consultaRetorno);
			return consultaRetorno;
		}

	}

	public String netrinCriarExecutaConsultaCNDTrabalhistaTST(String cnpjcpf) { 

		//if (SiscoatConstants.DEV)
		//	return "{\"cpf\":\"431.804.298-79\",\"nome\":\"Edielma Candido da Silva\",\"data\":\"2023-07-11T15:53:55.096-03:00\",\"processosCPF\":{\"totalProcessos\":0,\"totalProcessosAutor\":0,\"totalProcessosReu\":0,\"processosUltimos180dias\":0,\"processos\":[{\"numero\":\"\",\"dataNotificacao\":\"\",\"tipo\":\"\",\"assuntoPrincipal\":\"\",\"status\":\"\",\"varaJulgadora\":\"\",\"tribunal\":\"\",\"tribunalLevel\":\"\",\"tribunalTipo\":\"\",\"tribunalCidade\":\"\",\"estado\":\"\",\"partes\":null,\"dataNotificacaoDate\":null}],\"code\":null,\"message\":null},\"processoResumo\":{\"criminal\":null,\"trabalhista\":null,\"tituloExtraJudicial\":null,\"tituloExecucaoFiscal\":null,\"outros\":null,\"processos\":false,\"extraJudicialProtesto\":null,\"execucaoFiscalProtesto\":null,\"criminalProtesto\":null,\"trabalhistaProtesto\":null,\"outrosProtesto\":null}}";

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjcpf);
		// TODO: Verifcar consultas anteriores
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			String retornoConsulta;

			URL myURL;
			String sUrl = "https://servicos.galleriabank.com.br/netrin/api/v1/CNDTrabalhistaTST/"
					+ CommonsUtil.somenteNumeros(cnpjcpf);// + "/" + pagadorRecebedor.getNome().replace(" ", "%20").replace("/", "%2F");
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
	
	public String baixarDocumentoCNDTrabalhistaTST(DocumentoAnalise documentoAnalise) {
		return baixarDocumentoCNDTrabalhistaTST(documentoAnalise.getRetornoCNDTrabalhistaTST());
	}
	
	public String baixarDocumentoCNDTrabalhistaTST(String retornoCndtTST) {
		try {
			String base64 = null;
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/CNDTrabalhistaTST");

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
				byte[] input = retornoCndtTST.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Processo: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
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
	/// /\ /\ /\ /\ /\ /\ /\ /\ /\ /\ /\ /\  CND Trabalhista TST

	
	/// \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ CND Federal
	public void requestCNDFederal(DocumentoAnalise documentoAnalise) {
		if (CommonsUtil.semValor(documentoAnalise.getRetornoCNDFederal())) {
			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.CNDFEDERAL);

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoCNDFederal(pagadorRecebedorConsulta.getRetornoConsulta());
				documentoAnaliseDao.merge(documentoAnalise);
			} else
				netrinCriarConsultaCNDFederal(documentoAnalise);
		}
	}

	public FacesMessage netrinCriarConsultaCNDFederal(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {

			String retornoConsulta;

			// busca dados da receita se nao tiver ainda
			atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			
			retornoConsulta = netrinCriarExecutaConsultaCNDFederal(cnpjcpf);

			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR, "netrinCriarConsultaCNDFederal: Falha na consulta",
						"");
			} else {

				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoCNDFederal(retornoConsulta);

				documentoAnaliseDao.merge(documentoAnalise);

				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.CNDFEDERAL, retornoConsulta);
				
				String base64 = baixarDocumentoCNDFederal(documentoAnalise);
				FileService fileService = new FileService();
				fileService.salvarPdfRetorno(documentoAnalise, base64, "CND Federal", "interno");

				return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");

			}

		} catch (Exception e) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"netrinCriarConsultaCNDFederal: Falha  (Cod: " + e.getMessage() + ")", "");
		}
	}

	public String netrinCriarConsultaCNDFederal(String cnpjCpf) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjCpf);

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.CNDFEDERAL);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta()))
			return pagadorRecebedorConsulta.getRetornoConsulta();
		else {
			String consultaRetorno = netrinCriarExecutaConsultaCNDFederal(cnpjCpf);
			pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
					DocumentosAnaliseEnum.CNDFEDERAL, consultaRetorno);
			return consultaRetorno;
		}

	}

	public String netrinCriarExecutaConsultaCNDFederal(String cnpjcpf) { 

		//if (SiscoatConstants.DEV)
		//	return "{\"cpf\":\"431.804.298-79\",\"nome\":\"Edielma Candido da Silva\",\"data\":\"2023-07-11T15:53:55.096-03:00\",\"processosCPF\":{\"totalProcessos\":0,\"totalProcessosAutor\":0,\"totalProcessosReu\":0,\"processosUltimos180dias\":0,\"processos\":[{\"numero\":\"\",\"dataNotificacao\":\"\",\"tipo\":\"\",\"assuntoPrincipal\":\"\",\"status\":\"\",\"varaJulgadora\":\"\",\"tribunal\":\"\",\"tribunalLevel\":\"\",\"tribunalTipo\":\"\",\"tribunalCidade\":\"\",\"estado\":\"\",\"partes\":null,\"dataNotificacaoDate\":null}],\"code\":null,\"message\":null},\"processoResumo\":{\"criminal\":null,\"trabalhista\":null,\"tituloExtraJudicial\":null,\"tituloExecucaoFiscal\":null,\"outros\":null,\"processos\":false,\"extraJudicialProtesto\":null,\"execucaoFiscalProtesto\":null,\"criminalProtesto\":null,\"trabalhistaProtesto\":null,\"outrosProtesto\":null}}";

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjcpf);
		// TODO: Verifcar consultas anteriores
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			String retornoConsulta;

			URL myURL;
			String sUrl = "https://servicos.galleriabank.com.br/netrin/api/v1/CNDFederal/"
					+ CommonsUtil.somenteNumeros(cnpjcpf);// + "/" + pagadorRecebedor.getNome().replace(" ", "%20").replace("/", "%2F");
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
	
	public String baixarDocumentoCNDFederal(DocumentoAnalise documentoAnalise) {
		return baixarDocumentoCNDFederal(documentoAnalise.getRetornoCNDFederal());
	}
	
	public String baixarDocumentoCNDFederal(String retornoCndFederal) {
		try {
			String base64 = null;
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/CNDFederal");

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
				byte[] input = retornoCndFederal.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Processo: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
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
	/// /\ /\ /\ /\ /\ /\ /\ /\ /\ /\ /\ /\  CND Federal
	
	/// \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ \/ CND Estadual
	public void requestCNDEstadual(DocumentoAnalise documentoAnalise) {
		if (CommonsUtil.semValor(documentoAnalise.getRetornoCNDEstadual())) {
			PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(documentoAnalise.getPagador(), DocumentosAnaliseEnum.CNDESTADUAL);

			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
					&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta())
					&& DateUtil.getDaysBetweenDates(pagadorRecebedorConsulta.getDataConsulta(),
							DateUtil.getDataHoje()) <= 30) {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoCNDEstadual(pagadorRecebedorConsulta.getRetornoConsulta());
				documentoAnaliseDao.merge(documentoAnalise);
			} else
				netrinCriarConsultaCNDEstadual(documentoAnalise);
		}
	}

	public FacesMessage netrinCriarConsultaCNDEstadual(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		return netrinCriarConsultaCNDEstadual(documentoAnalise, null);
	}
	
	public FacesMessage netrinCriarConsultaCNDEstadual(DocumentoAnalise documentoAnalise, String uf) { // POST para gerar consulta
		try {
			String retornoConsulta;
			// busca dados da receita se nao tiver ainda
			atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			if(CommonsUtil.semValor(uf)) {
				retornoConsulta = netrinCriarExecutaConsultaCNDEstadual(cnpjcpf);
			} else {
				retornoConsulta = netrinCriarExecutaConsultaCNDEstadual(cnpjcpf, uf);
			}

			if (CommonsUtil.semValor(retornoConsulta)) {
				return new FacesMessage(FacesMessage.SEVERITY_ERROR, "netrinCriarConsultaCNDEstadual: Falha na consulta",
						"");
			} else {
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoCNDEstadual(retornoConsulta);

				documentoAnaliseDao.merge(documentoAnalise);
				String nomedoc = "CND Estadual";
				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				if(!CommonsUtil.semValor(uf)) {
					nomedoc = nomedoc + " " + uf;
					PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
							DocumentosAnaliseEnum.CNDESTADUAL, retornoConsulta, uf);
				} else {
					PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
							DocumentosAnaliseEnum.CNDESTADUAL, retornoConsulta);
				}
				String base64 = baixarDocumentoCNDEstadual(retornoConsulta);
				FileService fileService = new FileService();
				fileService.salvarPdfRetorno(documentoAnalise, base64, nomedoc, "interno");
				return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");
			}

		} catch (Exception e) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"netrinCriarConsultaCNDEstadual: Falha  (Cod: " + e.getMessage() + ")", "");
		}
	}

	public String netrinCriarConsultaCNDEstadual(String cnpjCpf) {

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjCpf);

		PagadorRecebedorService pagaPagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedorConsulta pagadorRecebedorConsulta = pagaPagadorRecebedorService
				.buscaConsultaNoPagadorRecebedor(pagadorRecebedor, DocumentosAnaliseEnum.CNDESTADUAL);

		if (!CommonsUtil.semValor(pagadorRecebedorConsulta)
				&& !CommonsUtil.semValor(pagadorRecebedorConsulta.getRetornoConsulta()))
			return pagadorRecebedorConsulta.getRetornoConsulta();
		else {
			String consultaRetorno = netrinCriarExecutaConsultaCNDEstadual(cnpjCpf);
			pagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
					DocumentosAnaliseEnum.CNDESTADUAL, consultaRetorno);
			return consultaRetorno;
		}

	}

	public String netrinCriarExecutaConsultaCNDEstadual(String cnpjcpf) { 
		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjcpf);	
		return netrinCriarExecutaConsultaCNDEstadual(cnpjcpf, pagadorRecebedor.getEstado());	
	}
	
	public String netrinCriarExecutaConsultaCNDEstadual(String cnpjcpf, String uf) { 

		//if (SiscoatConstants.DEV)
		//	return "{\"cpf\":\"431.804.298-79\",\"nome\":\"Edielma Candido da Silva\",\"data\":\"2023-07-11T15:53:55.096-03:00\",\"processosCPF\":{\"totalProcessos\":0,\"totalProcessosAutor\":0,\"totalProcessosReu\":0,\"processosUltimos180dias\":0,\"processos\":[{\"numero\":\"\",\"dataNotificacao\":\"\",\"tipo\":\"\",\"assuntoPrincipal\":\"\",\"status\":\"\",\"varaJulgadora\":\"\",\"tribunal\":\"\",\"tribunalLevel\":\"\",\"tribunalTipo\":\"\",\"tribunalCidade\":\"\",\"estado\":\"\",\"partes\":null,\"dataNotificacaoDate\":null}],\"code\":null,\"message\":null},\"processoResumo\":{\"criminal\":null,\"trabalhista\":null,\"tituloExtraJudicial\":null,\"tituloExecucaoFiscal\":null,\"outros\":null,\"processos\":false,\"extraJudicialProtesto\":null,\"execucaoFiscalProtesto\":null,\"criminalProtesto\":null,\"trabalhistaProtesto\":null,\"outrosProtesto\":null}}";

		PagadorRecebedorService pagadorRecebedorService = new PagadorRecebedorService();
		PagadorRecebedor pagadorRecebedor = pagadorRecebedorService.buscaOuInsere(cnpjcpf);
		// TODO: Verifcar consultas anteriores
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			String retornoConsulta;
			String cep;
			if(CommonsUtil.semValor(pagadorRecebedor.getCep())) {
				cep = "1";
			} else {
				cep = pagadorRecebedor.getCep();
			}
			
			URL myURL;
			String sUrl = "https://servicos.galleriabank.com.br/netrin/api/v1/CNDEstadual/"
					+ CommonsUtil.somenteNumeros(cnpjcpf)//
					+ "/" + uf.toUpperCase()
					+ "/" + cep;
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
	
	public String baixarDocumentoCNDEstadual(DocumentoAnalise documentoAnalise) {
		return baixarDocumentoCNDEstadual(documentoAnalise.getRetornoCNDEstadual());
	}
	
	public String baixarDocumentoCNDEstadual(String retornoCndEstadual) {
		try {
			String base64 = null;
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;

			URL myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/CNDEstadual");

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
				byte[] input = retornoCndEstadual.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			FacesMessage result = null;
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO
					&& myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Processo: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
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
	/// /\ /\ /\ /\ /\ /\ /\ /\ /\ /\ /\ /\  CND Estadual
	
	public FacesMessage pedirConsulta(NetrinConsulta netrinConsulta) {
		try {
			String url = netrinConsulta.getNetrinDocumentos().getUrlService();
			DocumentoAnalise documentoAnalise = netrinConsulta.getDocumentoAnalise();
			atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}
			PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
			NetrinConsultaDao netrinConsultaDao = new NetrinConsultaDao();
			netrinConsulta.setCpfCnpj(cnpjcpf);			
			
			if(CommonsUtil.mesmoValor(url, "/api/v1/processo")) {
				requestProcessoNetrinConulta(netrinConsulta, documentoAnalise, PagadorRecebedorService);
			} else if(CommonsUtil.mesmoValor(url, "/api/v1/CNDEstadual")) {
				requestCndEstadualNetrinConulta(netrinConsulta, documentoAnalise, PagadorRecebedorService);
			} else if(CommonsUtil.mesmoValor(url, "/api/v1/CNDFederal")) {
				requestCndFederalNetrinConulta(netrinConsulta, documentoAnalise, PagadorRecebedorService);
			} else if(CommonsUtil.mesmoValor(url, "/api/v1/CNDTrabalhistaTST")) {
				requestCndTrabalhistaTSTNetrinConulta(netrinConsulta, documentoAnalise, PagadorRecebedorService);
			}
			netrinConsultaDao.create(netrinConsulta);
			return new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", "");
		} catch (Exception e) {
			return new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					netrinConsulta.getNetrinDocumentos().getNome() + " - " + netrinConsulta.getCpfCnpj() + ": Já existente", "");
		}
	}

	private void requestProcessoNetrinConulta(NetrinConsulta netrinConsulta, DocumentoAnalise documentoAnalise,
			PagadorRecebedorService PagadorRecebedorService) {
		String retornoConsulta;
		String base64;
		String nomedoc;
		retornoConsulta = netrinCriarExecutaConsultaProcesso(documentoAnalise.getTipoPessoa(),
				netrinConsulta.getCpfCnpj(),documentoAnalise.getPagador().getNome());
		netrinConsulta.setRetorno(retornoConsulta);
		netrinConsulta.setPdf(baixarDocumentoProcesso(retornoConsulta));
		PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
				DocumentosAnaliseEnum.PROCESSO, retornoConsulta);
		base64 = baixarDocumentoProcesso(retornoConsulta);
		nomedoc = "Consulta Processual";
		FileService fileService = new FileService();
		fileService.salvarPdfRetorno(documentoAnalise, base64, nomedoc, "interno");
	}
	
	private void requestCndEstadualNetrinConulta(NetrinConsulta netrinConsulta, DocumentoAnalise documentoAnalise,
			PagadorRecebedorService PagadorRecebedorService) {
		String retornoConsulta;
		String base64;
		String nomedoc;
		retornoConsulta = netrinCriarExecutaConsultaCNDEstadual(netrinConsulta.getCpfCnpj(), netrinConsulta.getUf());
		netrinConsulta.setRetorno(retornoConsulta);
		netrinConsulta.setPdf(baixarDocumentoCNDEstadual(retornoConsulta));
		PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
				DocumentosAnaliseEnum.CNDESTADUAL, retornoConsulta, netrinConsulta.getUf());
		base64 = baixarDocumentoCNDEstadual(retornoConsulta);
		nomedoc = "CND Estadual " +  netrinConsulta.getUf().toUpperCase();
		FileService fileService = new FileService();
		fileService.salvarPdfRetorno(documentoAnalise, base64, nomedoc, "interno");
	}
	
	private void requestCndFederalNetrinConulta(NetrinConsulta netrinConsulta, DocumentoAnalise documentoAnalise,
			PagadorRecebedorService PagadorRecebedorService) {
		String retornoConsulta;
		String base64;
		String nomedoc;
		retornoConsulta = netrinCriarExecutaConsultaCNDFederal(netrinConsulta.getCpfCnpj());
		netrinConsulta.setRetorno(retornoConsulta);
		netrinConsulta.setPdf(baixarDocumentoCNDFederal(retornoConsulta));
		PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
				DocumentosAnaliseEnum.CNDFEDERAL, retornoConsulta);
		base64 = baixarDocumentoCNDFederal(retornoConsulta);
		nomedoc = "CND Federal";
		FileService fileService = new FileService();
		fileService.salvarPdfRetorno(documentoAnalise, base64, nomedoc, "interno");
	}

	private void requestCndTrabalhistaTSTNetrinConulta(NetrinConsulta netrinConsulta, DocumentoAnalise documentoAnalise,
			PagadorRecebedorService PagadorRecebedorService) {
		String retornoConsulta;
		String base64;
		String nomedoc;
		retornoConsulta = netrinCriarExecutaConsultaCNDTrabalhistaTST(netrinConsulta.getCpfCnpj());
		netrinConsulta.setRetorno(retornoConsulta);
		netrinConsulta.setPdf(baixarDocumentoCNDTrabalhistaTST(retornoConsulta));
		PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
				DocumentosAnaliseEnum.CNDTTST, retornoConsulta);
		base64 = baixarDocumentoCNDFederal(retornoConsulta);
		nomedoc = "CNDT TST";
		FileService fileService = new FileService();
		fileService.salvarPdfRetorno(documentoAnalise, base64, nomedoc, "interno");
	}
}
