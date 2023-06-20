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
import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;

import br.com.galleriabank.netrin.cliente.model.PPE.PpeResponse;
import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPF;
import br.com.galleriabank.netrin.cliente.model.receitafederal.ReceitaFederalPJ;
import br.com.galleriabank.serasacrednet.cliente.util.GsonUtil;

public class NetrinService {

	public void requestCenprot(DocumentoAnalise documentoAnalise) {

		if (CommonsUtil.semValor(documentoAnalise.getRetornoCenprot())) {
			netrinCriarConsultaCenprot(documentoAnalise);
		}
	}

	public FacesMessage netrinCriarConsultaCenprot(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			FacesMessage result = null;

			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			URL myURL;
			// if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
			myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/protesto/"
					+ CommonsUtil.somenteNumeros(cnpjcpf));
			// else
			// myURL = new
			// URL("https://api.netrin.com.br/v1/consulta-composta?s=protestos-cenprot&cnpj="
			// + CommonsUtil.somenteNumeros(cnpjcpf));

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
				documentoAnalise.setRetornoCenprot(response.toString());

				documentoAnaliseDao.merge(documentoAnalise);
				
				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.CENPROT, response.toString());

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

			URL myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/protesto/false");

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
				byte[] input = documentoAnalise.getRetornoCenprot().getBytes("utf-8");
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
		result =  netrinCriarConsultaCadastroPpePF(documentoAnalise);
		return result;
	}
	
	public ReceitaFederalPF requestCadastroPF(String sCpfCnpj) {
		FacesMessage facesMessage = new FacesMessage();		
		return netrinCriarConsultaCadastroPF(sCpfCnpj, facesMessage);
	}
	
	public ReceitaFederalPF requestCadastroPF(PagadorRecebedor pagadorRecebedor) {
		FacesMessage facesMessage = new FacesMessage();		
		
		ReceitaFederalPF receitaFederalPF  = netrinCriarConsultaCadastroPF(pagadorRecebedor.getCpf(), facesMessage);
		
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

	public FacesMessage netrinCriarConsultaCadastroPpePF(DocumentoAnalise documentoAnalise) { // POST para
																										// gerar
																										// consulta
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			FacesMessage result = null;


			atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();
			String numeorsCpfCnpj = CommonsUtil.somenteNumeros(documentoAnalise.getCnpjcpf());

			URL myURL;
			myURL = new URL("https://servicos.galleriabank.com.br/netrin/api/v1/ppe/"
					+ numeorsCpfCnpj + "/" + nomeConsultado.replace(" ","%20") );

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
				result = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"PPE: Falha  (Cod: " + myURLConnection.getResponseCode() + ")", "");
			} else {
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				PpeResponse resultPEP = GsonUtil.fromJson(response.toString(), PpeResponse.class);
				
				DocumentoAnaliseDao documentoAnaliseDao = new DocumentoAnaliseDao();
				documentoAnalise.setRetornoPpe(response.toString());

				documentoAnaliseDao.merge(documentoAnalise);
				
				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.PPE , response.toString());
				

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
	
	public ReceitaFederalPJ requestCadastroPJ(String sCpfCnpj) {

		FacesMessage facesMessage = new FacesMessage();
		return netrinCriarConsultaCadastroPJ(sCpfCnpj, facesMessage);
	}
	
	public ReceitaFederalPJ requestCadastroPJ(PagadorRecebedor pagadorRecebedor) {

		FacesMessage facesMessage = new FacesMessage();
		ReceitaFederalPJ receitaFederalPJ  = netrinCriarConsultaCadastroPJ(pagadorRecebedor.getCnpj(), facesMessage);

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

			String tipoConsulta = CommonsUtil.pessoaFisicaJuridicaCnpjCpf(numeorsCpfCnpj);
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
			netrinCriarConsultatProcesso(documentoAnalise);
		}
	}

	public FacesMessage netrinCriarConsultatProcesso(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
		try {
			// loginDocket();
			int HTTP_COD_SUCESSO = 200;
			FacesMessage result = null;
			
			//busca dados da receita se nao tiver ainda
			atualizaDadosPagadoRecebedorComReceitaFederal(documentoAnalise.getPagador());
			String nomeConsultado = documentoAnalise.getPagador().getNome();
					
			String cnpjcpf = documentoAnalise.getCnpjcpf();
			if (!CommonsUtil.semValor(documentoAnalise.getPagador())) {
				if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
					cnpjcpf = documentoAnalise.getPagador().getCpf();
				else
					cnpjcpf = documentoAnalise.getPagador().getCnpj();
			}

			URL myURL;
			String sUrl= "https://servicos.galleriabank.com.br/netrin/api/v1/processo/"
					+ CommonsUtil.somenteNumeros(cnpjcpf) + "/" + nomeConsultado.replace(" ","%20") ;
			// if (CommonsUtil.mesmoValor("PF", documentoAnalise.getTipoPessoa()))
			myURL = new URL(sUrl) ;
			// else
			// myURL = new
			// URL("https://api.netrin.com.br/v1/consulta-composta?s=protestos-cenprot&cnpj="
			// + CommonsUtil.somenteNumeros(cnpjcpf));

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			String sBearer  = br.com.galleriabank.jwt.common.JwtUtil.generateJWTServicos();
			myURLConnection.setRequestProperty("Authorization",
					"Bearer " + sBearer );
			myURLConnection.setDoOutput(true);

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
				documentoAnalise.setRetornoProcesso(response.toString());

				documentoAnaliseDao.merge(documentoAnalise);
				
				PagadorRecebedorService PagadorRecebedorService = new PagadorRecebedorService();
				PagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(documentoAnalise.getPagador(),
						DocumentosAnaliseEnum.PROCESSO, response.toString());

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

	private String atualizaDadosPagadoRecebedorComReceitaFederal(PagadorRecebedor pagadorRecebedor) {
		String nomeConsultado = "";
		if (!CommonsUtil.semValor(pagadorRecebedor.getCpf())) {
			PagadorRecebedorService ppagaPagadorRecebedorService = new PagadorRecebedorService();
			PagadorRecebedorConsulta pagadorRecebedorConsulta = ppagaPagadorRecebedorService
					.buscaConsultaNoPagadorRecebedor(pagadorRecebedor,
							DocumentosAnaliseEnum.RECEITA_FEDERAL);
			ReceitaFederalPF receitaFederalPF = null;
			if (!CommonsUtil.semValor(pagadorRecebedorConsulta)) {
				receitaFederalPF = GsonUtil.fromJson(pagadorRecebedorConsulta.getRetornConsulta(),
						ReceitaFederalPF.class);
			}else {
				receitaFederalPF = requestCadastroPF(pagadorRecebedor.getCpf());
			}

			if (!CommonsUtil.semValor(receitaFederalPF)
					&& !CommonsUtil.semValor(pagadorRecebedor.getId())) {
				ppagaPagadorRecebedorService.adicionarConsultaNoPagadorRecebedor(pagadorRecebedor,
						DocumentosAnaliseEnum.RECEITA_FEDERAL, GsonUtil.toJson(receitaFederalPF));
			}

			nomeConsultado = receitaFederalPF.getCpfBirthdate().getNome();
		}
		return nomeConsultado;
	}

	public String baixarDocumentoProcesso(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
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
				byte[] input = documentoAnalise.getRetornoProcesso().getBytes("utf-8");
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

	public String baixarDocumentoPpe(DocumentoAnalise documentoAnalise) { // POST para gerar consulta
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
				byte[] input = documentoAnalise.getRetornoPpe().getBytes("utf-8");
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

			boolean detalhadoProtesto  =false;
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

	
	
	
}
